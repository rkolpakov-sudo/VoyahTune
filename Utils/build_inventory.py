#!/usr/bin/env python3
"""WP0 шаг 0.2: расширенная инвентаризация payload.

Читает payload/manifest.json (schema 4, 29 артефактов), обогащает каждый артефакт:
  type, role, extract_strategy, category, destination (из recipe)
и пишет payload/inventory.json.
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

JS_HOOKS = {
    "apollo_tech.js", "app_client.js", "keyboard_lock_en.js", "keyboard_ru.js",
    "launcherdock.js", "multidisplay.js", "steeringwheelkeys.js", "vd_bypass.js",
    "voyahtune_acc_restore.js", "voyahtune_drive_reset.js",
}
SHELL_SCRIPTS = {"dns-helper.sh", "voyahtune.load.sh", "init.logcat.original.sh",
                 "load.bin"}  # load.bin начинается с "#!/s" — shell-loader, не бинарь
INIT_RC = {"voyahtune.load.rc", "voyahtune.updater.rc"}
CONFIG_JSON = {
    "voyahtune-ota-bootstrap.json", "voyahtune_keyboard_en_config.json",
    "voyahtune_keyboard_ru_config.json", "voyahtune_skb_qwerty_ru.json",
}
APK_SUPPORTED = {"native.apk", "restore_mode.apk", "voyahtune-updater.apk",
                 "voyahtune-ui-next.apk"}
APK_CONFIG = {"dns.apk"}
BLOBS = {"frida-inject", "voyahtune-ui-maintenance", "voyahtune-updater"}


def classify(name: str) -> dict:
    if name in APK_SUPPORTED:
        return dict(type="apk", role="supported-code",
                    extract_strategy="jadx + apktool (full decompile)",
                    category="code")
    if name in APK_CONFIG:
        return dict(type="apk", role="config-overlay",
                    extract_strategy="jadx (скриптовая часть)",
                    category="configuration")
    if name in JS_HOOKS:
        return dict(type="js", role="supported-source",
                    extract_strategy="copy-as-source",
                    category="code")
    if name in SHELL_SCRIPTS:
        return dict(type="shell", role="supported-source",
                    extract_strategy="copy-as-source",
                    category="code")
    if name in INIT_RC:
        return dict(type="init-rc", role="boot-contract",
                    extract_strategy="copy-as-source",
                    category="configuration")
    if name in CONFIG_JSON:
        return dict(type="json", role="configuration",
                    extract_strategy="copy-as-source",
                    category="configuration")
    if name == "whitelist.xml":
        return dict(type="xml", role="privapp-permissions",
                    extract_strategy="copy-as-source",
                    category="configuration")
    if name in BLOBS:
        return dict(type="binary", role="blob (rehost, no reverse, D16)",
                    extract_strategy="rehost-by-sha256",
                    category="binary-blob")
    return dict(type="unknown", role="TBD",
                extract_strategy="classify-manually",
                category="unknown")


def main() -> int:
    manifest = json.loads((ROOT / "payload" / "manifest.json").read_text(encoding="utf-8"))
    destinations = {}
    recipe = manifest.get("recipe", {})
    for entry in recipe.get("files", []) + recipe.get("packages", []):
        destinations[entry["artifact"]] = {
            "destination": entry.get("destination"),
            "phase": entry.get("phase", "packages"),
            "mode": entry.get("mode"),
        }

    artifacts = []
    unknown = []
    for art in manifest["artifacts"]:
        item = dict(art)
        item.update(classify(art["name"]))
        item.update(destinations.get(art["name"], {}))
        if item["category"] == "unknown":
            unknown.append(art["name"])
        artifacts.append(item)

    inventory = {
        "schema": "fork-inventory-v1",
        "base": {
            "product": manifest.get("product"),
            "releaseVersion": manifest.get("releaseVersion"),
            "buildRevision": manifest.get("buildRevision"),
            "manifestSchema": manifest.get("schema"),
        },
        "count": len(artifacts),
        "artifacts": artifacts,
    }
    out = ROOT / "payload" / "inventory.json"
    out.write_text(json.dumps(inventory, ensure_ascii=False, indent=2) + "\n",
                   encoding="utf-8")
    print(f"inventory: {len(artifacts)} artifacts -> {out}")
    if unknown:
        print("UNKNOWN:", ", ".join(unknown), file=sys.stderr)
    return 1 if unknown else 0


if __name__ == "__main__":
    raise SystemExit(main())
