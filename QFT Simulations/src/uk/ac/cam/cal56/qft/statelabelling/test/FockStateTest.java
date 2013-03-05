package uk.ac.cam.cal56.qft.statelabelling.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.statelabelling.FockState;

public class FockStateTest {

    @Test
    public void testNext() {
        int N = 4;
        int Pmax = 3;
        double m = 1.0, dx = 0.1;
        FockState f = new FockState(N, Pmax, m, dx);

        // step one state multiple times and verify samples
        f.next();
        assertEquals(f.getLabel(), 0);
        assertEquals(f.toList(), Arrays.asList());

        f.next();
        assertEquals(f.getLabel(), 1);
        assertEquals(f.toList(), Arrays.asList(0));

        f.next();
        assertEquals(f.getLabel(), 2);
        assertEquals(f.toList(), Arrays.asList(1));

        for (int i = 0; i < 18; i++)
            f.next();
        assertEquals(f.getLabel(), 20);
        assertEquals(f.toList(), Arrays.asList(0, 1, 2));

        for (int i = 0; i < 14; i++)
            f.next();
        assertEquals(f.getLabel(), 34);
        assertEquals(f.toList(), Arrays.asList(3, 3, 3));

        // check that it produces the right number of particles
        for (int P = 0; P < 30; P++) {
            FockState g = new FockState(N, P, m, dx);
            g.next();
            for (int i = 0; i < Combinatorics.S(N, P - 1); i++)
                g.next();
            assertEquals(g.getLabel(), Combinatorics.S(N, P - 1));
            assertEquals(g.getParticleNumber(), P);
        }
    }

    @Test
    public void testMomentumNumberAndParticleNumberAndEnergy() {
        int N = 4;
        double m = 1.0, dx = 0.1;
        FockState f = new FockState(N, 3, m, dx);
        f.next();
        for (int i = 0; i < 18; i++)
            f.next();
        assertEquals(f.getLabel(), 18);
        assertEquals(f.toList(), Arrays.asList(0, 0, 3));
        assertEquals(f.getParticleNumber(), 3);
        assertEquals(f.getMomentumNumber(), 3);
        assertEquals(f.getEnergy(), 16.17744688, 1e-7);
    }

    @Test
    public void testIterator() {
        int N = 4, Pmax = 6;
        double m = 1.0, dx = 0.1;
        FockState f = new FockState(N, Pmax, m, dx);

        int counter = 0;
        for (int i : f) {
            System.out.println(i);
            assertEquals(i, counter++);
        }

        assertEquals(f.getLabel(), Combinatorics.S(N, Pmax) - 1);
        assertEquals(f.toList(), Collections.nCopies(Pmax, N - 1));
    }

}
