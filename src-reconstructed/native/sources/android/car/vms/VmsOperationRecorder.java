package android.car.vms;

import android.annotation.SystemApi;
import android.util.Log;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public final class VmsOperationRecorder {
    private static final VmsOperationRecorder INSTANCE = new VmsOperationRecorder(new Writer());
    private static final String TAG = "VmsOperationRecorder";
    private final Writer mWriter;

    public static class Writer {
        private static final int LEVEL = 3;
        private static final String TAG = "VMS.RECORD.EVENT";

        public boolean isEnabled() {
            return Log.isLoggable(TAG, 3);
        }

        public void write(String str) {
            Log.println(3, TAG, str);
        }
    }

    public VmsOperationRecorder(Writer writer) {
        this.mWriter = writer;
    }

    public static VmsOperationRecorder get() {
        return INSTANCE;
    }

    private boolean isEnabled() {
        return this.mWriter.isEnabled();
    }

    private void recordOp(String str) {
        if (isEnabled()) {
            try {
                write(new JSONObject().put(str, new JSONObject()));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void recordOp(String str, VmsLayer vmsLayer) {
        if (isEnabled()) {
            try {
                recordOp(str, new JSONObject().put("layer", toJson(vmsLayer)));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void recordOp(String str, VmsLayersOffering vmsLayersOffering) {
        if (isEnabled()) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("publisherId", vmsLayersOffering.getPublisherId());
                JSONArray json = toJson(vmsLayersOffering);
                if (json.length() > 0) {
                    jSONObject.put("layerDependency", json);
                }
                recordOp(str, jSONObject);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void recordOp(String str, String str2, int i) {
        if (isEnabled()) {
            try {
                recordOp(str, new JSONObject().put(str2, i));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void recordOp(String str, String str2, int i, VmsLayer vmsLayer) {
        if (isEnabled()) {
            try {
                recordOp(str, new JSONObject().put(str2, i).put("layer", toJson(vmsLayer)));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void recordOp(String str, JSONObject jSONObject) {
        if (isEnabled()) {
            try {
                write(new JSONObject().put(str, jSONObject));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private static JSONArray toJson(VmsLayersOffering vmsLayersOffering) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        Iterator<VmsLayerDependency> it = vmsLayersOffering.getDependencies().iterator();
        while (it.hasNext()) {
            jSONArray.put(toJson(it.next()));
        }
        return jSONArray;
    }

    private static JSONObject toJson(VmsLayer vmsLayer) throws JSONException {
        return new JSONObject().put("type", vmsLayer.getType()).put("subtype", vmsLayer.getSubtype()).put("version", vmsLayer.getVersion());
    }

    private static JSONObject toJson(VmsLayerDependency vmsLayerDependency) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("layer", toJson(vmsLayerDependency.getLayer()));
        if (!vmsLayerDependency.getDependencies().isEmpty()) {
            JSONArray jSONArray = new JSONArray();
            Iterator<VmsLayer> it = vmsLayerDependency.getDependencies().iterator();
            while (it.hasNext()) {
                jSONArray.put(toJson(it.next()));
            }
            jSONObject.put("dependency", jSONArray);
        }
        return jSONObject;
    }

    private void write(JSONObject jSONObject) {
        this.mWriter.write(jSONObject.toString());
    }

    public void addHalSubscription(int i, VmsLayer vmsLayer) {
        recordOp("addHalSubscription", "sequenceNumber", i, vmsLayer);
    }

    public void addPromiscuousSubscription(int i) {
        recordOp("addPromiscuousSubscription", "sequenceNumber", i);
    }

    public void addSubscription(int i, VmsLayer vmsLayer) {
        recordOp("addSubscription", "sequenceNumber", i, vmsLayer);
    }

    public void getPublisherId(int i) {
        recordOp("getPublisherId", "publisherId", i);
    }

    public void removeHalSubscription(int i, VmsLayer vmsLayer) {
        recordOp("removeHalSubscription", "sequenceNumber", i, vmsLayer);
    }

    public void removePromiscuousSubscription(int i) {
        recordOp("removePromiscuousSubscription", "sequenceNumber", i);
    }

    public void removeSubscription(int i, VmsLayer vmsLayer) {
        recordOp("removeSubscription", "sequenceNumber", i, vmsLayer);
    }

    public void setHalPublisherLayersOffering(VmsLayersOffering vmsLayersOffering) {
        recordOp("setHalPublisherLayersOffering", vmsLayersOffering);
    }

    public void setLayersOffering(VmsLayersOffering vmsLayersOffering) {
        recordOp("setLayersOffering", vmsLayersOffering);
    }

    public void setPublisherLayersOffering(VmsLayersOffering vmsLayersOffering) {
        recordOp("setPublisherLayersOffering", vmsLayersOffering);
    }

    public void startMonitoring() {
        recordOp("startMonitoring");
    }

    public void stopMonitoring() {
        recordOp("stopMonitoring");
    }

    public void subscribe(VmsLayer vmsLayer) {
        recordOp("subscribe", vmsLayer);
    }

    public void subscribe(VmsLayer vmsLayer, int i) {
        recordOp("subscribe", "publisherId", i, vmsLayer);
    }

    public void unsubscribe(VmsLayer vmsLayer) {
        recordOp("unsubscribe", vmsLayer);
    }

    public void unsubscribe(VmsLayer vmsLayer, int i) {
        recordOp("unsubscribe", "publisherId", i, vmsLayer);
    }
}
