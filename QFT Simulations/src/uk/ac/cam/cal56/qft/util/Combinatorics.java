package uk.ac.cam.cal56.qft.util;

public class Combinatorics {

    // choose(n,r) = n!/(r!(n-r)!)
    public static long choose(int n, int r) {
        if(r<0) return 0;
        long result = 1L;
        int divFactor = r;
        for (int multfactor = n; multfactor > (n - r); multfactor--) {
            result *= multfactor;
            // continually divide by factors of dividing factorial to keep result from blowing up
            while (divFactor > 1 && result % divFactor == 0) {
                result /= divFactor;
                divFactor--;
            }
        }
        return result;
    }

    // S(N,P) = Sum(Choose(N+m-1,m), {m,0,P})
    public static long S(int N, int P) {
        long result = 0;
        for (int m = 0; m <= P; m++)
            result += choose(N + m - 1, m);
        return result;
    }
}
