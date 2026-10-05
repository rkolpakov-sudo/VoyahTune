#!/usr/bin/env python3
"""IMP-12: Offline payload verifier — validate payload ZIP against manifest + schema 4.
Usage: python3 Utils/offline_payload_verify.py <payload.zip> <manifest.json>
"""
import hashlib, json, sys, zipfile

def verify(payload_zip, manifest_path):
    with zipfile.ZipFile(payload_zip) as zf:
        if 'manifest.json' not in zf.namelist():
            return False, "manifest.json not in zip root"
        with zf.open('manifest.json') as f:
            manifest = json.load(f)
    with open(manifest_path) as f:
        ref = json.load(f)
    if manifest.get('schema') != 4:
        return False, f"schema={manifest.get('schema')} (expected 4)"
    if manifest.get('buildRevision') != ref.get('buildRevision'):
        return False, f"revision mismatch: {manifest.get('buildRevision')} vs {ref.get('buildRevision')}"
    for art in manifest.get('artifacts', []):
        zpath = f"common/{art['name']}"
        with zipfile.ZipFile(payload_zip) as zf:
            if zpath not in zf.namelist():
                return False, f"missing artifact: {zpath}"
    return True, f"OK revision={manifest.get('buildRevision')} artifacts={len(manifest.get('artifacts',[]))}"

if __name__ == '__main__':
    ok, msg = verify(sys.argv[1], sys.argv[2]) if len(sys.argv) >= 3 else (False, "Usage: ... <payload.zip> <manifest.json>")
    print(msg)
    sys.exit(0 if ok else 1)