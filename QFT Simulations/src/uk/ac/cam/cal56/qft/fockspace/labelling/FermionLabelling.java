package uk.ac.cam.cal56.qft.fockspace.labelling;

import java.util.List;

import uk.ac.cam.cal56.qft.fockspace.impl.ComponentFockState;
import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;

public class FermionLabelling {

    // given fermion ladder operators (b's and c's) and the lattice number (N)
    // this function returns the label
    public static int label(List<Integer> bs, List<Integer> cs, int N) { // b's and c's: creation ladder operators
        int bP = bs.size(); // particle number
        int cP = cs.size(); // antiparticle number
        int P = bP + cP; // total particle number

        // get to right particle number P
        // int sum = FermionFockState.S(N, P - 1);
        int sum = FermionFockState.S(N, P - 1);

        // get to right normal particle number bP
        for (int i = P; i > bP; i--)
            sum += ComponentFockState.sigma(N, i) * ComponentFockState.sigma(N, P - i);

        // find contribution from normal particles
        int bsum = 0;
        for (int p = 0; p < bP; p++)
            for (int m = 0; m < bs.get(p) - (p > 0 ? bs.get(p - 1) + 1 : 0); m++)
                bsum += ComponentFockState.sigma(N - 1 - (p > 0 ? bs.get(p - 1) + 1 : 0) - m, (bP - 1) - p);
        sum += ComponentFockState.sigma(N, cP) * bsum; // multiply by number of antiparticles per particle state

        // find contribution from antiparticles
        for (int p = 0; p < cP; p++)
            for (int m = 0; m < cs.get(p) - (p > 0 ? cs.get(p - 1) + 1 : 0); m++)
                sum += ComponentFockState.sigma(N - 1 - (p > 0 ? cs.get(p - 1) + 1 : 0) - m, (cP - 1) - p);

        // return result
        return sum;
    }

}
