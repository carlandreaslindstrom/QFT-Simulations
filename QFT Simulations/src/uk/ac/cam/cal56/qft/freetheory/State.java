package uk.ac.cam.cal56.qft.freetheory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class State {

    private double        _m;
    private double        _dx;
    private int           _N;
    private long          _coeffnum;

    private List<Complex> _nextcoeffs; // {c_n(t+dt)}

    public double         t;
    public List<Complex>  coeffs;     // {c_n(t)}

    public State(int N, int P, double m, double dx) {
        _N = N;
        _m = m;
        _dx = dx;
        t = 0.0;
        _coeffnum = Combinatorics.S(N, P);
        coeffs = new ArrayList<Complex>(Collections.nCopies((int) _coeffnum, Complex.zero()));
        coeffs.set(0, Complex.one());
        _nextcoeffs = new ArrayList<Complex>();
    }

    // steps the state by dt using an Euler first order method
    public void stepFirst(double dt) {
        for (int n = 0; n < _coeffnum; n++) { // loop over lattice points
            Complex cdot = (coeffs.get(n)).times(-energy(n)).timesi(); // i*cdot_n(t) = E_n*c_n(t)
            Complex coeff = (coeffs.get(n)).plus(cdot.times(dt));      // c_n(t+dt) = c_n(t) + dt*cdot_n(t)
            _nextcoeffs.add(coeff);
        }
    }

    // steps the state by dt using a symplectic algorithm
    public void step(double dt) {
        List<Complex> tempcoeffs = new ArrayList<Complex>();
        for (int n = 0; n < _coeffnum; n++) { // loop over lattice points
            Complex nextcoeff = _nextcoeffs.get(n); // c_n(t+dt)
            Complex nextcdot = nextcoeff.times(-energy(n)).timesi(); // cdot_n(t+dt) = -i*E_n*c_n(t+dt)
            Complex nextnextcoeff = (coeffs.get(n)).plus(nextcdot.times(2 * dt)); // c_n(t+2dt)=c_n(t)+2dt*cdot_n(t+dt)
            tempcoeffs.add(nextcoeff); // c_n(t) -> c_n(t+dt)
            _nextcoeffs.set(n, nextnextcoeff); // c_n(t+dt) -> c_n(t+2dt)
        }
        coeffs = tempcoeffs;
        t += dt;
    }

    // step by exact solution (only free theory case: phasor rotation)
    public void stepExact(double dt) {
        for (int n = 0; n < _coeffnum; n++) { // loop over lattice points
            Complex coeff = (coeffs.get(n)).timesexpi(-energy(n) * dt); // c_n(t+dt) = exp(i*E_n*dt)*c_n(t)
            coeffs.set(n, coeff);
        }
        t += dt;
    }

    public void printCoeffs() {
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.print(df.format(t) + " - ");
        for (int n = 0; n < _coeffnum; n++) {
            Complex coeff = coeffs.get(n);
            if (!coeff.isZero()) {
                System.out.print(" [" + n + "]:" + coeff.toString());
            }
        }
        System.out.println();
    }

    public double modSquared() {
        double ms = 0.0;
        for (Complex coeff : coeffs) {
            ms += coeff.modSquared();
        }
        return ms;
    }

    private double energy(int index) {
        int momnum = StateLabelling.momentumNumber(index, _N);
        double pterm = (2 / _dx) * Math.sin(Math.PI * momnum / _N);
        return Math.sqrt(_m * _m + pterm * pterm);
    }

}
