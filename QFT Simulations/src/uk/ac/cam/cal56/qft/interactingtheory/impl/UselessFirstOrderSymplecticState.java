package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Arrays;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;

public class UselessFirstOrderSymplecticState extends BaseState {

    private final int _K;    // order

    private Complex[] _prevc;

    public UselessFirstOrderSymplecticState(int order, int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
        _K = order;
    }

    // backward Euler method of order "_order"
    @Override
    protected void firstStep() {
        _prevc = Arrays.copyOf(_c, _S);
        Complex[] lastderiv = Arrays.copyOf(_c, _S);

        for (int k = 1; k <= _K; k++) {
            double factor = Math.pow(-_dt, k) / Combinatorics.factorial(k);
            for (int n = 0; n < _S; n++) {
                Complex sum = Complex.zero();
                for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                    sum = sum.plus(lastderiv[h_mn.getKey()].times(h_mn.getValue()));
                sum = sum.times(_lambda);
                sum = sum.plus(lastderiv[n].times(_Hfree.energies[n]));
                lastderiv[n] = sum.timesi(-1);
                _prevc[n] = _prevc[n].plus(lastderiv[n].times(factor));
            }
        }
    }

    @Override
    public void step() {
        if (_K % 2 == 1)
            for (int n = 0; n < _S; n++)
                _prevc[n] = _prevc[n].negative();
        Complex[] nextc = _prevc;
        Complex[] lastderiv = _c;
        Complex[] currentderiv = new Complex[_S];

        for (int k = 1; k < _K; k++) {
            double lastfactor = 2 * Math.pow(_dt, k - 1) / Combinatorics.factorial(k - 1);
            for (int n = 0; n < _S; n++) {
                if ((_K + k) % 2 == 0)
                    nextc[n] = nextc[n].plus(lastderiv[n].times(lastfactor));
                Complex sum = Complex.zero();
                for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                    sum = sum.plus(lastderiv[h_mn.getKey()].times(h_mn.getValue()));
                sum = sum.times(_lambda);
                sum = sum.plus(lastderiv[n].times(_Hfree.energies[n]));
                currentderiv[n] = sum.timesi(-1);
            }
            lastderiv = currentderiv;
        }

        _prevc = _c;
        _c = nextc;

        _time += _dt;
    }

}
