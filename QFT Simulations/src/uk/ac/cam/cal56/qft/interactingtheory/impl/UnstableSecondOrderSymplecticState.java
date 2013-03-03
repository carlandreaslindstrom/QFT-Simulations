package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;

public class UnstableSecondOrderSymplecticState extends BaseState implements State {

    private Complex[] _prevc;
    private Complex[] _cdot;
    private Complex[] _prevcdot;

    public UnstableSecondOrderSymplecticState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
    }

    @Override
    protected void firstStep() {
        _prevc = new Complex[_S];
        _cdot = new Complex[_S];
        _prevcdot = new Complex[_S];

        // calculate cdot_n(0) and c_n(-dt)
        for (int n = 0; n < _S; n++) {
            // calculate total transition contribution to the state
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda); // multiply by interaction strength

            // add free theory contribution
            sum = sum.plus(_c[n].times(_Hfree.energies[n]));

            // multiply my -i to ensure: i*cdot_n(t) = E_n*c_n(t) + lambda*Sum(H_mn*c_n(t),m)
            _cdot[n] = sum.timesi(-1);

            // backward Euler to find c_n(-dt) = c_n(0) - dt*cdot_n(0)
            _prevc[n] = _c[n].plus(_cdot[n].times(-_dt));
        }

        // calculate cdot_n(-dt)
        for (int n = 0; n < _S; n++) {
            // calculate total transition contribution to the state
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_prevc[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda); // multiply by interaction strength

            // add free theory contribution
            sum = sum.plus(_prevc[n].times(_Hfree.energies[n]));

            // multiply my -i to ensure: i*cdot_n(t) = E_n*c_n(t) + lambda*Sum(H_mn*c_n(t),m)
            _prevcdot[n] = sum.timesi(-1);
        }

    }

    @Override
    public void step() {
        // temporary placeholders for the next _c and _cdot
        Complex[] nextc = new Complex[_S];
        Complex[] nextcdot = new Complex[_S];

        for (int n = 0; n < _S; n++) {
            // calculate total transition contribution to the state
            Complex sum = Complex.zero();
            Complex dotsum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet()) {
                int m = h_mn.getKey();
                double element = h_mn.getValue();
                sum = sum.plus(_c[m].times(element));
                dotsum = dotsum.plus(_cdot[m].times(element));
            }
            // multiply by interaction strength
            sum = sum.times(_lambda);
            dotsum = dotsum.times(_lambda);

            // add free theory contribution
            sum = sum.plus(_c[n].times(_Hfree.energies[n]));
            dotsum = dotsum.plus(_cdot[n].times(_Hfree.energies[n]));

            // multiply my -i to ensure: i*cdot_n(t) = E_n*c_n(t) + lambda*Sum(H_mn*c_n(t),m)
            _cdot[n] = sum.timesi(-1);//
            Complex cdotdot = sum.timesi(-1);

            //
            nextc[n] = _prevc[n].plus(_cdot[n].times(2 * _dt));  // c_n(t+dt) = c_n(t-dt) - 2*dt*cdot_n(t)
            nextcdot[n] = _prevcdot[n].plus(cdotdot.times(2 * _dt)); // cdot_n(t+dt) = cdot_n(t-dt) - 2*dt*cdotdot_n(t)
        }

        // swap over coefficients
        _prevcdot = _cdot;
        _prevc = _c;
        _cdot = nextcdot;
        _c = nextc;

        // increment time
        _time += _dt;
    }

}
