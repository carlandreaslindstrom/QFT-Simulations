package uk.ac.cam.cal56.qft.freetheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.freetheory.State;

public class StateTest {

    @Test
    public void stepExactTest() {

        int N = 4;         // lattice space number
        int P = 3;         // maximum particle amount
        double L = 1.0;    // total lattice width
        double dt = 0.1;  // simulation time step
        int steps = 100;   // simulation step number
        double m = 1.0;    // particle mass

        double dx = L / N; // lattice spacing
        State phi = new State(N, P, m, dx); // initialize state

        // integrate steps (first order method)
        for (int i = 0; i < steps; i++) {
            phi.stepExact(dt);
            assertEquals(1.0, phi.modSquared(), 0.000000001);
            phi.printCoeffs();
        }
    }

    @Test
    public void stepTest() {

        int N = 4;         // lattice space number
        int P = 3;         // maximum particle amount
        double L = 1.0;    // total lattice width
        double dt = 0.01;  // simulation time step
        int steps = 1000;   // simulation step number
        double m = 1.0;    // particle mass

        double dx = L / N; // lattice spacing
        State phi = new State(N, P, m, dx); // initialize state

        // integrate steps (higher order method)
        phi.stepFirst(dt);
        phi.printCoeffs();
        for (int i = 0; i < steps; i++) {
            phi.step(dt);
            assertEquals(1.0, phi.modSquared(), 0.001);
            phi.printCoeffs();
        }
    }

}
