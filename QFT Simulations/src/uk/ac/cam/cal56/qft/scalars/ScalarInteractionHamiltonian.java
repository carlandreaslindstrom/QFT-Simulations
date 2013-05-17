package uk.ac.cam.cal56.qft.scalars;

import java.util.Map.Entry;

import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.fockspace.FockState;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.ScalarLabelling;
import uk.ac.cam.cal56.qft.impl.InteractionHamiltonian;

public class ScalarInteractionHamiltonian extends InteractionHamiltonian {

    public ScalarInteractionHamiltonian(int N, int Pmax, double m, double dx, Interaction interaction) {
        super(N, ScalarFockState.S(N, Pmax), m, dx, interaction);
        _ket = new ScalarFockState(N, Pmax, m, dx);
    }

    @Override
    protected void calculateRow(int n) {
        if (_interaction == Interaction.PHI_SQUARED)
            calculatePhiSquared();

        else if (_interaction == Interaction.PHI_CUBED)
            calculatePhiCubed();

        else if (_interaction == Interaction.PHI_FOURTH)
            calculatePhiFourth();
    }

    // PHI SQUARED THEORY (has analytical solution)
    private void calculatePhiSquared() {
        double constantTerm = 0.0;
        for (int p = 0; p < _N; p++) {
            double f = 1.0 / (2.0 * _E[p]);
            constantTerm += f;
            f *= f / L; // Ndx divisor cancels for constant term, only use for rest
            add(2 * f, new int[] { p }, p); // 2 a*_p a_p
            add(f, new int[] {}, p, -p); // a_p a_-p
            add(f, new int[] { p, -p }); // a*_p a*_-p
        }
        add(constantTerm, new int[] {}); // 1
    }

    // PHI THIRD THOERY
    // uses ladder ops split into sections (0,0), (p,0), (p,p), (p,q) for optimization
    private void calculatePhiCubed() {
        double divisor = 8.0 * L * L;
        double commutatorfactor = 2.0 * _m * L;
        double f;

        // h_00 = ( a_0^3 + a*_0^3 + 3a*_0 a_0^2 + 3a*_0^2 a_0 + 3a_0 + 3a*_0 ) / m^1.5
        f = 1 / (_m * _m * _m * divisor);
        add(f, new int[] {}, 0, 0, 0);
        add(f, new int[] { 0, 0, 0 });
        add(3 * f, new int[] { 0 }, 0, 0);
        add(3 * f, new int[] { 0, 0 }, 0);
        add(3 * f * commutatorfactor, new int[] {}, 0);
        add(3 * f * commutatorfactor, new int[] { 0 });

        // h_p0 = ( a_0 a_p a_-p + 2a*_p a_0 a_p + 2a*_p a*_0 a_p + a*_0 a_p a_-p + a*_p a*_-p a_0 + a*_p a*_0 a*_-p
        // + a_0 + a*_0 ) / (E_p sqrt(m))
        for (int p = 1; p < _N; p++) {
            f = 2 / (_m * _E[p] * _E[p] * divisor);
            add(f, new int[] {}, p, 0, -p);
            add(2 * f, new int[] { p }, 0, p);
            add(2 * f, new int[] { p, 0 }, p);
            add(f, new int[] { 0 }, p, -p);
            add(f, new int[] { p, -p }, 0);
            add(f, new int[] { p, 0, -p });
            add(f * commutatorfactor, new int[] { 0 });
            add(f * commutatorfactor, new int[] {}, 0);
        }

        // h_pp = ( a_p^2 a_-2p + a*_2p a_p^2 + a*_p^2 a_2p + a*_p^2 a*_-2p ) / (E_p sqrt(E_2p))
        // + (2a*_p a_0 a_p + 2a*_p a*_0 a_p + a_0 + a*_0 ) / (E_p sqrt(m))
        for (int p = 1; p < _N; p++) {
            f = 1 / (_E[mod(2 * p)] * _E[p] * _E[p] * divisor);
            add(f, new int[] {}, p, p, -2 * p);
            add(f, new int[] { 2 * p }, p, p);
            add(f, new int[] { p, p }, 2 * p);
            add(f, new int[] { p, p, -2 * p });
            f = 1 / (_m * _E[p] * _E[p] * divisor);
            add(2 * f, new int[] { p }, 0, p);
            add(2 * f, new int[] { 0, p }, p);
            add(f * commutatorfactor, new int[] {}, 0);
            add(f * commutatorfactor, new int[] { 0 });
        }

        // h_pq
        for (int p = 2; p < _N; p++) { // all errors occur in the form of a null from the Fast
            for (int q = 1; q < p; q++) {
                f = 2 / (_E[mod(p + q)] * _E[p] * _E[q] * divisor);
                add(f, new int[] {}, p, q, -p - q);
                add(f, new int[] { p + q }, p, q);
                add(f, new int[] { p, q }, p + q);
                add(f, new int[] { p, q, -p - q });
                f = 2 / (_E[p - q] * _E[p] * _E[q] * divisor);
                add(f, new int[] { q }, p, q - p);
                add(f, new int[] { p }, q, p - q);
                add(f, new int[] { q, p - q }, p);
                add(f, new int[] { p, q - p }, q);
            }
        }
    }

    // PHI FOURTH
    // Looks very lengthy because it represents the ladder operators written out normal ordered.
    // This is done in order to make use of the fast add() function [requires normal ordering].
    private void calculatePhiFourth() {
        double divisor = 16 * L * L * L;
        double f, L2E;

        for (int j = 0; j < _N; j++) {
            for (int k = 0; k < _N; k++) {
                for (int l = 0; l < _N; l++) {
                    double commonEnergyDivisor = divisor * _E[j] * _E[k] * _E[l];

                    // first quarter
                    f = 1.0 / (commonEnergyDivisor * _E[mod(j + k + l)]);
                    L2E = L * 2 * _E[mod(j + k + l)];
                    add(f, new int[] {}, j, k, l, mod(-j - k - l));
                    add(f, new int[] { mod(j + k + l) }, j, k, l);
                    if (delta(j + k))
                        add(f * L2E, new int[] {}, j, k);
                    if (delta(k + l))
                        add(f * L2E, new int[] {}, k, l);
                    if (delta(j + l))
                        add(f * L2E, new int[] {}, j, l);
                    add(f, new int[] { j, k, l }, mod(j + k + l));
                    add(f, new int[] { j, k, l, mod(-j - k - l) });

                    // second quarter
                    f = 1.0 / (commonEnergyDivisor * _E[mod(j + k - l)]);
                    L2E = L * 2 * _E[mod(j + k - l)];
                    add(f, new int[] { l }, j, k, mod(-j - k + l));
                    if (k == l)
                        add(f * L2E, new int[] {}, j, mod(-j));
                    if (j == l)
                        add(f * L2E, new int[] {}, k, mod(-k));
                    add(f, new int[] { l, mod(j + k - l) }, j, k);
                    if (k == l) {
                        add(f * L2E, new int[] { k }, k);
                        add(f * L2E, new int[] { j }, j);
                        add(f * L2E * L2E, new int[] {});
                    }
                    if (j == l) {
                        add(f * L2E, new int[] { j }, j);
                        add(f * L2E, new int[] { k }, k);
                        add(f * L2E * L2E, new int[] {});
                    }
                    add(f, new int[] { j, k }, l, mod(j + k - l));
                    add(f, new int[] { j, k, mod(-j - k + l) }, l);
                    if (delta(j + k))
                        add(f * L2E, new int[] { j, k });

                    // third quarter
                    f = 1.0 / (commonEnergyDivisor * _E[mod(j - k + l)]);
                    L2E = L * 2 * _E[mod(j - k + l)];
                    add(f, new int[] { k }, j, l, mod(-j + k - l));
                    if (j == k) {
                        add(f * L2E, new int[] {}, l, mod(-l));
                        add(f * L2E, new int[] { l }, l);
                        add(f * L2E, new int[] { k }, k);
                        add(f * L2E * L2E, new int[] {});
                    }
                    add(f, new int[] { k, mod(j - k + l) }, j, l);
                    if (k == l) {
                        add(f * L2E, new int[] { k }, k);
                        add(f * L2E, new int[] { j }, j);
                        add(f * L2E, new int[] { j, mod(-j) });
                    }
                    add(f, new int[] { j, l }, k, mod(j - k + l));
                    add(f, new int[] { j, l, mod(-j + k - l) }, k);
                    if (delta(j + l))
                        add(f * L2E, new int[] { j, l });

                    // fourth quarter
                    f = 1.0 / (commonEnergyDivisor * _E[mod(j - k - l)]);
                    L2E = L * 2 * _E[mod(j - k - l)];
                    add(f, new int[] { k, l }, j, mod(-j + k + l));
                    if (j == l) {
                        add(f * L2E, new int[] { k }, k);
                        add(f * L2E, new int[] { k, mod(-k) });
                        add(f * L2E, new int[] { j }, j);
                    }
                    if (j == k) {
                        add(f * L2E, new int[] { l }, l);
                        add(f * L2E, new int[] { l, mod(-l) });
                        add(f * L2E, new int[] { j }, j);
                    }
                    add(f, new int[] { k, l, mod(j - k - l) }, j);
                    if (delta(k + l))
                        add(f * L2E, new int[] { k, l });
                    add(f, new int[] { j }, k, l, mod(j - k - l));
                    add(f, new int[] { j, mod(-j + k + l) }, k, l);
                }
            }
        }
    }

    private int mod(int p) {
        return (p + 3 * _N) % _N;
    }

    private boolean delta(int p) {
        return mod(p) == 0;
    }

    // adds ladder op group contribution directly to the correct element
    private void add(double factor, int[] creators, int... annihilators) {

        // ket index
        int n = _ket.getLabel();

        // bra index
        Integer m = ScalarLabelling.braIndex(((ScalarFockState) _ket).getParticles(), creators, annihilators, _N);

        // quit if not a valid index
        if (m == null || m >= _S)
            return;

        // multiply by ladder operator combinatoric factor
        for (Entry<Integer, int[]> op : ScalarLabelling.toLadderOperatorMap(creators, annihilators, _N).entrySet()) {
            int p = op.getKey();
            int l_p = ((ScalarFockState) _ket).l_p(p);
            int n_p = op.getValue()[0];
            int m_p = op.getValue()[1];
            factor *= ScalarFockState.F_p(l_p, n_p, m_p, L * 2 * FockState.E_p(p, _N, _m, _dx));
        }

        // if not 0, save value in the Hamiltonian matrix
        if (Math.abs(factor) > EPSILON) {
            Double oldValue = get(n, m);
            _elements.get(n).put(m, factor + (oldValue == null ? 0 : oldValue));
        }
    }
}
