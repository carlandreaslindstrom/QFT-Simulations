package uk.ac.cam.cal56.qft.fockspace;

import java.util.Iterator;

public abstract class FockState implements Iterator<Integer>, Iterable<Integer> {

    protected int          _label = -1;                               // starts at -1
    protected final int    _N;
    protected final double _m;
    protected final double _dx;
    protected int          _S;

    protected FockState(int N, double m, double dx) {
        _N = N;
        _m = m;
        _dx = dx;
    }

    public int getLabel() {
        return _label;
    }

    // maximum label value (number of states in the Fock space)
    public int getS() {
        return _S;
    }

    public abstract double getEnergy();

    public abstract int getMomentumNumber();

    public abstract int getParticleNumber();

    @Override
    public boolean hasNext() {
        return _label < (_S - 1);
    }

    @Override
    public Iterator<Integer> iterator() {
        return this;
    }

    /**** STATIC FUNCTIONS ****/

    // E_p = sqrt(m^2 + (2/dx)^2*sin(p*dx/2)^2 )
    public static double E_p(int p, int N, double m, double dx) { // p = momentum number, not momentum
        double pterm = (2.0 / dx) * Math.sin(Math.PI * p / N);
        return Math.sqrt(m * m + pterm * pterm);
    }
}
