package ru.big.town.hil;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BuildCheckTest {
    @Test
    public void smoke() {
        assertEquals(42, BuildCheck.answer());
    }
}
