package android.car.vms;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseBooleanArray;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public final class VmsSubscriptionHelper {
    private boolean mPendingUpdate;
    private final Consumer<Set<VmsAssociatedLayer>> mUpdateHandler;
    private final Object mLock = new Object();
    private final Set<VmsLayer> mLayerSubscriptions = new ArraySet();
    private final Map<VmsLayer, SparseBooleanArray> mPublisherSubscriptions = new ArrayMap();

    public VmsSubscriptionHelper(Consumer<Set<VmsAssociatedLayer>> consumer) {
        Objects.requireNonNull(consumer, "updateHandler cannot be null");
        this.mUpdateHandler = consumer;
    }

    static /* synthetic */ VmsAssociatedLayer lambda$getSubscriptions$1(VmsLayer vmsLayer) {
        return new VmsAssociatedLayer(vmsLayer, Collections.emptySet());
    }

    static /* synthetic */ SparseBooleanArray lambda$subscribe$0(VmsLayer vmsLayer) {
        return new SparseBooleanArray();
    }

    private void publishSubscriptionUpdate() {
        synchronized (this.mLock) {
            if (this.mPendingUpdate) {
                this.mUpdateHandler.accept(getSubscriptions());
            }
            this.mPendingUpdate = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static VmsAssociatedLayer toAssociatedLayer(Map.Entry<VmsLayer, SparseBooleanArray> entry) {
        SparseBooleanArray value = entry.getValue();
        ArraySet arraySet = new ArraySet(value.size());
        for (int i = 0; i < value.size(); i++) {
            arraySet.add(Integer.valueOf(value.keyAt(i)));
        }
        return new VmsAssociatedLayer(entry.getKey(), arraySet);
    }

    public Set<VmsAssociatedLayer> getSubscriptions() {
        return (Set) Stream.concat(this.mLayerSubscriptions.stream().map(_$$Lambda$VmsSubscriptionHelper$4KOBp00snLAYhcGqXbddi7ERjOU.INSTANCE), this.mPublisherSubscriptions.entrySet().stream().filter(new Predicate(this) { // from class: android.car.vms._$$Lambda$VmsSubscriptionHelper$YCza2o8BZ9cUHZMh0u_iLg_u6Pc
            public final VmsSubscriptionHelper f$0;

            {
                this.f$0 = this;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$getSubscriptions$2$VmsSubscriptionHelper((Map.Entry) obj);
            }
        }).map(_$$Lambda$VmsSubscriptionHelper$1bSkcP5XRksL1jsdWdzfHaDGHyU.INSTANCE)).collect(Collectors.toSet());
    }

    public /* synthetic */ boolean lambda$getSubscriptions$2$VmsSubscriptionHelper(Map.Entry entry) {
        return !this.mLayerSubscriptions.contains(entry.getKey());
    }

    public void subscribe(VmsLayer vmsLayer) {
        Objects.requireNonNull(vmsLayer, "layer cannot be null");
        synchronized (this.mLock) {
            if (this.mLayerSubscriptions.add(vmsLayer)) {
                this.mPendingUpdate = true;
            }
            publishSubscriptionUpdate();
        }
    }

    public void subscribe(VmsLayer vmsLayer, int i) {
        Objects.requireNonNull(vmsLayer, "layer cannot be null");
        synchronized (this.mLock) {
            SparseBooleanArray sparseBooleanArrayComputeIfAbsent = this.mPublisherSubscriptions.computeIfAbsent(vmsLayer, _$$Lambda$VmsSubscriptionHelper$DEoibnAzFzopdSfPOCVQwF3NqGg.INSTANCE);
            if (!sparseBooleanArrayComputeIfAbsent.get(i)) {
                sparseBooleanArrayComputeIfAbsent.put(i, true);
                this.mPendingUpdate = true;
            }
            publishSubscriptionUpdate();
        }
    }

    public void unsubscribe(VmsLayer vmsLayer) {
        Objects.requireNonNull(vmsLayer, "layer cannot be null");
        synchronized (this.mLock) {
            if (this.mLayerSubscriptions.remove(vmsLayer)) {
                this.mPendingUpdate = true;
            }
            publishSubscriptionUpdate();
        }
    }

    public void unsubscribe(VmsLayer vmsLayer, int i) {
        Objects.requireNonNull(vmsLayer, "layer cannot be null");
        synchronized (this.mLock) {
            SparseBooleanArray sparseBooleanArray = this.mPublisherSubscriptions.get(vmsLayer);
            if (sparseBooleanArray != null && sparseBooleanArray.get(i)) {
                sparseBooleanArray.delete(i);
                if (sparseBooleanArray.size() == 0) {
                    this.mPublisherSubscriptions.remove(vmsLayer);
                }
                this.mPendingUpdate = true;
            }
            publishSubscriptionUpdate();
        }
    }
}
