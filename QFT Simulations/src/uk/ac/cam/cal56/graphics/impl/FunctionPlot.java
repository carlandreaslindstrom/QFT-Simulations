package uk.ac.cam.cal56.graphics.impl;

import java.awt.Graphics;

import javax.swing.JFrame;

import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.maths.Complex;

@SuppressWarnings("serial")
public class FunctionPlot extends Plot {

    public static final int PLOT_1D_WIDTH = 20;

    private Complex[]       _data;

    // constructors for simplified argument list (no min/max)
    public FunctionPlot(Object data, int maxwidth, int maxheight) {
        this(data, null, null, maxwidth, maxheight);
    }

    public FunctionPlot(Object data, Double min, Double max, int height) {
        this(data, min, max, PLOT_1D_WIDTH, height);
    }

    // constructors for full argument list
    public FunctionPlot(Object data, Double min, Double max, int maxwidth, int height) {
        _width = maxwidth;
        _height = height;
        update(data);
        setHeightAndWidth(maxwidth, height);
        setMinAndMax(min, max);
    }

    @Override
    protected void plot(Graphics g) {

        // search for bad scaling by finding the highest value
        double highest = Double.MIN_VALUE;

        // draw rectangular bars with length corresponding to data
        int imax = _data.length / _sampling;
        for (int i = 0; i < imax; i++) {
            double mod = (_data[i].modSquared() - _min) / (_max - _min);
            double arg = _data[i].arg();
            g.setColor(toFunctionColor(mod, arg));
            int barHeight = (int) (_height * mod);
            g.fillRect(i * _pointsize + PADDING, _height - barHeight, _pointsize, barHeight);
            // determine if highest
            if (mod > highest)
                highest = mod;
        }

        // rescale if necessary
        rescale(highest);
    }

    @Override
    public void update(Object data) {
        if (data.getClass() == Complex.class) {
            _data = new Complex[] { (Complex) data };
        }
        else if (data.getClass() == Complex[].class) {
            _data = (Complex[]) data;
            setPointSizeAndSampling(_width, _height);
            if (_width < _data.length) { // sampled
                _pointsize = 1;
                _sampling = (int) Math.ceil(_data.length / _width);
            }
            else { // scaled up
                _sampling = 1;
                _pointsize = _width / _data.length;
            }
        }
        repaint();
    }

    // sets height and width, appropriate according to data set
    protected void setHeightAndWidth(int maxwidth, int height) {
        setPointSizeAndSampling(maxwidth, height);
        _height = height;
        if (maxwidth < _data.length)
            _width = (int) (1.0 * _data.length / _sampling);
        else
            _width = _data.length * _pointsize;
        setBounds(0, 0, _width + PADDING, _height + PADDING);
    }

    // sets point size and sampling
    protected void setPointSizeAndSampling(int width, int height) {
        if (width < _data.length) { // sampled
            _pointsize = 1;
            _sampling = (int) Math.ceil(_data.length / width);
        }
        else { // scaled up
            _sampling = 1;
            _pointsize = width / _data.length;
        }
    }

    // sets min and max from input, or from data if null input
    protected void setMinAndMax(Double min, Double max) {
        if (min != null && max != null) {
            _min = min;
            _max = max;
        }
        else {
            for (int i = 0; i < _data.length; i++) {
                double value = _data[i].modSquared();
                if (min == null && value < _min)
                    _min = value;
                if (max == null && value > _max)
                    _max = value;
            }
        }
    }

    // TEST CASE
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // invent data
        int maxheight = 500, maxwidth = 1000, N = 500;
        Complex[] data = new Complex[N];
        for (int i = 0; i < N; i++) {
            double z1 = 5.0 * (i - N / 2) / N;
            data[i] = Complex.one().times(Math.exp(-z1 * z1)).timesexpi(2 * Math.PI * i / N);
        }

        // make plot
        Plot p = new FunctionPlot(data, maxwidth, maxheight);
        // Plot p = new NewFunctionPlot(0.7, 0, 1, maxwidth, maxheight);
        frame.setSize(p.getWidth(), p.getHeight() + 22);
        frame.add(p);

        frame.setVisible(true);
    }

}
