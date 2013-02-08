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
        assertEquals(Combinatorics.choose(30L, 20), 30045015L);
        assertEquals(Combinatorics.choose(40L, 20), 137846528820L);
        assertEquals(Combinatorics.choose(24, 100), 0);
        assertEquals(Combinatorics.choose(24, 23), 24);
        assertEquals(Combinatorics.choose(100, 2), 4950);
        assertEquals(Combinatorics.choose(100L, 10), 17310309456440L);
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
        assertEquals(Combinatorics.S(27L, 13), 12033222880L);
        assertEquals(Combinatorics.S(100, 3), 176851);
        assertEquals(Combinatorics.S(100, 4), 4598126);
    }
    
    @Test
    public void testF_p() {
        double epsilon = 1.0e-9;
        for(int l = 0; l < 200; l++) {
            // F_p(l,n,0)
            assertEquals(Combinatorics.F_p(l, 0, 0), 1, epsilon);
            assertEquals(Combinatorics.F_p(l, 1, 0), Math.sqrt(l+1), epsilon);
            assertEquals(Combinatorics.F_p(l, 2, 0), Math.sqrt((l+1)*(l+2)), epsilon);
            assertEquals(Combinatorics.F_p(l, 3, 0), Math.sqrt((l+1)*(l+2)*(l+3)), epsilon);
            assertEquals(Combinatorics.F_p(l, 4, 0), Math.sqrt((l+1)*(l+2)*(l+3)*(l+4)), epsilon);
            assertEquals(Combinatorics.F_p(l, 9, 0), Math.sqrt((l+1)*(l+2)*(l+3)*(l+4)*(l+5)*(l+6)*(l+7)*(l+8)*(l+9)), epsilon);
            
            // F_p(l,0,m) as well as testing m<=l or else = 0
            assertEquals(Combinatorics.F_p(l, 0, 1), Math.sqrt(l), epsilon);
            assertEquals(Combinatorics.F_p(l, 0, 2), Math.sqrt(l*(l-1)), epsilon);
            assertEquals(Combinatorics.F_p(l, 0, 3), Math.sqrt(l*(l-1)*(l-2)), epsilon);
            assertEquals(Combinatorics.F_p(l, 0, 4), Math.sqrt(l*(l-1)*(l-2)*(l-3)), epsilon);
            assertEquals(Combinatorics.F_p(l, 0, 9), Math.sqrt(l*(l-1)*(l-2)*(l-3)*(l-4)*(l-5)*(l-6)*(l-7)*(l-8)), epsilon);
            
            // other cases
            assertEquals(Combinatorics.F_p(l, 1, 1), l, epsilon);
            assertEquals(Combinatorics.F_p(l, 2, 1), l*Math.sqrt(l+1), epsilon);
            assertEquals(Combinatorics.F_p(l, 1, 2), (l-1)*Math.sqrt(l), epsilon);
            assertEquals(Combinatorics.F_p(l, 2, 2), (l-1)*l, epsilon);
            assertEquals(Combinatorics.F_p(l, 1, 3), (l-2)*Math.sqrt(l*(l-1)), epsilon);
            assertEquals(Combinatorics.F_p(l, 3, 1), l*Math.sqrt((l+1)*(l+2)), epsilon);
        }
        
    }

}
