package uk.ac.cam.cal56.qft.fockspace.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;

public class ScalarFockStateTest {

    @Test
    public void testNext() {
        int N = 4;
        int Pmax = 3;
        double m = 1.0, dx = 0.1;
        ScalarFockState f = new ScalarFockState(N, Pmax, m, dx);

        // step one state multiple times and verify samples
        f.next();
        assertEquals(f.getLabel(), 0);
        assertEquals(f.getParticles(), Arrays.asList());

        f.next();
        assertEquals(f.getLabel(), 1);
        assertEquals(f.getParticles(), Arrays.asList(0));

        f.next();
        assertEquals(f.getLabel(), 2);
        assertEquals(f.getParticles(), Arrays.asList(1));

        for (int i = 0; i < 18; i++)
            f.next();
        assertEquals(f.getLabel(), 20);
        assertEquals(f.getParticles(), Arrays.asList(0, 1, 2));

        for (int i = 0; i < 14; i++)
            f.next();
        assertEquals(f.getLabel(), 34);
        assertEquals(f.getParticles(), Arrays.asList(3, 3, 3));

        // check that it produces the right number of particles
        for (int P = 0; P < 30; P++) {
            ScalarFockState g = new ScalarFockState(N, P, m, dx);
            g.next();
            for (int i = 0; i < ScalarFockState.S(N, P - 1); i++)
                g.next();
            assertEquals(g.getLabel(), ScalarFockState.S(N, P - 1));
            assertEquals(g.getParticleNumber(), P);
        }
    }

    @Test
    public void testMomentumNumberAndParticleNumberAndEnergy() {
        int N = 4;
        double m = 1.0, dx = 0.1;
        ScalarFockState f = new ScalarFockState(N, 3, m, dx);
        f.next();
        for (int i = 0; i < 18; i++)
            f.next();
        assertEquals(f.getLabel(), 18);
        assertEquals(f.getParticles(), Arrays.asList(0, 0, 3));
        assertEquals(f.getParticleNumber(), 3);
        assertEquals(f.getMomentumNumber(), 3);
        assertEquals(f.getEnergy(), 16.17744688, 1e-7);
    }

    @Test
    public void testIterator() {
        int N = 4, Pmax = 6;
        double m = 1.0, dx = 0.1;
        ScalarFockState f = new ScalarFockState(N, Pmax, m, dx);

        int counter = 0;
        for (int i : f) {
            System.out.println(i);
            assertEquals(i, counter++);
        }

        assertEquals(f.getLabel(), ScalarFockState.S(N, Pmax) - 1);
        assertEquals(f.getParticles(), Collections.nCopies(Pmax, N - 1));
    }

    @Test
    public void testS() {
        assertEquals(ScalarFockState.S(4, -1), 0);
        assertEquals(ScalarFockState.S(4, 0), 1);
        assertEquals(ScalarFockState.S(4, 1), 5);
        assertEquals(ScalarFockState.S(4, 2), 15);
        assertEquals(ScalarFockState.S(4, 3), 35);
        assertEquals(ScalarFockState.S(4, 4), 70);
        assertEquals(ScalarFockState.S(4, 5), 126);

        assertEquals(ScalarFockState.S(6, 0), 1);
        assertEquals(ScalarFockState.S(6, 1), 7);
        assertEquals(ScalarFockState.S(6, 2), 28);
        assertEquals(ScalarFockState.S(6, 3), 84);

        assertEquals(ScalarFockState.S(6, -1), 0);

        assertEquals(ScalarFockState.S(24, 0), 1);

        assertEquals(ScalarFockState.S(27, 0), 1);
        assertEquals(ScalarFockState.S(27, 1), 28);
        assertEquals(ScalarFockState.S(27, 2), 406);
        assertEquals(ScalarFockState.S(27, 3), 4060);
        assertEquals(ScalarFockState.S(100, 3), 176851);
        assertEquals(ScalarFockState.S(100, 4), 4598126);
    }

    @Test
    public void testF_p() {
        double epsilon = 1.0e-6;
        final double L = 1.4;
        for (int l = 0; l < 100; l++) {
            // F_p(l,n,0)
            assertEquals(ScalarFockState.F_p(l, 0, 0, L), 1, epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 0, L), Math.sqrt((l + 1) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 0, L), Math.sqrt((l + 1) * L * (l + 2) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 3, 0, L), Math.sqrt((l + 1) * L * (l + 2) * L * (l + 3) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 4, 0, L), Math.sqrt((l + 1) * L * (l + 2) * L * (l + 3) * L * (l + 4) *
                                                                    L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 9, 0, L), Math.sqrt((l + 1) * L * (l + 2) * L * (l + 3) * L * (l + 4) *
                                                                    L * (l + 5) * L * (l + 6) * L * (l + 7) * L *
                                                                    (l + 8) * L * (l + 9) * L), epsilon);

            // F_p(l,0,m) as well as testing m<=l or else = 0
            assertEquals(ScalarFockState.F_p(l, 0, 1, L), Math.sqrt(l * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 2, L), Math.sqrt(l * L * (l - 1) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 3, L), Math.sqrt(l * L * (l - 1) * L * (l - 2) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 4, L), Math.sqrt(l * L * (l - 1) * L * (l - 2) * L * (l - 3) * L),
                         epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 9, L), Math.sqrt(l * L * (l - 1) * L * (l - 2) * L * (l - 3) * L *
                                                                    (l - 4) * L * (l - 5) * L * (l - 6) * L * (l - 7) *
                                                                    L * (l - 8) * L), epsilon);

            // other cases
            assertEquals(ScalarFockState.F_p(l, 1, 1, L), l * L, epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 1, L), l * L * Math.sqrt((l + 1) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 2, L), (l - 1) * L * Math.sqrt(l * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 2, L), (l - 1) * L * l * L, epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 3, L), (l - 2) * L * Math.sqrt(l * L * (l - 1) * L), epsilon);
            assertEquals(ScalarFockState.F_p(l, 3, 1, L), l * L * Math.sqrt((l + 1) * L * (l + 2) * L), epsilon);
        }
    }

}
