package uk.ac.cam.cal56.qft.interactingtheory;

import java.util.Arrays;
import java.util.Map.Entry;

import uk.ac.cam.cal56.graphics.DensityPlot;
import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FFT;
import uk.ac.cam.cal56.maths.FourierTransform;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class QuantumState implements State {

    private final int                    N;              // number of lattice points
    private final double                 dt;             // time step
    private double                       _lambda;        // interaction strength
    private final FreeHamiltonian        _Hfree;         // free theory Hamiltonian
    private final InteractionHamiltonian _Hint;          // interaction Hamiltonian
    private int                          _S;             // S(N,P) = total number of Fock state coefficients

    private double                       _time;
    private Complex[]                    _nextcoeffs;    // {c_n(t+dt)}
    private Complex[]                    coeffs;         // {c_n(t)}

    private FourierTransform             _ft = new FFT();

    public QuantumState(int N, int Pmax, double m, double dx, double dt, double lambda) {
        // initialise parameters
        this.N = N;
        this.dt = dt;
        this._lambda = lambda;
        _S = Combinatorics.S(N, Pmax);

        // set coefficients
        coeffs = new Complex[_S];
        _nextcoeffs = new Complex[_S];

        // calculate free theory energies and interaction amplitudes
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx);
        _Hint = new FastInteractionHamiltonian(N, Pmax, m, dx, Interaction.PHI_THIRD);
        _Hint.calculateElements(); // calculate elements in the Interaction Hamiltonian

        // set coefficients (to pure vacuum) and do first step
        reset();
    }

    // first order method for first step (calculates _nextcoeffs)
    private void firstStep() {
        for (int n = 0; n < _S; n++) { // loop over state labels
            Complex sum = coeffs[n].times(_Hfree.energies[n]);
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet()) {
                sum = sum.plus(coeffs[h_mn.getKey()].times(_lambda * h_mn.getValue()));
            }
            // i*cdot_n(t) = E_n*c_n(t) + Sum(H_mn*c_m(t),{m})
            _nextcoeffs[n] = coeffs[n].plus(sum.timesi(-dt)); // c_n(t+dt) = c_n(t) + dt*cdot_n(t)
        }
    }

    public void step() {
        step2ndOrderNonSymplectic();
    }

    private void step2ndOrderNonSymplectic() {
        Complex[] tempcoeffs = _nextcoeffs; // temporary buffer for later swap-over
        for (int n = 0; n < _S; n++) { // loop over state labels
            Complex nextcdot = _nextcoeffs[n].timesi(-_Hfree.energies[n]); // cdot_n(t+dt) = -i*E_n*c_n(t+dt) + ...
            for (Entry<Integer, Double> h_mn : _Hint.getRow(n).entrySet())
                nextcdot = nextcdot.plus(_nextcoeffs[h_mn.getKey()].timesi(-_lambda * h_mn.getValue())); // ...-i*Sum(H_mn*c_m(t+dt),{m})
            Complex nextnextcoeff = coeffs[n].plus(nextcdot.times(2 * dt)); // c_n(t+2dt)=c_n(t)+2dt*cdot_n(t+dt)
            _nextcoeffs[n] = nextnextcoeff; // c_n(t+dt) -> c_n(t+2dt)
        }
        coeffs = tempcoeffs; // c_n(t) -> c_n(t+dt) : swap-over
        _time += dt;
    }

    public void reset() {
        _time = 0.0;

        // vacuum
        // coeffs[0] = Complex.one();
        // for (int i = 1; i < _S; i++) coeffs[i] = Complex.zero();

        // gaussian particle
        coeffs[0] = Complex.zero();
        double sigma = N / 5.0;
        double mu = N / 2.0;
        for (int p = 0; p < N; p++) {
            double z = (p - mu) / sigma;
            double gaussian = Math.exp(-z * z / 2.0) / sigma;
            coeffs[p + 1] = Complex.one().times(gaussian);
        }

        for (int i = N + 1; i < _S; i++)
            coeffs[i] = Complex.zero();
        firstStep();
    }

    public double getTime() {
        return _time;
    }

    public double get0P() {
        return coeffs[0].modSquared();
    }

    public double[] get1PMomenta() {
        double[] probs = new double[N];
        for (int p = 0; p < N; p++)
            probs[p] = coeffs[p + 1].modSquared(); // +1 in order to avoid the 0P vacuum state
        return probs;
    }

    public double[][] get2PMomenta() {
        double[][] probs = new double[N][N];
        for (int p = 0; p < N; p++)
            for (int q = p; q < N; q++) {
                double value = coeffs[StateLabelling.index(Arrays.asList(p, q), N)].modSquared();
                probs[p][q] = value; // +(1+N) in order to avoid 0P and 1P states
                if (p != q)
                    probs[q][p] = value;
            }
        return probs;
    }

    public double[] get1PPositions() {
        double[] probs = new double[N];
        Complex[] transformed = _ft.transform(Arrays.copyOfRange(coeffs, 1, N + 1));
        for (int p = 0; p < N; p++)
            probs[p] = transformed[p].modSquared();
        return probs;
    }

    public double[][] get2PPositions() {
        Complex[][] cs = new Complex[N][N];

        for (int p = 0; p < N; p++)
            for (int q = p; q < N; q++) {
                Complex c = coeffs[StateLabelling.index(Arrays.asList(p, q), N)];
                cs[p][q] = c;
                if (p != q)
                    cs[q][p] = c;
            }
        double[][] probs = new double[N][N];
        cs = _ft.transform2D(cs);
        for (int p = 0; p < N; p++)
            for (int q = p; q < N; q++) {
                double value = cs[p][q].modSquared();
                probs[p][q] = value;
                if (p != q)
                    probs[q][p] = value;
            }
        return probs;
    }

    @Override
    public void setInteractionStrength(double lambda) {
        _lambda = lambda;
    }

    @Override
    public void updatePlots(Plot p0m, Plot p0p, Plot p1m, Plot p1p, DensityPlot p2m, DensityPlot p2p) {
        // TODO Auto-generated method stub
    }
}
