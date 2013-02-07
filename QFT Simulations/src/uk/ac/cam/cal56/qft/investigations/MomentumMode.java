package uk.ac.cam.cal56.qft.investigations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MomentumMode {

    // labels represent exponent of the i-th momentum ladder operator
    public final List<Integer> labels;
    public final int           N;
    public final int           particleNumber;

    public MomentumMode(int N) {
        this(new ArrayList<Integer>(Collections.nCopies(N, 0)));
    }

    public MomentumMode(List<Integer> labels) {
        this.labels = labels;
        this.N = (labels == null ? 0 : labels.size());

        int P = 0;
        for (int exponent : labels)
            P += exponent;
        this.particleNumber = P;
    }

    /* getters for various variables */

    public int getTotalMomentumNumber() {
        int ptot = 0;
        for (int momentumNumber = 0; momentumNumber < this.N; momentumNumber++)
            ptot += (momentumNumber * this.labels.get(momentumNumber));
        return ptot % this.N;
    }
}
