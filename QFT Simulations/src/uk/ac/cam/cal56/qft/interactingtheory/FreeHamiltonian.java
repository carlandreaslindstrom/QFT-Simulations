package uk.ac.cam.cal56.qft.interactingtheory;

import uk.ac.cam.cal56.maths.Combinatorics;
import uk.ac.cam.cal56.qft.statelabelling.FockState;

// Free Hamiltonian holds the energies for all the fock states of the
// system for faster access than recalculation every time step.
public class FreeHamiltonian {

    public final double[] energies;

    public FreeHamiltonian(int N, int Pmax, double m, double dx) {

        // calculate and store energies
        FockState phi = new FockState(N, Pmax, m, dx);
        double[] temp = new double[Combinatorics.S(N, Pmax)];
        for (int i : phi)
            temp[i] = phi.getEnergy();
        energies = temp;
    }
}
