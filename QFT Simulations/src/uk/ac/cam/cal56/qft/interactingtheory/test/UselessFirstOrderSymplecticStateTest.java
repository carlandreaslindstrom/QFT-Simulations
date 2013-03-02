package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.FirstOrderSymplecticState;
import uk.ac.cam.cal56.qft.interactingtheory.impl.UselessFirstOrderSymplecticState;

public class UselessFirstOrderSymplecticStateTest {

    private final double EPSILON = 1.0e-10;

    private final int    _N      = 1;
    private final int    _Pmax   = 1;
    private final double _m      = 1.0;
    private final double _dx     = 0.1;
    private final double _dt     = 0.01;
    private double       _lambda = 0.01;

    @Test
    public void testConstructor() {
        State state = new UselessFirstOrderSymplecticState(3, _N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.get0P(), 1.0, EPSILON);
    }

    @Test
    public void testStep() {
        State state = new UselessFirstOrderSymplecticState(4, _N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.get0P(), 1.0, EPSILON);

        State otherState = new FirstOrderSymplecticState(_N, _Pmax, _m, _dx, _dt, _lambda);

        state.reset(0);
        otherState.reset(0);

        double tfinal = 1.0;
        while (state.getTime() < tfinal) {
            double value = ((UselessFirstOrderSymplecticState) state).get(0).real();
            double otherValue = ((FirstOrderSymplecticState) otherState).get(0).real();
            System.out.println(value + " : " + otherValue);
            // assertEquals(value, otherValue, EPSILON);
            state.step();
            otherState.step();
        }
    }

    @Test
    public void testExactComparison() {
        State state = new UselessFirstOrderSymplecticState(5, _N, _Pmax, _m, _dx, _dt, 0.0);

        try {
            // Create file
            // String filename = "gnuplot/SymplecticNthOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 0.1;
            Complex exact = Complex.expi(-_m * tfinal);
            for (double dt = tfinal; dt > 1e-7; dt /= Math.sqrt(2)) {
                state.setTimeStep(dt);
                state.reset(0); // set to single zero momentum particle
                while (state.getTime() < tfinal)
                    state.step();
                Complex calc = ((UselessFirstOrderSymplecticState) state).get(0);
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
