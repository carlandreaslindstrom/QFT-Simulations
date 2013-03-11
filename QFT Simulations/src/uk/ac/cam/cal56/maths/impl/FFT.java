package uk.ac.cam.cal56.maths.impl;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FourierTransform;

// Copied from Robert Sedgewick and Kevin Wayne @ Princeton, then changed.
// Source: http://introcs.cs.princeton.edu/java/97data/FFT.java.html

public class FFT extends BaseFT {

    @Override
    public Complex[] transform(Complex[] f) {
        int N = f.length;
        int Nnew = (int) Math.pow(2, Math.ceil(Math.log(N) / Math.log(2)));
        if (N != Nnew) { // if not a power of two, let the DFT do the work
            FourierTransform dft = new DFT();
            return dft.transform(f);
        }
        else
            return transform(f, N);
    }

    private Complex[] transform(Complex[] f, int norm) {
        int N = f.length;

        // base case and fix normalisation
        if (N == 1)
            return new Complex[] { f[0].divide(Math.sqrt(norm)) };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0)
            throw new RuntimeException("N is not a power of 2");

        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++)
            even[k] = f[2 * k];
        Complex[] q = transform(even, norm);

        // fft of odd terms
        Complex[] odd = even;  // reuse the array
        for (int k = 0; k < N / 2; k++)
            odd[k] = f[2 * k + 1];
        Complex[] r = transform(odd, norm);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            y[k] = q[k].plus(r[k].timesexpi(-kth));
            y[k + N / 2] = q[k].minus(r[k].timesexpi(-kth));
        }

        return y;
    }

}
