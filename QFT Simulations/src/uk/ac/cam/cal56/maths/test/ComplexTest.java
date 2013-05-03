package uk.ac.cam.cal56.maths.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Complex;

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
        

        Complex x = new Complex(0, 0);
        Complex y = new Complex(-1, 0);
        
        assertEquals(x.arg(), 0.0, epsilon);
        assertEquals(y.arg(), Math.PI, epsilon);
    }

    @Test
    public void testEqualsAndToString() {
        Complex z = new Complex(4, -3);
        Complex w = new Complex(1, 2.5);

        assertTrue(w.conj().equals(new Complex(1, -2.5)));
        assertEquals(z.toString(), "(4,-3)");
    }

    @Test
    public void testPlusAndMinus() {
        Complex x = new Complex(1, 2);
        Complex y = new Complex(4, 2);

        Complex z = x.plus(y);
        assertTrue(z.equals(new Complex(5, 4)));

        Complex w = z.minus(x.times(2));
        assertTrue(w.equals(new Complex(3, 0)));
    }

    @Test
    public void testIsZero() {
        Complex x = new Complex(0, 0);
        Complex y = new Complex(4, 2);
        assert (x.isZero());
        assertFalse(y.isZero());
    }

    @Test
    public void testNegative() {
        Complex x = new Complex(1, 2);
        Complex y = new Complex(4, -2);
        Complex z = x.negative();
        Complex w = y.negative();
        assertTrue(z.equals(new Complex(-1, -2)));
        assertTrue(w.equals(new Complex(-4, 2)));
    }
    
    @Test
    public void testDotProduct() {
        Complex[] a = new Complex[]{Complex.zero(), Complex.one(), Complex.one().times(2)};
        Complex[] b = new Complex[]{Complex.one(), Complex.i(), Complex.one()};
        
        assertTrue(Complex.dotProduct(a,b).equals(new Complex(2,1)));
        assertTrue(Complex.dotProduct(b,a).equals(new Complex(2,-1)));
    }
    
    @Test
    public void testNorm() {
        Complex[] a = new Complex[]{Complex.zero(), Complex.one(), Complex.one().times(2)};
        Complex[] b = new Complex[]{Complex.one(), Complex.i(), Complex.one()};
        
        assertEquals(Complex.norm(a), Math.sqrt(5), 1e-9);
        assertEquals(Complex.norm(b), Math.sqrt(3), 1e-9);
    }

}
