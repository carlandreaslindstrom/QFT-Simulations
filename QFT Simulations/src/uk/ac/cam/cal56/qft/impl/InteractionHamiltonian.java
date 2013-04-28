package uk.ac.cam.cal56.qft.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.fockspace.FockState;

public abstract class InteractionHamiltonian implements Hamiltonian {

    protected final double               EPSILON = 1e-13;

    protected List<Map<Integer, Double>> _elements;
    protected int                        _N;
    protected final int                  _S;
    protected final double               _m;
    protected final double               _dx;
    protected final double               L;

    protected Interaction                _interaction;

    protected FockState                  _ket;                    // used internally for stepping

    protected double[]                   _E      = new double[_N]; // energy buffer

    protected InteractionHamiltonian(int N, int S, double mass, double dx, Interaction interaction) {
        _N = N;
        _S = S;
        _m = mass;
        _dx = dx;
        L = N * dx;
        _interaction = interaction;
        _elements = new ArrayList<Map<Integer, Double>>(_S);
        bufferEnergies();
    }

    private void bufferEnergies() {
        _E = new double[_N];
        for (int p = 0; p < _N; p++)
            _E[p] = FockState.E_p(p, _N, _m, _dx);
    }

    @Override
    public void calculateElements() {
        for (int n : _ket) {
            _elements.add(n, new HashMap<Integer, Double>()); // initialise the row Map
            calculateRow(n);
        }
    }

    protected abstract void calculateRow(int n);

    @Override
    public Double get(int n, int m) {
        return getRow(n).get(m);
    }

    @Override
    public Map<Integer, Double> getRow(int n) {
        return _elements.get(n);
    }

}
