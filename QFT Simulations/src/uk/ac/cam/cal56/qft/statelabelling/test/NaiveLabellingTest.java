package uk.ac.cam.cal56.qft.statelabelling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.ac.cam.cal56.qft.statelabelling.NaiveLabelling;

public class NaiveLabellingTest {

    @Test
    public void testPofNAndI() {
        assertEquals(NaiveLabelling.P(0, 3), 0);
        assertEquals(NaiveLabelling.P(1, 3), 1);
        assertEquals(NaiveLabelling.P(2, 3), 1);
        assertEquals(NaiveLabelling.P(3, 3), 1);
        assertEquals(NaiveLabelling.P(4, 3), 2);
        assertEquals(NaiveLabelling.P(5, 3), 2);
        assertEquals(NaiveLabelling.P(6, 3), 2);
        assertEquals(NaiveLabelling.P(7, 3), 2);
        assertEquals(NaiveLabelling.P(8, 3), 2);
        assertEquals(NaiveLabelling.P(9, 3), 2);
        assertEquals(NaiveLabelling.P(10, 3), 3);
        assertEquals(NaiveLabelling.P(11, 3), 3);
        assertEquals(NaiveLabelling.P(29, 3), 4);
        assertEquals(NaiveLabelling.P(600, 7), 5);
    }

    @Test
    public void testPofLs() {
        // N = 3
        assertEquals(NaiveLabelling.P(Arrays.asList(1, 0, 0)), 1);
        assertEquals(NaiveLabelling.P(Arrays.asList(1, 0, 3)), 4);
        assertNotSame(NaiveLabelling.P(Arrays.asList(1, 0, 3)), 3);

        // N = 5
        assertEquals(NaiveLabelling.P(Arrays.asList(1, 0, 0, 5, 6)), 12);
        assertEquals(NaiveLabelling.P(Arrays.asList(0, 1, 0, 14, 2)), 17);
    }

    @Test
    public void testIndex() {
        // N = 3
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 0)), 0);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 0)), 1);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 0)), 2);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 1)), 3);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 0, 0)), 4);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 1, 0)), 5);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 1)), 6);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 2, 0)), 7);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 1)), 8);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 2)), 9);
        assertEquals(NaiveLabelling.index(Arrays.asList(3, 0, 0)), 10);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 1, 0)), 11);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 0, 1)), 12);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 2, 0)), 13);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 1, 1)), 14);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 2)), 15);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 3, 0)), 16);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 2, 1)), 17);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 2)), 18);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 3)), 19);
        assertEquals(NaiveLabelling.index(Arrays.asList(4, 0, 0)), 20);
        assertEquals(NaiveLabelling.index(Arrays.asList(3, 1, 0)), 21);
        assertEquals(NaiveLabelling.index(Arrays.asList(3, 0, 1)), 22);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 2, 0)), 23);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 1, 1)), 24);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 0, 2)), 25);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 3, 0)), 26);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 2, 1)), 27);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 1, 2)), 28);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 3)), 29);

        // N = 5
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 0, 0, 0)), 0);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 0, 0, 0)), 1);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 0, 0, 0)), 2);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 1, 0, 0)), 3);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 0, 1, 0)), 4);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 0, 0, 1)), 5);
        assertEquals(NaiveLabelling.index(Arrays.asList(2, 0, 0, 0, 0)), 6);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 1, 0, 0, 0)), 7);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 1, 0, 0)), 8);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 0, 1, 0)), 9);
        assertEquals(NaiveLabelling.index(Arrays.asList(1, 0, 0, 0, 1)), 10);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 2, 0, 0, 0)), 11);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 1, 0, 0)), 12);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 0, 1, 0)), 13);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 1, 0, 0, 1)), 14);
        assertEquals(NaiveLabelling.index(Arrays.asList(0, 0, 2, 0, 0)), 15);

        assertEquals(NaiveLabelling.index(Arrays.asList(7, 0, 0, 0, 0)), 462);
        assertEquals(NaiveLabelling.index(Arrays.asList(6, 1, 0, 0, 0)), 463);
        assertEquals(NaiveLabelling.index(Arrays.asList(6, 0, 1, 0, 0)), 464);
        assertEquals(NaiveLabelling.index(Arrays.asList(6, 0, 0, 1, 0)), 465);
    }

    @Test
    public void testLabels() {
        assertEquals(NaiveLabelling.labels(0, 3), Arrays.asList(0, 0, 0));
        assertEquals(NaiveLabelling.labels(10, 3), Arrays.asList(3, 0, 0));
        assertEquals(NaiveLabelling.labels(29, 3), Arrays.asList(1, 0, 3));

        assertEquals(NaiveLabelling.labels(462, 5), Arrays.asList(7, 0, 0, 0, 0));
    }

    @Test
    public void testLabelsGrandVerification() {
        int maxN = 23;
        int maxIndex = 1000;
        for (int N = 1; N <= maxN; N++) {
            for (int i = 0; i < maxIndex; i++) {
                assertEquals(NaiveLabelling.index(NaiveLabelling.labels(i, N)), i);
            }
        }
    }

    @Test
    public void stressTestLabels() {
        long i = 3;
        int N = 24;
        List<Integer> ls = NaiveLabelling.labels(i, N);
        assertEquals(ls, Arrays.asList(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        long iCalculated = NaiveLabelling.index(ls);
        assertEquals(i, iCalculated);
    }
}
