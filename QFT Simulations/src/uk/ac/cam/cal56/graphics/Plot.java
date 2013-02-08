package uk.ac.cam.cal56.graphics;

import java.awt.Canvas;
import java.awt.Graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Plot extends Canvas {

    public final int width;
    public final int height;
    private double[] _data;
    private int      _sampling;
    private int      _pointsize;
    private double   _min = Double.MAX_VALUE;
    private double   _max = Double.MIN_VALUE;

    // paints the graph
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < _data.length / _sampling; i++) {
            double value = (_data[i] - _min) / (_max - _min);
            g.setColor(DensityPlot.doubleToRainbowColor(value));
            int barHeight = (int) (height * value);
            g.fillRect(i * _pointsize, height - barHeight, _pointsize, barHeight);
        }
    }

    // constructor for single value
    public Plot(double value, double min, double max, int maxwidth, int maxheight) {
        this(new double[] { value }, min, max, maxwidth, maxheight);
    }

    // constructors for data set
    public Plot(double[] data, int maxwidth, int maxheight) {
        this(data, null, null, maxwidth, maxheight);
    }

    // ...
    public Plot(double[] data, Double min, Double max, int maxwidth, int height) {
        _data = data;

        this.height = height;
        if (maxwidth < _data.length) { // sampled
            _pointsize = 1;
            _sampling = (int) Math.ceil(_data.length / maxwidth);
            width = (int) (1.0 * _data.length / _sampling);
        }
        else { // scaled up
            _sampling = 1;
            _pointsize = maxwidth / _data.length;
            width = _data.length * _pointsize;
        }

        // if max, min not set, determine them from data
        if (min != null && max != null) {
            _min = min;
            _max = max;
        }
        else {
            for (int i = 0; i < _data.length; i++) {
                double value = _data[i];
                if (value < _min)
                    _min = value;
                if (value > _max)
                    _max = value;
            }
        }
        this.setBounds(0, 0, width, height);
    }
    
    public void update(double[] data) {
        _data = data;
        if (width < _data.length) { // sampled
            _pointsize = 1;
            _sampling = (int) Math.ceil(_data.length / width);
        }
        else { // scaled up
            _sampling = 1;
            _pointsize = width / _data.length;
        }
    }
    
    public void update(double value) {
        _data = new double[] {value};
    }

    // TEST CASE
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // invent data
        int maxheight = 500, maxwidth = 500, N = 500;
        double[] d = new double[N];
        for (int i = 0; i < N; i++) {
            double z1 = 1.0 * (i - N / 2) / N;
            d[i] = Math.exp(-z1 * z1);
        }

        // make plot
        Plot p = new Plot(d, maxwidth, maxheight);
        // Plot p = new Plot(0.7, 0, 1, maxwidth, maxheight);
        frame.setSize(p.width, p.height + 22);
        frame.add(p);

        frame.setVisible(true);

    }

}
