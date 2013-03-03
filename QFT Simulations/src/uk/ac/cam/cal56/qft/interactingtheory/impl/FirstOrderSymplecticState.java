package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;

public class FirstOrderSymplecticState extends BaseState {

    private Complex[] _cdot;
    private Complex[] _prevc;

    public FirstOrderSymplecticState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
    }

    // first order (backward) Euler step to find _cdot and _prevc
    @Override
    protected void firstStep() {
        _cdot = new Complex[_S];
        _prevc = new Complex[_S];

        for (int n = 0; n < _S; n++) {
            // find coefficient derivatives
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda); // multiply by interaction strength
            sum = sum.plus(_c[n].times(_Hfree.energies[n])); // add Free Theory energy
            _cdot[n] = sum.timesi(-1);// i*cdot_n(t) = E_n*c_n(t) + lambda*Sum(H_mn*c_m(t),{m})

            // backward Euler to find previous coefficients
            _prevc[n] = _c[n].plus(_cdot[n].times(-_dt)); // c_n(-dt) = c_n(0) - dt*cdot_n(t)
        }
    }

    // third order symplectic stepping algorithm (suspicious => verify)
    @Override
    public void step() {
        Complex[] nextc = new Complex[_S];

        // find next coefficients
        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_cdot[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda); // multiply by interaction strength
            sum = sum.plus(_cdot[n].times(_Hfree.energies[n])); // add Free Theory energy
            Complex cdotdot = sum.timesi(-1);
            nextc[n] = (_c[n].times(2)).minus(_prevc[n]).plus(cdotdot.times(_dt * _dt));
        }

        // find next coefficient derivatives
        for (int n = 0; n < _S; n++) {
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(nextc[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda); // multiply by interaction strength
            sum = sum.plus(nextc[n].times(_Hfree.energies[n])); // add Free Theory energy
            _cdot[n] = sum.timesi(-1);// i*cdot_n(t) = E_n*c_n(t) + lambda*Sum(H_mn*c_m(t),{m})
        }

        // swap over old coefficients
        _prevc = _c;
        _c = nextc;

        // increment time
        _time += _dt;
    }

}
