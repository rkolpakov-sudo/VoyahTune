package com.qinggan.canbus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class VehicleStateTest {

    @Test
    public void stableIdsAreUnique() {
        Set<Integer> ids = new HashSet<>();
        for (VehicleState s : VehicleState.values()) {
            assertTrue("duplicate stableId for " + s, ids.add(s.getValue()));
        }
        assertEquals(30, VehicleState.values().length);
    }

    @Test
    public void getValueReturnsBoxedIntegerForReflectiveContract() {
        Object reflected = VehicleState.HUM_VSP_FUNCTION_SW.getValue();
        assertTrue(reflected instanceof Integer);
        assertEquals(665, ((Integer) reflected).intValue());
    }

    @Test
    public void idOfResolvesByName() {
        assertEquals(711, VehicleState.idOf("ASC_MAINTAIN_SWITCH"));
        assertEquals(1072, VehicleState.idOf("BCM_RSM_lightSWReason"));
    }
}
