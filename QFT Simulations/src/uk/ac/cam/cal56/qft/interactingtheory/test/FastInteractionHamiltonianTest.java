package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.InteractionHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.impl.FastInteractionHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SlowInteractionHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class FastInteractionHamiltonianTest {

    private final double EPSILON = 1.0e-10;

    @Test
    public void testCalculateElements() {
        int N = 15, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        InteractionHamiltonian ih = new FastInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);

        ih.calculateElements();
    }

    @Test
    public void testMatrixSize() {
        int N = 15, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        InteractionHamiltonian ih = new FastInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);

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
        assertEquals(elementCount, 3724);
    }

    @Test
    public void testDiagonalSymmetry() {
        int N = 15, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        InteractionHamiltonian ih = new FastInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);

        ih.calculateElements();

        for (int n = 0; n < Combinatorics.S(N, Pmax); n++) {
            for (int m = 0; m <= n; m++) {
                Double value = ih.get(n, m);
                Double diag = ih.get(m, n);
                if (value != null && diag != null) {
                    // System.out.println((diag / value) + " : " + value + " = " + diag);
                    assertEquals(value, diag, EPSILON);
                }
            }
        }
    }

    @Test
    public void testMomentumConservationAndParticleTransitions() {
        int N = 30, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        InteractionHamiltonian ih = new FastInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);

        ih.calculateElements();

        for (int n = 0; n < Combinatorics.S(N, Pmax); n++) {
            for (int m = 0; m < Combinatorics.S(N, Pmax); m++) {
                Double value = ih.get(n, m);
                if (value != null) {
                    // check if transitions are between equal momentum states
                    assertEquals(StateLabelling.momentumNumber(n, N), StateLabelling.momentumNumber(m, N));
                    // check if transitions are between particle numbers of an odd difference
                    assertEquals(Math.abs(StateLabelling.P(n, N) - StateLabelling.P(m, N)) % 2, 1);
                }
            }
        }
    }

    @Test
    public void testListRemove() {
        List<Integer> ks = new ArrayList<Integer>(Arrays.asList(0, 0, 1));
        System.out.println(ks);
        ks.remove((Integer) 0);
        System.out.println(ks);
        assertEquals(ks, Arrays.asList(0, 1));
    }

    @Test
    public void testImplementationComparison() {
        int N = 15, Pmax = 3;
        double mass = 1.0, dx = 0.1;

        // calculate both
        InteractionHamiltonian fih = new FastInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);
        InteractionHamiltonian sih = new SlowInteractionHamiltonian(N, Pmax, mass, dx, Interaction.PHI_THIRD);
        fih.calculateElements();
        sih.calculateElements();

        // compare
        for (int n = 0; n < Combinatorics.S(N, Pmax); n++) {
            for (int m = 0; m <= Combinatorics.S(N, Pmax); m++) {
                Double f = fih.get(n, m);
                Double s = sih.get(n, m);
                if (s != null) {
                    assertNotNull(f);
                    assertEquals((double) s, (double) f, EPSILON);
                }
            }
        }
    }
}
