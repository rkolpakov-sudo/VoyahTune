#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
node --check "$ROOT/Packaging/payload-common/voyahtune_acc_restore.js"
node "$ROOT/Packaging/tests/test_acc_restore_hook.js"
