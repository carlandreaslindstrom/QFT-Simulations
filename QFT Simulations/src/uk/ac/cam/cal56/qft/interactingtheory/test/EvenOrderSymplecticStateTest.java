package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.FirstOrderSymplecticState;
import uk.ac.cam.cal56.qft.interactingtheory.impl.EvenOrderSymplecticState;

public class EvenOrderSymplecticStateTest {

    private final double EPSILON = 1.0e-10;

    private final int    _N      = 5;
    private final int    _Pmax   = 3;
    private final double _m      = 1.0;
    private final double _dx     = 0.1;
    private final double _dt     = 0.01;
    private double       _lambda = 0.01;

    @Test
    public void testConstructor() {
        State state = new EvenOrderSymplecticState(3, _N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.get0P().modSquared(), 1.0, EPSILON);
    }

    @Test
    public void testStep() {
        State state = new EvenOrderSymplecticState(4, _N, _Pmax, _m, _dx, _dt, _lambda);
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.get0P().modSquared(), 1.0, EPSILON);

        State otherState = new FirstOrderSymplecticState(_N, _Pmax, _m, _dx, _dt, _lambda);

        state.reset(0);
        otherState.reset(0);

        double tfinal = 1.0;
        while (state.getTime() < tfinal) {
            double value = ((EvenOrderSymplecticState) state).get(0).real();
            double otherValue = ((FirstOrderSymplecticState) otherState).get(0).real();
            System.out.println(value + " : " + otherValue);
            // assertEquals(value, otherValue, EPSILON);
            state.step();
            otherState.step();
        }
    }

    @Test
    public void testSymplecticity() {
        State _state = new EvenOrderSymplecticState(8, _N, _Pmax, _m, _dx, _dt, _lambda); // recalculate
        _state.reset(0);

        try {
            // Create file
            // String filename = "gnuplot/Symplectic2ndOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 1000.0;
            int counter = 0;
            while (_state.getTime() < tfinal) {
                _state.step();
                if (counter++ % 1000 == 0)
                    System.out.println(_state.getTime() + " " + _state.getModSquared());
            }
            // out.write(dt + " " + Math.abs(exact.minus(calc).mod()));
            // out.newLine();

            // Close the output stream
            // out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testExactComparison() {
        State state = new EvenOrderSymplecticState(2, _N, _Pmax, _m, _dx, _dt, 0.0);

        try {
            // Create file
            // String filename = "gnuplot/SymplecticNthOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 1.0;
            Complex exact = Complex.expi(-_m * tfinal);
            for (double steps = 1; steps < 100000000 + 1; steps *= 10) {
                double dt = tfinal / steps;
                state.setTimeStep(dt);
                state.reset(0); // set to single zero momentum particle
                for (int i = 0; i < steps; i++)
                    state.step();
                Complex calc = ((EvenOrderSymplecticState) state).get(0);
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
