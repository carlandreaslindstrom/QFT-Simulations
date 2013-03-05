package uk.ac.cam.cal56.qft.statelabelling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.statelabelling.FockState;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class StateLabellingTest {

    @Test
    public void testPofNAndI() {
        assertEquals(StateLabelling.P(0, 3), 0);
        assertEquals(StateLabelling.P(1, 3), 1);
        assertEquals(StateLabelling.P(2, 3), 1);
        assertEquals(StateLabelling.P(3, 3), 1);
        assertEquals(StateLabelling.P(4, 3), 2);
        assertEquals(StateLabelling.P(5, 3), 2);
        assertEquals(StateLabelling.P(6, 3), 2);
        assertEquals(StateLabelling.P(7, 3), 2);
        assertEquals(StateLabelling.P(8, 3), 2);
        assertEquals(StateLabelling.P(9, 3), 2);
        assertEquals(StateLabelling.P(10, 3), 3);
        assertEquals(StateLabelling.P(11, 3), 3);
        assertEquals(StateLabelling.P(29, 3), 4);
        assertEquals(StateLabelling.P(600, 7), 5);
    }

    @Test
    public void testPofLs() {
        // N = 3
        assertEquals(StateLabelling.P(Arrays.asList(1, 0, 0)), 1);
        assertEquals(StateLabelling.P(Arrays.asList(1, 0, 3)), 4);
        assertNotSame(StateLabelling.P(Arrays.asList(1, 0, 3)), 3);

        // N = 5
        assertEquals(StateLabelling.P(Arrays.asList(1, 0, 0, 5, 6)), 12);
        assertEquals(StateLabelling.P(Arrays.asList(0, 1, 0, 14, 2)), 17);
    }

    @Test
    public void testIndex() {
        // N = 3
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 0)), 0);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 0)), 1);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 0)), 2);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 1)), 3);
        assertEquals(StateLabelling.index(Arrays.asList(2, 0, 0)), 4);
        assertEquals(StateLabelling.index(Arrays.asList(1, 1, 0)), 5);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 1)), 6);
        assertEquals(StateLabelling.index(Arrays.asList(0, 2, 0)), 7);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 1)), 8);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 2)), 9);
        assertEquals(StateLabelling.index(Arrays.asList(3, 0, 0)), 10);
        assertEquals(StateLabelling.index(Arrays.asList(2, 1, 0)), 11);
        assertEquals(StateLabelling.index(Arrays.asList(2, 0, 1)), 12);
        assertEquals(StateLabelling.index(Arrays.asList(1, 2, 0)), 13);
        assertEquals(StateLabelling.index(Arrays.asList(1, 1, 1)), 14);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 2)), 15);
        assertEquals(StateLabelling.index(Arrays.asList(0, 3, 0)), 16);
        assertEquals(StateLabelling.index(Arrays.asList(0, 2, 1)), 17);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 2)), 18);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 3)), 19);
        assertEquals(StateLabelling.index(Arrays.asList(4, 0, 0)), 20);
        assertEquals(StateLabelling.index(Arrays.asList(3, 1, 0)), 21);
        assertEquals(StateLabelling.index(Arrays.asList(3, 0, 1)), 22);
        assertEquals(StateLabelling.index(Arrays.asList(2, 2, 0)), 23);
        assertEquals(StateLabelling.index(Arrays.asList(2, 1, 1)), 24);
        assertEquals(StateLabelling.index(Arrays.asList(2, 0, 2)), 25);
        assertEquals(StateLabelling.index(Arrays.asList(1, 3, 0)), 26);
        assertEquals(StateLabelling.index(Arrays.asList(1, 2, 1)), 27);
        assertEquals(StateLabelling.index(Arrays.asList(1, 1, 2)), 28);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 3)), 29);

        // N = 5
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 0, 0, 0)), 0);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 0, 0, 0)), 1);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 0, 0, 0)), 2);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 1, 0, 0)), 3);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 0, 1, 0)), 4);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 0, 0, 1)), 5);
        assertEquals(StateLabelling.index(Arrays.asList(2, 0, 0, 0, 0)), 6);
        assertEquals(StateLabelling.index(Arrays.asList(1, 1, 0, 0, 0)), 7);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 1, 0, 0)), 8);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 0, 1, 0)), 9);
        assertEquals(StateLabelling.index(Arrays.asList(1, 0, 0, 0, 1)), 10);
        assertEquals(StateLabelling.index(Arrays.asList(0, 2, 0, 0, 0)), 11);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 1, 0, 0)), 12);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 0, 1, 0)), 13);
        assertEquals(StateLabelling.index(Arrays.asList(0, 1, 0, 0, 1)), 14);
        assertEquals(StateLabelling.index(Arrays.asList(0, 0, 2, 0, 0)), 15);

        assertEquals(StateLabelling.index(Arrays.asList(7, 0, 0, 0, 0)), 462);
        assertEquals(StateLabelling.index(Arrays.asList(6, 1, 0, 0, 0)), 463);
        assertEquals(StateLabelling.index(Arrays.asList(6, 0, 1, 0, 0)), 464);
        assertEquals(StateLabelling.index(Arrays.asList(6, 0, 0, 1, 0)), 465);
    }

    @Test
    public void testLabels() {
        assertEquals(StateLabelling.labels(0, 3), Arrays.asList(0, 0, 0));
        assertEquals(StateLabelling.labels(10, 3), Arrays.asList(3, 0, 0));
        assertEquals(StateLabelling.labels(29, 3), Arrays.asList(1, 0, 3));

        assertEquals(StateLabelling.labels(462, 5), Arrays.asList(7, 0, 0, 0, 0));
    }

    @Test
    public void testLabelsGrandVerification() {
        int maxN = 100;
        int maxIndex = 100;
        for (int N = 1; N <= maxN; N++) {
            for (int i = 0; i < maxIndex; i++) {
                assertEquals(StateLabelling.index(StateLabelling.labels(i, N)), i);
            }
        }
    }

    @Test
    public void speedTestLabels() {
        // experimentally time scales exponentially with maxIndex
        // almost constant time with N [if max index constant] (!!, due to lots of zeros)
        int N = 50; // lattice points
        int P = 4; // max particle number
        long maxIndex = Combinatorics.S(N, P); // this number explodes with rising P
        // System.out.println("Number of states: " + maxIndex);
        for (int i = 0; i < maxIndex; i++)
            assertNotNull(StateLabelling.labels(i, N));
    }

    @Test
    public void stressTestLabels() {
        // can handle "any" i for N up to ~1000.
        int i = 100000000;
        int N = 1000;
        List<Integer> ls = StateLabelling.labels(i, N);
        long iCalculated = StateLabelling.index(ls);
        assertEquals(i, iCalculated);
    }

    @Test
    public void testMomentumNumber() {
        assertEquals(StateLabelling.momentumNumber(Arrays.asList(0, 0, 0, 0, 0)), 0);
        assertEquals(StateLabelling.momentumNumber(Arrays.asList(3, 1, 1, 0, 0)), 3);
        assertEquals(StateLabelling.momentumNumber(Arrays.asList(2, 1, 1, 2, 3)), 1);
        assertEquals(StateLabelling.momentumNumber(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1)),
                     13);

        assertEquals(StateLabelling.momentumNumber(17, 18), 16);
        assertEquals(StateLabelling.momentumNumber(0, 100), 0);

        int N = 100;
        int P = 3;
        long maxIndex = Combinatorics.S(N, P); // this number explodes with rising P
        for (int i = 0; i < maxIndex; i++)
            assertEquals(StateLabelling.momentumNumber(i, N),
                         StateLabelling.momentumNumber(StateLabelling.labels(i, N)));
    }

    @Test
    public void testIndexFromEntries() {
        int N = 130;
        double m = 1.0, dx = 0.1;
        int Pmax = 3;
        assertEquals(Combinatorics.S(N, Pmax), 383306);
        FockState phi = new FockState(N, Pmax, m, dx);
        for (int i : phi)
            assertEquals(i, StateLabelling.index(phi.toList(), N));
        assertFalse(phi.getLabel() == -1); // fails if not stepped

        N = 12;
        Pmax = 10;
        assertEquals(Combinatorics.S(N, Pmax), 646646);
        phi = new FockState(N, Pmax, m, dx);
        for (int i : phi)
            assertEquals(i, StateLabelling.index(phi.toList(), N));
        assertFalse(phi.getLabel() == -1); // fails if not stepped
    }

    @Test
    public void testToLadderOpMap() {
        int N = 10;

        Map<Integer, int[]> calculated = StateLabelling.toLadderOpMap(new int[] { 0, 0 }, new int[] { 0 }, N);
        Map<Integer, int[]> expected = new HashMap<Integer, int[]>();
        expected.put(0, new int[] { 2, 1 });
        for (Entry<Integer, int[]> c : calculated.entrySet()) {
            int p = c.getKey();
            int n_p = c.getValue()[0];
            int m_p = c.getValue()[1];
            assertEquals(n_p, expected.get(p)[0]);
            assertEquals(m_p, expected.get(p)[1]);
        }

        calculated = StateLabelling.toLadderOpMap(new int[] { 0, 0, 1, 4 }, new int[] { 0, 0, 0, 0, 3, 4, 4 }, N);
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
        calculated = StateLabelling.toLadderOpMap(new int[] { 0, 5, -5 }, new int[] {}, N);
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
    @SuppressWarnings("unused")
    public void testBraIndex() {
        assertEquals(StateLabelling.braIndex(Arrays.asList(0, 0), new int[] { 1 }, new int[] { 0 }, 5), (Integer) 7);
        assertEquals(StateLabelling.braIndex(Arrays.asList(0, 0), new int[] {}, new int[] { 1 }, 5), null);
        assertEquals(StateLabelling.braIndex(Arrays.asList(0, 1), new int[] {}, new int[] { 1 }, 6), (Integer) 1);

        int N = 70, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        FockState phi = new FockState(N, Pmax, mass, dx);

        for (int n : phi) {
            for (int p = 0; p < N; p++) {
                Integer m = StateLabelling.braIndex(phi.toList(), new int[] {}, new int[] { p }, N);
                assertEquals(phi.toList().contains(p), m != null);
            }
        }

    }
}
