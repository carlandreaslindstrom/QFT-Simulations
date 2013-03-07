package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.interactingtheory.impl.FreeHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.FockState;

public class FreeHamiltonianTest {

    private final double EPSILON = 1e-10;

    @Test
    public void testConstructor() {
        int N = 10;
        int Pmax = 3;
        double m = 10.0;
        double dx = 0.1;
        FreeHamiltonian fh = new FreeHamiltonian(N, Pmax, m, dx);

        FockState phi = new FockState(N, Pmax, m, dx);
        for (int n : phi) {
            // System.out.println(fh.energies[n]);
            assertEquals(phi.getEnergy(), fh.getEnergy(n), EPSILON);
        }
    }

}
