package uk.ac.cam.cal56.qft.util;

public class Combinatorics {

    // Number of combinations possible of r elements out of n choices
    // choose(n,r) = n!/(r!(n-r)!)
    public static int choose(int n, int r) {
        if (r < 0)
            return 0;
        int result = 1;
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

    // same with longs (can handle bigger numbers), use by passing a long argument
    public static long choose(long n, int r) {
        if (r < 0)
            return 0;
        long result = 1;
        int divFactor = r;
        for (int multfactor = (int) n; multfactor > (n - r); multfactor--) {
            result *= multfactor;
            // continually divide by factors of dividing factorial to keep result from blowing up
            while (divFactor > 1 && result % divFactor == 0) {
                result /= divFactor;
                divFactor--;
            }
        }
        return result;
    }

    // Number of distinct Fock states with N lattice points and maximally P particles
    // S(N,P) = Sum(Choose(N+m-1,m), {m,0,P})
    public static int S(int N, int P) {
        int result = 0;
        for (int m = 0; m <= P; m++)
            result += choose(N + m - 1, m);
        return result;
    }

    // same with longs (can handle bigger numbers), use by passing a long argument
    public static long S(long N, int P) {
        long result = 0;
        for (int m = 0; m <= P; m++)
            result += choose(N + m - 1, m);
        return result;
    }

    // Combinatoric factor from a given momentum from sandwiching a state of l particles
    // in that momentum with n creation operators and m annihilation operators. Complete
    // combinatoric factor is obtained by the product these for all momenta.
    // F_p(l,n,m) = sqrt( (Product[a=1->n](l-m+a)) * (Product[b=1->m](l+1-b) )
    public static double F_p(int l, int n, int m) {
        if (m > l)
            return 0;
        int square = 1;
        for (int a = 1; a <= n; a++)
            square *= (l - m + a);
        for (int b = 1; b <= m; b++)
            square *= (l + 1 - b);
        return Math.sqrt(square);
    }

}
