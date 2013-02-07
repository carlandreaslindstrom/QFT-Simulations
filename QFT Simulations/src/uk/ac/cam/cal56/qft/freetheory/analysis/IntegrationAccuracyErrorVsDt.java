package uk.ac.cam.cal56.qft.freetheory.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;

import uk.ac.cam.cal56.qft.freetheory.State;

// Conclusion : Error scales as dt^2 => second order method
public class IntegrationAccuracyErrorVsDt {
    public static void main(String[] args) {

        int N = 4;         // lattice space number
        int P = 0;         // maximum particle amount
        double L = 1.0;    // total lattice width
        double m = 1.0;    // particle mass
        double tfinal = 1.0;  // simulation time step

        double dx = L / N; // lattice spacing

        try {
            // Create file
            String filename = "FreeTheoryAccuracyErrorVsDt.txt";
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            System.out.println(filename);

            // loop over increasing step numbers
            for (int steps = 10; steps < 1000000000 ; steps *= 3) {
                double dt = tfinal / steps;
                // initialise states
                State phiExact = new State(N, P, m, dx); // initialize state
                State phiIntegrated = new State(N, P, m, dx); // initialize state

                phiIntegrated.stepFirst(dt);
                for (int i = 0; i < steps; i++) {
                    phiIntegrated.step(dt);
                }
                phiExact.stepExact(tfinal);
                
                // save errors in mod and arg
                double modError = Math.abs(phiIntegrated.coeffs.get(0).mod() - phiExact.coeffs.get(0).mod());
                double argError = Math.abs(phiIntegrated.coeffs.get(0).arg() - phiExact.coeffs.get(0).arg());
                System.out.println(dt + " " + modError + " " + argError);
                out.write(dt + " " + modError + " " + argError);
                out.newLine();
            }

            // Close the output stream
            out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
