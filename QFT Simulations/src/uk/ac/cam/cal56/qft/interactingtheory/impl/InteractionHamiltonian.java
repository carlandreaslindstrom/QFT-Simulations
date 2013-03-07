package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.FockState;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class InteractionHamiltonian implements Hamiltonian {

    private final double               EPSILON = 1e-13;

    private List<Map<Integer, Double>> _elements;
    private int                        _N;
    private int                        _S;
    private double                     _m;
    private double                     _dx;

    private Interaction                _interaction;

    private FockState                  _ket;                    // used internally

    private double[]                   _E      = new double[_N]; // energy buffer

    public InteractionHamiltonian(int N, int Pmax, double mass, double dx, Interaction interaction) {
        _N = N;
        _S = Combinatorics.S(N, Pmax);
        _m = mass;
        _dx = dx;
        _interaction = interaction;
        _ket = new FockState(N, Pmax, mass, dx);
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

    private void calculateRow(int n) {

        // PHI SQUARED THEORY (has analytical solution)
        if (_interaction == Interaction.PHI_SQUARED) {
            double constantTerm = 0.0;
            for (int p = 0; p < _N; p++) {
                double f = 1.0 / (2.0 * _E[p]);
                constantTerm += f;
                add(2 * f, new int[] { p }, p); // 2 a*_p a_p
                add(f, new int[] {}, p, -p); // a_p a_-p
                add(f, new int[] { p, -p }); // a*_p a*_-p
            }
            add(constantTerm, new int[] {}); // 1
        }

        // PHI THIRD THOERY
        // uses ladder ops split into sections (0,0), (p,0), (p,p), (p,q) for optimization
        else if (_interaction == Interaction.PHI_CUBED) {

            double divisor = Math.sqrt(8.0 * _N * _dx);
            double f;

            // h_00 = ( a_0^3 + a*_0^3 + 3a*_0 a_0^2 + 3a*_0^2 a_0 + 3a_0 + 3a*_0 ) / m^1.5
            f = 1 / (Math.sqrt(_m * _m * _m) * divisor);
            add(f, new int[] {}, 0, 0, 0);
            add(f, new int[] { 0, 0, 0 });
            add(3 * f, new int[] { 0 }, 0, 0);
            add(3 * f, new int[] { 0, 0 }, 0);
            add(3 * f, new int[] {}, 0);
            add(3 * f, new int[] { 0 });

            // h_p0 = ( a_0 a_p a_-p + 2a*_p a_0 a_p + 2a*_p a*_0 a_p + a*_0 a_p a_-p + a*_p a*_-p a_0 + a*_p a*_0 a*_-p
            // + a_0 + a*_0 ) / (E_p sqrt(m))
            for (int p = 1; p < _N; p++) {
                f = 2 / (Math.sqrt(_m) * _E[p] * divisor);
                add(f, new int[] {}, p, 0, -p);
                add(2 * f, new int[] { p }, 0, p);
                add(2 * f, new int[] { p, 0 }, p);
                add(f, new int[] { 0 }, p, -p);
                add(f, new int[] { p, -p }, 0);
                add(f, new int[] { p, 0, -p });
                add(f, new int[] { 0 });
                add(f, new int[] {}, 0);
            }

            // h_pp = ( a_p^2 a_-2p + a*_2p a_p^2 + a*_p^2 a_2p + a*_p^2 a*_-2p ) / (E_p sqrt(E_2p))
            // + (2a*_p a_0 a_p + 2a*_p a*_0 a_p + a_0 + a*_0 ) / (E_p sqrt(m))
            for (int p = 1; p < _N; p++) { // some value errors, some uncalculated
                f = 1 / (Math.sqrt(_E[(2 * p) % _N]) * _E[p] * divisor);
                add(f, new int[] {}, p, p, -2 * p);
                add(f, new int[] { 2 * p }, p, p);
                add(f, new int[] { p, p }, 2 * p);
                add(f, new int[] { p, p, -2 * p });
                f = 1 / (Math.sqrt(_m) * _E[p] * divisor);
                add(2 * f, new int[] { p }, 0, p);
                add(2 * f, new int[] { 0, p }, p);
                add(f, new int[] {}, 0);
                add(f, new int[] { 0 });
            }

            // h_pq
            for (int p = 2; p < _N; p++) { // all errors occur in the form of a null from the Fast
                for (int q = 1; q < p; q++) {
                    f = 2 / (Math.sqrt(_E[(p + q) % _N] * _E[p] * _E[q]) * divisor);
                    add(f, new int[] {}, p, q, -p - q);
                    add(f, new int[] { p + q }, p, q);
                    add(f, new int[] { p, q }, p + q);
                    add(f, new int[] { p, q, -p - q });
                    f = 2 / (Math.sqrt(_E[p - q] * _E[p] * _E[q]) * divisor);
                    add(f, new int[] { q }, p, q - p);
                    add(f, new int[] { p }, q, p - q);
                    add(f, new int[] { q, p - q }, p);
                    add(f, new int[] { p, q - p }, q);
                }
            }
        }
    }

    // adds ladder op group contribution directly to the correct element
    private void add(double factor, int[] creators, int... annihilators) {
        int n = _ket.getLabel();
        Integer m = StateLabelling.braIndex(_ket.toList(), creators, annihilators, _N);
        if (m == null || m >= _S)
            return;
        for (Entry<Integer, int[]> op : StateLabelling.toLadderOpMap(creators, annihilators, _N).entrySet()) {
            int l_p = _ket.l_p(op.getKey());
            int n_p = op.getValue()[0];
            int m_p = op.getValue()[1];
            factor *= Combinatorics.F_p(l_p, n_p, m_p);
        }
        if (Math.abs(factor) > EPSILON) {
            Double oldValue = get(n, m);
            _elements.get(n).put(m, factor + (oldValue == null ? 0 : oldValue));
        }
    }

    @Override
    public Double get(int n, int m) {
        return getRow(n).get(m);
    }

    @Override
    public Map<Integer, Double> getRow(int n) {
        return _elements.get(n);
    }

}
