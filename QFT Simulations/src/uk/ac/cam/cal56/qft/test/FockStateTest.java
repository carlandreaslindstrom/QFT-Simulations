package uk.ac.cam.cal56.qft.test;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.cam.cal56.qft.FockState;

public class FockStateTest {

	@Test
	public void testMod() {
		FockState phi = new FockState();
		
		double epsilon = 0.00000001;
		
		assertEquals(phi.mod(), 1.0, epsilon);
	}

}
