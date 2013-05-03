package uk.ac.cam.cal56.qft.impl.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.impl.FreeHamiltonian;

public class FreeHamiltonianTest {

    private final double EPSILON = 1e-10;

    @Test
    public void testConstructor() {
        int N = 10;
        int Pmax = 3;
        double m = 10.0;
        double dx = 0.1;
        FreeHamiltonian fh = new FreeHamiltonian(N, Pmax, m, dx, ScalarFockState.class);

        ScalarFockState phi = new ScalarFockState(N, Pmax, m, dx);
        for (int n : phi) {
            // System.out.println(fh.energies[n]);
            assertEquals(phi.getEnergy(), fh.getEnergy(n), EPSILON);
        }
    }

}
