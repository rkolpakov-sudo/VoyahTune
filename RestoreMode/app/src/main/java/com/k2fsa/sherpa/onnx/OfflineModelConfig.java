package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u007f\b\u0086\b\u0018\u00002\u00020\u0001Bÿ\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u001d\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u001f\u0012\b\b\u0002\u0010 \u001a\u00020!\u0012\b\b\u0002\u0010\"\u001a\u00020#\u0012\b\b\u0002\u0010$\u001a\u00020%\u0012\b\b\u0002\u0010&\u001a\u00020'\u0012\b\b\u0002\u0010(\u001a\u00020)\u0012\b\b\u0002\u0010*\u001a\u00020%\u0012\b\b\u0002\u0010+\u001a\u00020%\u0012\b\b\u0002\u0010,\u001a\u00020%\u0012\b\b\u0002\u0010-\u001a\u00020%\u0012\b\b\u0002\u0010.\u001a\u00020%¢\u0006\u0002\u0010/J\n\u0010\u008a\u0001\u001a\u00020\u0003HÆ\u0003J\n\u0010\u008b\u0001\u001a\u00020\u0015HÆ\u0003J\n\u0010\u008c\u0001\u001a\u00020\u0017HÆ\u0003J\n\u0010\u008d\u0001\u001a\u00020\u0019HÆ\u0003J\n\u0010\u008e\u0001\u001a\u00020\u001bHÆ\u0003J\n\u0010\u008f\u0001\u001a\u00020\u001dHÆ\u0003J\n\u0010\u0090\u0001\u001a\u00020\u001fHÆ\u0003J\n\u0010\u0091\u0001\u001a\u00020!HÆ\u0003J\n\u0010\u0092\u0001\u001a\u00020#HÆ\u0003J\n\u0010\u0093\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u0094\u0001\u001a\u00020'HÆ\u0003J\n\u0010\u0095\u0001\u001a\u00020\u0005HÆ\u0003J\n\u0010\u0096\u0001\u001a\u00020)HÆ\u0003J\n\u0010\u0097\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u0098\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u0099\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u009a\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u009b\u0001\u001a\u00020%HÆ\u0003J\n\u0010\u009c\u0001\u001a\u00020\u0007HÆ\u0003J\n\u0010\u009d\u0001\u001a\u00020\tHÆ\u0003J\n\u0010\u009e\u0001\u001a\u00020\u000bHÆ\u0003J\n\u0010\u009f\u0001\u001a\u00020\rHÆ\u0003J\n\u0010 \u0001\u001a\u00020\u000fHÆ\u0003J\n\u0010¡\u0001\u001a\u00020\u0011HÆ\u0003J\n\u0010¢\u0001\u001a\u00020\u0013HÆ\u0003J\u0084\u0002\u0010£\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\b\b\u0002\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020!2\b\b\u0002\u0010\"\u001a\u00020#2\b\b\u0002\u0010$\u001a\u00020%2\b\b\u0002\u0010&\u001a\u00020'2\b\b\u0002\u0010(\u001a\u00020)2\b\b\u0002\u0010*\u001a\u00020%2\b\b\u0002\u0010+\u001a\u00020%2\b\b\u0002\u0010,\u001a\u00020%2\b\b\u0002\u0010-\u001a\u00020%2\b\b\u0002\u0010.\u001a\u00020%HÆ\u0001J\u0015\u0010¤\u0001\u001a\u00020)2\t\u0010¥\u0001\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\n\u0010¦\u0001\u001a\u00020'HÖ\u0001J\n\u0010§\u0001\u001a\u00020%HÖ\u0001R\u001a\u0010.\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u00101\"\u0004\b2\u00103R\u001a\u0010 \u001a\u00020!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u00105\"\u0004\b6\u00107R\u001a\u0010\"\u001a\u00020#X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010;R\u001a\u0010(\u001a\u00020)X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b@\u0010A\"\u0004\bB\u0010CR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bD\u0010E\"\u0004\bF\u0010GR\u001a\u0010\u001e\u001a\u00020\u001fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bH\u0010I\"\u0004\bJ\u0010KR\u001a\u0010\u001a\u001a\u00020\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bL\u0010M\"\u0004\bN\u0010OR\u001a\u0010\u0018\u001a\u00020\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bP\u0010Q\"\u0004\bR\u0010SR\u001a\u0010+\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bT\u00101\"\u0004\bU\u00103R\u001a\u0010-\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bV\u00101\"\u0004\bW\u00103R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bX\u0010Y\"\u0004\bZ\u0010[R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\\\u0010]\"\u0004\b^\u0010_R\u001a\u0010&\u001a\u00020'X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b`\u0010a\"\u0004\bb\u0010cR\u001a\u0010\u0016\u001a\u00020\u0017X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bd\u0010e\"\u0004\bf\u0010gR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bh\u0010i\"\u0004\bj\u0010kR\u001a\u0010*\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bl\u00101\"\u0004\bm\u00103R\u001a\u0010\u001c\u001a\u00020\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bn\u0010o\"\u0004\bp\u0010qR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\br\u0010s\"\u0004\bt\u0010uR\u001a\u0010$\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bv\u00101\"\u0004\bw\u00103R\u001a\u0010,\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bx\u00101\"\u0004\by\u00103R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bz\u0010{\"\u0004\b|\u0010}R\u001c\u0010\u0014\u001a\u00020\u0015X\u0086\u000e¢\u0006\u0010\n\u0000\u001a\u0004\b~\u0010\u007f\"\u0006\b\u0080\u0001\u0010\u0081\u0001R\u001e\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0082\u0001\u0010\u0083\u0001\"\u0006\b\u0084\u0001\u0010\u0085\u0001R\u001e\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0086\u0001\u0010\u0087\u0001\"\u0006\b\u0088\u0001\u0010\u0089\u0001¨\u0006¨\u0001"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineModelConfig;", "", "transducer", "Lcom/k2fsa/sherpa/onnx/OfflineTransducerModelConfig;", "paraformer", "Lcom/k2fsa/sherpa/onnx/OfflineParaformerModelConfig;", "whisper", "Lcom/k2fsa/sherpa/onnx/OfflineWhisperModelConfig;", "fireRedAsr", "Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrModelConfig;", "moonshine", "Lcom/k2fsa/sherpa/onnx/OfflineMoonshineModelConfig;", "nemo", "Lcom/k2fsa/sherpa/onnx/OfflineNemoEncDecCtcModelConfig;", "senseVoice", "Lcom/k2fsa/sherpa/onnx/OfflineSenseVoiceModelConfig;", "dolphin", "Lcom/k2fsa/sherpa/onnx/OfflineDolphinModelConfig;", "zipformerCtc", "Lcom/k2fsa/sherpa/onnx/OfflineZipformerCtcModelConfig;", "wenetCtc", "Lcom/k2fsa/sherpa/onnx/OfflineWenetCtcModelConfig;", "omnilingual", "Lcom/k2fsa/sherpa/onnx/OfflineOmnilingualAsrCtcModelConfig;", "medasr", "Lcom/k2fsa/sherpa/onnx/OfflineMedAsrCtcModelConfig;", "funasrNano", "Lcom/k2fsa/sherpa/onnx/OfflineFunAsrNanoModelConfig;", "qwen3Asr", "Lcom/k2fsa/sherpa/onnx/OfflineQwen3AsrModelConfig;", "fireRedAsrCtc", "Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrCtcModelConfig;", "canary", "Lcom/k2fsa/sherpa/onnx/OfflineCanaryModelConfig;", "cohereTranscribe", "Lcom/k2fsa/sherpa/onnx/OfflineCohereTranscribeModelConfig;", "teleSpeech", "", "numThreads", "", "debug", "", "provider", "modelType", "tokens", "modelingUnit", "bpeVocab", "(Lcom/k2fsa/sherpa/onnx/OfflineTransducerModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineParaformerModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineWhisperModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineMoonshineModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineNemoEncDecCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineSenseVoiceModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineDolphinModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineZipformerCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineWenetCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineOmnilingualAsrCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineMedAsrCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineFunAsrNanoModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineQwen3AsrModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineCanaryModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineCohereTranscribeModelConfig;Ljava/lang/String;IZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getBpeVocab", "()Ljava/lang/String;", "setBpeVocab", "(Ljava/lang/String;)V", "getCanary", "()Lcom/k2fsa/sherpa/onnx/OfflineCanaryModelConfig;", "setCanary", "(Lcom/k2fsa/sherpa/onnx/OfflineCanaryModelConfig;)V", "getCohereTranscribe", "()Lcom/k2fsa/sherpa/onnx/OfflineCohereTranscribeModelConfig;", "setCohereTranscribe", "(Lcom/k2fsa/sherpa/onnx/OfflineCohereTranscribeModelConfig;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getDolphin", "()Lcom/k2fsa/sherpa/onnx/OfflineDolphinModelConfig;", "setDolphin", "(Lcom/k2fsa/sherpa/onnx/OfflineDolphinModelConfig;)V", "getFireRedAsr", "()Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrModelConfig;", "setFireRedAsr", "(Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrModelConfig;)V", "getFireRedAsrCtc", "()Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrCtcModelConfig;", "setFireRedAsrCtc", "(Lcom/k2fsa/sherpa/onnx/OfflineFireRedAsrCtcModelConfig;)V", "getFunasrNano", "()Lcom/k2fsa/sherpa/onnx/OfflineFunAsrNanoModelConfig;", "setFunasrNano", "(Lcom/k2fsa/sherpa/onnx/OfflineFunAsrNanoModelConfig;)V", "getMedasr", "()Lcom/k2fsa/sherpa/onnx/OfflineMedAsrCtcModelConfig;", "setMedasr", "(Lcom/k2fsa/sherpa/onnx/OfflineMedAsrCtcModelConfig;)V", "getModelType", "setModelType", "getModelingUnit", "setModelingUnit", "getMoonshine", "()Lcom/k2fsa/sherpa/onnx/OfflineMoonshineModelConfig;", "setMoonshine", "(Lcom/k2fsa/sherpa/onnx/OfflineMoonshineModelConfig;)V", "getNemo", "()Lcom/k2fsa/sherpa/onnx/OfflineNemoEncDecCtcModelConfig;", "setNemo", "(Lcom/k2fsa/sherpa/onnx/OfflineNemoEncDecCtcModelConfig;)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getOmnilingual", "()Lcom/k2fsa/sherpa/onnx/OfflineOmnilingualAsrCtcModelConfig;", "setOmnilingual", "(Lcom/k2fsa/sherpa/onnx/OfflineOmnilingualAsrCtcModelConfig;)V", "getParaformer", "()Lcom/k2fsa/sherpa/onnx/OfflineParaformerModelConfig;", "setParaformer", "(Lcom/k2fsa/sherpa/onnx/OfflineParaformerModelConfig;)V", "getProvider", "setProvider", "getQwen3Asr", "()Lcom/k2fsa/sherpa/onnx/OfflineQwen3AsrModelConfig;", "setQwen3Asr", "(Lcom/k2fsa/sherpa/onnx/OfflineQwen3AsrModelConfig;)V", "getSenseVoice", "()Lcom/k2fsa/sherpa/onnx/OfflineSenseVoiceModelConfig;", "setSenseVoice", "(Lcom/k2fsa/sherpa/onnx/OfflineSenseVoiceModelConfig;)V", "getTeleSpeech", "setTeleSpeech", "getTokens", "setTokens", "getTransducer", "()Lcom/k2fsa/sherpa/onnx/OfflineTransducerModelConfig;", "setTransducer", "(Lcom/k2fsa/sherpa/onnx/OfflineTransducerModelConfig;)V", "getWenetCtc", "()Lcom/k2fsa/sherpa/onnx/OfflineWenetCtcModelConfig;", "setWenetCtc", "(Lcom/k2fsa/sherpa/onnx/OfflineWenetCtcModelConfig;)V", "getWhisper", "()Lcom/k2fsa/sherpa/onnx/OfflineWhisperModelConfig;", "setWhisper", "(Lcom/k2fsa/sherpa/onnx/OfflineWhisperModelConfig;)V", "getZipformerCtc", "()Lcom/k2fsa/sherpa/onnx/OfflineZipformerCtcModelConfig;", "setZipformerCtc", "(Lcom/k2fsa/sherpa/onnx/OfflineZipformerCtcModelConfig;)V", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineModelConfig {
    private String bpeVocab;
    private OfflineCanaryModelConfig canary;
    private OfflineCohereTranscribeModelConfig cohereTranscribe;
    private boolean debug;
    private OfflineDolphinModelConfig dolphin;
    private OfflineFireRedAsrModelConfig fireRedAsr;
    private OfflineFireRedAsrCtcModelConfig fireRedAsrCtc;
    private OfflineFunAsrNanoModelConfig funasrNano;
    private OfflineMedAsrCtcModelConfig medasr;
    private String modelType;
    private String modelingUnit;
    private OfflineMoonshineModelConfig moonshine;
    private OfflineNemoEncDecCtcModelConfig nemo;
    private int numThreads;
    private OfflineOmnilingualAsrCtcModelConfig omnilingual;
    private OfflineParaformerModelConfig paraformer;
    private String provider;
    private OfflineQwen3AsrModelConfig qwen3Asr;
    private OfflineSenseVoiceModelConfig senseVoice;
    private String teleSpeech;
    private String tokens;
    private OfflineTransducerModelConfig transducer;
    private OfflineWenetCtcModelConfig wenetCtc;
    private OfflineWhisperModelConfig whisper;
    private OfflineZipformerCtcModelConfig zipformerCtc;

    public OfflineModelConfig() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null, null, null, 33554431, null);
    }

    public static /* synthetic */ OfflineModelConfig copy$default(OfflineModelConfig offlineModelConfig, OfflineTransducerModelConfig offlineTransducerModelConfig, OfflineParaformerModelConfig offlineParaformerModelConfig, OfflineWhisperModelConfig offlineWhisperModelConfig, OfflineFireRedAsrModelConfig offlineFireRedAsrModelConfig, OfflineMoonshineModelConfig offlineMoonshineModelConfig, OfflineNemoEncDecCtcModelConfig offlineNemoEncDecCtcModelConfig, OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig, OfflineDolphinModelConfig offlineDolphinModelConfig, OfflineZipformerCtcModelConfig offlineZipformerCtcModelConfig, OfflineWenetCtcModelConfig offlineWenetCtcModelConfig, OfflineOmnilingualAsrCtcModelConfig offlineOmnilingualAsrCtcModelConfig, OfflineMedAsrCtcModelConfig offlineMedAsrCtcModelConfig, OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig, OfflineQwen3AsrModelConfig offlineQwen3AsrModelConfig, OfflineFireRedAsrCtcModelConfig offlineFireRedAsrCtcModelConfig, OfflineCanaryModelConfig offlineCanaryModelConfig, OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig, String str, int i, boolean z, String str2, String str3, String str4, String str5, String str6, int i2, Object obj) {
        String str7;
        String str8;
        OfflineTransducerModelConfig offlineTransducerModelConfig2 = (i2 & 1) != 0 ? offlineModelConfig.transducer : offlineTransducerModelConfig;
        OfflineParaformerModelConfig offlineParaformerModelConfig2 = (i2 & 2) != 0 ? offlineModelConfig.paraformer : offlineParaformerModelConfig;
        OfflineWhisperModelConfig offlineWhisperModelConfig2 = (i2 & 4) != 0 ? offlineModelConfig.whisper : offlineWhisperModelConfig;
        OfflineFireRedAsrModelConfig offlineFireRedAsrModelConfig2 = (i2 & 8) != 0 ? offlineModelConfig.fireRedAsr : offlineFireRedAsrModelConfig;
        OfflineMoonshineModelConfig offlineMoonshineModelConfig2 = (i2 & 16) != 0 ? offlineModelConfig.moonshine : offlineMoonshineModelConfig;
        OfflineNemoEncDecCtcModelConfig offlineNemoEncDecCtcModelConfig2 = (i2 & 32) != 0 ? offlineModelConfig.nemo : offlineNemoEncDecCtcModelConfig;
        OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig2 = (i2 & 64) != 0 ? offlineModelConfig.senseVoice : offlineSenseVoiceModelConfig;
        OfflineDolphinModelConfig offlineDolphinModelConfig2 = (i2 & 128) != 0 ? offlineModelConfig.dolphin : offlineDolphinModelConfig;
        OfflineZipformerCtcModelConfig offlineZipformerCtcModelConfig2 = (i2 & 256) != 0 ? offlineModelConfig.zipformerCtc : offlineZipformerCtcModelConfig;
        OfflineWenetCtcModelConfig offlineWenetCtcModelConfig2 = (i2 & 512) != 0 ? offlineModelConfig.wenetCtc : offlineWenetCtcModelConfig;
        OfflineOmnilingualAsrCtcModelConfig offlineOmnilingualAsrCtcModelConfig2 = (i2 & 1024) != 0 ? offlineModelConfig.omnilingual : offlineOmnilingualAsrCtcModelConfig;
        OfflineMedAsrCtcModelConfig offlineMedAsrCtcModelConfig2 = (i2 & 2048) != 0 ? offlineModelConfig.medasr : offlineMedAsrCtcModelConfig;
        OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig2 = (i2 & 4096) != 0 ? offlineModelConfig.funasrNano : offlineFunAsrNanoModelConfig;
        OfflineQwen3AsrModelConfig offlineQwen3AsrModelConfig2 = (i2 & 8192) != 0 ? offlineModelConfig.qwen3Asr : offlineQwen3AsrModelConfig;
        OfflineTransducerModelConfig offlineTransducerModelConfig3 = offlineTransducerModelConfig2;
        OfflineFireRedAsrCtcModelConfig offlineFireRedAsrCtcModelConfig2 = (i2 & 16384) != 0 ? offlineModelConfig.fireRedAsrCtc : offlineFireRedAsrCtcModelConfig;
        OfflineCanaryModelConfig offlineCanaryModelConfig2 = (i2 & 32768) != 0 ? offlineModelConfig.canary : offlineCanaryModelConfig;
        OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig2 = (i2 & 65536) != 0 ? offlineModelConfig.cohereTranscribe : offlineCohereTranscribeModelConfig;
        String str9 = (i2 & 131072) != 0 ? offlineModelConfig.teleSpeech : str;
        int i3 = (i2 & 262144) != 0 ? offlineModelConfig.numThreads : i;
        boolean z2 = (i2 & 524288) != 0 ? offlineModelConfig.debug : z;
        String str10 = (i2 & 1048576) != 0 ? offlineModelConfig.provider : str2;
        String str11 = (i2 & 2097152) != 0 ? offlineModelConfig.modelType : str3;
        String str12 = (i2 & 4194304) != 0 ? offlineModelConfig.tokens : str4;
        String str13 = (i2 & 8388608) != 0 ? offlineModelConfig.modelingUnit : str5;
        if ((i2 & 16777216) != 0) {
            str8 = str13;
            str7 = offlineModelConfig.bpeVocab;
        } else {
            str7 = str6;
            str8 = str13;
        }
        return offlineModelConfig.copy(offlineTransducerModelConfig3, offlineParaformerModelConfig2, offlineWhisperModelConfig2, offlineFireRedAsrModelConfig2, offlineMoonshineModelConfig2, offlineNemoEncDecCtcModelConfig2, offlineSenseVoiceModelConfig2, offlineDolphinModelConfig2, offlineZipformerCtcModelConfig2, offlineWenetCtcModelConfig2, offlineOmnilingualAsrCtcModelConfig2, offlineMedAsrCtcModelConfig2, offlineFunAsrNanoModelConfig2, offlineQwen3AsrModelConfig2, offlineFireRedAsrCtcModelConfig2, offlineCanaryModelConfig2, offlineCohereTranscribeModelConfig2, str9, i3, z2, str10, str11, str12, str8, str7);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineTransducerModelConfig component1() {
        return this.transducer;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final OfflineWenetCtcModelConfig component10() {
        return this.wenetCtc;
    }

    /* JADX INFO: renamed from: component11, reason: from getter */
    public final OfflineOmnilingualAsrCtcModelConfig component11() {
        return this.omnilingual;
    }

    /* JADX INFO: renamed from: component12, reason: from getter */
    public final OfflineMedAsrCtcModelConfig component12() {
        return this.medasr;
    }

    /* JADX INFO: renamed from: component13, reason: from getter */
    public final OfflineFunAsrNanoModelConfig component13() {
        return this.funasrNano;
    }

    /* JADX INFO: renamed from: component14, reason: from getter */
    public final OfflineQwen3AsrModelConfig component14() {
        return this.qwen3Asr;
    }

    /* JADX INFO: renamed from: component15, reason: from getter */
    public final OfflineFireRedAsrCtcModelConfig component15() {
        return this.fireRedAsrCtc;
    }

    /* JADX INFO: renamed from: component16, reason: from getter */
    public final OfflineCanaryModelConfig component16() {
        return this.canary;
    }

    /* JADX INFO: renamed from: component17, reason: from getter */
    public final OfflineCohereTranscribeModelConfig component17() {
        return this.cohereTranscribe;
    }

    /* JADX INFO: renamed from: component18, reason: from getter */
    public final String component18() {
        return this.teleSpeech;
    }

    /* JADX INFO: renamed from: component19, reason: from getter */
    public final int component19() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OfflineParaformerModelConfig component2() {
        return this.paraformer;
    }

    /* JADX INFO: renamed from: component20, reason: from getter */
    public final boolean component20() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component21, reason: from getter */
    public final String component21() {
        return this.provider;
    }

    /* JADX INFO: renamed from: component22, reason: from getter */
    public final String component22() {
        return this.modelType;
    }

    /* JADX INFO: renamed from: component23, reason: from getter */
    public final String component23() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component24, reason: from getter */
    public final String component24() {
        return this.modelingUnit;
    }

    /* JADX INFO: renamed from: component25, reason: from getter */
    public final String component25() {
        return this.bpeVocab;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final OfflineWhisperModelConfig component3() {
        return this.whisper;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final OfflineFireRedAsrModelConfig component4() {
        return this.fireRedAsr;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final OfflineMoonshineModelConfig component5() {
        return this.moonshine;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final OfflineNemoEncDecCtcModelConfig component6() {
        return this.nemo;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final OfflineSenseVoiceModelConfig component7() {
        return this.senseVoice;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final OfflineDolphinModelConfig component8() {
        return this.dolphin;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final OfflineZipformerCtcModelConfig component9() {
        return this.zipformerCtc;
    }

    public final OfflineModelConfig copy(OfflineTransducerModelConfig transducer, OfflineParaformerModelConfig paraformer, OfflineWhisperModelConfig whisper, OfflineFireRedAsrModelConfig fireRedAsr, OfflineMoonshineModelConfig moonshine, OfflineNemoEncDecCtcModelConfig nemo, OfflineSenseVoiceModelConfig senseVoice, OfflineDolphinModelConfig dolphin, OfflineZipformerCtcModelConfig zipformerCtc, OfflineWenetCtcModelConfig wenetCtc, OfflineOmnilingualAsrCtcModelConfig omnilingual, OfflineMedAsrCtcModelConfig medasr, OfflineFunAsrNanoModelConfig funasrNano, OfflineQwen3AsrModelConfig qwen3Asr, OfflineFireRedAsrCtcModelConfig fireRedAsrCtc, OfflineCanaryModelConfig canary, OfflineCohereTranscribeModelConfig cohereTranscribe, String teleSpeech, int numThreads, boolean debug, String provider, String modelType, String tokens, String modelingUnit, String bpeVocab) {
        Intrinsics.checkNotNullParameter(transducer, "transducer");
        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
        Intrinsics.checkNotNullParameter(whisper, "whisper");
        Intrinsics.checkNotNullParameter(fireRedAsr, "fireRedAsr");
        Intrinsics.checkNotNullParameter(moonshine, "moonshine");
        Intrinsics.checkNotNullParameter(nemo, "nemo");
        Intrinsics.checkNotNullParameter(senseVoice, "senseVoice");
        Intrinsics.checkNotNullParameter(dolphin, "dolphin");
        Intrinsics.checkNotNullParameter(zipformerCtc, "zipformerCtc");
        Intrinsics.checkNotNullParameter(wenetCtc, "wenetCtc");
        Intrinsics.checkNotNullParameter(omnilingual, "omnilingual");
        Intrinsics.checkNotNullParameter(medasr, "medasr");
        Intrinsics.checkNotNullParameter(funasrNano, "funasrNano");
        Intrinsics.checkNotNullParameter(qwen3Asr, "qwen3Asr");
        Intrinsics.checkNotNullParameter(fireRedAsrCtc, "fireRedAsrCtc");
        Intrinsics.checkNotNullParameter(canary, "canary");
        Intrinsics.checkNotNullParameter(cohereTranscribe, "cohereTranscribe");
        Intrinsics.checkNotNullParameter(teleSpeech, "teleSpeech");
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(modelType, "modelType");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        return new OfflineModelConfig(transducer, paraformer, whisper, fireRedAsr, moonshine, nemo, senseVoice, dolphin, zipformerCtc, wenetCtc, omnilingual, medasr, funasrNano, qwen3Asr, fireRedAsrCtc, canary, cohereTranscribe, teleSpeech, numThreads, debug, provider, modelType, tokens, modelingUnit, bpeVocab);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineModelConfig)) {
            return false;
        }
        OfflineModelConfig offlineModelConfig = (OfflineModelConfig) other;
        return Intrinsics.areEqual(this.transducer, offlineModelConfig.transducer) && Intrinsics.areEqual(this.paraformer, offlineModelConfig.paraformer) && Intrinsics.areEqual(this.whisper, offlineModelConfig.whisper) && Intrinsics.areEqual(this.fireRedAsr, offlineModelConfig.fireRedAsr) && Intrinsics.areEqual(this.moonshine, offlineModelConfig.moonshine) && Intrinsics.areEqual(this.nemo, offlineModelConfig.nemo) && Intrinsics.areEqual(this.senseVoice, offlineModelConfig.senseVoice) && Intrinsics.areEqual(this.dolphin, offlineModelConfig.dolphin) && Intrinsics.areEqual(this.zipformerCtc, offlineModelConfig.zipformerCtc) && Intrinsics.areEqual(this.wenetCtc, offlineModelConfig.wenetCtc) && Intrinsics.areEqual(this.omnilingual, offlineModelConfig.omnilingual) && Intrinsics.areEqual(this.medasr, offlineModelConfig.medasr) && Intrinsics.areEqual(this.funasrNano, offlineModelConfig.funasrNano) && Intrinsics.areEqual(this.qwen3Asr, offlineModelConfig.qwen3Asr) && Intrinsics.areEqual(this.fireRedAsrCtc, offlineModelConfig.fireRedAsrCtc) && Intrinsics.areEqual(this.canary, offlineModelConfig.canary) && Intrinsics.areEqual(this.cohereTranscribe, offlineModelConfig.cohereTranscribe) && Intrinsics.areEqual(this.teleSpeech, offlineModelConfig.teleSpeech) && this.numThreads == offlineModelConfig.numThreads && this.debug == offlineModelConfig.debug && Intrinsics.areEqual(this.provider, offlineModelConfig.provider) && Intrinsics.areEqual(this.modelType, offlineModelConfig.modelType) && Intrinsics.areEqual(this.tokens, offlineModelConfig.tokens) && Intrinsics.areEqual(this.modelingUnit, offlineModelConfig.modelingUnit) && Intrinsics.areEqual(this.bpeVocab, offlineModelConfig.bpeVocab);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v39, types: [int] */
    /* JADX WARN: Type inference failed for: r1v37, types: [int] */
    /* JADX WARN: Type inference failed for: r1v46 */
    /* JADX WARN: Type inference failed for: r1v47 */
    public int hashCode() {
        int iHashCode = ((((((((((((((((((((((((((((((((((((this.transducer.hashCode() * 31) + this.paraformer.hashCode()) * 31) + this.whisper.hashCode()) * 31) + this.fireRedAsr.hashCode()) * 31) + this.moonshine.hashCode()) * 31) + this.nemo.hashCode()) * 31) + this.senseVoice.hashCode()) * 31) + this.dolphin.hashCode()) * 31) + this.zipformerCtc.hashCode()) * 31) + this.wenetCtc.hashCode()) * 31) + this.omnilingual.hashCode()) * 31) + this.medasr.hashCode()) * 31) + this.funasrNano.hashCode()) * 31) + this.qwen3Asr.hashCode()) * 31) + this.fireRedAsrCtc.hashCode()) * 31) + this.canary.hashCode()) * 31) + this.cohereTranscribe.hashCode()) * 31) + this.teleSpeech.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((((((((((iHashCode + r1) * 31) + this.provider.hashCode()) * 31) + this.modelType.hashCode()) * 31) + this.tokens.hashCode()) * 31) + this.modelingUnit.hashCode()) * 31) + this.bpeVocab.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OfflineModelConfig(transducer=");
        sb.append(this.transducer).append(", paraformer=").append(this.paraformer).append(", whisper=").append(this.whisper).append(", fireRedAsr=").append(this.fireRedAsr).append(", moonshine=").append(this.moonshine).append(", nemo=").append(this.nemo).append(", senseVoice=").append(this.senseVoice).append(", dolphin=").append(this.dolphin).append(", zipformerCtc=").append(this.zipformerCtc).append(", wenetCtc=").append(this.wenetCtc).append(", omnilingual=").append(this.omnilingual).append(", medasr=");
        sb.append(this.medasr).append(", funasrNano=").append(this.funasrNano).append(", qwen3Asr=").append(this.qwen3Asr).append(", fireRedAsrCtc=").append(this.fireRedAsrCtc).append(", canary=").append(this.canary).append(", cohereTranscribe=").append(this.cohereTranscribe).append(", teleSpeech=").append(this.teleSpeech).append(", numThreads=").append(this.numThreads).append(", debug=").append(this.debug).append(", provider=").append(this.provider).append(", modelType=").append(this.modelType).append(", tokens=").append(this.tokens);
        sb.append(", modelingUnit=").append(this.modelingUnit).append(", bpeVocab=").append(this.bpeVocab).append(')');
        return sb.toString();
    }

    public OfflineModelConfig(OfflineTransducerModelConfig transducer, OfflineParaformerModelConfig paraformer, OfflineWhisperModelConfig whisper, OfflineFireRedAsrModelConfig fireRedAsr, OfflineMoonshineModelConfig moonshine, OfflineNemoEncDecCtcModelConfig nemo, OfflineSenseVoiceModelConfig senseVoice, OfflineDolphinModelConfig dolphin, OfflineZipformerCtcModelConfig zipformerCtc, OfflineWenetCtcModelConfig wenetCtc, OfflineOmnilingualAsrCtcModelConfig omnilingual, OfflineMedAsrCtcModelConfig medasr, OfflineFunAsrNanoModelConfig funasrNano, OfflineQwen3AsrModelConfig qwen3Asr, OfflineFireRedAsrCtcModelConfig fireRedAsrCtc, OfflineCanaryModelConfig canary, OfflineCohereTranscribeModelConfig cohereTranscribe, String teleSpeech, int i, boolean z, String provider, String modelType, String tokens, String modelingUnit, String bpeVocab) {
        Intrinsics.checkNotNullParameter(transducer, "transducer");
        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
        Intrinsics.checkNotNullParameter(whisper, "whisper");
        Intrinsics.checkNotNullParameter(fireRedAsr, "fireRedAsr");
        Intrinsics.checkNotNullParameter(moonshine, "moonshine");
        Intrinsics.checkNotNullParameter(nemo, "nemo");
        Intrinsics.checkNotNullParameter(senseVoice, "senseVoice");
        Intrinsics.checkNotNullParameter(dolphin, "dolphin");
        Intrinsics.checkNotNullParameter(zipformerCtc, "zipformerCtc");
        Intrinsics.checkNotNullParameter(wenetCtc, "wenetCtc");
        Intrinsics.checkNotNullParameter(omnilingual, "omnilingual");
        Intrinsics.checkNotNullParameter(medasr, "medasr");
        Intrinsics.checkNotNullParameter(funasrNano, "funasrNano");
        Intrinsics.checkNotNullParameter(qwen3Asr, "qwen3Asr");
        Intrinsics.checkNotNullParameter(fireRedAsrCtc, "fireRedAsrCtc");
        Intrinsics.checkNotNullParameter(canary, "canary");
        Intrinsics.checkNotNullParameter(cohereTranscribe, "cohereTranscribe");
        Intrinsics.checkNotNullParameter(teleSpeech, "teleSpeech");
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(modelType, "modelType");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        this.transducer = transducer;
        this.paraformer = paraformer;
        this.whisper = whisper;
        this.fireRedAsr = fireRedAsr;
        this.moonshine = moonshine;
        this.nemo = nemo;
        this.senseVoice = senseVoice;
        this.dolphin = dolphin;
        this.zipformerCtc = zipformerCtc;
        this.wenetCtc = wenetCtc;
        this.omnilingual = omnilingual;
        this.medasr = medasr;
        this.funasrNano = funasrNano;
        this.qwen3Asr = qwen3Asr;
        this.fireRedAsrCtc = fireRedAsrCtc;
        this.canary = canary;
        this.cohereTranscribe = cohereTranscribe;
        this.teleSpeech = teleSpeech;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
        this.modelType = modelType;
        this.tokens = tokens;
        this.modelingUnit = modelingUnit;
        this.bpeVocab = bpeVocab;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ OfflineModelConfig(OfflineTransducerModelConfig offlineTransducerModelConfig, OfflineParaformerModelConfig offlineParaformerModelConfig, OfflineWhisperModelConfig offlineWhisperModelConfig, OfflineFireRedAsrModelConfig offlineFireRedAsrModelConfig, OfflineMoonshineModelConfig offlineMoonshineModelConfig, OfflineNemoEncDecCtcModelConfig offlineNemoEncDecCtcModelConfig, OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig, OfflineDolphinModelConfig offlineDolphinModelConfig, OfflineZipformerCtcModelConfig offlineZipformerCtcModelConfig, OfflineWenetCtcModelConfig offlineWenetCtcModelConfig, OfflineOmnilingualAsrCtcModelConfig offlineOmnilingualAsrCtcModelConfig, OfflineMedAsrCtcModelConfig offlineMedAsrCtcModelConfig, OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig, OfflineQwen3AsrModelConfig offlineQwen3AsrModelConfig, OfflineFireRedAsrCtcModelConfig offlineFireRedAsrCtcModelConfig, OfflineCanaryModelConfig offlineCanaryModelConfig, OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig, String str, int i, boolean z, String str2, String str3, String str4, String str5, String str6, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(((i2 & 1) != 0 ? new OfflineTransducerModelConfig(null, null, null, null, 15, null) : offlineTransducerModelConfig), ((i2 & 2) != 0 ? new OfflineParaformerModelConfig(null, null, 3, null) : offlineParaformerModelConfig), ((i2 & 4) != 0 ? new OfflineWhisperModelConfig(null, null, null, null, 0, false, false, WorkQueueKt.MASK, null) : offlineWhisperModelConfig), ((i2 & 8) != 0 ? new OfflineFireRedAsrModelConfig(null, null, 3, null) : offlineFireRedAsrModelConfig), ((i2 & 16) != 0 ? new OfflineMoonshineModelConfig(null, null, null, null, null, 31, null) : offlineMoonshineModelConfig), ((i2 & 32) != 0 ? new OfflineNemoEncDecCtcModelConfig(null, 1, null) : offlineNemoEncDecCtcModelConfig), ((i2 & 64) != 0 ? new OfflineSenseVoiceModelConfig(null, null, false, null, 15, null) : offlineSenseVoiceModelConfig), ((i2 & 128) != 0 ? new OfflineDolphinModelConfig(null, 1, null) : offlineDolphinModelConfig), ((i2 & 256) != 0 ? new OfflineZipformerCtcModelConfig(null, null, 3, null) : offlineZipformerCtcModelConfig), ((i2 & 512) != 0 ? new OfflineWenetCtcModelConfig(null, 1, null) : offlineWenetCtcModelConfig), ((i2 & 1024) != 0 ? new OfflineOmnilingualAsrCtcModelConfig(null, 1, null) : offlineOmnilingualAsrCtcModelConfig), ((i2 & 2048) != 0 ? new OfflineMedAsrCtcModelConfig(null, 1, null) : offlineMedAsrCtcModelConfig), ((i2 & 4096) != 0 ? new OfflineFunAsrNanoModelConfig(null, null, null, null, null, null, 0, 0.0f, 0.0f, 0, null, false, null, 8191, null) : offlineFunAsrNanoModelConfig), ((i2 & 8192) != 0 ? new OfflineQwen3AsrModelConfig(null, null, null, null, 0, 0, 0.0f, 0.0f, 0, null, 1023, null) : offlineQwen3AsrModelConfig), (i2 & 16384) != 0 ? new OfflineFireRedAsrCtcModelConfig(null, 1, null) : offlineFireRedAsrCtcModelConfig, (i2 & 32768) != 0 ? new OfflineCanaryModelConfig(null, null, null, null, false, 31, null) : offlineCanaryModelConfig, (i2 & 65536) != 0 ? new OfflineCohereTranscribeModelConfig(null, null, null, false, false, 31, null) : offlineCohereTranscribeModelConfig, (i2 & 131072) != 0 ? "" : str, (i2 & 262144) == 0 ? i : 1, (i2 & 524288) != 0 ? false : z, (i2 & 1048576) != 0 ? "cpu" : str2, (i2 & 2097152) != 0 ? "" : str3, (i2 & 4194304) != 0 ? "" : str4, (i2 & 8388608) != 0 ? "" : str5, (i2 & 16777216) != 0 ? "" : str6);
    }

    public final OfflineTransducerModelConfig getTransducer() {
        return this.transducer;
    }

    public final void setTransducer(OfflineTransducerModelConfig offlineTransducerModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTransducerModelConfig, "<set-?>");
        this.transducer = offlineTransducerModelConfig;
    }

    public final OfflineParaformerModelConfig getParaformer() {
        return this.paraformer;
    }

    public final void setParaformer(OfflineParaformerModelConfig offlineParaformerModelConfig) {
        Intrinsics.checkNotNullParameter(offlineParaformerModelConfig, "<set-?>");
        this.paraformer = offlineParaformerModelConfig;
    }

    public final OfflineWhisperModelConfig getWhisper() {
        return this.whisper;
    }

    public final void setWhisper(OfflineWhisperModelConfig offlineWhisperModelConfig) {
        Intrinsics.checkNotNullParameter(offlineWhisperModelConfig, "<set-?>");
        this.whisper = offlineWhisperModelConfig;
    }

    public final OfflineFireRedAsrModelConfig getFireRedAsr() {
        return this.fireRedAsr;
    }

    public final void setFireRedAsr(OfflineFireRedAsrModelConfig offlineFireRedAsrModelConfig) {
        Intrinsics.checkNotNullParameter(offlineFireRedAsrModelConfig, "<set-?>");
        this.fireRedAsr = offlineFireRedAsrModelConfig;
    }

    public final OfflineMoonshineModelConfig getMoonshine() {
        return this.moonshine;
    }

    public final void setMoonshine(OfflineMoonshineModelConfig offlineMoonshineModelConfig) {
        Intrinsics.checkNotNullParameter(offlineMoonshineModelConfig, "<set-?>");
        this.moonshine = offlineMoonshineModelConfig;
    }

    public final OfflineNemoEncDecCtcModelConfig getNemo() {
        return this.nemo;
    }

    public final void setNemo(OfflineNemoEncDecCtcModelConfig offlineNemoEncDecCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineNemoEncDecCtcModelConfig, "<set-?>");
        this.nemo = offlineNemoEncDecCtcModelConfig;
    }

    public final OfflineSenseVoiceModelConfig getSenseVoice() {
        return this.senseVoice;
    }

    public final void setSenseVoice(OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig) {
        Intrinsics.checkNotNullParameter(offlineSenseVoiceModelConfig, "<set-?>");
        this.senseVoice = offlineSenseVoiceModelConfig;
    }

    public final OfflineDolphinModelConfig getDolphin() {
        return this.dolphin;
    }

    public final void setDolphin(OfflineDolphinModelConfig offlineDolphinModelConfig) {
        Intrinsics.checkNotNullParameter(offlineDolphinModelConfig, "<set-?>");
        this.dolphin = offlineDolphinModelConfig;
    }

    public final OfflineZipformerCtcModelConfig getZipformerCtc() {
        return this.zipformerCtc;
    }

    public final void setZipformerCtc(OfflineZipformerCtcModelConfig offlineZipformerCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineZipformerCtcModelConfig, "<set-?>");
        this.zipformerCtc = offlineZipformerCtcModelConfig;
    }

    public final OfflineWenetCtcModelConfig getWenetCtc() {
        return this.wenetCtc;
    }

    public final void setWenetCtc(OfflineWenetCtcModelConfig offlineWenetCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineWenetCtcModelConfig, "<set-?>");
        this.wenetCtc = offlineWenetCtcModelConfig;
    }

    public final OfflineOmnilingualAsrCtcModelConfig getOmnilingual() {
        return this.omnilingual;
    }

    public final void setOmnilingual(OfflineOmnilingualAsrCtcModelConfig offlineOmnilingualAsrCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineOmnilingualAsrCtcModelConfig, "<set-?>");
        this.omnilingual = offlineOmnilingualAsrCtcModelConfig;
    }

    public final OfflineMedAsrCtcModelConfig getMedasr() {
        return this.medasr;
    }

    public final void setMedasr(OfflineMedAsrCtcModelConfig offlineMedAsrCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineMedAsrCtcModelConfig, "<set-?>");
        this.medasr = offlineMedAsrCtcModelConfig;
    }

    public final OfflineFunAsrNanoModelConfig getFunasrNano() {
        return this.funasrNano;
    }

    public final void setFunasrNano(OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig) {
        Intrinsics.checkNotNullParameter(offlineFunAsrNanoModelConfig, "<set-?>");
        this.funasrNano = offlineFunAsrNanoModelConfig;
    }

    public final OfflineQwen3AsrModelConfig getQwen3Asr() {
        return this.qwen3Asr;
    }

    public final void setQwen3Asr(OfflineQwen3AsrModelConfig offlineQwen3AsrModelConfig) {
        Intrinsics.checkNotNullParameter(offlineQwen3AsrModelConfig, "<set-?>");
        this.qwen3Asr = offlineQwen3AsrModelConfig;
    }

    public final OfflineFireRedAsrCtcModelConfig getFireRedAsrCtc() {
        return this.fireRedAsrCtc;
    }

    public final void setFireRedAsrCtc(OfflineFireRedAsrCtcModelConfig offlineFireRedAsrCtcModelConfig) {
        Intrinsics.checkNotNullParameter(offlineFireRedAsrCtcModelConfig, "<set-?>");
        this.fireRedAsrCtc = offlineFireRedAsrCtcModelConfig;
    }

    public final OfflineCanaryModelConfig getCanary() {
        return this.canary;
    }

    public final void setCanary(OfflineCanaryModelConfig offlineCanaryModelConfig) {
        Intrinsics.checkNotNullParameter(offlineCanaryModelConfig, "<set-?>");
        this.canary = offlineCanaryModelConfig;
    }

    public final OfflineCohereTranscribeModelConfig getCohereTranscribe() {
        return this.cohereTranscribe;
    }

    public final void setCohereTranscribe(OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig) {
        Intrinsics.checkNotNullParameter(offlineCohereTranscribeModelConfig, "<set-?>");
        this.cohereTranscribe = offlineCohereTranscribeModelConfig;
    }

    public final String getTeleSpeech() {
        return this.teleSpeech;
    }

    public final void setTeleSpeech(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.teleSpeech = str;
    }

    public final int getNumThreads() {
        return this.numThreads;
    }

    public final void setNumThreads(int i) {
        this.numThreads = i;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean z) {
        this.debug = z;
    }

    public final String getProvider() {
        return this.provider;
    }

    public final void setProvider(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.provider = str;
    }

    public final String getModelType() {
        return this.modelType;
    }

    public final void setModelType(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.modelType = str;
    }

    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokens = str;
    }

    public final String getModelingUnit() {
        return this.modelingUnit;
    }

    public final void setModelingUnit(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.modelingUnit = str;
    }

    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    public final void setBpeVocab(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.bpeVocab = str;
    }
}
