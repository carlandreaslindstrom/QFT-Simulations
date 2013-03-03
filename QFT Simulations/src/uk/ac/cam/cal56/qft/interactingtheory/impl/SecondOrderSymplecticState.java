package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;

public class SecondOrderSymplecticState extends BaseState implements State {

    private Complex[] _prevc;    // {c_n(t+dt)}

    public SecondOrderSymplecticState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
    }

    // first order Euler backward step to calculate _prevc 
    @Override
    protected void firstStep() {
        _prevc = new Complex[_S];
        for (int n = 0; n < _S; n++) { // loop over state labels
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_c[n].times(_Hfree.energies[n]));
            Complex cdot = sum.timesi(-1);
            _prevc[n] = _c[n].minus(cdot.times(_dt)); // c_n(-dt) = c_n(0) - dt*cdot_n(0)
        }
    }

    // second order symplectic stepping algorithm (mid point) to calculate 
    @Override
    public void step() {
        Complex[] nextc = new Complex[_S]; // temporary buffer for later swap-over
        for (int n = 0; n < _S; n++) { // loop over Fock State labels
            Complex sum = Complex.zero();
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
            sum = sum.times(_lambda);
            sum = sum.plus(_c[n].times(_Hfree.energies[n]));
            Complex cdot = sum.timesi(-1);
            nextc[n] = _prevc[n].plus(cdot.times(2 * _dt)); // c_n(t+dt)=c_n(t-dt)+2dt*cdot_n(t)
        }
        
        // swap to new coefficients
        _prevc = _c;
        _c = nextc;

        // increment time
        _time += _dt;
    }

}
