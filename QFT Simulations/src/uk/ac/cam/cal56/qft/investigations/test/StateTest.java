package uk.ac.cam.cal56.qft.investigations.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.cam.cal56.qft.investigations.State;

public class StateTest {

    @Test
    public void testNAndLabels() {
        int N = 3;
        State psi = new State(N);

        assertEquals(psi.N, N);
        for (int i = 0; i < N; i++) {
            assertEquals((int) psi.labels.get(i), 0);
        }
        assertEquals(psi.particleNumber, 0);
    }

    @Test
    public void testParticleNumber() {
        State psi = new State(Arrays.asList(0, 2, 0, 1, 4, 0));

        assertEquals(psi.particleNumber, 7);
    }

    @Test
    public void testGetTotalMomentumNumber() {
        State psi = new State(Arrays.asList(1, 2, 1));
        State phi = new State(Arrays.asList(18, 0, 4, 0, 3, 0, 1));

        assertEquals(psi.getTotalMomentumNumber(), 1);
        assertEquals(phi.getTotalMomentumNumber(), 5);
    }

    @Test
    public void testNaiveIncrementState() {
        int N = 3;
        State psi = new State(N);
        State phi = psi.naiveIncrementedState();

        assertEquals(psi.labels, Arrays.asList(0, 0, 0));
        assertEquals(phi.labels, Arrays.asList(1, 0, 0));
    }

}
