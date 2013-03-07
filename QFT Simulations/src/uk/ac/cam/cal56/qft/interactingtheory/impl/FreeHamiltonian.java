package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.FockState;

// Free Hamiltonian holds the energies for all the fock states of the
// system for faster access than recalculation every time step.
public class FreeHamiltonian implements Hamiltonian {

    private int            _N;
    private int            _Pmax;
    private double         _m;
    private double         _dx;

    private double[] _energies;

    public FreeHamiltonian(int N, int Pmax, double m, double dx) {
        _N = N;
        _Pmax = Pmax;
        _m = m;
        _dx = dx;
        calculateElements();
    }

    @Override
    public void calculateElements() {
        // calculate and store energies
        _energies = new double[Combinatorics.S(_N, _Pmax)];
        FockState phi = new FockState(_N, _Pmax, _m, _dx);
        for (int n : phi)
            _energies[n] = phi.getEnergy();
    }

    @Override
    public Double get(int n, int m) {
        if (n == m)
            return _energies[n];
        else
            return null;
    }

    @Override
    public Map<Integer, Double> getRow(int n) {
        Map<Integer, Double> map = new HashMap<Integer, Double>(1);
        map.put(n, _energies[n]);
        return map;
    }

    public double getEnergy(int n) {
        return _energies[n];
    }
}
