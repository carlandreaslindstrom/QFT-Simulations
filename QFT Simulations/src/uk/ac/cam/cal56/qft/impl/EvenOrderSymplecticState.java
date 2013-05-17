package uk.ac.cam.cal56.qft.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;

public abstract class EvenOrderSymplecticState extends BaseState {

    private final int _K;    // global order, (local order = _K + 2)

    private Complex[] _prevc;

    protected EvenOrderSymplecticState(int order, int N, double dt, double dx, double m,
            Map<Interaction, Double> lambdas) {
        super(N, dt, dx, m, lambdas);
        _K = order - (order % 2); // ensure even numbers and add 2 due to accumulative effects of integration
    }

    // backward Euler method of order _K
    @Override
    protected void firstStep() {
        _prevc = Arrays.copyOf(_c, _S);
        Complex[] lastderiv = Arrays.copyOf(_c, _S);
        Complex[] currentderiv = new Complex[_S];

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
                currentderiv[n] = sum.timesi(-1);

                _prevc[n] = _prevc[n].plus(currentderiv[n].times(factor));
            }
            lastderiv = currentderiv;
        }
    }

    @Override
    public void step() {
        Complex[] nextc = Arrays.copyOf(_prevc, _S);
        Complex[] lastderiv = Arrays.copyOf(_c, _S);
        Complex[] currentderiv = new Complex[_S];

        double lastfactor = 2.0;

        // loops through all derivatives d^k/dt^k
        for (int k = 1; k <= _K; k++) {
            lastfactor = 2.0 * Math.pow(_dt, k - 1) / Combinatorics.factorial(k - 1);

            for (int n = 0; n < _S; n++) {

                // add to sum only if odd order (adds from last iteration, hence k even here)
                if (k % 2 == 0) {
                    nextc[n] = nextc[n].plus(lastderiv[n].times(lastfactor));
                    if(k == _K) continue; // jump on if last k iteration (recalculation of derivs not needed)
                }
                
                // first add free theory
                Complex sum = lastderiv[n].times(_Hfree.getEnergy(n));

                // then interactions
                for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                    Complex subsum = Complex.zero();
                    for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                        subsum = subsum.plus(lastderiv[h_mn.getKey()].times(h_mn.getValue()));
                    sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
                }
                currentderiv[n] = sum.timesi(-1);
            }
            lastderiv = currentderiv; // delete last, recast to current, then start over
        }
        
        _prevc = _c;
        _c = nextc;

        _time += _dt;
    }

}
