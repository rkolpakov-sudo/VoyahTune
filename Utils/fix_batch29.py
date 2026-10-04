import io, pathlib, re, sys

ROOT = pathlib.Path("RestoreMode/app/src/main/java/ru/big/town/restoremode")

def load(p): return io.open(p, encoding="utf-8").read()
def save(p, t): io.open(p, "w", encoding="utf-8", newline="").write(t)

ok = True
def rep(t, old, new, path, count=1):
    global ok
    n = t.count(old)
    if n != count:
        print(f"  !! {path.name}: x{n} (expected {count}): {old[:90]!r}")
        ok = False
        return t
    return t.replace(old, new)

# ---------------- AdvanceActivity ----------------
p = ROOT / "AdvanceActivity.java"
t = load(p)
t = rep(t, "import com.google.android.material.card.MaterialCardViewHelper;\n", "", p)
t = rep(t, "import com.google.android.material.timepicker.TimeModel;\n", "", p)
t = rep(t, "ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 213, 240, 260, 280, MaterialCardViewHelper.DEFAULT_FADE_ANIM_DURATION",
           "200, 213, 240, 260, 280, 300", p)
t = rep(t, 'String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(sb.length()))',
           'String.format("%d", Integer.valueOf(sb.length()))', p, 1)
t = rep(t, "        for (final int i = 0; i < entry.profiles.size(); i++) {\n            final AppWidgetStore.Profile profile = entry.profiles.get(i);",
           "        for (int iIdx = 0; iIdx < entry.profiles.size(); iIdx++) {\n            final int i = iIdx;\n            final AppWidgetStore.Profile profile = entry.profiles.get(i);", p)
t = rep(t, "        for (final int i = 0; i < listLoad.size(); i++) {\n            SplitStore.Preset preset = listLoad.get(i);",
           "        for (int iIdx = 0; iIdx < listLoad.size(); iIdx++) {\n            final int i = iIdx;\n            SplitStore.Preset preset = listLoad.get(i);", p)
t = rep(t, '                    Log.w("SystemMetrics", "CPU read failed: " + e.getMessage());\n                    return null;', '                    return null;', p)
t = rep(t, "        final int i = 0;\n        while (i < listLoad.size()) {\n",
           "        int iIdx = 0;\n        while (iIdx < listLoad.size()) {\n            final int i = iIdx;\n", p)
t = rep(t, "            linearLayout.addView(viewInflate);\n            i = i2;\n        }\n    }",
           "            linearLayout.addView(viewInflate);\n            iIdx = i2;\n        }\n    }", p)
save(p, t); print("AdvanceActivity ok =", ok)

# ---------------- AppWidgetStore ----------------
p = ROOT / "AppWidgetStore.java"
t = load(p)
t = rep(t, "import com.google.android.material.card.MaterialCardViewHelper;\n", "", p)
t = rep(t, "ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 213, 240, 260, 280, MaterialCardViewHelper.DEFAULT_FADE_ANIM_DURATION",
           "200, 213, 240, 260, 280, 300", p)
t = rep(t, 'OptJSONObject.optBoolean("autoStart", r5)', 'OptJSONObject.optBoolean("autoStart", r5 != 0)', p)
save(p, t); print("AppWidgetStore ok =", ok)

# ---------------- TileDragController ----------------
p = ROOT / "TileDragController.java"
t = load(p)
t = rep(t, "import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;\n", "", p)
t = rep(t, "View.MeasureSpec.makeMeasureSpec(this.grid.getHeight(), BasicMeasure.EXACTLY)",
           "View.MeasureSpec.makeMeasureSpec(this.grid.getHeight(), 1073741824)", p)
save(p, t); print("TileDragController ok =", ok)

# ---------------- NowPlayingClient ----------------
p = ROOT / "NowPlayingClient.java"
t = load(p)
t = rep(t, "import androidx.constraintlayout.core.motion.utils.TypedValues;\n", "", p)
t = rep(t, "TypedValues.TransitionType.S_DURATION", '"duration"', p, 2)
t = rep(t, "                } catch (Throwable th2) {\n                    Throwable th = th2;",
           "                } catch (Throwable th2) {\n                    th = th2;", p)
save(p, t); print("NowPlayingClient ok =", ok)

# ---------------- VoiceSettingsPage ----------------
p = ROOT / "VoiceSettingsPage.java"
t = load(p)
t = rep(t, "        LinkedHashMap linkedHashMap = new LinkedHashMap();\n        LinkedHashMap linkedHashMap2 = new LinkedHashMap();",
           "        LinkedHashMap<String, VoiceCommandCatalog.Command> linkedHashMap = new LinkedHashMap<>();\n        LinkedHashMap<String, LinkedHashSet<String>> linkedHashMap2 = new LinkedHashMap<>();", p)
t = rep(t, "            LinkedHashMap linkedHashMap3 = new LinkedHashMap();",
           "            LinkedHashMap<String, VoiceCommandCatalog.Command> linkedHashMap3 = new LinkedHashMap<>();", p)
t = rep(t, "VoiceCommandGroups.Group group, Map map, LinearLayout linearLayout, Map map2) {",
           "VoiceCommandGroups.Group group, Map<String, VoiceCommandCatalog.Command> map, LinearLayout linearLayout, Map<String, LinkedHashSet<String>> map2) {", p)
save(p, t); print("VoiceSettingsPage ok =", ok)

# ---------------- VoiceFuzzyMatcher ----------------
p = ROOT / "VoiceFuzzyMatcher.java"
t = load(p)
t = rep(t, "        HashMap map = new HashMap();\n        HashMap map2 = new HashMap();\n        HashMap map3 = new HashMap();",
           '        HashMap<String, Integer> map = new HashMap<>();\n        HashMap<String, VoiceCommandCatalog.Command> map2 = new HashMap<>();\n        HashMap<String, Boolean> map3 = new HashMap<>();', p)
save(p, t); print("VoiceFuzzyMatcher ok =", ok)

# ---------------- MainActivity ----------------
p = ROOT / "MainActivity.java"
t = load(p)
t = rep(t, "for (String str2 : new ArrayList(this.embeddedWidgetSurfaces.keySet()))",
           "for (String str2 : new ArrayList<>(this.embeddedWidgetSurfaces.keySet()))", p)
t = rep(t, "        for (final int i = 0; i < entry.profiles.size(); i++) {\n            AppWidgetStore.Profile profile = entry.profiles.get(i);",
           "        for (int iIdx = 0; iIdx < entry.profiles.size(); iIdx++) {\n            final int i = iIdx;\n            AppWidgetStore.Profile profile = entry.profiles.get(i);", p)
t = rep(t, """                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.AnonymousClass9.lambda$$0(view);
                }
            };
        }

        static /* synthetic */ void lambda$$0(View view) {
            if (view != null) {
                view.setVisibility(8);
            }
        }
""",
    """                @Override // java.lang.Runnable
                public final void run() {
                    if (view != null) {
                        view.setVisibility(8);
                    }
                }
            };
        }
""", p)
save(p, t); print("MainActivity ok =", ok)

# ---------------- SuspensionWidgetView ----------------
p = ROOT / "SuspensionWidgetView.java"
t = load(p)
t = rep(t, "        final Bitmap bitmapDecodeStream = null;", "        Bitmap bitmapDecodeStream = null;", p)
save(p, t); print("SuspensionWidgetView ok =", ok)

# ---------------- VoiceActivity ----------------
p = ROOT / "VoiceActivity.java"
t = load(p)
t = rep(t, ".getLifecycle().getState().isAtLeast(", ".getLifecycle().getCurrentState().isAtLeast(", p)
save(p, t); print("VoiceActivity ok =", ok)

# ---------------- VoiceRecognizer ----------------
p = ROOT / "VoiceRecognizer.java"
t = load(p)
t = rep(t, "            final boolean z = false;\n            try {\n                prepareEngine(context, VoiceAudioConfig.read(context.getSharedPreferences(\"DrivePreferences\", 0)));\n                z = true;",
           "            boolean zReady = false;\n            try {\n                prepareEngine(context, VoiceAudioConfig.read(context.getSharedPreferences(\"DrivePreferences\", 0)));\n                zReady = true;", p)
t = rep(t, "VoiceRecognizer.lambda$keepWarm$0(j, consumer, z);",
           "VoiceRecognizer.lambda$keepWarm$0(j, consumer, zReady);", p)
t = rep(t, """            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                VoiceRecognizer.lambda$start$8(jBegin, applicationContext, voiceAudioConfig, listener);
            }""",
    """            @Override // java.lang.Runnable
            public final void run() {
                try {
                    VoiceRecognizer.lambda$start$8(jBegin, applicationContext, voiceAudioConfig, listener);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }""", p)
t = rep(t, "                                            } catch (Throwable th2) {\n                                                Throwable th = th2;\n                                                th = th;\n",
           "                                            } catch (Throwable th2) {\n                                                th = th2;\n", p)
t = rep(t, "                                    } catch (Throwable th3) {\n                                        Throwable th = th3;\n                                        file2 = file3;",
           "                                    } catch (Throwable th3) {\n                                        th = th3;\n                                        file2 = file3;", p)
t = rep(t, "                            } catch (Throwable th4) {\n                                Throwable th = th4;\n                                file = file3;",
           "                            } catch (Throwable th4) {\n                                th = th4;\n                                file = file3;", p)
save(p, t); print("VoiceRecognizer ok =", ok)

# ---------------- VoiceEngineCache ----------------
p = ROOT / "VoiceEngineCache.java"
t = load(p)
t = rep(t, "VoiceEngineCache$$ExternalSyntheticThrowIAE2.m(t);", "throw new IllegalArgumentException(t);", p)
save(p, t); print("VoiceEngineCache ok =", ok)

# ---------------- VoiceCommands ----------------
p = ROOT / "VoiceCommands.java"
t = load(p)
t = rep(t, "        boolean z = c;", "        boolean z = c == 1;", p)
save(p, t); print("VoiceCommands ok =", ok)

# ---------------- VoiceModels ----------------
p = ROOT / "VoiceModels.java"
t = load(p)
t = rep(t, """                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                    }
                    throw th;
                }
                fileOutputStream.close();""",
    """                }
                fileOutputStream.close();""", p)
save(p, t); print("VoiceModels ok =", ok)

# ---------------- VoiceSeatCommands ----------------
p = ROOT / "VoiceSeatCommands.java"
t = load(p)
t, n = re.subn(r"(\b\w+) == true \? 1 : 0", r"\1 == 1 ? 1 : 0", t)
print(f"  VoiceSeatCommands ==true replacements: {n} (expected 8)")
if n != 8: ok = False
t = rep(t, "            boolean z = i6 == 1 ? 1 : 0;", "            boolean z = i6 == 1;", p)
t = rep(t, 'str + " " + str4 + ": " + str2, i8, (String[]) arrayList2.toArray(new String[i8])',
           'str + " " + str4 + ": " + str2, i8 != 0, (String[]) arrayList2.toArray(new String[i8])', p)
save(p, t); print("VoiceSeatCommands ok =", ok)

# ---------------- VoiceWarmupService ----------------
p = ROOT / "VoiceWarmupService.java"
t = load(p)
t = rep(t, "import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;",
           "import android.content.pm.ServiceInfo;", p)
t = rep(t, "startForeground(NOTIFICATION, notification, BasicMeasure.EXACTLY);",
           "startForeground(NOTIFICATION, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);", p)
save(p, t); print("VoiceWarmupService ok =", ok)

print("ALL OK" if ok else "SOME PATTERNS FAILED")
sys.exit(0 if ok else 1)
