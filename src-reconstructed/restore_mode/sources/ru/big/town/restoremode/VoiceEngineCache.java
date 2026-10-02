package ru.big.town.restoremode;

import java.lang.AutoCloseable;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceEngineCache<T extends AutoCloseable> {
    private T engine;
    private volatile boolean retained;

    interface Factory<T> {
        T create() throws Exception;
    }

    VoiceEngineCache() {
    }

    void retain(boolean z) {
        this.retained = z;
    }

    boolean retained() {
        return this.retained;
    }

    T acquire(Factory<T> factory) throws Exception {
        if (this.engine == null) {
            this.engine = factory.create();
        }
        return this.engine;
    }

    void releaseIfUnretained() {
        if (this.retained) {
            return;
        }
        release();
    }

    void release() {
        T t = this.engine;
        this.engine = null;
        if (t != null) {
            try {
                VoiceEngineCache$$ExternalSyntheticThrowIAE2.m(t);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot release voice engine", e);
            }
        }
    }
}
