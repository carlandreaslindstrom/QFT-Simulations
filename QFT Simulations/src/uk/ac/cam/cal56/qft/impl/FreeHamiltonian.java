package uk.ac.cam.cal56.qft.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.fockspace.FockState;

// Free Hamiltonian holds the energies for all the fock states of the
// system for faster access than recalculation every time step.
public class FreeHamiltonian implements Hamiltonian {

    private FockState _fockstate;
    private double    _max = Double.MIN_VALUE;

    private double[]  _energies;

    public FreeHamiltonian(int N, int Pmax, double m, double dx, Class<? extends FockState> fockspace) {
        try {
            Constructor<? extends FockState> c = fockspace.getConstructor(int.class, int.class, double.class,
                                                                          double.class);
            _fockstate = c.newInstance(N, Pmax, m, dx);
            _energies = new double[_fockstate.getS()];
        }
        catch (Exception e) {
            System.out.println("Free hamiltonian fail");
        }
        calculateElements();
    }

    @Override
    public void calculateElements() {
        // calculate and store energies
        _fockstate.remove();
        for (int n : _fockstate) {
            double energy = _fockstate.getEnergy();
            _energies[n] = energy;
            if (energy > _max)
                _max = energy;
        }

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

    public double getMaxEnergy() {
        return _max;
    }

    public double getEnergy(int n) {
        return _energies[n];
    }
}
