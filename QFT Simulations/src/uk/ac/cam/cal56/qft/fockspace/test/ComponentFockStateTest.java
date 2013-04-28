package uk.ac.cam.cal56.qft.fockspace.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.ComponentFockState;

public class ComponentFockStateTest {

    // test by inspection
    @Test
    public void testStepping() {
        int N = 2;
        int Pmax = 2;
        double dx = 1.0;
        double m = 1.0;
        ComponentFockState f = new ComponentFockState(N, Pmax, m, dx);

        for (int i : f) {
            System.out.println(i + " " + f.getParticles());
        }
    }

    @Test
    public void testParticleNumbers() {
        double dx = 1.0;
        double m = 1.0;
        for (int N = 0; N < 19; N++) {
            int Pmax = N;
            ComponentFockState f = new ComponentFockState(N, Pmax, m, dx);
            int stateCount = 0;
            for (int i : f)
                stateCount = i + 1;
            assertEquals(stateCount, ComponentFockState.S(N, Pmax));
        }
    }
}
