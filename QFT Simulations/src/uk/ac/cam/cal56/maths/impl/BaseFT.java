package uk.ac.cam.cal56.maths.impl;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FourierTransform;

public abstract class BaseFT implements FourierTransform {

    @Override
    public abstract Complex[] transform(Complex[] f);

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

    // transforms f(x,y) to F(p,q) - f(x,y) = f[x][y] = f.get(x).get(y)
    // Performance: O(NlnN) if f(x,y) is NxN
    @Override
    public Complex[][] transform2D(Complex[][] f) {
        int N = f.length, M = f[0].length;
        Complex[][] Atranspose = new Complex[M][N]; // A^T(p,y)

        for (int y = 0; y < M; y++) {
            Complex[] col = new Complex[N];
            for (int x = 0; x < N; x++)
                col[x] = f[x][y];
            Atranspose[y] = transform(col);
        }

        Complex[][] F = new Complex[N][M]; // F(p,q)
        for (int p = 0; p < N; p++) {
            Complex[] row = new Complex[M];
            for (int y = 0; y < M; y++)
                row[y] = Atranspose[y][p];
            F[p] = transform(row);
        }
        return F;
    }

    // transforms F(p,q) to f(x,y) - f(x,y) = f[x][y] = f.get(x).get(y)
    @Override
    public Complex[][] inversetransform2D(Complex[][] F) {
        int N = F.length, M = F[0].length;
        Complex[][] f = new Complex[N][M];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                f[i][j] = F[i][j].conj();
        f = transform2D(f);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                f[i][j] = f[i][j].conj();
        return f;
    }

}
