package uk.ac.cam.cal56.qft.fockspace.labelling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.ScalarLabelling;

public class StateLabellingTest {

    @Test
    public void scalarIndexVerification() {
        int[] Nmax = new int[]{0/*1000000000*/,0/*10000*/,0/*1000*/,0/*200*/,0/*100*/,0/*60*/,0/*40*/,0/*20*/,0/*20*/,0/*20*/}; // limit for 0P, 1P, etc...
        for (int Pmax = 0; Pmax < Nmax.length; Pmax++) {
            for (int N = 1; N <= Nmax[Pmax]; N++) {
                ScalarFockState ket = new ScalarFockState(N, Pmax, 0.0, 0.0);
                for (int i_S : ket)
                    assertEquals(i_S, ScalarLabelling.label(ket.getParticles(), N));
            }
        }
    }

    @Test
    public void testIndexFromEntries() {
        int N = 130;
        double m = 1.0, dx = 0.1;
        int Pmax = 3;
        assertEquals(ScalarFockState.S(N, Pmax), 383306);
        ScalarFockState phi = new ScalarFockState(N, Pmax, m, dx);
        for (int i : phi)
            assertEquals(i, ScalarLabelling.label(phi.getParticles(), N));
        assertFalse(phi.getLabel() == -1); // fails if not stepped

        N = 12;
        Pmax = 10;
        assertEquals(ScalarFockState.S(N, Pmax), 646646);
        phi = new ScalarFockState(N, Pmax, m, dx);
        for (int i : phi)
            assertEquals(i, ScalarLabelling.label(phi.getParticles(), N));
        assertFalse(phi.getLabel() == -1); // fails if not stepped
    }

    @Test
    public void testToLadderOpMap() {
        int N = 10;

        Map<Integer, int[]> calculated = ScalarLabelling.toLadderOperatorMap(new int[] { 0, 0 }, new int[] { 0 }, N);
        Map<Integer, int[]> expected = new HashMap<Integer, int[]>();
        expected.put(0, new int[] { 2, 1 });
        for (Entry<Integer, int[]> c : calculated.entrySet()) {
            int p = c.getKey();
            int n_p = c.getValue()[0];
            int m_p = c.getValue()[1];
            assertEquals(n_p, expected.get(p)[0]);
            assertEquals(m_p, expected.get(p)[1]);
        }

        calculated = ScalarLabelling.toLadderOperatorMap(new int[] { 0, 0, 1, 4 }, new int[] { 0, 0, 0, 0, 3, 4, 4 }, N);
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
        calculated = ScalarLabelling.toLadderOperatorMap(new int[] { 0, 5, -5 }, new int[] {}, N);
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
        assertEquals(ScalarLabelling.braIndex(Arrays.asList(0, 0), new int[] { 1 }, new int[] { 0 }, 5), (Integer) 7);
        assertEquals(ScalarLabelling.braIndex(Arrays.asList(0, 0), new int[] {}, new int[] { 1 }, 5), null);
        assertEquals(ScalarLabelling.braIndex(Arrays.asList(0, 1), new int[] {}, new int[] { 1 }, 6), (Integer) 1);

        int N = 70, Pmax = 3;
        double mass = 1.0, dx = 0.1;
        ScalarFockState phi = new ScalarFockState(N, Pmax, mass, dx);

        for (int n : phi) {
            for (int p = 0; p < N; p++) {
                Integer m = ScalarLabelling.braIndex(phi.getParticles(), new int[] {}, new int[] { p }, N);
                assertEquals(phi.getParticles().contains(p), m != null);
            }
        }

    }
}
