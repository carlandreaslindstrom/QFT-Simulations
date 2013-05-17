package uk.ac.cam.cal56.qft.fockspace.impl;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.fockspace.SingleFieldFockState;

public class ScalarFockState extends SingleFieldFockState {

    public ScalarFockState(int N, int Pmax, double m, double dx) {
        super(N, m, dx);
        _S = S(N, Pmax);
    }
    
    // alternative constructor if Pmax not known
    public ScalarFockState(int N, double m, double dx, int S) {
        super(N, m, dx);
        _S = S;
    }

    // recursive function used in incrementing particle entries
    @Override
    protected void particleIncrementer(int index) {
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

    @Override
    protected void setToFirstStateOfParticleNumber(int P) {
        _particles.clear();
        for (int i = 0; i < P; i++)
            _particles.add(0); // fill with zeros
    }

    /**** STATIC FUNCTIONS ****/

    // Number of distinct Fock states with N lattice points and maximally P particles
    // S(N,P) = Sum(Choose(N+m-1,m), {m,0,P})
    public static int S(int N, int P) {
        int sum = 0;
        for (int m = 0; m <= P; m++)
            sum += sigma(N, m);
        return sum;
    }

    // number of states with a given particle number m if N lattice points
    public static int sigma(int N, int m) {
        return Combinatorics.choose(N + m - 1, m);
    }

    // Combinatoric factor from a given momentum from sandwiching a state of l particles
    // in that momentum with n creation operators and m annihilation operators. Complete
    // combinatoric factor is obtained by the product these for all momenta.
    // F_p(l,n,m) = sqrt( (Product[a=1->n](l-m+a)) * (Product[b=1->m](l+1-b) )
    public static double F_p(int l_p, int n_p, int m_p, double L2Ep) {
        if (m_p > l_p)
            return 0;
        double squaredProduct = 1;
        for (int a = 1; a <= n_p; a++)
            squaredProduct *= L2Ep * (l_p - m_p + a);
        for (int b = 1; b <= m_p; b++)
            squaredProduct *= L2Ep * (l_p + 1 - b);
        return Math.sqrt(squaredProduct);
    }

}
