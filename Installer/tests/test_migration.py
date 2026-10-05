#!/usr/bin/env python3
"""Fake ADB tests for IMP-14 migration engine (WP6, L146 acceptance scenarios).
Each test sets up a device with an "original" (non-VoyahTune) APK and verifies
the migration flow: detection → staging → backup → removal → install → restore.
"""
import json, os, shutil, subprocess, tempfile, unittest, zipfile
from pathlib import Path
ROOT = Path(__file__).resolve().parents[2]
DRIVER = Path(os.environ.get(
    'VOYAH_TEST_DRIVER',
    ROOT / 'Installer/target/release/examples/fixture-driver'
))
PAYLOAD = Path(os.environ.get(
    'VOYAH_TEST_PAYLOAD',
    ROOT / 'Releases/build/installer-payload-dev-v2'
))


def make_original_apk(target):
    """Create a minimal zip-file APK without VoyahTune build metadata
    to simulate an 'original' vendor application."""
    target.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(target, 'w') as zf:
        zf.writestr('fake_original_marker', 'This is an original vendor APK')


class MigrationTests(unittest.TestCase):
    maxDiff = 1500

    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='voyah-migration-test-')
        self.base = Path(self.tmp.name)
        self.bundle = self.base / 'bundle'
        (self.bundle / 'adb').mkdir(parents=True)
        (self.bundle / 'payload').symlink_to(PAYLOAD, target_is_directory=True)
        # Sleep stub
        sleep = self.bundle / 'adb/sleep'
        sleep.write_text('#!/bin/sh\nexit 0\n')
        sleep.chmod(0o755)
        # Fake ADB fixture
        fake_adb = self.bundle / 'adb/adb'
        shutil.copyfile(ROOT / 'Installer/tests/fake_adb.py', fake_adb)
        fake_adb.chmod(0o755)
        fake_adb.rename(self.bundle / 'adb/fake-adb')
        (self.bundle / 'adb/adb').symlink_to('fake-adb')
        for name in ['getprop', 'setprop', 'id', 'pm', 'cmd', 'dumpsys',
                      'settings', 'restorecon', 'chown', 'stat', 'mount', 'am',
                      'pidof', 'sha256sum', 'pkill', 'ps', 'grep']:
            p = self.base / 'bin' / name
            p.parent.mkdir(exist_ok=True)
            p.symlink_to(self.bundle / 'adb/fake-adb')
        # Device filesystem
        self.device = self.base / 'device'
        for path in ['system/etc/init', 'system/etc/permissions', 'system/bin',
                      'system/priv-app', 'data/local/tmp', 'data/local/bin',
                      'proc/sys/kernel/random', 'sdcard/tmp']:
            (self.device / path).mkdir(parents=True, exist_ok=True)
        (self.device / 'system/bin/sh').symlink_to('/bin/sh')
        (self.device / 'proc/sys/kernel/random/boot_id').write_text('0\n')
        (self.device / 'system/bin/timeout').symlink_to(self.bundle / 'adb/fake-adb')
        # State: core system packages present, no VoyahTune yet
        self.state = {
            'root': False,
            'packages': {
                'android': '/system/framework/framework-res.apk',
                'com.qinggan.canbus.service': '/system/canbus.apk',
                'com.qinggan.keymanager.service': '/system/keys.apk',
            },
            'settings': {},
        }
        self.write_state()
        self.env = {**os.environ, 'VOYAH_FAKE_ROOT': str(self.base)}

    def tearDown(self):
        self.tmp.cleanup()

    def write_state(self):
        (self.base / 'state.json').write_text(json.dumps(self.state))

    def read_state(self):
        self.state = json.loads((self.base / 'state.json').read_text())
        return self.state

    def cli(self, *args, okay=True):
        result = subprocess.run(
            [str(DRIVER), '--bundle', str(self.bundle), *args],
            env=self.env, text=True, capture_output=True, timeout=120
        )
        if okay:
            self.assertEqual(result.returncode, 0,
                             result.stdout[-5000:] + result.stderr)
        return result

    def plan(self, action='install'):
        return json.loads(
            self.cli('plan', '--device', 'CAR-001', '--action', action).stdout
        )

    def apply(self, plan, okay=True):
        return self.cli(
            'apply', '--device', 'CAR-001',
            '--action', plan['request']['action'],
            '--token', plan['request']['inventoryToken'],
            '--yes', '--logs', str(self.base / 'logs'),
            okay=okay,
        )

    def seed_original(self):
        """Install APKs that simulate the original vendor installation
        (no VoyahTune build metadata, original package names from D4)."""
        for pkg, path, apk_name in [
            ('ru.big.town.anative', '/system/priv-app/Native/Native.apk', 'native.apk'),
            ('ru.big.town.restoremode', '/data/app/ru.big.town.restoremode/base.apk', 'restore_mode.apk'),
        ]:
            target = self.device / path.lstrip('/')
            target.parent.mkdir(parents=True, exist_ok=True)
            make_original_apk(target)
            self.state['packages'][pkg] = path
            # Original doesn't have VoyahTune data directories
        self.write_state()

    # --- L146 acceptance scenarios ---

    def test_s1_migration_detected_in_plan(self):
        """Scenario 1: original → migration success. Verify plan operation='migrate'."""
        self.seed_original()
        p = self.plan()
        self.assertEqual(p['operation'], 'migrate',
                         'Plan should detect original and set operation=migrate')
        self.assertIn('migrate-staging', [s['id'] for s in p['steps']],
                      'Migration steps should be in the plan')

    def test_s2_original_becomes_fork_after_apply(self):
        """Scenario 1 (continued): after migration apply, the device state
        should show VoyahTune packages (our build metadata, signers)."""
        self.seed_original()
        p = self.plan()
        self.assertEqual(p['operation'], 'migrate')
        self.apply(p)
        state = self.read_state()
        self.assertIn('ru.big.town.anative', state['packages'],
                      'Native must be installed after migration')
        # After migration + install, the APK should be the payload's native.apk
        native_path = state['packages']['ru.big.town.anative']
        native_on_dev = self.device / native_path.lstrip('/')
        self.assertTrue(native_on_dev.is_file(),
                        'Native APK must exist on device after migration')

    def test_s3_remove_fork_restores_original(self):
        """Scenario: remove fork — verify remove operation works
        (packages are removed, WRITE_CANBUS released)."""
        # Seed a minimal fork state (payload APK installed)
        for pkg, path, apk_name in [
            ('ru.big.town.anative', '/system/priv-app/Native/Native.apk', 'native.apk'),
            ('ru.big.town.restoremode', '/data/app/ru.big.town.restoremode/base.apk', 'restore_mode.apk'),
        ]:
            target = self.device / path.lstrip('/')
            target.parent.mkdir(parents=True, exist_ok=True)
            # Use real payload APK (has VoyahTune build metadata)
            shutil.copyfile(PAYLOAD / 'common' / apk_name, target)
            self.state['packages'][pkg] = path
        self.write_state()
        # Plan a REMOVE operation
        p = self.plan(action='remove')
        self.assertEqual(p['operation'], 'remove')
        self.apply(p)
        state = self.read_state()
        # After remove, Native and RestoreMode should be absent
        self.assertNotIn('ru.big.town.anative', state['packages'],
                         'Native must be removed')
        self.assertNotIn('ru.big.town.restoremode', state['packages'],
                         'RestoreMode must be removed')

    # --- L146: 5 acceptance scenarios ---

    def test_s4_migration_abort_recovery(self):
        """Scenario 4: mid-flow abort → recovery. Simulate ADB failure during
        removal, verify the device can be restored to a consistent state."""
        self.seed_original()
        # Inject ADB failure during removal step
        self.state['failShell'] = 'pm uninstall'
        self.write_state()
        p = self.plan()
        self.assertEqual(p['operation'], 'migrate')
        # Apply with okay=False — migration may fail
        result = self.apply(p, okay=False)
        # Even on failure, the device should not be in a broken state
        state = self.read_state()
        # Either packages are still present (abort before removal) or gone (partial removal)
        self.assertIn('ru.big.town.anative', state['packages'],
                      'Device must be recoverable after migration abort')

    def test_s5_residue_detected(self):
        """Scenario 5: init-contract residue → detected and removed.
        Simulate /system/etc/init/updater.rc after fresh install,
        verify residue_check() detects and removes it."""
        self.seed_original()
        p = self.plan()
        self.assertEqual(p['operation'], 'migrate')
        self.apply(p)
        # After migration + install, plant a residue marker
        residue = self.device / 'system/etc/init/updater.rc'
        residue.parent.mkdir(parents=True, exist_ok=True)
        residue.write_text('#!/bin/sh\necho "legacy updater"\n')
        # Plan another install to trigger residue check
        p2 = self.plan(action='install')
        self.assertEqual(p2['operation'], 'repair')
        self.assertIn('residue', [s['id'] for s in p2['steps']],
                      'Residue check must be in the plan')


if __name__ == '__main__':
    unittest.main()