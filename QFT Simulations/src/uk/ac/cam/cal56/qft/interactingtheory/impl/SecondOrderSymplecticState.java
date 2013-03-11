package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.Hamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.WavePacket;

public class SecondOrderSymplecticState extends BaseState {

    private Complex[] _prevc;    // c_n(t-dt)

    public SecondOrderSymplecticState(int N, int Pmax, double m, double dx, double dt, Map<Interaction, Double> lambdas) {
        super(N, Pmax, m, dx, dt, lambdas, new MomentumWavePacket(N));
    }

    public SecondOrderSymplecticState(int N, int Pmax, double m, double dx, double dt,
            Map<Interaction, Double> lambdas, WavePacket wavepacket) {
        super(N, Pmax, m, dx, dt, lambdas, wavepacket);
    }

    // first order Euler backward step to calculate _prevc
    @Override
    protected void firstStep() {
        _prevc = new Complex[_S];
        for (int n = 0; n < _S; n++) { // loop over state labels

            // first add free theory
            Complex sum = _c[n].times(_Hfree.getEnergy(n));

            // then interactions
            for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                Complex subsum = Complex.zero();
                for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                    subsum = subsum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
                sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
            }
            Complex cdot = sum.timesi(-1);

            // c_n(-dt) = c_n(0) - dt*cdot_n(0)
            _prevc[n] = _c[n].minus(cdot.times(_dt));
        }
    }

    // second order symplectic stepping algorithm (mid point) to calculate
    @Override
    public void step() {
        Complex[] nextc = new Complex[_S]; // next coefficients
        for (int n = 0; n < _S; n++) { // loop over all states

            // first add free theory
            Complex sum = _c[n].times(_Hfree.getEnergy(n));

            // then interactions
            for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                Complex subsum = Complex.zero();
                for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                    subsum = subsum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
                sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
            }
            Complex cdot = sum.timesi(-1);

            // c_n(t+dt)=c_n(t-dt)+2dt*cdot_n(t)
            nextc[n] = _prevc[n].plus(cdot.times(2 * _dt));
        }

        // swap to new coefficients
        _prevc = _c;
        _c = nextc;

        // increment time
        _time += _dt;
    }

}
