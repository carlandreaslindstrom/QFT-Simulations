package uk.ac.cam.cal56.qft.scalars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fockspace.FockState;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.ScalarLabelling;
import uk.ac.cam.cal56.qft.impl.FreeHamiltonian;
import uk.ac.cam.cal56.qft.impl.SecondOrderSymplecticState;

public class ScalarState extends SecondOrderSymplecticState {

    public ScalarState(int N, int Pmax, double dt, double dx, double m, Map<Interaction, Double> lambdas, WavePacket wp) {
        super(N, dt, dx, m, lambdas);
        _S = ScalarFockState.S(N, Pmax);

        // set coefficients
        _c = new Complex[_S];

        // calculate free theory energies
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx, ScalarFockState.class);

        // add interaction Hamiltonians
        if (lambdas == null)
            lambdas = new HashMap<Interaction, Double>();
        for (Entry<Interaction, Double> lambda : lambdas.entrySet())
            _hamiltonians.put(lambda.getKey(), new ScalarInteractionHamiltonian(N, Pmax, m, dx, lambda.getKey()));

        // calculate elements of these
        for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet())
            h.getValue().calculateElements();

        // set coefficients (to pure vacuum) and do first step
        setWavePacket(wp);
    }

    @Override
    public Complex[] get1PMom() {
        if (_S <= 1)
            return null;
        return Arrays.copyOfRange(_c, 1, _N + 1);
    }

    @Override
    public Double getRemainingProbability() {
        int S2 = ScalarFockState.S(_N, 2);
        if (S2 >= _S)
            return null; // if only 2 particles, return null
        double probSquared = 0;
        for (int n = S2; n < _S; n++)
            probSquared += _c[n].modSquared(); // add up remaining probabilities
        return probSquared;
    }

    @Override
    public Complex[][] get2PMom() {
        if (_S <= 1 + _N)
            return null;
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                Complex value = _c[ScalarLabelling.label(Arrays.asList(p, q), _N)];
                ampls[p][q] = value;
                if (p != q)
                    ampls[q][p] = value;
            }
        return ampls;
    }

    // field phi
    public Complex[] getPhi() {

        // initialise phi to all zeros
        Complex[] phi = new Complex[_N];
        for (int n = 0; n < _N; n++)
            phi[n] = Complex.zero();

        ScalarFockState ket = new ScalarFockState(_N, _m, _dx, _S);
        for (int j : ket) {
            if (_c[j].isZero())
                continue; // skip if coefficient is zero
            for (int p = 0; p < _N; p++) {
                double E_p = FockState.E_p(p, _N, _m, _dx);

                // a term
                Complex term1 = null;
                Integer i_minus = ScalarLabelling.braIndex(ket.getParticles(), new int[] {}, new int[] { p }, _N);
                if (i_minus != null) // ensure bra exists
                    term1 = ((_c[i_minus].conj()).times(_c[j])).times(Math.sqrt(ket.l_p(p) / (2 * _N * _dx * E_p)));

                // a dagger term
                Integer i_plus = ScalarLabelling.braIndex(ket.getParticles(), new int[] { p }, new int[] {}, _N);
                Complex term2 = null;
                if (i_plus < _S) // ensure bra within range
                    term2 = ((_c[i_plus].conj()).times(_c[j])).times(Math.sqrt((ket.l_p(p) + 1) / (2 * _N * _dx * E_p)));

                for (int n = 0; n < _N; n++) {
                    if (term1 != null)
                        phi[n] = phi[n].plus(term1.timesexpi(2 * Math.PI * n * p / _N));
                    if (term2 != null)
                        phi[n] = phi[n].plus(term2.timesexpi(-2 * Math.PI * n * p / _N));
                }
            }
        }
        ket.remove();

        return phi;
    }

    // conjugate momentum field pi
    public Complex[] getPi() {

        // initialise pi to all zeros
        Complex[] pi = new Complex[_N];
        for (int n = 0; n < _N; n++)
            pi[n] = Complex.zero();

        ScalarFockState ket = new ScalarFockState(_N, _m, _dx, _S);
        for (int j : ket) {
            if (_c[j].isZero())
                continue; // skip if coefficient is zero
            for (int p = 0; p < _N; p++) {
                double E_p = FockState.E_p(p, _N, _m, _dx);

                // a term
                Complex term1 = null;
                Integer i_minus = ScalarLabelling.braIndex(ket.getParticles(), new int[] {}, new int[] { p }, _N);
                if (i_minus != null) // ensure bra exists
                    term1 = ((_c[i_minus].conj()).times(_c[j])).times(Math.sqrt(ket.l_p(p) * E_p / (2 * _N * _dx)));

                // a dagger term
                Integer i_plus = ScalarLabelling.braIndex(ket.getParticles(), new int[] { p }, new int[] {}, _N);
                Complex term2 = null;
                if (i_plus < _S) // ensure bra within range
                    term2 = ((_c[i_plus].conj()).times(_c[j])).times(Math.sqrt((ket.l_p(p) + 1) * E_p / (2 * _N * _dx)));

                for (int n = 0; n < _N; n++) {
                    if (term1 != null)
                        pi[n] = pi[n].plus(term1.timesi().timesexpi(2 * Math.PI * n * p / _N));
                    if (term2 != null)
                        pi[n] = pi[n].plus(term2.timesi(-1).timesexpi(-2 * Math.PI * n * p / _N));
                }
            }
        }
        ket.remove();

        return pi;
    }

    public Complex[] getCoefficients() {
        return _c;
    }
}
