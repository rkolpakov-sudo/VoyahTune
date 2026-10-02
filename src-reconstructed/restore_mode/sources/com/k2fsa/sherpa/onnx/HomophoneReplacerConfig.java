package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: HomophoneReplacerConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\nR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\b\"\u0004\b\u000e\u0010\n¨\u0006\u0019"}, d2 = {"Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;", "", "dictDir", "", "lexicon", "ruleFsts", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDictDir", "()Ljava/lang/String;", "setDictDir", "(Ljava/lang/String;)V", "getLexicon", "setLexicon", "getRuleFsts", "setRuleFsts", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class HomophoneReplacerConfig {
    private String dictDir;
    private String lexicon;
    private String ruleFsts;

    public HomophoneReplacerConfig() {
        this(null, null, null, 7, null);
    }

    public static /* synthetic */ HomophoneReplacerConfig copy$default(HomophoneReplacerConfig homophoneReplacerConfig, String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str = homophoneReplacerConfig.dictDir;
        }
        if ((i & 2) != 0) {
            str2 = homophoneReplacerConfig.lexicon;
        }
        if ((i & 4) != 0) {
            str3 = homophoneReplacerConfig.ruleFsts;
        }
        return homophoneReplacerConfig.copy(str, str2, str3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getDictDir() {
        return this.dictDir;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getLexicon() {
        return this.lexicon;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    public final HomophoneReplacerConfig copy(String dictDir, String lexicon, String ruleFsts) {
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        return new HomophoneReplacerConfig(dictDir, lexicon, ruleFsts);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HomophoneReplacerConfig)) {
            return false;
        }
        HomophoneReplacerConfig homophoneReplacerConfig = (HomophoneReplacerConfig) other;
        return Intrinsics.areEqual(this.dictDir, homophoneReplacerConfig.dictDir) && Intrinsics.areEqual(this.lexicon, homophoneReplacerConfig.lexicon) && Intrinsics.areEqual(this.ruleFsts, homophoneReplacerConfig.ruleFsts);
    }

    public int hashCode() {
        return (((this.dictDir.hashCode() * 31) + this.lexicon.hashCode()) * 31) + this.ruleFsts.hashCode();
    }

    public String toString() {
        return "HomophoneReplacerConfig(dictDir=" + this.dictDir + ", lexicon=" + this.lexicon + ", ruleFsts=" + this.ruleFsts + ')';
    }

    public HomophoneReplacerConfig(String dictDir, String lexicon, String ruleFsts) {
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        this.dictDir = dictDir;
        this.lexicon = lexicon;
        this.ruleFsts = ruleFsts;
    }

    public /* synthetic */ HomophoneReplacerConfig(String str, String str2, String str3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3);
    }

    public final String getDictDir() {
        return this.dictDir;
    }

    public final void setDictDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dictDir = str;
    }

    public final String getLexicon() {
        return this.lexicon;
    }

    public final void setLexicon(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lexicon = str;
    }

    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    public final void setRuleFsts(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ruleFsts = str;
    }
}
