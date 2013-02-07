package uk.ac.cam.cal56.qft.freetheory.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;

import uk.ac.cam.cal56.qft.freetheory.State;

// Conclusion : Error indipendent of N
public class IntegrationAccuracyErrorVsN {
    public static void main(String[] args) {

        int P = 0;         // maximum particle amount
        double L = 1.0;    // total lattice width
        double m = 1.0;    // particle mass
        double tfinal = 1.0;  // simulation time step
        int steps = 10000;
        double dt = tfinal / steps;

        try {
            // Create file
            String filename = "FreeTheoryAccuracyErrorVsN.txt";
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            System.out.println(filename);

            // loop over lattice space number
            for (int N = 1; N < 100000; N *= 3) {

                double dx = L / N; // lattice spacing

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
                System.out.println(N + " " + modError + " " + argError);
                out.write(N + " " + modError + " " + argError);
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
