package uk.ac.cam.cal56.qft.fermions.test;

import org.junit.Test;

import uk.ac.cam.cal56.qft.Hamiltonian;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.fermions.FermionInteractionHamiltonian;
import uk.ac.cam.cal56.qft.fockspace.FockState;
import uk.ac.cam.cal56.qft.fockspace.impl.FermionFockState;

public class FermionInteractionHamiltonianTest {

    // diagonal symmetry not needed.
    @Test
    public void testMatrix() {
        int N = 8;
        int Pmax = 3;
        double dx = 1.0;
        double mass = 1.0;
        Hamiltonian fih = new FermionInteractionHamiltonian(N, Pmax, dx, mass, Interaction.PHI_SQUARED);
        fih.calculateElements();

        FockState ket = new FermionFockState(N, Pmax, dx, mass);
        for (int n : ket) {
            FockState bra = new FermionFockState(N, Pmax, dx, mass);
            for (int m : bra) {
                Double element = fih.get(n, m);
                if (element != null) {
                    System.out.println(n + " " + m + " " + element + " " + fih.get(m, n));
                }

            }
        }

    }
}
