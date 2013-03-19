package uk.ac.cam.cal56.qft.fockspace.test;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;

public class FermionFockStateTest {

    @Test
    public void testStepping() {
        int N = 3;
        int Pmax = 6;
        double dx = 1.0;
        double m = 1.0;
        FermionFockState f = new FermionFockState(N, Pmax, m, dx);

        for (int i : f) {
            System.out.println(i + " " + f.toString());
        }
    }

}
