package uk.ac.cam.cal56.graphics.impl;

import java.awt.Graphics;

import javax.swing.JFrame;

import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.maths.Complex;

@SuppressWarnings("serial")
public class DensityPlot extends Plot {

    private Complex[][] _data;

    // constructors for simplified argument list (no min/max)
    public DensityPlot(Complex[][] data, int maxwidth, int maxheight) {
        this(data, null, null, maxwidth, maxheight);
    }

    // constructors for full argument list
    public DensityPlot(Complex[][] data, Double min, Double max, int maxwidth, int maxheight) {
        _width = maxwidth;
        _height = maxheight;
        update(data);
        setHeightAndWidth(maxwidth, maxheight);
        setMinAndMax(min, max);
    }

    @Override
    protected void plot(Graphics g) {

        // search for bad scaling by finding the highest value
        double highest = Double.MIN_VALUE;

        // find number of rectangles needed in each direction
        int imax = _data.length / _sampling;
        int jmax = _data[0].length / _sampling;

        // plot rectangles with color according to data
        for (int i = 0; i < imax; i++) {
            for (int j = 0; j < jmax; j++) {
                // convert complex coefficient to a percentage of plot height
                double mod = (_data[i * _sampling][j * _sampling].modSquared() - _min) / (_max - _min);
                double arg = _data[i * _sampling][j * _sampling].arg();
                // determine colour/shade and draw in on the canvas
                g.setColor(toDensityColor(mod, arg));
                g.fillRect(i * _pointsize + PADDING, (jmax - 1 - j) * _pointsize, _pointsize, _pointsize);
                // determine if highest
                if (mod > highest)
                    highest = mod;
            }
        }

        // rescale if necessary
        rescale(highest);
    }

    @Override
    public void update(Object data) {
        _data = (Complex[][]) data;
        setPointSizeAndSampling(_width, _height);
        repaint();
    }

    // sets height and width, appropriate according to data set
    protected void setHeightAndWidth(int maxwidth, int maxheight) {
        setPointSizeAndSampling(maxwidth, maxheight);
        if (maxwidth < _data.length || maxheight < _data[0].length) { // sampled
            _width = (int) (1.0 * _data.length / _sampling);
            _height = (int) (1.0 * _data[0].length / _sampling);
        }
        else { // scaled up
            _width = _data.length * _pointsize;
            _height = _data[0].length * _pointsize;
        }
        setBounds(0, 0, _width + PADDING, _height + PADDING);
    }

    // sets point size and sampling
    protected void setPointSizeAndSampling(int width, int height) {
        if (width < _data.length || height < _data[0].length) {
            _pointsize = 1;
            _sampling = Math.max((int) Math.ceil(_data.length / width), (int) Math.ceil(_data[0].length / height));
        }
        else {
            _pointsize = Math.min(width / _data.length, height / _data[0].length);
            _sampling = 1;
        }
    }

    // sets min and max from input, or from data if null input
    protected void setMinAndMax(Double min, Double max) {
        if (min != null && max != null) {
            _min = min;
            _max = max;
        }
        else {
            int imax = _data.length;
            int jmax = _data[0].length;
            for (int i = 0; i < imax; i++) {
                for (int j = 0; j < jmax; j++) {
                    double value = _data[i][j].modSquared();
                    if (max == null && value < _min)
                        _min = value;
                    if (max == null && value > _max)
                        _max = value;
                }
            }
        }
    }

    // TEST CASE
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // invent data
        int maxheight = 400, maxwidth = 400, N = 100;
        Complex[][] data = new Complex[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double z1 = 5.0 * (i - N / 2) / N;
                double z2 = 5.0 * (j - 2 * N / 3) / N;
                double z3 = 2.0 * (i - N / 4) / N;
                double z4 = 3.0 * (j - N / 6) / N;
                data[i][j] = Complex.one().times(Math.exp(-z1 * z1 - z2 * z2) + Math.exp(-z3 * z3 - z4 * z4));
            }
        }

        // make plot
        Plot dp = new DensityPlot(data, maxwidth, maxheight);
        frame.setSize(dp.getWidth(), dp.getHeight() + 22);
        frame.add(dp);

        frame.setVisible(true);
    }
}
