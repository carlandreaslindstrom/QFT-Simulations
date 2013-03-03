package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.DivergingFourthOrderState;

public class DivergingFourthOrderStateTest {

    private final double EPSILON = 1.0e-10;

    private final int    _N      = 1;
    private final int    _Pmax   = 1;
    private final double _m      = 1.0;
    private final double _dx     = 0.1;
    private final double _dt     = 0.0001;
    private double       _lambda = 0.1;

    private State        _state;

    @Before
    public void setUp() {
        _state = new DivergingFourthOrderState(_N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(_state.getTime(), 0.0, EPSILON);
        assertEquals(_state.get0P().modSquared(), 1.0, EPSILON);
    }

    @Test
    public void testStep() {

        double tfinal = 0.01;
        while (_state.getTime() < tfinal) {
            _state.step();
            Complex calc = ((DivergingFourthOrderState) _state).get();
            System.out.println(_state.getTime() + " : " + calc.real());
        }
        _state.reset();
        assertEquals(_state.getTime(), 0.0, EPSILON);
        assertEquals(_state.get0P().modSquared(), 1.0, EPSILON);
    }

    @Test
    public void testExactComparison() {
        _state.setInteractionStrength(0); // free theory

        try {
            // Create file
            // String filename = "gnuplot/Symplectic4thOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 1;
            Complex exact = Complex.expi(-_m * tfinal);
            for (double dt = tfinal; dt > 1e-7; dt /= Math.sqrt(10)) {
                _state.setTimeStep(dt);
                _state.reset(0); // set to single zero momentum particle
                while (_state.getTime() < tfinal)
                    _state.step();
                Complex calc = ((DivergingFourthOrderState) _state).get(0);
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
