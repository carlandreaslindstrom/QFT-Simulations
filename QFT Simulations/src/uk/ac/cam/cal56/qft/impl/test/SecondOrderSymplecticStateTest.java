package uk.ac.cam.cal56.qft.impl.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fermions.FermionState;
import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

public class SecondOrderSymplecticStateTest {

    // SCALARS
    // error vs dt: error proportional to dt^2
    @Test
    public void errorAnalysisErrorVsDt() {

        int N = 10;
        int Pmax = 1;
        double m = 1.0;
        double dx = 1.0;

        int S = ScalarFockState.S(N, Pmax);

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        double tfinal = 0.1;
        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", tfinal=" + tfinal);
        
        // find exact values
        Complex[] analyticCoeffs = initialWavePacket.getCoefficients(S);
        ScalarFockState ket = new ScalarFockState(N, Pmax, m, dx);
        for (int i : ket)
            analyticCoeffs[i] = analyticCoeffs[i].timesexpi(-tfinal * ket.getEnergy());
        
        // find integrated values
        for (int numsteps = 1; numsteps < 100000000; numsteps *= 2) {
            double dt = tfinal / numsteps;
            if(dt>1.0) continue;

            ScalarState state = new ScalarState(N, Pmax, dt, dx, m, null, initialWavePacket);
            state.step(numsteps);

            Complex[] integratedCoeffs = state.getCoefficients();

            // adding all relative errors
            double sum = 0;
            for (int i = 1; i < N + 1; i++)
                sum += (integratedCoeffs[i].minus(analyticCoeffs[i])).mod() / analyticCoeffs[i].mod();
            double avgRelativeError = sum / N;

            // System.out.println(Math.log(avgRelativeError)/Math.log(dt));
            System.out.println(dt + " " + avgRelativeError);
        }
    }

    // SCALARS
    // error vs tfinal : error proportional to tfinal
    @Test
    public void errorAnalysisErrorVsFinalTime() {

        int N = 10;
        int Pmax = 1;
        double m = 50.0;
        double dx = 1.0;

        int S = ScalarFockState.S(N, Pmax);

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        double dt = 1e-5;

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", dt=" + dt);

        for (int numsteps = 1; numsteps < 100000000; numsteps *= 2) {
            double tfinal = dt * numsteps;
            if(dt>1.0) continue;
            
            // find exact values
            Complex[] analyticCoeffs = initialWavePacket.getCoefficients(S);
            ScalarFockState ket = new ScalarFockState(N, Pmax, m, dx);
            for (int i : ket)
                analyticCoeffs[i] = analyticCoeffs[i].timesexpi(-tfinal * ket.getEnergy());

            ScalarState state = new ScalarState(N, Pmax, dt, dx, m, null, initialWavePacket);
            state.step(numsteps);

            Complex[] integratedCoeffs = state.getCoefficients();

            // adding all relative errors
            double sum = 0;
            for (int i = 1; i < N + 1; i++)
                sum += (integratedCoeffs[i].minus(analyticCoeffs[i])).mod() / analyticCoeffs[i].mod();
            double avgRelativeError = sum / N;

            System.out.println(tfinal + " " + avgRelativeError);
        }
    }

    // SCALARS
    // calculate energy over time
    @Test
    public void errorAnalysisEnergyVsTime() {

        int N = 10;
        int Pmax = 3;
        double m = 1.0;
        double dx = 1.0;

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        int numData = 1000;
        int stepsPerData = 1000;
        double dt = 0.0002;

        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        //lambdas.put(Interaction.PHI_CUBED, 0.1);
        ScalarState state = new ScalarState(N, Pmax, dt, dx, m, lambdas, initialWavePacket);

        double startEnergy = state.getTotalEnergy();

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", dt=" + dt);

        for (int step = 0; step < numData; step++) {
            state.step(stepsPerData);
            double t = step * stepsPerData * dt;
            double relError = (state.getTotalEnergy() - startEnergy) / startEnergy;
            System.out.println(t + " " + relError + " " + (state.getModSquared()-1));
            if(step % 2 == 0) state.step(step);
        }
    }
    

    // SCALARS
    // calculate energy over time
    @Test
    public void errorAnalysisEnergyVsTimeInteractingTheory() {

        int N = 16;
        int Pmax = 2;
        double m = 1.0;
        double dx = 0.1;

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        int numData = 1000;
        int stepsPerData = 100;
        double dt = 0.002;

        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_SQUARED, 0.1);
        lambdas.put(Interaction.PHI_CUBED, 0.1);
        lambdas.put(Interaction.PHI_FOURTH, 0.1);
        ScalarState state = new ScalarState(N, Pmax, dt, dx, m, lambdas, initialWavePacket);

        double startEnergy = state.getTotalEnergy();

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", dt=" + dt);

        for (int step = 0; step < numData; step++) {
            state.step(stepsPerData);
            double t = step * stepsPerData * dt;
            double relError = (state.getTotalEnergy() - startEnergy) / startEnergy;
            System.out.println(t + " " + relError + " " + (state.getModSquared()-1));
        }
    }

    // FERMIONS
    // error vs dt: error proportional to dt^2
    @Test
    public void errorAnalysisErrorVsDtFermions() {

        int N = 10;
        int Pmax = 1;
        double m = 1.0;
        double dx = 1.0;

        int S = FermionFockState.S(N, Pmax);

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        double tfinal = 10;

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", tfinal=" + tfinal);

        for (int numsteps = 4; numsteps < 100000000; numsteps *= 2) {
            double dt = tfinal / numsteps;

            // find exact values
            Complex[] analyticCoeffs = initialWavePacket.getCoefficients(S);
            FermionFockState ket = new FermionFockState(N, Pmax, m, dx);
            for (int i : ket)
                analyticCoeffs[i] = analyticCoeffs[i].timesexpi(-tfinal * ket.getEnergy());

            FermionState state = new FermionState(N, Pmax, dt, dx, m, null, initialWavePacket);
            state.step(numsteps);

            Complex[] integratedCoeffs = state.getCoefficients();

            // adding all relative errors
            double sum = 0;
            for (int i = 1; i < N + 1; i++)
                sum += (integratedCoeffs[i].minus(analyticCoeffs[i])).mod() / analyticCoeffs[i].mod();
            double avgRelativeError = sum / N;

            // System.out.println(Math.log(avgRelativeError)/Math.log(dt));
            System.out.println(dt + " " + avgRelativeError);
        }
    }
    
    // FERMIONS
    // error vs tfinal : error proportional to tfinal
    @Test
    public void errorAnalysisErrorVsFinalTimeFermions() {

        int N = 10;
        int Pmax = 1;
        double m = 1.0;
        double dx = 1.0;

        int S = FermionFockState.S(N, Pmax);

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        double dt = 0.0000001;

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", dt=" + dt);

        for (int numsteps = 4; numsteps < 100000000; numsteps *= 2) {
            double tfinal = dt * numsteps;

            // find exact values
            Complex[] analyticCoeffs = initialWavePacket.getCoefficients(S);
            FermionFockState ket = new FermionFockState(N, Pmax, m, dx);
            for (int i : ket)
                analyticCoeffs[i] = analyticCoeffs[i].timesexpi(-tfinal * ket.getEnergy());

            FermionState state = new FermionState(N, Pmax, dt, dx, m, null, initialWavePacket);
            state.step(numsteps);

            Complex[] integratedCoeffs = state.getCoefficients();

            // adding all relative errors
            double sum = 0;
            for (int i = 1; i < N + 1; i++)
                sum += (integratedCoeffs[i].minus(analyticCoeffs[i])).mod() / analyticCoeffs[i].mod();
            double avgRelativeError = sum / N;

            System.out.println(tfinal + " " + avgRelativeError);
        }
    }
    
    // FERMIONS
    // calculate energy over time
    @Test
    public void errorAnalysisEnergyVsTimeFermions() {

        int N = 10;
        int Pmax = 3;
        double m = 1.0;
        double dx = 1.0;

        WavePacket initialWavePacket = new MomentumWavePacket(N, new int[] { N / 4 }, new double[] { 0.0 }, 0.5);

        int numData = 1000;
        int stepsPerData = 500;
        double dt = 0.0001;

        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        //lambdas.put(Interaction.PHI_CUBED, 0.1);
        FermionState state = new FermionState(N, Pmax, dt, dx, m, lambdas, initialWavePacket);

        double startEnergy = state.getTotalEnergy();

        System.out.println("# N=" + N + ", m=" + m + ", dx=" + dx + ", dt=" + dt);

        for (int step = 0; step < numData; step++) {
            state.step(stepsPerData);
            double t = step * stepsPerData * dt;
            double relError = (state.getTotalEnergy() - startEnergy) / startEnergy;
            System.out.println(t + " " + relError);
        }
    }
}
