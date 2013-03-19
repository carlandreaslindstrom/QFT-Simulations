package uk.ac.cam.cal56.maths.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.maths.Combinatorics;

public class CombinatoricsTest {

    @Test
    public void testChoose() {
        assertEquals(Combinatorics.choose(6, 1), 6);
        assertEquals(Combinatorics.choose(0, 0), 1);
        assertEquals(Combinatorics.choose(10, 4), 210);
        assertEquals(Combinatorics.choose(24, 100), 0);
        assertEquals(Combinatorics.choose(24, 23), 24);
        assertEquals(Combinatorics.choose(100, 2), 4950);
        assertEquals(Combinatorics.choose(100, 0), 1);
        assertEquals(Combinatorics.choose(100, -10), 0);
        assertEquals(Combinatorics.choose(90, 90), 1);
    }

    @Test
    public void testFactorial() {
        assertEquals(Combinatorics.factorial(0), 1);
        assertEquals(Combinatorics.factorial(1), 1);
        assertEquals(Combinatorics.factorial(2), 2);
        assertEquals(Combinatorics.factorial(3), 6);
        assertEquals(Combinatorics.factorial(4), 24);
        assertEquals(Combinatorics.factorial(5), 120);
        assertEquals(Combinatorics.factorial(6), 720);
        assertEquals(Combinatorics.factorial(7), 5040);
        assertEquals(Combinatorics.factorial(8), 40320);
        assertEquals(Combinatorics.factorial(9), 362880);
        assertEquals(Combinatorics.factorial(10), 3628800);
        assertEquals(Combinatorics.factorial(11), 39916800);
        assertEquals(Combinatorics.factorial(12), 479001600);
    }

}
