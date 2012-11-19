package uk.ac.cam.cal56.qft.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.util.Combinatorics;

public class CombinatoricsTest {

    @Test
    public void testChoose() {
        assertEquals(Combinatorics.choose(6, 1), 6);
        assertEquals(Combinatorics.choose(0, 0), 1);
        assertEquals(Combinatorics.choose(10, 4), 210);
        assertEquals(Combinatorics.choose(30, 20), 30045015);
        assertEquals(Combinatorics.choose(40, 20), 137846528820L);
        assertEquals(Combinatorics.choose(24, 100), 0);
        assertEquals(Combinatorics.choose(24, 23), 24);
        assertEquals(Combinatorics.choose(100, 2), 4950);
        assertEquals(Combinatorics.choose(100, 10), 17310309456440L);
        assertEquals(Combinatorics.choose(100, 0), 1);
        assertEquals(Combinatorics.choose(100, -10), 0);
        assertEquals(Combinatorics.choose(90, 90), 1);
    }

    @Test
    public void testS() {
        assertEquals(Combinatorics.S(4, 0), 1);
        assertEquals(Combinatorics.S(4, 1), 5);
        assertEquals(Combinatorics.S(4, 2), 15);
        assertEquals(Combinatorics.S(4, 3), 35);
        assertEquals(Combinatorics.S(4, 4), 70);
        assertEquals(Combinatorics.S(4, 5), 126);

        assertEquals(Combinatorics.S(6, 0), 1);
        assertEquals(Combinatorics.S(6, 1), 7);
        assertEquals(Combinatorics.S(6, 2), 28);
        assertEquals(Combinatorics.S(6, 3), 84);

        assertEquals(Combinatorics.S(6, -1), 0);

        assertEquals(Combinatorics.S(24, 0), 1);
        
        assertEquals(Combinatorics.S(27, 0), 1);
        assertEquals(Combinatorics.S(27, 1), 28);
        assertEquals(Combinatorics.S(27, 2), 406);
        assertEquals(Combinatorics.S(27, 3), 4060);
        assertEquals(Combinatorics.S(27, 13), 12033222880L);
    }

}
