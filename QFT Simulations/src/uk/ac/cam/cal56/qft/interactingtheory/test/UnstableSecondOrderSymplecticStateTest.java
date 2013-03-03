package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.UnstableSecondOrderSymplecticState;

public class UnstableSecondOrderSymplecticStateTest {

    private final double EPSILON = 1.0e-10;

    private final int    _N      = 1;
    private final int    _Pmax   = 1;
    private final double _m      = 1.0;
    private final double _dx     = 0.1;
    private final double _dt     = 0.01;
    private double       _lambda = 0.1;

    private State        _state;

    @Before
    public void setUp() {
        _state = new UnstableSecondOrderSymplecticState(_N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(_state.getTime(), 0.0, EPSILON);
        assertEquals(_state.get0P(), 1.0, EPSILON);
    }

    @Test
    public void testStep() {

        double tfinal = 1000.0;
        while (_state.getTime() < tfinal) {
            _state.step();
            double modSquared = _state.getModSquared();
            // double error = 1e-3;
            // assertEquals(Math.abs(modSquared - 1.0), 0.0, error);
            System.out.println((int) (_state.getTime() / _dt) + " : " + modSquared);
        }
        _state.reset();
        assertEquals(_state.getTime(), 0.0, EPSILON);
        assertEquals(_state.get0P(), 1.0, EPSILON);
    }

    @Test
    public void testExactComparison() {
        _state.setInteractionStrength(0); // free theory

        try {
            // Create file
            // String filename = "gnuplot/Symplectic1stOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 100;
            Complex exact = Complex.expi(-_m * tfinal);
            for (double dt = tfinal; dt > 1e-7; dt /= Math.sqrt(2)) {
                _state.setTimeStep(dt);
                _state.reset(0); // set to single zero momentum particle
                while (_state.getTime() < tfinal-_dt/2)
                    _state.step();
                Complex calc = ((UnstableSecondOrderSymplecticState) _state).get(0);
                System.out.println(dt + " " + Math.abs(exact.minus(calc).mod()));
                // out.write(dt + " " + Math.abs(exact.minus(calc).mod()));
                // out.newLine();
            }

            // Close the output stream
            // out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
