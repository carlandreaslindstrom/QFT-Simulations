package uk.ac.cam.cal56.qft.interactingtheory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;

public class QuantumState {

    public final int               N;          // number of lattice points
    public final int               Pmax;       // maximum number of particles in Fock states
    public final double            dt;         // time step
    public final double            dx;         // lattice spacing
    public final double            m;          // particle mass
    public double lambda; // interaction strength
    private FreeHamiltonian        _Hfree;
    private InteractionHamiltonian _Hint;      // interaction Hamiltonian
    private int                    _S;         // S(N,P) = total number of Fock state coefficients

    private double                 _time = 0.0;
    private Map<Integer, Complex>  _nextcoeffs; // {c_n(t+dt)}
    public Map<Integer, Complex>   coeffs;     // {c_n(t)}

    public QuantumState(int N, int Pmax, double m, double dt, double dx) {
        // initialise parameters
        this.N = N;
        this.Pmax = Pmax;
        this.m = m;
        this.dt = dt;
        this.dx = dx;
        _S = Combinatorics.S(N, Pmax);
        coeffs = new HashMap<Integer, Complex>();
        _Hfree = new FreeHamiltonian(N, Pmax, m, dx);
        _Hint = new InteractionHamiltonian(N, Pmax, m, dx, Interaction.PHI_THIRD);

        // calculate elements in the Interaction Hamiltonian
        _Hint.calculateElements();

        // do the first (Euler) step
        firstStep();
    }

    // first order method for first step (calculated _nextcoeffs)
    private void firstStep() {
        for (int n = 0; n < _S; n++) { // loop over
            Complex minussum = (coeffs.get(n)).times(_Hfree.energies[n]);
            for (Entry<Integer, Double> h : _Hint.getRow(n).entrySet()) {
                minussum = minussum.minus(coeffs.get(h.getKey()).times(h.getValue()));
            }
            Complex cdot = minussum.timesi(); // i*cdot_n(t) = E_n*c_n(t) + Sum(H_mn*c_m(t),{m})
            Complex coeff = (coeffs.get(n)).plus(cdot.times(dt));      // c_n(t+dt) = c_n(t) + dt*cdot_n(t)
            _nextcoeffs.put(n, coeff);
        }
    }

    public void step() {
        // TODO: step state using interaction Hamiltonian
        _time += dt;
    }

    public double getTime() {
        return _time;
    }

    public double getVacuum() {
        return coeffs.get(0).modSquared();
    }

    public double[] getOneParticle() {
        double[] probs = new double[N];
        for (int p = 0; p < N; p++)
            probs[p] = coeffs.get(p + 1).modSquared(); // [p+1] in order to avoid vacuum state [0]
        return probs;
    }

}
