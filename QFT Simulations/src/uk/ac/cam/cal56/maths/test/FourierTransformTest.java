package uk.ac.cam.cal56.maths.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.junit.Test;

import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.graphics.impl.FunctionPlot;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FourierTransform;
import uk.ac.cam.cal56.maths.impl.DFT;
import uk.ac.cam.cal56.maths.impl.FFT;

public class FourierTransformTest {

    private static final double EPSILON = 1e-10;

    // check if transforming, then inverse transforming
    // using FFT gives back same answer
    @Test
    public void testFFT1DUnitarity() {
        int N = (int) Math.pow(2, 17);
        Complex[] f = new Complex[N];
        for (int i = 0; i < N; i++) {
            double arg = 2 * (i - N / 2) / N;
            f[i] = Complex.one().times(Math.exp(-arg * arg));
        }

        FourierTransform ft = new FFT();
        Complex[] F = ft.transform(f);
        Complex[] g = ft.inversetransform(F);

        for (int i = 0; i < N; i++) {
            // System.out.println(f[i].toString() + " = " + g[i].toString());
            assertTrue(g[i].equals(f[i]));
        }
    }

    // check if transforming, then inverse transforming
    // using DFT gives back same answer
    @Test
    public void testDFT1DUnitarity() {
        int N = (int) Math.pow(2, 10);
        Complex[] f = new Complex[N];
        for (int i = 0; i < N; i++) {
            double arg = 2 * (i - N / 2) / N;
            f[i] = Complex.one().times(Math.exp(-arg * arg));
        }

        FourierTransform ft = new DFT();
        Complex[] F = ft.transform(f);
        Complex[] g = ft.inversetransform(F);

        for (int i = 0; i < N; i++) {
            // System.out.println(f[i].toString() + " = " + g[i].toString());
            assertTrue(g[i].equals(f[i]));
        }
    }

    @Test
    public void testFFT2DUnitarity() {
        int N = (int) Math.pow(2, 8);
        int M = (int) Math.pow(2, 10);
        Complex[][] f = new Complex[N][M];
        for (int i = 0; i < N; i++) {
            Complex[] row = new Complex[M];
            for (int j = 0; j < M; j++) {
                double sigma1 = N * 0.1;
                double z1 = 1.0 * (i - N / 2) / sigma1;
                double sigma2 = M * 0.03;
                double z2 = 1.0 * (j - M / 2) / sigma2;
                row[j] = Complex.one().times(Math.exp(-z1 * z1 - z2 * z2));
            }
            f[i] = row;
        }

        FourierTransform ft = new FFT();
        Complex[][] F = ft.transform2D(f);
        Complex[][] g = ft.inversetransform2D(F);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                // System.out.println(f.get(i).get(j).toString() + " = " + fnew.get(i).get(j).toString());
                assertTrue(g[i][j].equals(f[i][j]));
            }
        }
    }

    @Test
    public void testDFT2DUnitarity() {
        int N = 128;
        int M = 128;
        Complex[][] f = new Complex[N][M];
        for (int i = 0; i < N; i++) {
            double sigma1 = N * 0.1;
            double z1 = 1.0 * (i - N / 2) / sigma1;
            Complex[] row = new Complex[M];
            for (int j = 0; j < M; j++) {
                double sigma2 = M * 0.03;
                double z2 = 1.0 * (j - M / 2) / sigma2;
                row[j] = Complex.one().times(Math.exp(-z1 * z1 - z2 * z2));
            }
            f[i] = row;
        }

        FourierTransform ft = new DFT();
        Complex[][] F = ft.transform2D(f);
        Complex[][] g = ft.inversetransform2D(F);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                // System.out.println(f.get(i).get(j).toString() + " = " + fnew.get(i).get(j).toString());
                assertTrue(g[i][j].equals(f[i][j]));
            }
        }
    }

    // test if Fourier Transform preserves normalisation
    @Test
    public void testNormalisationPreservation() {
        int N = 64;
        
        double norm = 0.0;
        double[][] values = new double[N][N];
        for (int i = 0; i < N; i++) {
            double sigma1 = N * 0.1;
            double z1 = 1.0 * (i - N / 2) / sigma1;
            for (int j = 0; j < N; j++) {
                double sigma2 = N * 0.03;
                double z2 = 1.0 * (j - N / 2) / sigma2;
                double value = Math.exp(-z1 * z1 - z2 * z2);
                values[i][j] = value;
                norm += value*value;
            }
        }
        norm = Math.sqrt(norm);
        
        double normTester = 0.0;
        Complex[][] f = new Complex[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                f[i][j] = Complex.one().times(values[i][j]/norm);
                normTester += f[i][j].modSquared();
            }
        }
        
        assertEquals(normTester, 1.0, EPSILON);
        
        Complex[][] F = new FFT().inversetransform2D(f);
        Complex[][] G = new FFT().transform2D(f);
        
        double normFTester = 0.0;
        double normGTester = 0.0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                normFTester += F[i][j].modSquared();
                normGTester += G[i][j].modSquared();
            }
        }
        
        assertEquals(normFTester, 1.0, EPSILON);
        assertEquals(normGTester, 1.0, EPSILON);
        
    }

    public static double[][] testFFT2D() {
        int N = (int) Math.pow(2, 7);
        int M = (int) Math.pow(2, 6);
        Complex[][] f = new Complex[N][M];
        for (int i = 0; i < N; i++) {
            Complex[] row = new Complex[M];
            for (int j = 0; j < M; j++) {
                double sigma1 = N * 0.01;
                double z1 = 1.0 * (i - N / 2) / sigma1;
                double sigma2 = M * 0.01;
                double z2 = 1.0 * (j - M / 2) / sigma2;
                row[j] = Complex.one().times(Math.exp(-z1 * z1 - z2 * z2));
            }
            f[i] = row;
        }

        FourierTransform ft = new FFT();
        Complex[][] F = ft.transform2D(f);

        double[][] data = new double[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                data[i][j] = F[i][j].modSquared();
                // data[i][j] = f[i][j].modSquared();
            }
        }
        return data;
    }

    public static double[][] testDFT2D() {
        int N = 50;
        int M = 50;
        Complex[][] f = new Complex[N][M];
        for (int i = 0; i < N; i++) {
            Complex[] row = new Complex[M];
            for (int j = 0; j < M; j++) {
                double sigma1 = N * 0.1;
                double z1 = 1.0 * (i - N / 2) / sigma1;
                double sigma2 = M * 0.03;
                double z2 = 1.0 * (j - M / 2) / sigma2;
                row[j] = Complex.one().times(Math.exp(-z1 * z1 - z2 * z2));
            }
            f[i] = row;
        }

        FourierTransform ft = new DFT();
        Complex[][] F = ft.transform2D(f);

        double[][] data = new double[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                data[i][j] = F[i][j].modSquared();
                // data[i][j] = f[i][j].modSquared();
            }
        }
        return data;
    }

    public static double[] testFFT1D() {
        int N = (int) Math.pow(2, 20);
        Complex[] f = new Complex[N];
        for (int i = 0; i < N; i++) {
            double sigma = N * 0.01;
            double z = 1.0 * (i - N / 2) / sigma;
            f[i] = Complex.one().times(Math.exp(-z * z));
        }

        FFT ft = new FFT();
        Complex[] F = ft.transform(f);

        double[] data = new double[N];
        for (int i = 0; i < N; i++) {
            data[i] = F[i].modSquared();
            // data[i] = f[i].modSquared();
        }
        return data;
    }

    public static double[] testDFT1D() {
        int N = (int) Math.pow(2, 10);
        Complex[] f = new Complex[N];
        for (int i = 0; i < N; i++) {
            double sigma = N * 0.01;
            double z = 1.0 * (i - N / 2) / sigma;
            f[i] = Complex.one().times(Math.exp(-z * z));
        }

        FourierTransform ft = new DFT();
        Complex[] F = ft.transform(f);

        double[] data = new double[N];
        for (int i = 0; i < N; i++) {
            data[i] = F[i].modSquared();
            // data[i] = f[i].modSquared();
        }
        return data;
    }

    // do tests graphically
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    int maxwidth = 500, maxheight = 500;
                    // Plot p = new Plot(testDFT1D(), maxwidth, maxheight);
                    // Plot p = new Plot(testFFT1D(), maxwidth, maxheight);
                    // DensityPlot p = new DensityPlot(testDFT2D(), maxwidth, maxheight);
                    Plot p = new FunctionPlot(testFFT2D(), maxwidth, maxheight);
                    frame.add(p);
                    frame.setBounds(0, 0, p.getWidth(), p.getHeight() + 22);
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
