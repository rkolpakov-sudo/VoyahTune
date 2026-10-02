package ru.big.town.anative;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class ApolloRestorePolicy {
    static final String ACC_FUNC_ENABLE_SA = "ACC_FUNC_ENABLE_SA";
    static final int ACC_FUNC_ENABLE_SA_ID = 1177;
    static final String APA_FUNC_ENABLE_SA = "APA_FUNC_ENABLE_SA";
    static final int APA_FUNC_ENABLE_SA_ID = 1174;
    private static final int DISABLED = 1;
    static final String ELK_FUNC_ENABLE = "ELK_FUNC_ENABLE";
    static final int ELK_FUNC_ENABLE_ID = 1172;
    private static final int ENABLED = 2;
    static final String ESA_FUNC_ENABLE = "ESA_FUNC_ENABLE";
    static final int ESA_FUNC_ENABLE_ID = 1173;
    static final String GLA_LIGHT_CHANGE_SWITCH = "GLA_LIGHT_CHANGE_SWITCH";
    static final int GLA_LIGHT_CHANGE_SWITCH_ID = 1150;
    static final String GLA_SWITCH = "GLA_SWITCH";
    static final int GLA_SWITCH_ID = 1149;
    static final String GLC_FUNC_ENABLE = "GLC_FUNC_ENABLE";
    static final int GLC_FUNC_ENABLE_ID = 1168;
    static final String HANP_FUNC_ENABLE_SA = "HANP_FUNC_ENABLE_SA";
    static final int HANP_FUNC_ENABLE_SA_ID = 1180;
    static final String HAVP_FUNC_ENABLE_SA = "HAVP_FUNC_ENABLE_SA";
    static final int HAVP_FUNC_ENABLE_SA_ID = 1176;
    static final String HPP_FUNC_ENABLE = "HPP_FUNC_ENABLE";
    static final int HPP_FUNC_ENABLE_ID = 1167;
    static final String ICA_FUNC_ENABLE_SA = "ICA_FUNC_ENABLE_SA";
    static final int ICA_FUNC_ENABLE_SA_ID = 1178;
    static final String ISA_FUNC_ENABLE_SA = "ISA_FUNC_ENABLE_SA";
    static final int ISA_FUNC_ENABLE_SA_ID = 1181;
    static final String ISLC_FUNC_ENABLE = "ISLC_FUNC_ENABLE";
    static final int ISLC_FUNC_ENABLE_ID = 1169;
    static final String ISLC_FUNC_ENABLE_SA = "ISLC_FUNC_ENABLE_SA";
    static final int ISLC_FUNC_ENABLE_SA_ID = 1182;
    static final String NOA_FUNC_ENABLE = "NOA_FUNC_ENABLE";
    static final int NOA_FUNC_ENABLE_ID = 1171;
    static final String PLC_FUNC_ENABLE_SA = "PLC_FUNC_ENABLE_SA";
    static final int PLC_FUNC_ENABLE_SA_ID = 1179;
    static final String PLC_SWITCH = "PLC_SWITCH";
    static final int PLC_SWITCH_ID = 1135;
    static final String RPA_FUNC_ENABLE = "RPA_FUNC_ENABLE";
    static final int RPA_FUNC_ENABLE_ID = 1166;
    static final String RPA_FUNC_ENABLE_SA = "RPA_FUNC_ENABLE_SA";
    static final int RPA_FUNC_ENABLE_SA_ID = 1175;
    static final String TLA_FUNC_ENABLE_SA = "TLA_FUNC_ENABLE_SA";
    static final int TLA_FUNC_ENABLE_SA_ID = 1183;
    static final String TLC_FUNC_ENABLE = "TLC_FUNC_ENABLE";
    static final int TLC_FUNC_ENABLE_ID = 1170;
    static final String TSR_SWITCH = "TSR_SWITCH";
    static final int TSR_SWITCH_ID = 277;

    private static int state(boolean z) {
        return z ? 2 : 1;
    }

    private ApolloRestorePolicy() {
    }

    static void appendTo(Map<String, Integer> map, Map<String, Integer> map2, boolean z, boolean z2, boolean z3, boolean z4) {
        if (map == null || map2 == null) {
            throw new IllegalArgumentException("Apollo target maps are null");
        }
        if (z || z2 || z4) {
            putAllEntitlements(map, 2);
        }
        map2.put(PLC_SWITCH, Integer.valueOf(state(z)));
        map2.put(GLA_SWITCH, Integer.valueOf(state(z2)));
        map2.put(GLA_LIGHT_CHANGE_SWITCH, Integer.valueOf(state(z2 && z3)));
        map2.put(TSR_SWITCH, Integer.valueOf(z4 ? 1 : 2));
    }

    private static void putAllEntitlements(Map<String, Integer> map, int i) {
        map.put(RPA_FUNC_ENABLE, Integer.valueOf(i));
        map.put(HPP_FUNC_ENABLE, Integer.valueOf(i));
        map.put(GLC_FUNC_ENABLE, Integer.valueOf(i));
        map.put(ISLC_FUNC_ENABLE, Integer.valueOf(i));
        map.put(TLC_FUNC_ENABLE, Integer.valueOf(i));
        map.put(NOA_FUNC_ENABLE, Integer.valueOf(i));
        map.put(ELK_FUNC_ENABLE, Integer.valueOf(i));
        map.put(ESA_FUNC_ENABLE, Integer.valueOf(i));
        map.put(APA_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(RPA_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(HAVP_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(ACC_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(ICA_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(PLC_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(HANP_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(ISA_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(ISLC_FUNC_ENABLE_SA, Integer.valueOf(i));
        map.put(TLA_FUNC_ENABLE_SA, Integer.valueOf(i));
    }

    static Map<String, Integer> stableIds() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(PLC_SWITCH, Integer.valueOf(PLC_SWITCH_ID));
        linkedHashMap.put(GLA_SWITCH, Integer.valueOf(GLA_SWITCH_ID));
        linkedHashMap.put(GLA_LIGHT_CHANGE_SWITCH, Integer.valueOf(GLA_LIGHT_CHANGE_SWITCH_ID));
        linkedHashMap.put(TSR_SWITCH, Integer.valueOf(TSR_SWITCH_ID));
        linkedHashMap.put(RPA_FUNC_ENABLE, Integer.valueOf(RPA_FUNC_ENABLE_ID));
        linkedHashMap.put(HPP_FUNC_ENABLE, Integer.valueOf(HPP_FUNC_ENABLE_ID));
        linkedHashMap.put(GLC_FUNC_ENABLE, Integer.valueOf(GLC_FUNC_ENABLE_ID));
        linkedHashMap.put(ISLC_FUNC_ENABLE, Integer.valueOf(ISLC_FUNC_ENABLE_ID));
        linkedHashMap.put(TLC_FUNC_ENABLE, Integer.valueOf(TLC_FUNC_ENABLE_ID));
        linkedHashMap.put(NOA_FUNC_ENABLE, Integer.valueOf(NOA_FUNC_ENABLE_ID));
        linkedHashMap.put(ELK_FUNC_ENABLE, Integer.valueOf(ELK_FUNC_ENABLE_ID));
        linkedHashMap.put(ESA_FUNC_ENABLE, Integer.valueOf(ESA_FUNC_ENABLE_ID));
        linkedHashMap.put(APA_FUNC_ENABLE_SA, Integer.valueOf(APA_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(RPA_FUNC_ENABLE_SA, Integer.valueOf(RPA_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(HAVP_FUNC_ENABLE_SA, Integer.valueOf(HAVP_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(ACC_FUNC_ENABLE_SA, Integer.valueOf(ACC_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(ICA_FUNC_ENABLE_SA, Integer.valueOf(ICA_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(PLC_FUNC_ENABLE_SA, Integer.valueOf(PLC_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(HANP_FUNC_ENABLE_SA, Integer.valueOf(HANP_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(ISA_FUNC_ENABLE_SA, Integer.valueOf(ISA_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(ISLC_FUNC_ENABLE_SA, Integer.valueOf(ISLC_FUNC_ENABLE_SA_ID));
        linkedHashMap.put(TLA_FUNC_ENABLE_SA, Integer.valueOf(TLA_FUNC_ENABLE_SA_ID));
        return Collections.unmodifiableMap(linkedHashMap);
    }
}
