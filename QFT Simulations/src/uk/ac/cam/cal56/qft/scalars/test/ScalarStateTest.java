package uk.ac.cam.cal56.qft.scalars.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.impl.SecondOrderSymplecticState;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

public class ScalarStateTest {

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
        State _state = new ScalarState(_N, _Pmax, _dt, _dx, _m, lambdas, new MomentumWavePacket(_N)); // recalculate

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
        State state = new ScalarState(N, Pmax, dtInitial, dx, m, lambdas, new MomentumWavePacket(N)); // recalculate

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
                state.reset(); // set to single zero momentum particle
                for (int i = 0; i < steps; i++)
                    state.step();
                Complex calc = ((SecondOrderSymplecticState) state).get1PMom()[0];
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
    public void testGetRemainingProbability() {
        int N = 10;
        int Pmax = 3;
        double m = 1.0;
        double dx = 0.1;
        double dt = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_CUBED, 0.1);
        State state = new ScalarState(N, Pmax, dt, dx, m, lambdas, new MomentumWavePacket(N)); // recalculate

        double tfinal = 1.0;
        while (state.getTime() < tfinal) {
            state.step();

            double exact = state.getModSquared();

            double calc = state.getVacuum().modSquared();
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

    @Test
    public void testEigenEnergyDifference() {
        int N = 1;
        int Pmax = 32;
        double mass = 1.0;
        double dx = 0.1;
        double dt = 0.01;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        for (double lambda = 1e-6; lambda < 20; lambda *= Math.sqrt(10)) {
            lambdas.clear();
            lambdas.put(Interaction.PHI_SQUARED, lambda);
            State state = new ScalarState(N, Pmax, dt, dx, mass, lambdas, new MomentumWavePacket(N));

            state.setToGroundState();
            double groundStateEnergy = state.getTotalEnergy();
            state.setToFirstState();
            double firstStateEnergy = state.getTotalEnergy();

            double measuredEffectiveMass = firstStateEnergy - groundStateEnergy;

            double predictedEffectiveMass = Math.sqrt(mass * mass + 2 * lambda);

            System.out.println(lambda + " " + Math.abs(measuredEffectiveMass - predictedEffectiveMass) + " " +
                               measuredEffectiveMass);
        }
    }

    @Test
    public void testEigenEnergyDifferencePmaxAndNDependence() {
        int N = 4;
        double mass = 1.0;
        double dx = 0.1;
        double dt = 0.01;
        double lambda = 1e-3;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        for (int Pmax = 1; Pmax < 100; Pmax++) {
            lambdas.clear();
            lambdas.put(Interaction.PHI_SQUARED, lambda);
            State state = new ScalarState(N, Pmax, dt, dx, mass, lambdas, new MomentumWavePacket(N));

            state.setToGroundState();
            double groundStateEnergy = state.getTotalEnergy();
            state.setToFirstState();
            double firstStateEnergy = state.getTotalEnergy();

            double measuredEffectiveMass = firstStateEnergy - groundStateEnergy;

            double predictedEffectiveMass = Math.sqrt(mass * mass + 2 * lambda);

            System.out.println(Pmax + " " + Math.abs(measuredEffectiveMass - predictedEffectiveMass) + " " +
                               measuredEffectiveMass);
        }
    }
    
    @Test
    public void testEigenEnergyDifferenceNDependence() {
        int Pmax = 2;
        double mass = 1.0;
        double dx = 1.0;
        double dt = 0.01;
        double lambda = 1e-3;
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        for (int N = 1; N < 100; N++) {
            lambdas.clear();
            lambdas.put(Interaction.PHI_SQUARED, lambda);
            State state = new ScalarState(N, Pmax, dt, dx, mass, lambdas, new MomentumWavePacket(N));

            state.setToGroundState();
            double groundStateEnergy = state.getTotalEnergy();
            state.setToFirstState();
            double firstStateEnergy = state.getTotalEnergy();

            double measuredEffectiveMass = firstStateEnergy - groundStateEnergy;

            double predictedEffectiveMass = Math.sqrt(mass * mass + 2 * lambda);

            System.out.println(N + " " + Math.abs(measuredEffectiveMass - predictedEffectiveMass) + " " +
                               measuredEffectiveMass);
        }
    }

}
