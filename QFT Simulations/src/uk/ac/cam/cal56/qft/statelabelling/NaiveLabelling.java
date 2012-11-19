package uk.ac.cam.cal56.qft.statelabelling;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cal56.qft.util.Combinatorics;

// This labelling scheme steps through states by particle number,
// but not by total momentum within particle number (hence "naive").
public class NaiveLabelling {

    // particle number is the amount of times one can
    // subtract choose(N+m-1,m) from i with increasing m
    public static int P(long i, int N) {

        if (i < 0 || N < 0)
            throw new IllegalArgumentException();
        int m = 0;
        while (!(i < 0)) {
            i -= Combinatorics.choose(N + m - 1, m);
            m++;
        }
        return m - 1;
    }

    // P({l_n}) = Sum(l_n,{n,0,N-1}) : i.e. sum of all elements
    public static int P(List<Integer> ls) {
        int result = 0;
        for (Integer l : ls)
            result += l;
        return result;
    }

    // something wrong here ...
    // i({l_n}) = S(N,P-1) + Sum(Product((Sum(l_n, {n,N-k-1,N-1})+m)/(m+1) ,{m,0,k}), {k,0,N-2})
    public static long index(List<Integer> ls) {
        int N = ls.size();
        long result = Combinatorics.S(N, P(ls) - 1);
        for (int k = 0; k < (N - 1); k++) {
            long product = 1;
            int divFactor = 2; // k + 1;
            for (int m = 0; m <= k; m++) {
                int sum = 0;
                for (int n = N - k - 1; n < N; n++)
                    sum += ls.get(n);
                product *= (sum + m);
                // continually divide by factors of dividing factorial to keep result from blowing up
                while (divFactor <= k+1 && product % divFactor == 0) {
                    product /= divFactor;
                    divFactor++;
                }
            }
            result += product;
        }
        return result;
    }

    public static List<Integer> labels(long i, int N) {
        List<Integer> ls = new ArrayList<Integer>();
        List<Long> js = new ArrayList<Long>();
        for (int n = 0; n < N; n++) {
            if (n > 0)
                js.add(js.get(n - 1) - Combinatorics.S(N + 1 - n, P(js.get(n - 1), N + 1 - n) - 1));
            else
                js.add(i);
            ls.add(label(js.get(n), N - n));
        }
        return ls;
    }

    private static int label(long i, int N) {
        if (N == 1)
            return (int) i; // type casting OK as this will be small
        for (int k = 1;; k++) {
            for (int m = 0; m < k; m++) {
                i -= Combinatorics.choose(N + m - 2, m);
                if (i < 0)
                    return k - (m + 1);
            }
        }
    }

}
