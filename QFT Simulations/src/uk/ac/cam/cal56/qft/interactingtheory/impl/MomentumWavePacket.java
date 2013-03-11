package uk.ac.cam.cal56.qft.interactingtheory.impl;

import java.util.Arrays;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.interactingtheory.WavePacket;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class MomentumWavePacket extends WavePacket {

    public MomentumWavePacket(int N) {
        this(N, new int[] {}, new double[] {});
    }

    public MomentumWavePacket(int N, int[] particles, double[] phases) {
        this(N, particles, phases, PEAK_PROBABILITY_DEFAULT);
    }

    public MomentumWavePacket(int N, int[] particles, double[] phases, double peakProbability) {
        super(N, particles, phases, peakProbability);
    }

    public Complex[] getCoefficients(int S) {
        Complex[] coeffs = new Complex[S];

        // 0 particles = vacuum
        if (_particles.length == 0)
            coeffs[0] = Complex.one();
        else
            coeffs[0] = Complex.zero();

        // 1 particle
        if (_particles.length == 1) {

            double sigma = 1.0 / (Math.sqrt(Math.PI) * _peakProbability);
            int pPeak = _particles[0];
            double[] values = new double[_N];
            double norm = 0.0;
            for (int p = 0; p < _N; p++) {
                double z1 = (p - pPeak) / sigma;
                double z2 = (p - _N - pPeak) / sigma;
                double z3 = (p + _N - pPeak) / sigma;
                double value = Math.sqrt(_peakProbability) *
                               (Math.exp(-z1 * z1 / 2) + Math.exp(-z2 * z2 / 2) + Math.exp(-z3 * z3 / 2));
                values[p] = value;
                norm += value * value;
            }
            norm = Math.sqrt(norm);

            for (int i = 0; i < _N; i++)
                coeffs[i + 1] = Complex.expi(-_phases[0] * i).times(values[i] / norm);
        }
        else {
            for (int i = 0; i < _N; i++)
                coeffs[i + 1] = Complex.zero();
        }

        // 2 particles

        // find start and stop labels for 2P states
        int S2 = Combinatorics.S(_N, 1);
        int S3 = Combinatorics.S(_N, 2);

        if (_particles.length == 2 && S3 <= S) {
            // set Gaussian wavepacket width (sigma)
            double sigma = 1.0 / (Math.sqrt(Math.PI) * _peakProbability);
            int pPeak = Math.min(_particles[0], _particles[1]);
            int qPeak = Math.max(_particles[0], _particles[1]);

            // set values
            Complex[] values = new Complex[S3 - S2];
            double norm = 0.0;
            for (int p = 0; p < _N; p++) {
                double z1p = (p - pPeak) / sigma;
                for (int q = p; q < _N; q++) {
                    double z1q = (q - qPeak) / sigma;
                    double value = Math.sqrt(_peakProbability) * (Math.exp(-(z1p * z1p + z1q * z1q) / 2));
                    int i = StateLabelling.index(Arrays.asList(p, q), _N) - S2;
                    values[i] = Complex.expi(-_phases[0] * p - _phases[1] * q).times(value);
                    norm += value * value;
                }
            }
            // find normalisation (sqrt of all values squared)
            norm = Math.sqrt(norm);

            // set coefficients
            for (int i = S2; i < S3; i++) {
                coeffs[i] = values[i - S2].divide(norm);
            }

        }
        else if (S3 <= S) {
            for (int i = S2; i < S3; i++)
                coeffs[i] = Complex.zero();
        }

        // TODO: implement possibility for more particles
        // rest of coefficients
        for (int i = S3; i < S; i++)
            coeffs[i] = Complex.zero();

        return coeffs;

    }

}
