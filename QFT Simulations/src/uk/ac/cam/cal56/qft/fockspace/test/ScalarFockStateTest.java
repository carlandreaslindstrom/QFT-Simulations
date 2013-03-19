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
        assertEquals(f.toList(), Arrays.asList(0, 0, 3));
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
        assertEquals(f.toList(), Collections.nCopies(Pmax, N - 1));
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
        double epsilon = 1.0e-9;
        for (int l = 0; l < 200; l++) {
            // F_p(l,n,0)
            assertEquals(ScalarFockState.F_p(l, 0, 0), 1, epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 0), Math.sqrt(l + 1), epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 0), Math.sqrt((l + 1) * (l + 2)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 3, 0), Math.sqrt((l + 1) * (l + 2) * (l + 3)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 4, 0), Math.sqrt((l + 1) * (l + 2) * (l + 3) * (l + 4)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 9, 0), Math.sqrt((l + 1) * (l + 2) * (l + 3) * (l + 4) * (l + 5) *
                                                                 (l + 6) * (l + 7) * (l + 8) * (l + 9)), epsilon);

            // F_p(l,0,m) as well as testing m<=l or else = 0
            assertEquals(ScalarFockState.F_p(l, 0, 1), Math.sqrt(l), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 2), Math.sqrt(l * (l - 1)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 3), Math.sqrt(l * (l - 1) * (l - 2)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 4), Math.sqrt(l * (l - 1) * (l - 2) * (l - 3)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 0, 9), Math.sqrt(l * (l - 1) * (l - 2) * (l - 3) * (l - 4) * (l - 5) *
                                                                 (l - 6) * (l - 7) * (l - 8)), epsilon);

            // other cases
            assertEquals(ScalarFockState.F_p(l, 1, 1), l, epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 1), l * Math.sqrt(l + 1), epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 2), (l - 1) * Math.sqrt(l), epsilon);
            assertEquals(ScalarFockState.F_p(l, 2, 2), (l - 1) * l, epsilon);
            assertEquals(ScalarFockState.F_p(l, 1, 3), (l - 2) * Math.sqrt(l * (l - 1)), epsilon);
            assertEquals(ScalarFockState.F_p(l, 3, 1), l * Math.sqrt((l + 1) * (l + 2)), epsilon);
        }

    }

}
