package uk.ac.cam.cal56.maths.impl;

import uk.ac.cam.cal56.maths.Complex;

// Using DFT: Discrete Fourier Transform algorithm.
// Source used: http://www.cs.cf.ac.uk/Dave/Vision_lecture/node20.html

public class DFT extends BaseFT {

    // transforms f(x) to F(p)
    // Performance: O(N^2) if f(x) is N long
    @Override
    public Complex[] transform(Complex[] f) {
        int N = f.length;
        Complex[] F = new Complex[N];
        for (int p = 0; p < N; p++) {
            Complex element = Complex.zero();
            for (int x = 0; x < N; x++)
                element = element.plus(f[x].timesexpi(2 * Math.PI * p * x / N));
            F[p] = element.divide(Math.sqrt(N));
        }
        return F;
    }

    // inverse transforms F(p) to f(x)
    @Override
    public Complex[] inversetransform(Complex[] F) {
        int N = F.length;
        Complex[] f = new Complex[N];
        for (int i = 0; i < N; i++)
            f[i] = F[i].conj();
        f = transform(f);
        for (int i = 0; i < N; i++)
            f[i] = f[i].conj();
        return f;
    }
}
