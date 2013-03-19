package uk.ac.cam.cal56.qft.scalars.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.fockspace.FockState;
import uk.ac.cam.cal56.qft.fockspace.impl.ScalarFockState;
import uk.ac.cam.cal56.qft.scalars.impl.InteractionHamiltonian;

public class InteractionHamiltonianTest {

    private final double      EPSILON      = 1.0e-10;

    private final int         _N           = 16;
    private final int         _Pmax        = 3;
    private final double      _mass        = 1.0;
    private final double      _dx          = 0.1;
    private final Interaction _interaction = Interaction.PHI_CUBED;

    private final int         _S           = ScalarFockState.S(_N, _Pmax);

    private Hamiltonian       _ih;

    @Before
    public void setUp() throws Exception {
        _ih = new InteractionHamiltonian(_N, _Pmax, _mass, _dx, _interaction);
        _ih.calculateElements();
    }

    @Test
    public void testMatrixSize() {
        int elementCount = 0;
        double total = 0;
        for (int n = 0; n < _S; n++) {
            for (Entry<Integer, Double> element : _ih.getRow(n).entrySet()) {
                elementCount++;
                total += element.getValue();
                assertTrue(element.getKey() < _S);
                assertTrue(element.getValue() > 0.0);
            }
        }
        assertEquals(total, 12191.019844742212, EPSILON);
        assertEquals(elementCount, 4488);
    }

    @Test
    public void testDiagonalSymmetry() {
        for (int n = 0; n < _S; n++) {
            for (int m = 0; m <= n; m++) {
                Double value = _ih.get(n, m);
                Double diag = _ih.get(m, n);
                if (value != null && diag != null) {
                    // System.out.println((diag / value) + " : " + value + " = " + diag);
                    assertEquals(value, diag, EPSILON);
                }
            }
        }
    }

    @Test
    public void testMomentumConservationAndParticleTransitions() {
        FockState bra = new ScalarFockState(_N, _Pmax, _mass, _dx);
        FockState ket = new ScalarFockState(_N, _Pmax, _mass, _dx);
        for (int n : bra) {
            for (int m : ket) {
                Double value = _ih.get(n, m);
                if (value != null) {
                    // check if transitions are between equal momentum states
                    assertEquals(bra.getMomentumNumber(), ket.getMomentumNumber());
                    // check if transitions are between particle numbers of an odd difference
                    // assertEquals(Math.abs(StateLabelling.P(n, _N) - StateLabelling.P(m, _N)) % 2, 1); // odd orders
                    assertEquals(Math.abs(bra.getParticleNumber() - ket.getParticleNumber()) % 2, 1); // even orders
                }
            }
        }
    }
}