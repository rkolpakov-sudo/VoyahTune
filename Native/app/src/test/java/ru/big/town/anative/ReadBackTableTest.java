package ru.big.town.anative;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReadBackTableTest {

    @Test public void modesEnergyRecycleMapToVcuIndication() {
        assertEquals(ReadBackTable.SOURCE_VCU_INDICATION, ReadBackTable.sourceFor(ReadBackTable.FEATURE_DRIVE_MODE));
        assertEquals(ReadBackTable.SOURCE_VCU_INDICATION, ReadBackTable.sourceFor(ReadBackTable.FEATURE_ENERGY));
        assertEquals(ReadBackTable.SOURCE_VCU_INDICATION, ReadBackTable.sourceFor(ReadBackTable.FEATURE_RECYCLE));
    }

    @Test public void suspensionMapsToAsc() {
        assertEquals(ReadBackTable.SOURCE_ASC, ReadBackTable.sourceFor(ReadBackTable.FEATURE_SUSPENSION));
    }

    @Test public void lightMapsToSwReason() {
        assertEquals(ReadBackTable.SOURCE_LIGHT_SW_REASON, ReadBackTable.sourceFor(ReadBackTable.FEATURE_LIGHT));
    }

    @Test public void avasMapsToTx57Bit() {
        assertEquals(ReadBackTable.SOURCE_AVAS_TX57, ReadBackTable.sourceFor(ReadBackTable.FEATURE_AVAS));
    }

    @Test public void unknownFeatureHasNoSource() {
        assertNull(ReadBackTable.sourceFor("climate"));
        assertFalse(ReadBackTable.isTracked("climate"));
        assertFalse(ReadBackTable.isTracked(ReadBackTable.FEATURE_AVAS));
    }

    @Test public void trackedFeaturesCoverReadBackTable() {
        assertTrue(ReadBackTable.isTracked(ReadBackTable.FEATURE_DRIVE_MODE));
        assertTrue(ReadBackTable.isTracked(ReadBackTable.FEATURE_ENERGY));
        assertTrue(ReadBackTable.isTracked(ReadBackTable.FEATURE_RECYCLE));
        assertTrue(ReadBackTable.isTracked(ReadBackTable.FEATURE_SUSPENSION));
        assertTrue(ReadBackTable.isTracked(ReadBackTable.FEATURE_LIGHT));
    }
}
