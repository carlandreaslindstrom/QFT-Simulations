package uk.ac.cam.cal56.qft.interactingtheory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.InteractionHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.impl.FastInteractionHamiltonian;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SlowInteractionHamiltonian;
import uk.ac.cam.cal56.qft.statelabelling.StateLabelling;

public class FastInteractionHamiltonianTest {

    private final double           EPSILON      = 1.0e-10;

    private final int              _N           = 15;
    private final int              _Pmax        = 3;
    private final double           _mass        = 1.0;
    private final double           _dx          = 0.1;
    private final Interaction      _interaction = Interaction.PHI_THIRD;

    private final int              _S           = Combinatorics.S(_N, _Pmax);

    private InteractionHamiltonian _ih;

    @Before
    public void setUp() throws Exception {
        _ih = new FastInteractionHamiltonian(_N, _Pmax, _mass, _dx, _interaction);
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
        assertEquals(total, 893.6862834805144, EPSILON);
        assertEquals(elementCount, 3724);
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
        for (int n = 0; n < _S; n++) {
            for (int m = 0; m < _S; m++) {
                Double value = _ih.get(n, m);
                if (value != null) {
                    // check if transitions are between equal momentum states
                    assertEquals(StateLabelling.momentumNumber(n, _N), StateLabelling.momentumNumber(m, _N));
                    // check if transitions are between particle numbers of an odd difference
                    assertEquals(Math.abs(StateLabelling.P(n, _N) - StateLabelling.P(m, _N)) % 2, 1);
                }
            }
        }
    }

    @Test
    public void testImplementationComparison() {

        InteractionHamiltonian slowih = new SlowInteractionHamiltonian(_N, _Pmax, _mass, _dx, _interaction);
        slowih.calculateElements();

        // compare
        for (int n = 0; n < _S; n++) {
            for (int m = 0; m < _S; m++) {
                Double fast = _ih.get(n, m);
                Double slow = slowih.get(n, m);
                if (slow != null) {
                    assertNotNull(fast);
                    assertEquals((double) slow, (double) fast, EPSILON);
                }
            }
        }
    }
}
