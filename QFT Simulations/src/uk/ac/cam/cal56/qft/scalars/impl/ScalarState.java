package uk.ac.cam.cal56.qft.scalars.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.ScalarLabelling;
import uk.ac.cam.cal56.qft.impl.FreeHamiltonian;
import uk.ac.cam.cal56.qft.impl.SecondOrderSymplecticState;

public class ScalarState extends SecondOrderSymplecticState {

    public ScalarState(int N, int Pmax, double m, double dx, double dt, Map<Interaction, Double> lambdas, WavePacket wp) {
        super(N, Pmax, m, dx, dt, lambdas, wp);
        _S = ScalarFockState.S(N, Pmax);

        // set coefficients
        _c = new Complex[_S];

        // calculate free theory energies
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx, ScalarFockState.class);

        // add interaction Hamiltonians
        for (Entry<Interaction, Double> lambda : lambdas.entrySet())
            _hamiltonians.put(lambda.getKey(), new InteractionHamiltonian(N, Pmax, m, dx, lambda.getKey()));

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
    public Complex[][] get2PMom() {
        if (_S <= 1 + _N)
            return null;
        Complex[][] ampls = new Complex[_N][_N];
        for (int p = 0; p < _N; p++)
            for (int q = p; q < _N; q++) {
                Complex value = _c[ScalarLabelling.index(Arrays.asList(p, q), _N)];
                ampls[p][q] = value;
                if (p != q)
                    ampls[q][p] = value;
            }
        return ampls;
    }

}
