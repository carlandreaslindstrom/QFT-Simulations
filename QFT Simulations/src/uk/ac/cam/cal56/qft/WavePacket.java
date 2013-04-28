package uk.ac.cam.cal56.qft;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;

public abstract class WavePacket {

    protected static final double PEAK_PROBABILITY_DEFAULT = 0.3;

    protected final int           _N;
    protected final double        _peakProbability;
    protected final int[]         _particles;
    protected final double[]      _phases;

    protected WavePacket(int N, int[] particles, double[] phases, double peakProbability) {
        _N = N;
        _peakProbability = peakProbability;
        _particles = particles;
        _phases = phases;
        if (particles.length != phases.length || particles.length > 2)
            throw new RuntimeException();
    }

    public static WavePacket getVacuum(int N) {
        return new MomentumWavePacket(N);
    }

    public abstract Complex[] getCoefficients(int S);

}
