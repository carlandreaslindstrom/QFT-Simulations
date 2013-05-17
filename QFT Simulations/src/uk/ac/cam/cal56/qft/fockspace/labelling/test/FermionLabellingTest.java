package uk.ac.cam.cal56.qft.fockspace.labelling.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;
import uk.ac.cam.cal56.qft.fockspace.labelling.FermionLabelling;

public class FermionLabellingTest {

    @Test
    public void fermionIndexVerification() {
        int[] Nmax = new int[] { 0/* 100000000 */, 0/*4000 */, 0/*400 */, 0/* 100 */, 0/* 40 */, 0/* 40 */,
            0/* 20 */, 0/* 10 */, 0/*10*/, 0/*10*/, 0/*10*/, 0/*10*/, 0/*10*/}; // limit for 0P, 1P, etc...
        for (int Pmax = 0; Pmax < Nmax.length; Pmax++) {
            for (int N = 1; N <= Nmax[Pmax]; N++) {
                FermionFockState ket = new FermionFockState(N, Pmax, 0.0, 0.0);
                for (int i_F : ket)
                    assertEquals(i_F, FermionLabelling.label(ket.getParticles(), ket.getAntiParticles(), N));
            }
        }
    }
    
    @Test
    public void fermionAllParticlesIndexVerification() {
        int Nmax = 9;
        for (int N = 1; N <= Nmax; N++) {
            for (int Pmax = 0; Pmax <= 2*N; Pmax++) {
                FermionFockState ket = new FermionFockState(N, Pmax, 0.0, 0.0);
                for (int i_F : ket)
                    assertEquals(i_F, FermionLabelling.label(ket.getParticles(), ket.getAntiParticles(), N));
            }
        }
    }

    @Test
    public void testIndex() {
        double m = 1.0;
        double dx = 1.0;
        int N, Pmax;
        FermionFockState f;

        N = 100;
        Pmax = 2;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));

        N = 16;
        Pmax = 6;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));

        N = 2;
        Pmax = 4;
        f = new FermionFockState(N, Pmax, m, dx);
        for (int i : f)
            assertEquals(i, FermionLabelling.label(f.getParticles(), f.getAntiParticles(), N));
    }

}
