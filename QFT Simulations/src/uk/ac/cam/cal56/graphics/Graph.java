package uk.ac.cam.cal56.graphics;

import java.awt.Canvas;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Graph extends JPanel {

    private final static int STANDARD_PADDING = 20;

    private Canvas           _canvas;
    public final int         width;
    public final int         height;

    public Graph(double value, double min, double max, int width, int height) {
        this.width = width;
        this.height = height;
        _canvas = new Plot(value, min, max, width - 2 * STANDARD_PADDING, height - 2 * STANDARD_PADDING);
        setup();
    }

    public Graph(double[] data, double min, double max, int width, int height) {
        this.width = width;
        this.height = height;
        _canvas = new Plot(data, min, max, width - 2 * STANDARD_PADDING, height - 2 * STANDARD_PADDING);
        setup();
    }

    public Graph(double[][] data, double min, double max, int width, int height) {
        this.width = width;
        this.height = height;
        _canvas = new DensityPlot(data, min, max, width - 2 * STANDARD_PADDING, height - 2 * STANDARD_PADDING);
        setup();
    }

    private void setup() {
        this.setBounds(0, 0, width, height);
        this.add(_canvas);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // invent data
        int maxheight = 800, maxwidth = 800, N = 800;
        double[][] d = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double z1 = 5.0 * (i - N / 2) / N;
                double z2 = 5.0 * (j - 2 * N / 3) / N;
                double z3 = 2.0 * (i - N / 4) / N;
                double z4 = 3.0 * (j - N / 6) / N;
                d[i][j] = Math.exp(-z1 * z1 - z2 * z2) + Math.exp(-z3 * z3 - z4 * z4);
            }
        }

        // make plot
        Graph g = new Graph(d, 0, 1, maxwidth, maxheight);
        frame.setSize(g.width, g.height + 22);
        frame.add(g);

        frame.setVisible(true);
    }

}
