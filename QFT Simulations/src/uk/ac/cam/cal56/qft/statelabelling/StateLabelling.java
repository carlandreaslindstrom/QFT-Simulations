package uk.ac.cam.cal56.qft.statelabelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cal56.maths.Combinatorics;

// This labelling scheme steps through states by particle number,
// but not by total momentum within particle number (hence "naive").
public class StateLabelling {

    // particle number is the amount of times one can
    // subtract choose(N+m-1,m) from i with increasing m
    public static int P(int i, int N) {
        int p = 0;
        for (; i >= 0; i -= Combinatorics.sigma(N, p++));
        return p - 1;
    }

    // P({l_n}) = Sum(l_n,{n,0,N-1}) : i.e. sum of all elements
    public static int P(List<Integer> exponents) {
        int sum = 0;
        for (Integer exponent : exponents)
            sum += exponent;
        return sum;
    }

    // p_tot({l_n}) = Sum(n*l_n,{n,0,N-1})
    public static int momentumNumber(List<Integer> exponents) {
        int result = 0;
        int N = exponents.size();
        for (int n = 1; n < N; n++) {
            result += n * exponents.get(n);
            result %= N;
        }
        return result;
    }

    public static int momentumNumber(int i, int N) {
        int sum = 0;
        List<Integer> js = new ArrayList<Integer>();
        for (int n = 0; n < N; n++) {
            if (n > 0)
                js.add(js.get(n - 1) - Combinatorics.S(N + 1 - n, P(js.get(n - 1), N + 1 - n) - 1));
            else
                js.add(i);
            sum += n * label(js.get(n), N - n);
            sum %= N;
        }
        return sum;
    }

    // i({l_n}) = S(N,P-1) + Sum(Product((Sum(l_n, {n,N-k-1,N-1})+m)/(m+1) ,{m,0,k}), {k,0,N-2})
    public static int index(List<Integer> ls) {
        int N = ls.size();
        int result = Combinatorics.S(N, P(ls) - 1);
        for (int k = 0; k < (N - 1); k++) {
            long product = 1;
            int divFactor = 2; // k + 1;
            for (int m = 0; m <= k; m++) {
                int sum = 0;
                for (int n = N - k - 1; n < N; n++)
                    sum += ls.get(n);
                product *= (sum + m);
                // continually divide by factors of dividing factorial to keep result from blowing up
                while (divFactor <= k + 1 && product % divFactor == 0) {
                    product /= divFactor;
                    divFactor++;
                }
            }
            result += product;
        }
        return result;
    }

    public static List<Integer> labels(int i, int N) {
        List<Integer> ls = new ArrayList<Integer>(), js = new ArrayList<Integer>();
        for (int n = 0; n < N; n++) {
            if (n > 0)
                js.add(js.get(n - 1) - Combinatorics.S(N + 1 - n, P(js.get(n - 1), N + 1 - n) - 1));
            else
                js.add(i);
            ls.add(label(js.get(n), N - n));
        }
        return ls;
    }

    // used by the above label function
    private static int label(int i, int N) {
        if (N == 1)
            return i;
        for (int k = 1;; k++) {
            for (int m = 0; m < k; m++) {
                i -= Combinatorics.choose(N + m - 2, m);
                if (i < 0)
                    return k - (m + 1);
            }
        }
    }

    public static int index(List<Integer> e, int N) {
        int P = e.size();
        int sum = Combinatorics.S(N, P - 1);
        for (int p = 0; p < P; p++)
            for (int m = 0; m < e.get(p) - (p > 0 ? e.get(p - 1) : 0); m++)
                sum += Combinatorics.sigma(N - (p > 0 ? e.get(p - 1) : 0) - m, (P - 1) - p);
        return sum;
    }

    public static Map<Integer, int[]> toLadderOpMap(int[] cs, int[] as, int N) {
        Map<Integer, int[]> ops = new HashMap<Integer, int[]>(); // [0] = creators {n_p}, [1] = annihilators {m_p}
        for (int c : cs) {
            c = mod(c, N);
            int[] op = ops.get(c);
            if (op == null)
                op = new int[] { 1, 0 };
            else
                op = new int[] { op[0] + 1, op[1] };
            ops.put(c, op);
        }
        for (int a : as) {
            a = mod(a, N);
            int[] op = ops.get(a);
            if (op == null)
                op = new int[] { 0, 1 };
            else
                op = new int[] { op[0], op[1] + 1 };
            ops.put(a, op);
        }
        return ops;
    }

    private static int mod(int p, int N) {
        return (p + 2 * N) % N;
    }

    // returns the index of the only bra which gives a non-trivial state sandwich
    public static Integer braIndex(List<Integer> ketEntries, int[] creators, int[] annihilators, int N) {
        List<Integer> ks = new ArrayList<Integer>(ketEntries);
        for (int a : annihilators) {
            a = (a + 2 * N) % N;
            if (ks.contains(a))
                ks.remove((Integer) a); // first subtract annihilators
            else
                return null; // stop if annihilating entire state
        }
        for (int c : creators)
            ks.add((c + 2 * N) % N); // then add creators
        Collections.sort(ks);
        return StateLabelling.index(ks, N);
    }
}
