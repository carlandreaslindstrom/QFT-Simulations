package uk.ac.cam.cal56.qft.fockspace.impl;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.fockspace.SingleFieldFockState;

public class ComponentFockState extends SingleFieldFockState {

    private int _untilNextParticleNumber = 0; // used in the isLastParticleNumberState() function

    public ComponentFockState(int N, int Pmax, double m, double dx) {
        super(N, m, dx);
        _S = S(N, Pmax);
    }

    // iterator stepping function.
    @Override
    public Integer next() {
        if (_label > -1) {
            if (_label == 0) {
                _particles.add(0);
                _untilNextParticleNumber = _N;
            }
            else
                particleIncrementer(getParticleNumber() - 1);
            _untilNextParticleNumber--;
        }

        _label++;
        return _label;
    }

    // recursive function used in incrementing particle entries
    @Override
    protected void particleIncrementer(int index) {
        int particle = _particles.get(index);
        particle++;
        if (particle >= _N - getParticleNumber() + 1 + index) {
            if (index == 0) {
                particle = 0;
                int nextP = getParticleNumber();
                _particles.add(nextP);
                _untilNextParticleNumber = sigma(_N, nextP + 1);
            }
            else {

                particleIncrementer(index - 1);
                particle = _particles.get(index - 1) + 1;
            }
        }
        _particles.set(index, particle);
    }

    // sets the state to the first state particle number P
    @Override
    protected void setToFirstStateOfParticleNumber(int P) {
        _particles.clear();
        for (int i = 0; i < P; i++)
            _particles.add(i); // fill with lowest distinct numbers (due to Pauli exclusion principle)
        _label = S(_N, P - 1);
        _untilNextParticleNumber = sigma(_N, P) - 1;
    }

    // tests if current state is the last state before another particle is added
    protected boolean isLastParticleNumberState() {
        return _untilNextParticleNumber == 0;
    }

    /**** STATIC FUNCTIONS ****/

    // Number of distinct fermion component Fock states with N lattice points and maximally P particles
    // S(N,P) = Sum(Choose(N,m), {m,0,P})
    public static int S(int N, int P) {
        int sum = 0;
        for (int m = 0; m <= Math.min(P, N); m++)
            sum += sigma(N, m);
        return sum;
    }

    // number of states with a given particle number m if N lattice points
    public static int sigma(int N, int p) {
        return Combinatorics.choose(N, p);
    }

}
