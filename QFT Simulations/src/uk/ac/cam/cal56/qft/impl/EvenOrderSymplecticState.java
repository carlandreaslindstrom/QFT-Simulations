package uk.ac.cam.cal56.qft.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;

public abstract class EvenOrderSymplecticState extends BaseState {

    private final int _K;    // global order, (local order = _K + 2)

    private Complex[] _prevc;

    protected EvenOrderSymplecticState(int order, int N, int Pmax, double m, double dx, double dt,
            Map<Interaction, Double> lambdas, WavePacket wavepacket) {
        super(N, Pmax, m, dx, dt, lambdas, wavepacket);
        _K = order - (order % 2) + 2; // ensure even numbers and add 2 due to accumulative effects of integration
    }

    // backward Euler method of order "_order"
    @Override
    protected void firstStep() {
        _prevc = Arrays.copyOf(_c, _S);
        Complex[] lastderiv = Arrays.copyOf(_c, _S);

        for (int k = 1; k <= _K; k++) {
            double factor = Math.pow(-_dt, k) / Combinatorics.factorial(k);
            for (int n = 0; n < _S; n++) {
                // first add free theory
                Complex sum = lastderiv[n].times(_Hfree.getEnergy(n));

                // then interactions
                for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                    Complex subsum = Complex.zero();
                    for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                        subsum = subsum.plus(lastderiv[h_mn.getKey()].times(h_mn.getValue()));
                    sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
                }
                lastderiv[n] = sum.timesi(-1);

                _prevc[n] = _prevc[n].plus(lastderiv[n].times(factor));
            }
        }
    }

    @Override
    public void step() {
        Complex[] nextc = Arrays.copyOf(_prevc, _S);
        Complex[] lastderiv = _c;
        Complex[] currentderiv = new Complex[_S];

        for (int k = 1; k < _K; k++) {
            double lastfactor = 2.0 * Math.pow(_dt, k - 1) / Combinatorics.factorial(k - 1);
            for (int n = 0; n < _S; n++) {

                // add to sum only if odd order (adds from last iteration)
                if (k % 2 == 0)
                    nextc[n] = nextc[n].plus(lastderiv[n].times(lastfactor));

                // first add free theory
                Complex sum = _c[n].times(_Hfree.getEnergy(n));

                // then interactions
                for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                    Complex subsum = Complex.zero();
                    for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                        subsum = subsum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
                    sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
                }
                currentderiv[n] = sum.timesi(-1);
            }
            lastderiv = currentderiv;
        }

        _prevc = _c;
        _c = nextc;

        _time += _dt;
    }

}
