package uk.ac.cam.cal56.qft.fockspace.labelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;

public class ScalarLabelling {

    public static int label(List<Integer> as, int N) { // a's = creation ladder operators
        int aP = as.size(); // particle number
        int sum = ScalarFockState.S(N, aP - 1);
        for (int p = 0; p < aP; p++)
            for (int m = 0; m < as.get(p) - (p > 0 ? as.get(p - 1) : 0); m++)
                sum += ScalarFockState.sigma(N - (p > 0 ? as.get(p - 1) : 0) - m, (aP - 1) - p);
        return sum;
    }

    // convert lists of creation and annihilation operators to a Map (needed in fast calculation of Hamiltonian)
    public static Map<Integer, int[]> toLadderOperatorMap(int[] cs, int[] as, int N) {
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
    public static Integer braIndex(List<Integer> ketParticles, int[] adaggers, int[] as, int N) {
        List<Integer> braParticles = new ArrayList<Integer>(ketParticles);
        for (int a : as) {
            a = mod(a, N);
            if (braParticles.contains(a))
                braParticles.remove((Integer) a); // first subtract annihilators
            else
                return null; // stop if annihilating entire state
        }
        for (int adagger : adaggers)
            braParticles.add(mod(adagger, N)); // then add creators
        Collections.sort(braParticles);
        return label(braParticles, N);
    }
}
