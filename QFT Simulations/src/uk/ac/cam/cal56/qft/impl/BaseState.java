package uk.ac.cam.cal56.qft.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;

public abstract class BaseState implements State {

    protected final int                     _N;                                                     // number of
                                                                                                     // lattice points
    protected double                        _dt;                                                    // time step
    protected int                           _S;                                                     // S(N, Pmax) =
    // number of
    // coefficients
    protected FreeHamiltonian               _Hfree;                                                 // free theory
                                                                                                     // Hamiltonian
    protected Map<Interaction, Hamiltonian> _hamiltonians = new HashMap<Interaction, Hamiltonian>(); // interaction
                                                                                                     // Hamiltonians
    protected Map<Interaction, Double>      _lambdas;                                               // interaction
    // strength

    protected double                        _time;
    protected Complex[]                     _c;                                                     // {c_n(t)}

    private WavePacket                      _wavePacket;

    protected BaseState(int N, int Pmax, double m, double dx, double dt, Map<Interaction, Double> lambdas, WavePacket wp) {
        _N = N;
        _dt = dt;
        _lambdas = lambdas;
    }

    protected abstract void firstStep();

    @Override
    public abstract void step();

    @Override
    public void step(int numSteps) {
        for (int i = 0; i < numSteps; i++)
            step();
    }

    @Override
    public void reset() {
        _c = _wavePacket.getCoefficients(_S);
        _time = 0.0;
        firstStep();
    }

    @Override
    public void setWavePacket(WavePacket wavePacket) {
        _wavePacket = wavePacket;
        reset();
    }

    @Override
    public int getN() {
        return _N;
    }

    @Override
    public double getTime() {
        return _time;
    }

    @Override
    public double getModSquared() {
        double sum = 0.0;
        for (int n = 0; n < _S; n++)
            sum += _c[n].modSquared();
        return sum;
    }

    @Override
    public Complex getVacuum() {
        return _c[0];
    }

    @Override
    public Double getRemainingProbability() {
        int S2 = ScalarFockState.S(_N, 2);
        if (S2 >= _S)
            return null; // if only 2 particles, return null
        double probSquared = 0;
        for (int n = S2; n < _S; n++)
            probSquared += _c[n].modSquared(); // add up remaining probabilities
        return probSquared;
    }

    @Override
    public void setInteractionStrength(Interaction interaction, double lambda) {
        _lambdas.put(interaction, lambda);
        firstStep();
    }

    @Override
    public void setTimeStep(double dt) {
        _dt = dt;
        firstStep();
    }
    
    @Override
    public void setToGroundState() {
        // TODO
    }
    
    public double getTotalEnergy() {
     
        Complex energy = Complex.zero();
        for(int n = 0; n < _c.length; n++) {
         // first add free theory
            Complex sum = _c[n].times(_Hfree.getEnergy(n));
    
            // then interactions
            for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                Complex subsum = Complex.zero();
                for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                    subsum = subsum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
                sum = sum.plus(subsum.times(_lambdas.get(h.getKey())));
            }
            
            // E = <c|(H|c>)
            energy = energy.plus(_c[n].conj().times(sum));
        }
        
        // assume this is real, ignore imaginary part (mod is mathematically equivalent)
        return energy.real();
    }

}
