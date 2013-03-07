package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.FockState;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

// Interaction Hamiltonian holds all the coefficients for jumping between states.
// Only calculated once per simulation, but takes a lot of effort to calculate.
// It is therefore heavily optimized (and possibly hard to read/understand).
public class SlowInteractionHamiltonian implements Hamiltonian {

    private List<Map<Integer, Double>> _elements;
    private int                        _N;
    private double                     _m;
    private double                     _dx;

    private Interaction                _interaction;

    private FockState                  _ket;        // |n> : used internally
    private FockState                  _bra;        // <m| :for stepping

    public SlowInteractionHamiltonian(int N, int Pmax, double mass, double dx, Interaction interaction) {
        _N = N;
        _m = mass;
        _dx = dx;
        _interaction = interaction;
        _ket = new FockState(N, Pmax, mass, dx);
        _bra = new FockState(N, Pmax, mass, dx);
        _elements = new ArrayList<Map<Integer, Double>>(Combinatorics.S(_N, Pmax));
    }

    public void calculateElements() {
        for (int n : _ket) { // |n>
            _elements.add(n, new HashMap<Integer, Double>());
            int momnum = _ket.getMomentumNumber();
            int partnum = _ket.getParticleNumber();
            for (int m : _bra) { // <m| states m with same momentum and appropriate particle number
                if (m > n)
                    break;
                boolean momentumConserved = (_bra.getMomentumNumber() == momnum);
                if (momentumConserved && allowedTransition(partnum))
                    calculateElement(n, m);
            }
            _bra.remove();
        }
        _ket.remove();
    }

    private void calculateElement(int n, int m) {
        double element = 0;

        // uses ladder ops split into sections (0,0), (p,0), (p,p), (p,q) for optimization
        if (_interaction == Interaction.PHI_CUBED) {

            // h_00 = ( a_0^3 + a*_0^3 + 3a*_0 a_0^2 + 3a*_0^2 a_0 + 3a_0 + 3a*_0 ) / m^1.5
            double h_00 = F(new int[] {}, 0, 0, 0) + F(new int[] { 0, 0, 0 }) + 3 * F(new int[] { 0 }, 0, 0) + 3 *
                          F(new int[] { 0, 0 }, 0) + 3 * F(new int[] {}, 0) + 3 * F(new int[] { 0 });
            element += h_00 / Math.sqrt(_m * _m * _m);

            // h_p0 = 2*(a_0 a_p a_-p + 2a*_p a_0 a_p + 2a*_p a*_0 a_p + a*_0 a_p a_-p + a*_p a*_-p a_0
            // + a*_p a*_0 a*_-p+ a_0 + a*_0 ) / (E_p sqrt(m))
            for (int p = 1; p < _N; p++) {
                double h_p0 = F(new int[] {}, p, 0, -p) + 2 * F(new int[] { p }, 0, p) + 2 * F(new int[] { p, 0 }, p) +
                              F(new int[] { 0 }, p, -p) + F(new int[] { p, -p }, 0) + F(new int[] { p, 0, -p }) +
                              F(new int[] { 0 }) + F(new int[] {}, 0);
                element += 2 * h_p0 / (Math.sqrt(_m) * E_p(p));
            }

            // h_pp = ( a_p^2 a_-2p + a*_2p a_p^2 + a*_p^2 a_2p + a*_p^2 a*_-2p ) / (E_p sqrt(E_2p))
            // + (2a*_p a_0 a_p + 2a*_p a*_0 a_p + a_0 + a*_0 ) / (E_p sqrt(m))
            for (int p = 1; p < _N; p++) {
                double h_pp1 = F(new int[] {}, p, p, -2 * p) + F(new int[] { 2 * p }, p, p) +
                               F(new int[] { p, p }, 2 * p) + F(new int[] { p, p, -2 * p });
                double h_pp2 = 2 * F(new int[] { p }, 0, p) + 2 * F(new int[] { 0, p }, p) + F(new int[] { 0 }) +
                               F(new int[] {}, 0);
                element += (h_pp1 / Math.sqrt(E_p(2 * p)) + h_pp2 / Math.sqrt(_m)) / E_p(p);
            }

            // h_pq = 2 * (
            for (int p = 2; p < _N; p++) {
                for (int q = 1; q < p; q++) {
                    double h_pq1 = F(new int[] {}, p, q, -p - q) + F(new int[] { p + q }, p, q) +
                                   F(new int[] { p, q }, p + q) + F(new int[] { p, q, -p - q });
                    double h_pq2 = F(new int[] { q }, p, q - p) + F(new int[] { p }, q, p - q) +
                                   F(new int[] { q, p - q }, p) + F(new int[] { p, q - p }, q);
                    element += 2 * (h_pq1 / Math.sqrt(E_p(p + q)) + h_pq2 / Math.sqrt(E_p(p - q))) /
                               Math.sqrt(E_p(p) * E_p(q));
                }
            }
            element /= Math.sqrt(8.0 * _N * _dx);
        }
        double epsilon = 1e-13;
        if (Math.abs(element) > epsilon) {
            _elements.get(n).put(m, element);
            if (n != m)
                _elements.get(m).put(n, element);
        }
    }

    // calculate sandwich factor F = Product(F_p,p)
    private double F(int[] creators, int... annihilators) {
        Map<Integer, int[]> ops = StateLabelling.toLadderOpMap(creators, annihilators, _N);
        double product = 1;
        for (int p = 0; p < _N; p++) {
            int k_p = _bra.l_p(p);
            int l_p = _ket.l_p(p);
            if (ops.containsKey(p)) {
                int n_p = ops.get(p)[0]; // creation operators
                int m_p = ops.get(p)[1];
                if (l_p >= m_p && k_p >= n_p && k_p + m_p == l_p + n_p)
                    product *= Combinatorics.F_p(l_p, n_p, m_p);
                else
                    return 0;
            }
            else if (k_p != l_p)
                return 0;
        }
        return product;
    }

    private boolean allowedTransition(int Pket) {
        return allowedTransition(Pket, _bra.getParticleNumber(), _interaction);
    }

    private static boolean allowedTransition(int Pket, int Pbra, Interaction interaction) {
        if (interaction == Interaction.PHI_CUBED) {
            if (Pbra == Pket - 3 || Pbra == Pket - 1 || Pbra == Pket + 1 || Pbra == Pket + 3)
                return true;
        }
        else if (interaction == Interaction.PHI_FOURTH) {
            if (Pbra == Pket - 4 || Pbra == Pket - 2 || Pbra == Pket || Pbra == Pket + 2 || Pbra == Pket + 4)
                return true;
        }
        return false;
    }

    // E_p = sqrt(m^2 + (2/dx)^2*sin(p*dx/2)^2 )
    private double E_p(int i) { // i = momentum number
        return FockState.E_p(i, _N, _m, _dx);
    }

    public Double get(int n, int m) {
        return getRow(n).get(m); // <m|H_int|n>
    }

    public Map<Integer, Double> getRow(int n) {
        return _elements.get(n);
    }

}
