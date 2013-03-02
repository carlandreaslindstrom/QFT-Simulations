package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SecondOrderNonSymplecticState;

public class SecondOrderNonSymplecticStateTest {

    private static final double EPSILON = 1.0e-10;

    @Test
    public void testConstructor() {
        int N = 10, Pmax = 3;
        double m = 1.0, dx = 1.0, dt = 0.01, lambda = 0.01;
        SecondOrderNonSymplecticState state = new SecondOrderNonSymplecticState(N, Pmax, m, dx, dt, lambda);
        assertEquals(state.getTime(), 0.0, EPSILON);
    }

    @Test
    public void testGetProbabilities() {
        int N = 11, Pmax = 3;
        double m = 1.0, dx = 1.0, dt = 0.01, lambda = 0.01;
        SecondOrderNonSymplecticState state = new SecondOrderNonSymplecticState(N, Pmax, m, dx, dt, lambda);
        assertEquals(state.getTime(), 0.0, 1e-9);

        // 0P vacuum state
        assertEquals(state.get0P(), 1.0, EPSILON);

        // 1P states
        double[] probs1P = state.get1PMomenta();
        for (int p = 0; p < N; p++)
            assertEquals(probs1P[p], 0.0, EPSILON);

        // 2P states
        double[][] probs2P = state.get2PMomenta();
        for (int p = 0; p < N; p++)
            for (int q = 0; q < N; q++)
                assertEquals(probs2P[p][q], 0.0, EPSILON);
    }

    @Test
    public void testStepping() {
        int N = 8, Pmax = 3;
        double m = 1.0, dx = 1.0, dt = 0.01, lambda = 0.0;
        SecondOrderNonSymplecticState state = new SecondOrderNonSymplecticState(N, Pmax, m, dx, dt, lambda);

        // check if initially all amplitude is in the vacuum state
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.get0P(), 1.0, EPSILON);
        double[] probs1P = state.get1PMomenta();
        for (int p = 0; p < N; p++)
            assertEquals(probs1P[p], 0.0, EPSILON);
        double[][] probs2P = state.get2PMomenta();
        for (int p = 0; p < N; p++)
            for (int q = 0; q < N; q++)
                assertEquals(probs2P[p][q], 0.0, EPSILON);

        state.step();

        // check if some amplitude has shifted into other states
        assertEquals(state.getTime(), dt, EPSILON);
        assertTrue(state.get0P() <= 1.0);
        probs1P = state.get1PMomenta();
        for (int p = 0; p < N; p++)
            assertTrue(probs1P[p] >= 0.0);
        probs2P = state.get2PMomenta();
        for (int p = 0; p < N; p++)
            for (int q = 0; q < N; q++)
                assertTrue(probs2P[p][q] >= 0.0);

        double tfinal = 30.0;
        int count = 0;
        for (double t = dt; t < tfinal; t += dt) {
            assertEquals(state.getTime(), t, EPSILON); // check if time increment works
            if (count++ % 100 == 0)
                System.out.println(t + " => " + state.get0P());
            state.step();
        }

    }

    @Test
    public void testExactComparison() {
        int _N = 15;
        int _Pmax = 3;
        double _m = 1.0;
        double _dx = 0.1;
        double _dt = 0.01;
        double _lambda = 0.0;
        State _state = new SecondOrderNonSymplecticState(_N, _Pmax, _m, _dx, _dt, _lambda); // recalculate

        // TODO: fix just like the other implementation test
        for (double tfinal = 0.0; tfinal < 10.0; tfinal += Math.sqrt(2)) {
            while (_state.getTime() < tfinal + _dt)
                _state.step();
            // Complex exact = Complex.expi(-_m * tfinal);
        }
    }

}
