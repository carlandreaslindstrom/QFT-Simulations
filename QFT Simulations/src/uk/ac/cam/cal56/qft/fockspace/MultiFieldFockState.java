package uk.ac.cam.cal56.qft.fockspace;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;

public class MultiFieldFockState extends FockState {

    protected List<FermionFockState> _fermions = new ArrayList<FermionFockState>(); // fermion fields
    protected List<ScalarFockState>  _scalars  = new ArrayList<ScalarFockState>(); // scalar fields

    protected MultiFieldFockState(int numberOfFermions, int numberOfScalars, int N, int Pmax, double m, double dx) {
        super(N, m, dx);

        // add fermion fields
        for (int i = 0; i < numberOfFermions; i++)
            _fermions.add(new FermionFockState(N, Pmax, m, dx));

        // add scalar fields
        for (int i = 0; i < numberOfScalars; i++)
            _scalars.add(new ScalarFockState(N, Pmax, m, dx));
    }

    // total energy (sum of all fields)
    @Override
    public double getEnergy() {
        double energy = 0.0;
        for (ScalarFockState scalar : _scalars)
            energy += scalar.getEnergy();
        for (FermionFockState fermion : _fermions)
            energy += fermion.getEnergy();
        return energy;
    }

    // total momentum number (sum of all fields, mod N)
    @Override
    public int getMomentumNumber() {
        int momentumNumber = 0;
        for (ScalarFockState scalar : _scalars)
            momentumNumber += scalar.getMomentumNumber();
        for (FermionFockState fermion : _fermions)
            momentumNumber += fermion.getMomentumNumber();
        return momentumNumber % _N;
    }

    // total particle number (sum of all fields)
    @Override
    public int getParticleNumber() {
        int particleNumber = 0;
        for (ScalarFockState scalar : _scalars)
            particleNumber += scalar.getParticleNumber();
        for (FermionFockState fermion : _fermions)
            particleNumber += fermion.getParticleNumber();
        return particleNumber;
    }

    @Override
    public Integer next() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub

    }
}
