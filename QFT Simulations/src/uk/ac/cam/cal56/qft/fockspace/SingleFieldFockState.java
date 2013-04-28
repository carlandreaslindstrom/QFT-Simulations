package uk.ac.cam.cal56.qft.fockspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SingleFieldFockState extends FockState {

    protected List<Integer> _particles = new ArrayList<Integer>(); // particle entries

    protected SingleFieldFockState(int N, double m, double dx) {
        super(N, m, dx);
    }

    public String toString() {
        return _particles.toString();
    }

    protected abstract void setToFirstStateOfParticleNumber(int P);

    // ladder operator exponent for momentum p
    public int l_p(int p) {
        return Collections.frequency(_particles, p);
    }

    // particle entries as list (for unit testing)
    public List<Integer> getParticles() {
        return _particles;
    }

    // total energy
    @Override
    public double getEnergy() {
        double sum = 0;
        for (int p : _particles)
            sum += E_p(p, _N, _m, _dx); // E = Sum( E_p )
        return sum;
    }

    // total momentum number
    @Override
    public int getMomentumNumber() {
        int sum = 0;
        for (int p : _particles)
            sum += p;
        return sum % _N;
    }

    // particle number
    @Override
    public int getParticleNumber() {
        return _particles.size();
    }

    /**** Iterator implementation ****/

    // iterator stepping function
    @Override
    public Integer next() {
        if (_label > -1) {
            if (_label == 0)
                _particles.add(0);
            else
                particleIncrementer(getParticleNumber() - 1);
        }
        _label++;
        return _label;
    }

    protected abstract void particleIncrementer(int index);

    // reset state
    @Override
    public void remove() {
        _particles.clear();
        _label = -1;
    }

}
