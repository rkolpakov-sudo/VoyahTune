package com.android.car.internal;

import android.util.SparseArray;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class CarRatedFloatListeners<T> {
    private static final float NANOSECOND_PER_SECOND = 1.0E9f;
    private float mUpdateRate;
    private final Map<T, Float> mListenersToRate = new HashMap(4);
    private final Map<T, Long> mListenersUpdateTime = new HashMap(4);
    protected SparseArray<Long> mAreaIdToLastUpdateTime = new SparseArray<>();

    protected CarRatedFloatListeners(float f) {
        this.mUpdateRate = f;
    }

    public boolean addAndUpdateRate(T t, float f) {
        Float fPut = this.mListenersToRate.put(t, Float.valueOf(f));
        this.mListenersUpdateTime.put(t, 0L);
        if (this.mUpdateRate < f) {
            this.mUpdateRate = f;
            return true;
        }
        if (fPut == null || fPut.floatValue() != this.mUpdateRate) {
            return false;
        }
        Float f2 = (Float) Collections.max(this.mListenersToRate.values());
        if (f2.floatValue() == this.mUpdateRate) {
            return false;
        }
        this.mUpdateRate = f2.floatValue();
        return true;
    }

    public boolean contains(T t) {
        return this.mListenersToRate.containsKey(t);
    }

    public Collection<T> getListeners() {
        return this.mListenersToRate.keySet();
    }

    public float getRate() {
        return this.mUpdateRate;
    }

    public boolean isEmpty() {
        return this.mListenersToRate.isEmpty();
    }

    public boolean needUpdateForAreaId(int i, long j) {
        if (j < this.mAreaIdToLastUpdateTime.get(i, 0L).longValue()) {
            return false;
        }
        this.mAreaIdToLastUpdateTime.put(i, Long.valueOf(j));
        return true;
    }

    public boolean needUpdateForSelectedListener(T t, long j) {
        Long l = this.mListenersUpdateTime.get(t);
        Float f = this.mListenersToRate.get(t);
        if (f.floatValue() == 0.0f) {
            return true;
        }
        if (l.longValue() > j) {
            return false;
        }
        this.mListenersUpdateTime.put(t, Long.valueOf(Float.valueOf(NANOSECOND_PER_SECOND / f.floatValue()).longValue() + j));
        return true;
    }

    public boolean remove(T t) {
        this.mListenersToRate.remove(t);
        this.mListenersUpdateTime.remove(t);
        if (this.mListenersToRate.isEmpty()) {
            return false;
        }
        Float f = (Float) Collections.max(this.mListenersToRate.values());
        if (f.floatValue() == this.mUpdateRate) {
            return false;
        }
        this.mUpdateRate = f.floatValue();
        return true;
    }
}
