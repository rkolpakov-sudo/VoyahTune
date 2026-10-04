import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/WiperColdService.java")
t = p.read_text(encoding="utf-8", errors="replace")
log = []

old1 = """            } catch (Exception e) {
                Log.w(TAG, "pauseActiveMedia: getStreamVolume: " + e.getMessage());
                i2 = -1;
            }"""
new1 = """            } catch (Exception e) {
                Log.w(TAG, "pauseActiveMedia: getStreamVolume: " + e.getMessage());
                streamVolume = -1;
            }"""
assert old1 in t, "MISS1"
t = t.replace(old1, new1, 1)
log.append("streamVolume catch fixed")

old2 = """    private void dispatchDoorPause(AudioManager audioManager, int i) {
        boolean z;"""
new2 = """    private void dispatchDoorPause(AudioManager audioManager, int i) {
        boolean z = false;"""
assert old2 in t, "MISS2"
t = t.replace(old2, new2, 1)
log.append("z init false")

p.write_text(t, encoding="utf-8")
print("\n".join(log))
