package uk.ac.cam.cal56.qft.fockspace.impl;

import java.util.List;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.fockspace.FockState;

// 1D fermion fields have two real spinor components. These two
// degrees of freedom represent particles and antiparticles.

public class FermionFockState extends FockState {

    private ComponentFockState _particles;
    private ComponentFockState _antiparticles;

    public FermionFockState(int N, int Pmax, double m, double dx) {
        super(N, m, dx);

        // initialise two spinor components
        _particles = new ComponentFockState(N, Pmax, m, dx);
        _antiparticles = new ComponentFockState(N, Pmax, m, dx);

        // set number of states
        _S = S(N, Pmax);
    }

    // total energy (sum of particle and antiparticle energies)
    @Override
    public double getEnergy() {
        return _particles.getEnergy() + _antiparticles.getEnergy();
    }

    // total momentum number (sum of particle and antiparticle momenta [mod N])
    @Override
    public int getMomentumNumber() {
        return (_particles.getMomentumNumber() + _antiparticles.getMomentumNumber()) % _N;
    }

    // total particles number (sum of particles and antiparticles)
    @Override
    public int getParticleNumber() {
        return _particles.getParticleNumber() + _antiparticles.getParticleNumber();
    }

    public List<Integer> getParticles() {
        return _particles.getParticles();
    }

    public List<Integer> getAntiParticles() {
        return _antiparticles.getParticles();
    }

    // stepping function for two fields (complicated, follow comments carefully to understand)
    @Override
    public Integer next() {
        if (_label > -1) {

            int totalP = getParticleNumber(); // total particle number
            int particleP = _particles.getParticleNumber(); // normal particle number

            if (_antiparticles.isLastParticleNumberState()) { // if at last state before increasing antiparticle number

                if (_particles.isLastParticleNumberState()) { // if at last state before increasing particle number

                    int lastParticleP = Math.max(0, totalP - _N);
                    if (particleP == lastParticleP) { // if exhausted all permutations within total particle number

                        if (totalP >= _N) { // if enough particles to fill lattice completely
                            _particles.setToFirstStateOfParticleNumber(_N); // fill completely with particles
                            _antiparticles.setToFirstStateOfParticleNumber(lastParticleP + 1); // rest = antiparticles
                        }
                        else { // if not enough to fill lattice completely
                            _particles.setToFirstStateOfParticleNumber(totalP + 1); // add one more particle
                            _antiparticles.setToFirstStateOfParticleNumber(0); // remove all antiparticles
                        }
                    }
                    else { // if still permutations left within total particle number

                        _particles.setToFirstStateOfParticleNumber(particleP - 1); // remove particle

                        int newAntiparticleP = totalP - particleP + 1; // add one antiparticle
                        _antiparticles.setToFirstStateOfParticleNumber(newAntiparticleP); // reset antiparticles
                    }
                }
                else { // if not last state within normal particle number

                    _particles.next(); // step particles (within same particle number)

                    int antiParticleP = totalP - particleP; // same antiparticle number
                    _antiparticles.setToFirstStateOfParticleNumber(antiParticleP); // reset antiparticles
                }
            }
            else { // if not last antiparticle state before next antiparticle number
                _antiparticles.next(); // step antiparticles (within same antiparticle number)
            }
        }
        _label++;
        return _label;
    }

    @Override
    public void remove() {
        _particles.remove();
        _antiparticles.remove();
        _label = -1;
    }

    /**** STATIC FUNCTIONS ****/

    // Number of distinct fermionic Fock states with N lattice points and maximally P particles
    // S(N,P) = Sum(Choose(2N,m), {m,0,P})
    public static int S(int N, int P) {
        int sum = 0;
        for (int m = 0; m <= Math.min(P, 2 * N); m++)
            sum += sigma(N, m);
        return sum;
    }

    // number of states with a given particle number m if N lattice points
    public static int sigma(int N, int p) {
        return Combinatorics.choose(2 * N, p); // times 2 due to particles and antiparticles
    }

    // Combinatoric factor from a given momentum from sandwiching a state of l particles
    // in that momentum with n creation operators and m annihilation operators. Complete
    // combinatoric factor is obtained by the product these for all momenta.
    // F_p(l,n,m) = sqrt( (Product[a=1->n](l-m+a)) * (Product[b=1->m](l+1-b) )
    public static double F_p(int l_p, int n_p, int m_p, double L) {
        int k_p = l_p + n_p - m_p;
        if (m_p > l_p || n_p > k_p || l_p > 1 || n_p > 1 || m_p > 1 || k_p > 1)
            return 0;
        return Math.pow(L, (n_p + m_p) / 2.0);
    }

}
