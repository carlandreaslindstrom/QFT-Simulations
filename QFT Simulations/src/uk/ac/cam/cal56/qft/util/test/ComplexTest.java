package uk.ac.cam.cal56.qft.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.cam.cal56.qft.util.Complex;

public class ComplexTest {

	@Test
	public void testModAndArg() {
		Complex z = new Complex(1,0);
		Complex w = new Complex(2,2);
		
		double epsilon = 0.00000001;
		
		assertEquals(z.mod(), 1.0, epsilon);
		assertEquals(z.arg(), 0.0, epsilon);
		
		assertEquals(w.mod(), 2.0*Math.sqrt(2.0), epsilon);
		assertEquals(w.arg(), Math.PI/4, epsilon);
	}
	
	@Test
	public void testEqualsAndToString() {
		Complex z = new Complex(4,-3);
		Complex w = new Complex(1,2.5);
		
		assert(w.conj().equals(new Complex(1,-2.5)));
		assertEquals(z.toString(), "(4.0,-3.0)");
	}
	
	
}
