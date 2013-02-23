package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.InteractionHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class InteractionHamiltonianTest {

    @Test
    public void testToLadderOpMap() {
        int N = 10, Pmax = 3;
        double m = 1.0, dx = 0.1;
        InteractionHamiltonian ih = new InteractionHamiltonian(N, Pmax, m, dx, Interaction.PHI_THIRD);

        Map<Integer, int[]> calculated = ih.toLadderOpMap(new int[] { 0, 0 }, new int[] { 0 });
        Map<Integer, int[]> expected = new HashMap<Integer, int[]>();
        expected.put(0, new int[] { 2, 1 });
        for (Entry<Integer, int[]> c : calculated.entrySet()) {
            int p = c.getKey();
            int n_p = c.getValue()[0];
            int m_p = c.getValue()[1];
            assertEquals(n_p, expected.get(p)[0]);
            assertEquals(m_p, expected.get(p)[1]);
        }

        calculated = ih.toLadderOpMap(new int[] { 0, 0, 1, 4 }, new int[] { 0, 0, 0, 0, 3, 4, 4 });
        expected = new HashMap<Integer, int[]>();
        expected.put(0, new int[] { 2, 4 });
        expected.put(1, new int[] { 1, 0 });
        expected.put(3, new int[] { 0, 1 });
        expected.put(4, new int[] { 1, 2 });
        for (Entry<Integer, int[]> c : calculated.entrySet()) {
            int p = c.getKey();
            int n_p = c.getValue()[0];
            int m_p = c.getValue()[1];
            assertEquals(n_p, expected.get(p)[0]);
            assertEquals(m_p, expected.get(p)[1]);
        }

        // test for momenta outside 0..<N (modulus function)
        calculated = ih.toLadderOpMap(new int[] { 0, 5, -5 }, new int[] {});
        expected = new HashMap<Integer, int[]>();
        expected.put(0, new int[] { 1, 0 });
        expected.put(5, new int[] { 2, 0 });
        for (Entry<Integer, int[]> c : calculated.entrySet()) {
            int p = c.getKey();
            int n_p = c.getValue()[0];
            int m_p = c.getValue()[1];
            assertEquals(n_p, expected.get(p)[0]);
            assertEquals(m_p, expected.get(p)[1]);
        }
    }

    @Test
    public void testCalculateElements() {
        int N = 20, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        Interaction interaction = Interaction.PHI_THIRD;
        InteractionHamiltonian ih = new InteractionHamiltonian(N, Pmax, mass, dx, interaction);

        ih.calculateElements();

        for (int n = 0; n < Combinatorics.S(N, Pmax); n++) {
            for (int m = 0; m <= n; m++) {
                Double value = ih.get(n, m);
                if (value != null) {
                    // check if Hamiltonian is symmetric
                    assertEquals(value, ih.get(m, n));
                    // check if transitions are between equal momentum states
                    assertEquals(StateLabelling.momentumNumber(n, N), StateLabelling.momentumNumber(m, N));
                    // check if transitions have the correct particle number differences (odd)
                    assertTrue(InteractionHamiltonian.allowedTransition(StateLabelling.P(n, N), StateLabelling.P(m, N),
                                                                        interaction));
                }
            }
        }
    }

}
