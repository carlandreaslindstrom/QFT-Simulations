package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;

public class DivergingFourthOrderState extends BaseState implements State {

    private Complex[][] _prevc;

    public DivergingFourthOrderState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
    }

    @Override
    protected void firstStep() {
        _prevc = new Complex[3][_S];

        // first order first step
        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_c[n].times(_Hfree.energies[n]));
            Complex cdot = sum.timesi(-1);

            _prevc[0][n] = _c[n].plus(cdot.times(-_dt));
        }

        // second order second and third step
        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_prevc[0][h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_prevc[0][n].times(_Hfree.energies[n]));
            Complex prev0cdot = sum.timesi(-1);

            _prevc[1][n] = _c[n].plus(prev0cdot.times(-2 * _dt));
        }

        // second order second and third step
        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_prevc[1][h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_prevc[1][n].times(_Hfree.energies[n]));
            Complex prev1cdot = sum.timesi(-1);

            _prevc[2][n] = _prevc[0][n].plus(prev1cdot.times(-2 * _dt));
        }

    }

    @Override
    public void step() {
        Complex[] nextc = new Complex[_S];

        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_prevc[0][h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_prevc[0][n].times(_Hfree.energies[n]));
            Complex prev0cdot = sum.timesi(-1);

            nextc[n] = _prevc[2][n].minus((_prevc[1][n].minus(_c[n])).times(8)).minus(prev0cdot.times(12 * _dt));
        }

        // swap over
        _prevc[2] = _prevc[1];
        _prevc[1] = _prevc[0];
        _prevc[0] = _c;
        _c = nextc;

        _time += _dt;
    }

}
