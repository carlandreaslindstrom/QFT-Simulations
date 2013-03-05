package uk.ac.cam.cal56.qft.statelabelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.ac.cam.cal56.maths.Combinatorics;

public class FockState implements Iterator<Integer>, Iterable<Integer> {

    private List<Integer> _particles = new ArrayList<Integer>(); // particle entries
    private int           _label     = -1;                      // starts at -1
    private int           _N;
    private double        _m;
    private double        _dx;
    private int           _S;

    public FockState(int N, int Pmax, double m, double dx) {
        _N = N;
        _m = m;
        _dx = dx;
        _S = Combinatorics.S(N, Pmax);
    }

    // E_p = sqrt(m^2 + (2/dx)^2*sin(p*dx/2)^2 )
    public static double E_p(int n, int N, double m, double dx) { // i = momentum number
        double pterm = (2.0 / dx) * Math.sin(Math.PI * n / N);
        return Math.sqrt(m * m + pterm * pterm);
    }

    // total energy
    public double getEnergy() {
        double sum = 0;
        for (int p : _particles)
            sum += E_p(p, _N, _m, _dx); // E = Sum( E_p )
        return sum;
    }

    // total momentum number
    public int getMomentumNumber() {
        int sum = 0;
        for (int p : _particles)
            sum += p;
        return sum % _N;
    }

    // particle number
    public int getParticleNumber() {
        return _particles.size();
    }

    // label
    public int getLabel() {
        return _label;
    }

    // ladder operator exponent for momentum p
    public int l_p(int p) {
        return Collections.frequency(_particles, p);
    }

    // particle entries as list (for unit testing)
    public List<Integer> toList() {
        return _particles;
    }

    // ITERATOR FUNCTIONS

    @Override
    public Integer next() {
        if (_label > -1) {
            if (!hasNext())
                return null;
            if (getParticleNumber() == 0)
                _particles.add(0);
            else
                particleIncrementer(getParticleNumber() - 1);
        }
        _label++;
        return _label;
    }

    @Override
    public boolean hasNext() {
        return _label < (_S - 1);
    }

    @Override
    public void remove() {
        _particles.clear();
        _label = -1;
    }

    @Override
    public Iterator<Integer> iterator() {
        return this;
    }

    // recursive function used in incrementing particle entries
    private void particleIncrementer(int index) {
        int particle = _particles.get(index);
        particle++;
        if (particle == _N) {
            if (index == 0) {
                particle = 0;
                _particles.add(0);
            }
            else {
                particleIncrementer(index - 1);
                particle = _particles.get(index - 1);
            }

        }
        _particles.set(index, particle);
    }
}
