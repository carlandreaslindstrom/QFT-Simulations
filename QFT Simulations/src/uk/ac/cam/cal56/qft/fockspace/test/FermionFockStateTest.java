package uk.ac.cam.cal56.qft.fockspace.test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testF_p() {
        double epsilon = 1.0e-9;
        final double L = 1.4;

        assertEquals(FermionFockState.F_p(0, 0, 0, L), 1, epsilon); // (<0|)(|0>) = 1
        assertEquals(FermionFockState.F_p(1, 0, 0, L), 1, epsilon); // (<0|b)(b*|0>) = 1
        assertEquals(FermionFockState.F_p(2, 0, 1, L), 0, epsilon); // (<0|)(b*^2|0>) = 0 by Pauli exclusion
        assertEquals(FermionFockState.F_p(0, 1, 0, L), Math.sqrt(L), epsilon); // (<0|b)b*(|0>) = sqrt(L)
        assertEquals(FermionFockState.F_p(0, 0, 1, L), 0, epsilon); // (<0|)b(|0>) = 0 by annihilation
        assertEquals(FermionFockState.F_p(1, 1, 0, L), 0, epsilon); // (<0|)b*(b*|0>) = 0 by Pauli exclusion
        assertEquals(FermionFockState.F_p(1, 0, 1, L), Math.sqrt(L), epsilon); // (<0|)b(b*|0>) = sqrt(L)
        assertEquals(FermionFockState.F_p(0, 1, 1, L), 0, epsilon); // (<0|)b*b(|0>) by annihilation
        assertEquals(FermionFockState.F_p(1, 1, 1, L), L, epsilon); // (<0|b)b*b(b*|0>) = L
        assertEquals(FermionFockState.F_p(1, 2, 1, L), 0, epsilon); // (<0|b)b*^2b(b*|0>) = 0 by annihilation
    }

}
