package uk.ac.cam.cal56.qft.investigations.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.cam.cal56.qft.investigations.MomentumMode;

public class MomentumModeTest {

    @Test
    public void testNAndLabels() {
        int N = 3;
        MomentumMode psi = new MomentumMode(N);

        assertEquals(psi.N, N);
        for (int i = 0; i < N; i++) {
            assertEquals((int) psi.labels.get(i), 0);
        }
        assertEquals(psi.particleNumber, 0);
    }

    @Test
    public void testParticleNumber() {
        MomentumMode psi = new MomentumMode(Arrays.asList(0, 2, 0, 1, 4, 0));

        assertEquals(psi.particleNumber, 7);
    }

    @Test
    public void testGetTotalMomentumNumber() {
        MomentumMode psi = new MomentumMode(Arrays.asList(1, 2, 1));
        MomentumMode phi = new MomentumMode(Arrays.asList(18, 0, 4, 0, 3, 0, 1));

        assertEquals(psi.getTotalMomentumNumber(), 1);
        assertEquals(phi.getTotalMomentumNumber(), 5);
    }

}
