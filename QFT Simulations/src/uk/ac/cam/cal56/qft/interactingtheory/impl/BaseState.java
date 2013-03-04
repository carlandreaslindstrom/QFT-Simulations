package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.FreeHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.InteractionHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public abstract class BaseState implements State {

    protected final int                    _N;     // number of lattice points
    protected double                       _dt;    // time step
    protected final int                    _S;     // S(N,P) = total number of Fock state coefficients
    protected final FreeHamiltonian        _Hfree; // free theory Hamiltonian
    protected final InteractionHamiltonian _Hint;  // interaction Hamiltonian
    protected double                       _lambda;        // interaction strength

    protected double                       _time;
    protected Complex[]                    _c;     // {c_n(t)}

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

    @Override
    public void step(int numSteps) {
        for (int i = 0; i < numSteps; i++)
            step();
    }

    @Override
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

    @Override
    public double getTime() {
        return _time;
    }

    @Override
    public double getModSquared() {
        double sum = 0.0;
        for (int n = 0; n < _S; n++)
            sum += _c[n].modSquared();
        return sum;
    }

    // for unit tests
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

    @Override
    public Complex get0P() {
        return _c[0];
    }

    @Override
    public Complex[] get1PMom() {
        return Arrays.copyOfRange(_c, 1, _N + 1);
    }

    @Override
    public Complex[][] get2PMom() {
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                Complex value = _c[StateLabelling.index(Arrays.asList(p, q), _N)];
                ampls[p][q] = value;
                if (p != q)
                    ampls[q][p] = value;
            }
        return ampls;
    }

    @Override
    public double getRemainingProbability() {
        double probSquared = 0;
        for (int n = Combinatorics.S(_N, 2); n < _S; n++)
            probSquared += _c[n].modSquared();
        return probSquared;
    }

    @Override
    public void setInteractionStrength(double lambda) {
        _lambda = lambda;
        firstStep();
    }

    @Override
    public void setTimeStep(double dt) {
        _dt = dt;
        firstStep();
    }

}
