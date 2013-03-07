package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SlowInteractionHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class SlowInteractionHamiltonianTest {

    private final double EPSILON = 1e-10;

    @Test
    public void testCalculateElements() {
        int N = 10, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        Hamiltonian ih = new SlowInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_CUBED);

        ih.calculateElements();

        for (int n = 0; n < Combinatorics.S(N, Pmax); n++) {
            for (int m = 0; m <= n; m++) {
                Double value = ih.get(n, m);
                if (value != null) {
                    // check if Hamiltonian is symmetric
                    assertEquals(value, ih.get(m, n));
                    // check if transitions are between equal momentum states
                    assertEquals(StateLabelling.momentumNumber(n, N), StateLabelling.momentumNumber(m, N));
                    // check if transitions are between particle numbers of an odd difference
                    assertEquals(Math.abs(StateLabelling.P(n, N) - StateLabelling.P(m, N)) % 2, 1);
                }
            }
        }
    }

    @Test
    public void testMatrixSize() {
        int N = 15, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        Hamiltonian ih = new SlowInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_CUBED);

        ih.calculateElements();
        int S = Combinatorics.S(N, Pmax);
        int elementCount = 0;
        double total = 0;
        for (int n = 0; n < S; n++) {
            for (Entry<Integer, Double> element : ih.getRow(n).entrySet()) {
                elementCount++;
                total += element.getValue();
                assertTrue(element.getKey() < S);
                assertTrue(element.getValue() > 0.0);
            }
        }
        assertEquals(total, 893.6862834805144, EPSILON);
        assertEquals(elementCount, 3724); // TODO: check if this is the correct element count
    }

}
