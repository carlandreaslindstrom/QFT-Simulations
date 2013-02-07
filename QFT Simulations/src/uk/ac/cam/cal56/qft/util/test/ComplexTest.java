package uk.ac.cam.cal56.qft.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import uk.ac.cam.cal56.qft.util.Complex;

public class ComplexTest {

    @Test
    public void testModAndArg() {
        Complex z = new Complex(1, 0);
        Complex w = new Complex(2, 2);

        double epsilon = 0.00000001;

        assertEquals(z.mod(), 1.0, epsilon);
        assertEquals(z.arg(), 0.0, epsilon);

        assertEquals(w.mod(), 2.0 * Math.sqrt(2.0), epsilon);
        assertEquals(w.arg(), Math.PI / 4, epsilon);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUndefinedArgument() {
        Complex z = Complex.zero();
        z.arg();
    }

    @Test
    public void testEqualsAndToString() {
        Complex z = new Complex(4, -3);
        Complex w = new Complex(1, 2.5);

        assert (w.conj().equals(new Complex(1, -2.5)));
        assertEquals(z.toString(), "(4.0,-3.0)");
    }

    @Test
    public void testPlusAndMinus() {
        Complex x = new Complex(1, 2);
        Complex y = new Complex(4, 2);

        Complex z = x.plus(y);
        assert (z.equals(new Complex(5, 4)));

        Complex w = z.minus(x.times(2));
        assert (w.equals(new Complex(3, 0)));
    }
    
    @Test
    public void testIsZero() {
        Complex x = new Complex(0, 0);
        Complex y = new Complex(4, 2);
        assert(x.isZero());
        assertFalse(y.isZero());
    }

}
