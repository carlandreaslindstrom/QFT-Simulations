package uk.ac.cam.cal56.qft.fockspace.labelling.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.FermionLabelling;

public class FermionLabellingTest {

    @Test
    public void testIndex() {
        double m = 1.0;
        double dx = 1.0;
        int N, Pmax;
        FermionFockState f;

        N = 100;
        Pmax = 2;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));

        N = 16;
        Pmax = 6;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));

        N = 2;
        Pmax = 4;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));
    }

}
