package uk.ac.cam.cal56.qft.impl.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.impl.EvenOrderSymplecticState;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.impl.SecondOrderSymplecticState;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

public class EvenOrderSymplecticStateTest {

    // TODO: fix that this is unused.

    private final double             EPSILON  = 1.0e-10;

    private final int                _N       = 5;
    private final int                _Pmax    = 3;
    private final double             _m       = 1.0;
    private final double             _dx      = 0.1;
    private final double             _dt      = 0.01;
    private double                   _lambda  = 0.01;

    private Map<Interaction, Double> _lambdas = new HashMap<Interaction, Double>();

    @Test
    public void testConstructor() {
        _lambdas.put(Interaction.PHI_CUBED, _lambda);
        State state = new ScalarState(_N, _Pmax, _dt, _dx, _m, _lambdas, new MomentumWavePacket(_N));
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.getVacuum().modSquared(), 1.0, EPSILON);
    }

    @Test
    public void testStep() {
        WavePacket wp = new MomentumWavePacket(_N, new int[] { 0 }, new double[] { 0 });
        State state = new ScalarState(_N, _Pmax, _dt, _dx, _m, _lambdas, wp);
        assertEquals(state.getTime(), 0.0, EPSILON);
        assertEquals(state.getVacuum().modSquared(), 1.0, EPSILON);
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, _lambda);
        State otherState = new ScalarState(_N, _Pmax, _dt, _dx, _m, lambdas, wp);

        state.setWavePacket(wp);
        otherState.setWavePacket(wp);

        double tfinal = 1.0;
        while (state.getTime() < tfinal) {
            double value = ((EvenOrderSymplecticState) state).get1PMom()[0].real();
            double otherValue = ((SecondOrderSymplecticState) otherState).get1PMom()[0].real();
            System.out.println(value + " : " + otherValue);
            // assertEquals(value, otherValue, EPSILON);
            state.step();
            otherState.step();
        }
    }

    @Test
    public void testSymplecticity() {
        WavePacket wp = new MomentumWavePacket(_N, new int[] { 0 }, new double[] { 0 });
        State _state = new ScalarState(_N, _Pmax, _dt, _dx, _m, _lambdas, wp); // recalculate

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
        _lambdas.put(Interaction.PHI_CUBED, 0.0);
        WavePacket wp = new MomentumWavePacket(_N, new int[] { 0 }, new double[] { 0 });
        State state = new ScalarState(_N, _Pmax, _dt, _dx, _m, _lambdas, wp);

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
                state.reset(); // set to single zero momentum particle
                for (int i = 0; i < steps; i++)
                    state.step();
                Complex calc = ((EvenOrderSymplecticState) state).get1PMom()[0];
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
