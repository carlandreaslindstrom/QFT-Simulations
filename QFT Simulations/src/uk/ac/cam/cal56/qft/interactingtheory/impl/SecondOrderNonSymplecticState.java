package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.State;

public class SecondOrderNonSymplecticState extends BaseState implements State {

    private Complex[] _nextc;    // {c_n(t+dt)}

    public SecondOrderNonSymplecticState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        super(N, Pmax, m, dx, dt, lambda);
    }

    // first order method for first step (calculates _nextcoeffs)
    @Override
    protected void firstStep() {
        _nextc = new Complex[_S];
        for (int n = 0; n < _S; n++) { // loop over state labels
            Complex sum = _c[n].times(_Hfree.energies[n]); // i*cdot_n(t) = E_n*c_n(t) + Sum(H_mn*c_m(t),{m})
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                sum = sum.plus(_c[h_mn.getKey()].times(_lambda * h_mn.getValue()));
            _nextc[n] = _c[n].plus(sum.timesi(-_dt)); // c_n(t+dt) = c_n(t) + dt*cdot_n(t)
        }
    }

    // second order non-symplectic stepping algorithm
    @Override
    public void step() {
        Complex[] tempcoeffs = _nextc; // temporary buffer for later swap-over
        for (int n = 0; n < _S; n++) { // loop over Fock State labels
            Complex nextcdot = _nextc[n].timesi(-_Hfree.energies[n]); // cdot_n(t+dt) = -i*E_n*c_n(t+dt) + ...
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                nextcdot = nextcdot.plus(_nextc[h_mn.getKey()].timesi(-_lambda * h_mn.getValue())); // ...-i*Sum(H_mn*c_m(t+dt),{m})
            Complex nextnextcoeff = _c[n].plus(nextcdot.times(2 * _dt)); // c_n(t+2dt)=c_n(t)+2dt*cdot_n(t+dt)
            _nextc[n] = nextnextcoeff; // c_n(t+dt) -> c_n(t+2dt)
        }
        // swap to new coefficients
        _c = tempcoeffs; // c_n(t) -> c_n(t+dt)

        // increment time
        _time += _dt;
    }
    
}
