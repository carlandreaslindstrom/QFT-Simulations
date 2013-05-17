package uk.ac.cam.cal56.qft.fermions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.FermionLabelling;
import uk.ac.cam.cal56.qft.impl.FreeHamiltonian;
import uk.ac.cam.cal56.qft.impl.SecondOrderSymplecticState;

public class FermionState extends SecondOrderSymplecticState {

    public FermionState(int N, int Pmax, double dt, double dx, double m, Map<Interaction, Double> lambdas,
            WavePacket wavepacket) {
        super(N, dt, dx, m, lambdas);
        _S = FermionFockState.S(N, Pmax);

        // set coefficients
        _c = new Complex[_S];

        // calculate free theory energies
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx, FermionFockState.class);

        // add interaction Hamiltonians
        if (lambdas == null)
            lambdas = new HashMap<Interaction, Double>();
        for (Entry<Interaction, Double> lambda : lambdas.entrySet())
            _hamiltonians.put(lambda.getKey(), new FermionInteractionHamiltonian(N, Pmax, m, dx, lambda.getKey()));

        // calculate elements of these
        for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet())
            h.getValue().calculateElements();

        // set coefficients (to pure vacuum) and do first step
        setWavePacket(wavepacket);
    }

    @Override
    public Complex[] get1PMom() {
        if (_S <= 1)
            return null;
        return Arrays.copyOfRange(_c, 1, _N + 1);
    }

    public Complex[] get1AntiPMom() {
        if (_S <= 1)
            return null;
        return Arrays.copyOfRange(_c, _N + 1, 2 * _N + 1);
    }

    @Override
    public Complex[][] get2PMom() {
        if (_S <= FermionFockState.S(_N, 1)) // ensure more than 1 total particle
            return null;
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++) {
            for (int q = p + 1; q < _N; q++) {
                int i = FermionLabelling.label(Arrays.asList(p, q), new ArrayList<Integer>(), _N);
                Complex value = _c[i];
                ampls[p][q] = value;
                ampls[q][p] = value.negative(); // minus sign from Fermi-Dirac statistics
            }
            ampls[p][p] = Complex.zero(); // vanishing diagonal (from Pauli's exclusion principle)
        }
        return ampls;
    }

    public Complex[][] get1P1AntiPMom() {
        if (_S <= FermionFockState.S(_N, 1)) // ensure more than 1 total particle
            return null;
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++) {
            for (int q = 0; q < _N; q++) {
                int i = FermionLabelling.label(Arrays.asList(p), Arrays.asList(q), _N);
                ampls[p][q] = _c[i];
            }
        }
        return ampls;
    }

    public Complex[][] get2AntiPMom() {
        if (_S <= FermionFockState.S(_N, 1)) // ensure more than 1 total particle
            return null;
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++) {
            for (int q = p + 1; q < _N; q++) {
                int i = FermionLabelling.label(new ArrayList<Integer>(), Arrays.asList(p, q), _N);
                Complex value = _c[i];
                ampls[p][q] = value;
                ampls[q][p] = value.negative(); // minus sign from Fermi-Dirac statistics
            }
            ampls[p][p] = Complex.zero(); // vanishing diagonal (from Pauli's exclusion principle)
        }
        return ampls;
    }

    @Override
    public Double getRemainingProbability() {
        int S2 = FermionFockState.S(_N, 2);
        if (S2 >= _S)
            return null; // if only 2 particles, return null
        double probSquared = 0;
        for (int n = S2; n < _S; n++)
            probSquared += _c[n].modSquared(); // add up remaining probabilities
        return probSquared;
    }

    public Complex[] getCoefficients() {
        return _c;
    }
}
