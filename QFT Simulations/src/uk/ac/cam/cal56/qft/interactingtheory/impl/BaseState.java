package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public abstract class BaseState implements State {

    protected final int                     _N;           // number of lattice points
    protected double                        _dt;          // time step
    protected final int                     _S;           // S(N,Pmax) = number of coefficients
    protected FreeHamiltonian               _Hfree;       // free theory Hamiltonian
    protected Map<Interaction, Hamiltonian> _hamiltonians; // interaction Hamiltonians
    protected Map<Interaction, Double>      _lambdas;     // interaction strength

    protected double                        _time;
    protected Complex[]                     _c;           // {c_n(t)}

    public BaseState(int N, int Pmax, double m, double dx, double dt, Map<Interaction, Double> lambdas,
            int... particleMomenta) {
        // initialise parameters
        _N = N;
        _dt = dt;
        _S = Combinatorics.S(N, Pmax);
        _lambdas = lambdas;
        _hamiltonians = new HashMap<Interaction, Hamiltonian>();

        // set coefficients
        _c = new Complex[_S];

        // calculate free theory energies
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx);

        // add interaction Hamiltonians
        for (Entry<Interaction, Double> lambda : lambdas.entrySet())
            _hamiltonians.put(lambda.getKey(), new InteractionHamiltonian(N, Pmax, m, dx, lambda.getKey()));

        // calculate elements of these
        // TODO: make this part report on its progress (useful if multithreaded)
        for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet())
            h.getValue().calculateElements();

        // set coefficients (to pure vacuum) and do first step
        reset(particleMomenta);
    }

    protected abstract void firstStep();

    public abstract void step();

    @Override
    public void step(int numSteps) {
        for (int i = 0; i < numSteps; i++)
            step();
    }

    @Override
    public void reset(int... particleMomenta) {
        _time = 0.0;
        setWavePackets(_N / 8.0, particleMomenta);
        firstStep();
    }

    public void setWavePackets(double sigma, int... particleMomenta) {

        // TODO: implement something for more than 2 particles
        // VACUUM
        if (particleMomenta.length == 0 || particleMomenta.length > 2) {
            // set all coefficients = 0, apart from vacuum = 1
            _c[0] = Complex.one();
            for (int i = 1; i < _S; i++)
                _c[i] = Complex.zero();
        }
        // ONE PARTICLE WAVE PACKET
        else if (particleMomenta.length == 1) {
            // calculate gaussian and normalisation
            double norm = 0.0;
            double[] values = new double[_N];
            for (int i = 0; i < _N; i++) {
                double z1 = (i - particleMomenta[0]) / (sigma);
                double z2 = ((i - _N - particleMomenta[0])) / (sigma);
                double value = Math.exp(-z1 * z1 / 2) + Math.exp(-z2 * z2 / 2);
                values[i] = value;
                norm += value;
            }
            norm = Math.sqrt(norm);

            // set all coefficients = 0, apart from 1 particle states
            _c[0] = Complex.zero();
            for (int i = 0; i < _N; i++)
                _c[i + 1] = Complex.one().times(values[i] / norm);
            for (int i = _N + 1; i < _S; i++)
                _c[i] = Complex.zero();
        }

        // TWO PARTICLE WAVE PACKETS
        // else if (particleMomenta.length == 2) { }
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
        if (_S <= 1)
            return null;
        return Arrays.copyOfRange(_c, 1, _N + 1);
    }

    @Override
    public Complex[][] get2PMom() {
        if (_S <= 1 + _N)
            return null;
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
    public void setInteractionStrength(Interaction interaction, double lambda) {
        _lambdas.put(interaction, lambda);
        firstStep();
    }

    @Override
    public void setTimeStep(double dt) {
        _dt = dt;
        firstStep();
    }

}
