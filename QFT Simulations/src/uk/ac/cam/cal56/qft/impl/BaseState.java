package uk.ac.cam.cal56.qft.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.WavePacket;

public abstract class BaseState implements State {

    protected final int                     _N;                                                     // # lattice points
    protected double                        _dt;                                                    // time step
    protected double                        _dx;                                                    // time step
    protected double                        _m;                                                     // time step
    protected int                           _S;                                                     // # coefficients
    protected FreeHamiltonian               _Hfree;                                                 // free theory H
    protected Map<Interaction, Hamiltonian> _hamiltonians = new HashMap<Interaction, Hamiltonian>(); // interaction H
    protected Map<Interaction, Double>      _lambdas;                                               // int. strengths

    protected double                        _time;
    protected Complex[]                     _c;                                                     // coeffs

    private WavePacket                      _wavePacket;

    protected BaseState(int N, double dt, double dx, double m, Map<Interaction, Double> lambdas) {
        _N = N;
        _dt = dt;
        _dx = dx;
        _m = m;
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
        return Complex.normSquared(_c);
    }

    @Override
    public Complex getVacuum() {
        return _c[0];
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
        setToEigenState(0);
    }

    @Override
    public void setToFirstState() {
        setToEigenState(1);
    }

    @Override
    public void setToSecondState() {
        setToEigenState(2);
    }

    public void setToEigenState(int eigennumber) {
        // _wavePacket = WavePacket.getVacuum(_N); // set to vacuum
        // _c = _wavePacket.getCoefficients(_S);
        for (int n = 0; n < _S; n++)
            _c[n] = Complex.one().times(1.0 / _S);

        Complex[][] eigencs = new Complex[eigennumber + 1][_S];

        // power iteration parameters
        double multfactor = 5;
        double energyMax = multfactor * _Hfree.getMaxEnergy(); // TODO: find instead the actual largest eigenvalue
        for (double lambda : _lambdas.values())
            energyMax += multfactor * Math.abs(lambda) * _Hfree.getMaxEnergy();
        double tolerance = 1e-16;
        int maxcount = 100000;

        for (int eig = 0; eig <= eigennumber; eig++) {
            double energy = getTotalEnergy();
            double error = 1;

            for (int step = 0; step < maxcount && error > tolerance; step++) {

                double lastEnergy = energy;

                for (int n = 0; n < _S; n++) {

                    // first add free theory
                    Complex H = _c[n].times(_Hfree.getEnergy(n));

                    // then interactions
                    for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                        Complex subsum = Complex.zero();
                        for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet())
                            subsum = subsum.plus(_c[h_mn.getKey()].times(h_mn.getValue()));
                        H = H.plus(subsum.times(_lambdas.get(h.getKey())));
                    }

                    // |c> -> (1-e*H)|c>
                    eigencs[eig][n] = _c[n].minus(H.divide(energyMax));
                }

                // remove lower eigenstates by orthogonality
                for (int i = 0; i < eig; i++) {
                    Complex overlap = Complex.dotProduct(eigencs[i], eigencs[eig]);
                    // System.out.println(overlap.mod());
                    for (int n = 0; n < _S; n++)
                        eigencs[eig][n] = eigencs[eig][n].minus(eigencs[i][n].times(overlap));
                }
                Complex.normalise(eigencs[eig]); // normalise
                _c = eigencs[eig]; // set coefficients

                energy = getTotalEnergy();
                error = Math.abs((lastEnergy - energy) / energy);

                // System.out.println(error);
            }
        }

        _time = 0.0;
        firstStep();
    }

    public double getTotalEnergy() {

        double energy = 0;
        for (int n = 0; n < _c.length; n++) {
            // first add free theory
            double realsum = _c[n].real() * _Hfree.getEnergy(n);
            double imagsum = _c[n].imag() * _Hfree.getEnergy(n);

            // then interactions
            for (Entry<Interaction, Hamiltonian> h : _hamiltonians.entrySet()) {
                double realsubsum = 0, imagsubsum = 0;
                for (Entry<Integer, Double> h_mn : h.getValue().getRow(n).entrySet()) {
                    realsubsum += _c[h_mn.getKey()].real() * h_mn.getValue();
                    imagsubsum += _c[h_mn.getKey()].imag() * h_mn.getValue();
                }
                realsum += realsubsum * _lambdas.get(h.getKey());
                imagsum += imagsubsum * _lambdas.get(h.getKey());
            }

            // E = <c|(H|c>)
            energy += realsum * _c[n].real();
            energy += imagsum * _c[n].imag();
        }

        // assume this is real, ignore imaginary part (mod is mathematically equivalent)
        return energy;
    }

}
