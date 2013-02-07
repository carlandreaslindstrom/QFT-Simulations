package uk.ac.cam.cal56.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class DensityPlot extends Canvas {

    public final int   width;
    public final int   height;
    private double[][] _data;
    private int        _sampling;
    private int        _pointsize;
    private double     _min = Double.MAX_VALUE;
    private double     _max = Double.MIN_VALUE;

    // paints the 2D density plot using a rainbow color scheme
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int imax = _data.length / _sampling;
        int jmax = _data[0].length / _sampling;
        for (int i = 0; i < imax; i++)
            for (int j = 0; j < jmax; j++) {
                double value = (_data[i * _sampling][j * _sampling] - _min) / (_max - _min);
                g.setColor(doubleToRainbowColor(value));
                g.fillRect(i * _pointsize, j * _pointsize, _pointsize, _pointsize);
            }
    }

    // TODO: constructor with diagsym param

    // constructors with and without automatically set min and max
    public DensityPlot(double[][] data, int maxwidth, int maxheight) {
        this(data, null, null, maxwidth, maxheight);
    }

    // ...
    public DensityPlot(double[][] data, Double min, Double max, int maxwidth, int maxheight) {
        _data = data;

        if (maxwidth < _data.length || maxheight < _data[0].length) { // sampled
            _pointsize = 1;
            _sampling = Math.max((int) Math.ceil(_data.length / maxwidth), (int) Math.ceil(_data[0].length / maxheight));
            width = (int) (1.0 * _data.length / _sampling);
            height = (int) (1.0 * _data[0].length / _sampling);

        }
        else { // scaled up
            _sampling = 1;
            _pointsize = Math.min(maxwidth / _data.length, maxheight / _data[0].length);
            width = _data.length * _pointsize;
            height = _data[0].length * _pointsize;
        }

        // if max, min not set, determine them from data
        if (min != null && max != null) {
            _min = min;
            _max = max;
        }
        else {
            int imax = _data.length;
            int jmax = _data[0].length;
            for (int i = 0; i < imax; i++) {
                for (int j = 0; j < jmax; j++) {
                    double value = _data[i][j];
                    if (value < _min)
                        _min = value;
                    if (value > _max)
                        _max = value;
                }
            }
        }
    }

    // transforms a linear number scale to a rainbow color scale
    public static Color doubleToRainbowColor(double num) {
        if (num < 0.0 || num > 1.0)
            return Color.BLACK;
        double freq = 5.5;
        double phase = 0.2;
        int scale = 127;
        int red = (int) (scale * (1 + Math.sin(freq * num + (phase + 3.7) * Math.PI / 3)));
        int green = (int) (scale * (1 + Math.sin(freq * num + (phase + 4.7) * Math.PI / 3)));
        int blue = (int) (scale * (1 + Math.sin(freq * num + phase * Math.PI / 3)));
        return new Color(red, green, blue);
    }

    // TEST CASE
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
        DensityPlot dp = new DensityPlot(d, maxwidth, maxheight);
        frame.setSize(dp.width, dp.height + 22);
        frame.add(dp);

        frame.setVisible(true);
    }
}
