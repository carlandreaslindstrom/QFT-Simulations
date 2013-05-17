package uk.ac.cam.cal56.qft.fermions;

import java.util.Map.Entry;

import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.fockspace.FockState;
import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.FermionLabelling;
import uk.ac.cam.cal56.qft.fockspace.labelling.ScalarLabelling;
import uk.ac.cam.cal56.qft.impl.InteractionHamiltonian;

public class FermionInteractionHamiltonian extends InteractionHamiltonian {

    public FermionInteractionHamiltonian(int N, int Pmax, double m, double dx, Interaction interaction) {
        super(N, FermionFockState.S(N, Pmax), m, dx, interaction);
        _ket = new FermionFockState(N, Pmax, m, dx);
    }

    @Override
    protected void calculateRow(int n) {
        if (_interaction == Interaction.PHI_SQUARED) {
            calculatePsiSquared();
        }
    }

    private void calculatePsiSquared() {
        double constantTerm = 0.0;
        for (int p = 0; p < _N; p++) {
            double emterm = (_E[p] + _m) / (8 * _E[p] * _E[p] * L); // ((E+m)/E) / 2L
            double epterm = 2.0 * Math.sin(_dx * p / 2.0) / (_dx * (_E[p] + _m)); // p/(E+m)
            double diffterm = 2 * epterm * emterm;
            double sameterm = (1 - epterm * epterm) * emterm;
            constantTerm -= sameterm * L * 2 * _E[p];
            add(sameterm, new int[] { p }, new int[] {}, new int[] {}, p);
            add(sameterm, new int[] {}, new int[] { p }, new int[] { p });
            add(diffterm, new int[] {}, new int[] {}, new int[] { -p }, p);
            add(diffterm, new int[] { p }, new int[] { -p }, new int[] {});
        }
        add(constantTerm, new int[] {}, new int[] {}, new int[] {}); // constant term
    }

    private void add(double factor, int[] bdaggers, int[] cdaggers, int[] cs, int... bs) {

        // ket index
        int n = _ket.getLabel();

        // bra index
        Integer m = FermionLabelling.braIndex((FermionFockState) _ket, bdaggers, cdaggers, cs, bs, _N);

        // quit if not a valid index
        if (m == null || m >= _S)
            return;

        // particles: multiply by ladder operator combinatoric factor
        for (Entry<Integer, int[]> op : ScalarLabelling.toLadderOperatorMap(bdaggers, bs, _N).entrySet()) {
            int p = op.getKey();
            int l_p = ((FermionFockState) _ket).getParticles().contains((Integer) p) ? 1 : 0;
            int n_p = op.getValue()[0];
            int m_p = op.getValue()[1];
            factor *= FermionFockState.F_p(l_p, n_p, m_p, L);
        }

        // antiparticles: multiply by ladder operator combinatoric factor
        for (Entry<Integer, int[]> op : ScalarLabelling.toLadderOperatorMap(cdaggers, cs, _N).entrySet()) {
            int p = op.getKey();
            int l_p = ((FermionFockState) _ket).getAntiParticles().contains((Integer) p) ? 1 : 0;
            int n_p = op.getValue()[0];
            int m_p = op.getValue()[1];
            factor *= FermionFockState.F_p(l_p, n_p, m_p, L * 2 * FockState.E_p(p, _N, _m, _dx));
        }

        // if not 0, save value in the Hamiltonian matrix
        if (Math.abs(factor) > EPSILON) {
            Double oldValue = get(n, m);
            _elements.get(n).put(m, factor + (oldValue == null ? 0 : oldValue));
        }

    }

}
