package uk.ac.cam.cal56.qft.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.interactingtheory.FockState;

public class FockStateTest {

    @Test
    public void testConstructor() {
        int N = 10;
        FockState f = new FockState(N);
        assertEquals(f.particleNumber(), 0);
        assertEquals(f.label(), 0);

        f.create(3);
        assertEquals(f.particleNumber(), 1);
        assertEquals(f.label(), 4);

        f.create(0);
        assertEquals(f.particleNumber(), 2);
        assertEquals(f.label(), 14);
    }

    @Test
    public void testSandwichLabel() {
        int N = 10;
        FockState f = new FockState(N);
        f.create(3);
        f.create(4);
        assertEquals(f.sandwichLabel(null, null), (Long) 14L);
    }
}
