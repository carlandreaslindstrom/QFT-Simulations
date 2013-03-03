package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.cam.cal56.graphics.DensityPlot;
import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FFT;
import uk.ac.cam.cal56.maths.FourierTransform;
import uk.ac.cam.cal56.qft.interactingtheory.FreeHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.InteractionHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public abstract class BaseState {

    protected final int                    _N;             // number of lattice points
    protected double                       _dt;            // time step
    protected final int                    _S;             // S(N,P) = total number of Fock state coefficients
    protected final FreeHamiltonian        _Hfree;         // free theory Hamiltonian
    protected final InteractionHamiltonian _Hint;          // interaction Hamiltonian
    protected double                       _lambda;        // interaction strength

    protected double                       _time;
    protected Complex[]                    _c;             // {c_n(t)}

    private FourierTransform               _ft = new FFT();

    public BaseState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        // initialise parameters
        _N = N;
        _dt = dt;
        _lambda = lambda;
        _S = Combinatorics.S(N, Pmax);

        // set coefficients
        _c = new Complex[_S];

        // calculate free theory energies and interaction amplitudes
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx);
        _Hint = new FastInteractionHamiltonian(N, Pmax, m, dx, Interaction.PHI_THIRD);
        _Hint.calculateElements(); // calculate elements in the Interaction Hamiltonian

        // set coefficients (to pure vacuum) and do first step
        reset();
    }

    protected abstract void firstStep();

    public abstract void step();

    public void reset(int... particles) {
        _time = 0.0;

        List<Integer> ls = new ArrayList<Integer>();
        for (int p : particles)
            ls.add(p);
        Integer n = StateLabelling.index(ls, _N);
        if (n == null || n >= _S)
            n = 0;
        
        // set one particle
        for (int i = 0; i < _S; i++)
            _c[i] = Complex.zero();
        _c[n] = Complex.one();

        firstStep();
    }

    public double getTime() {
        return _time;
    }

    public double getModSquared() {
        double sum = 0.0;
        for (int n = 0; n < _S; n++)
            sum += _c[n].modSquared();
        return sum;
    }

    public Complex get(int... particles) {
        List<Integer> ls = new ArrayList<Integer>();
        for (int p : particles)
            ls.add(p);
        Integer n = StateLabelling.index(ls, _N);
        if (n == null || n >= _S)
            return null;
        else
            return _c[n];

    }

    public double get0P() {
        return _c[0].modSquared();
    }

    public double[] get1PMomenta() {
        double[] probs = new double[_N];
        for (int p = 0; p < _N; p++)
            probs[p] = _c[p + 1].modSquared(); // +1 in order to avoid the 0P vacuum state
        return probs;
    }

    public double[][] get2PMomenta() {
        double[][] probs = new double[_N][_N];
        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                double value = _c[StateLabelling.index(Arrays.asList(p, q), _N)].modSquared();
                probs[p][q] = value; // +(1+N) in order to avoid 0P and 1P states
                if (p != q)
                    probs[q][p] = value;
            }
        return probs;
    }

    public double[] get1PPositions() {
        double[] probs = new double[_N];
        Complex[] transformed = _ft.transform(Arrays.copyOfRange(_c, 1, _N + 1));
        for (int p = 0; p < _N; p++)
            probs[p] = transformed[p].modSquared();
        return probs;
    }

    public double[][] get2PPositions() {
        Complex[][] coeffs = new Complex[_N][_N];

        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                Complex coeff = _c[StateLabelling.index(Arrays.asList(p, q), _N)];
                coeffs[p][q] = coeff;
                if (p != q)
                    coeffs[q][p] = coeff;
            }
        double[][] probs = new double[_N][_N];
        coeffs = _ft.transform2D(coeffs);
        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                double value = coeffs[p][q].modSquared();
                probs[p][q] = value;
                if (p != q)
                    probs[q][p] = value;
            }
        return probs;
    }

    public void setInteractionStrength(double lambda) {
        _lambda = lambda;
        firstStep();
    }

    public void setTimeStep(double dt) {
        _dt = dt;
        firstStep();
    }

    public void updatePlots(Plot p0m, Plot p0p, Plot p1m, Plot p1p, DensityPlot p2m, DensityPlot p2p) {
        // TODO Auto-generated method stub
    }
}
