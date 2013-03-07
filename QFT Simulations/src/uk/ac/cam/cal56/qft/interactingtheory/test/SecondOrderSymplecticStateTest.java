package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.impl.EvenOrderSymplecticState;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SecondOrderSymplecticState;

public class SecondOrderSymplecticStateTest {

    private final double EPSILON = 1.0e-10;

    @Test
    public void testSymplecticity() {
        int _N = 10;
        int _Pmax = 3;
        double _m = 1.0;
        double _dx = 0.1;
        double _dt = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, 1.0);
        State _state = new SecondOrderSymplecticState(_N, _Pmax, _m, _dx, _dt, lambdas); // recalculate
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
                if (counter++ % 300 == 0)
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
        int N = 1;
        int Pmax = 1;
        double m = 1.0;
        double dx = 0.1;
        double dtInitial = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, 0.0);
        State state = new SecondOrderSymplecticState(N, Pmax, m, dx, dtInitial, lambdas); // recalculate

        try {
            // Create file
            // String filename = "gnuplot/Symplectic2ndOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 1.0;
            Complex exact = Complex.expi(-m * tfinal);
            for (double steps = 1; steps < 100000000 + 1; steps *= 10) {
                double dt = tfinal / steps;
                state.setTimeStep(dt);
                state.reset(0); // set to single zero momentum particle
                for (int i = 0; i < steps; i++)
                    state.step();
                Complex calc = ((SecondOrderSymplecticState) state).get(0);
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

    @Test
    public void testSecondOrderComparison() {
        int N = 6;
        int Pmax = 3;
        double m = 1.0;
        double dx = 0.1;
        double dtInitial = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, 0.1);
        State state = new SecondOrderSymplecticState(N, Pmax, m, dx, dtInitial, lambdas);
        State otherState = new EvenOrderSymplecticState(4, N, Pmax, m, dx, dtInitial, lambdas);

        try {
            // Create file
            // String filename = "gnuplot/Symplectic2ndOrderErrorVsDt.txt";
            // FileWriter fstream = new FileWriter(filename);
            // BufferedWriter out = new BufferedWriter(fstream);
            // System.out.println(filename);

            double tfinal = 10.0;
            for (double steps = 1000; steps < 10000 + 1; steps *= 10) {
                double dt = tfinal / steps;
                state.setTimeStep(dt);
                otherState.setTimeStep(dt);
                state.reset(0); // set to single zero momentum particle
                otherState.reset(0);
                for (int i = 0; i < steps; i++) {
                    state.step();
                    otherState.step();
                }
                Complex calc = ((SecondOrderSymplecticState) state).get(0);
                Complex otherCalc = ((EvenOrderSymplecticState) otherState).get(0);
                System.out.println(dt + " " + Math.abs(calc.minus(otherCalc).mod()));
                // out.write(dt + " " + Math.abs(exact.minus(calc).mod()));
                // out.newLine();
            }

            dtInitial = 0.001;
            state.setTimeStep(dtInitial);
            otherState.setTimeStep(dtInitial);
            state.reset(0);
            otherState.reset(0);
            tfinal = 100;
            int counter = 0;
            while (state.getTime() < tfinal) {
                state.step();
                otherState.step();
                if (counter++ % 1000 == 0) {
                    Complex calc = ((SecondOrderSymplecticState) state).get(0);
                    Complex otherCalc = ((EvenOrderSymplecticState) otherState).get(0);
                    System.out.println(state.getTime() + " " + Math.abs(calc.minus(otherCalc).mod()));
                }
            }

            // Close the output stream
            // out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testGetRemainingProbability() {
        int N = 10;
        int Pmax = 3;
        double m = 1.0;
        double dx = 0.1;
        double dt = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, 0.1);
        State state = new SecondOrderSymplecticState(N, Pmax, m, dx, dt, lambdas); // recalculate

        double tfinal = 1.0;
        while (state.getTime() < tfinal) {
            state.step();

            double exact = state.getModSquared();

            double calc = state.get0P().modSquared();
            Complex[] oneParticle = state.get1PMom();
            for (int p = 0; p < N; p++)
                calc += oneParticle[p].modSquared();
            Complex[][] twoParticles = state.get2PMom();
            for (int p = 0; p < N; p++)
                for (int q = 0; q <= p; q++)
                    calc += twoParticles[p][q].modSquared();
            calc += state.getRemainingProbability();

            assertEquals(calc, exact, EPSILON);
        }

    }
}
