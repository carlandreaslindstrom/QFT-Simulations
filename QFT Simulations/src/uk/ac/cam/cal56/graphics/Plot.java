package uk.ac.cam.cal56.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import uk.ac.cam.cal56.graphics.impl.FunctionPlot;

@SuppressWarnings("serial")
public abstract class Plot extends Canvas {

    private final static String   SCALE_TEXT            = "Scale: ";

    protected final static double RESCALE_UPPER_ABS_LIM = 1.0;
    protected final static double RESCALE_LOWER_ABS_LIM = 0.02;
    protected final static double RESCALE_UPPER_REL_LIM = 0.90;
    protected final static double RESCALE_LOWER_REL_LIM = 0.30;
    protected final static int    RESCALE_AT_SCORE  = 50;
    protected final static int    SCORE_SCALE  = 20;
    protected final static double RESCALE_TO_MULTIPLE   = 2;

    private final static int      PLOT_PADDING          = 3;
    private final static int      FRAME_PADDING         = 15;
    private final static int      TICK_SIZE             = 2;
    public final static int       PADDING               = PLOT_PADDING + FRAME_PADDING;

    protected final static Color  AXIS_COLOR            = Color.GRAY;
    protected final static Color  OVERFLOW_COLOUR       = Color.RED;

    protected int                 _width;
    protected int                 _height;
    protected int                 _sampling;
    protected int                 _pointsize;
    protected double              _min                  = Double.MAX_VALUE;
    protected double              _max                  = Double.MIN_VALUE;

    protected int                 _rescaleScore  = 0;

    protected abstract void plot(Graphics g);

    public abstract void update(Object data);

    protected abstract void setHeightAndWidth(int width, int height);

    protected abstract void setPointSizeAndSampling(int width, int height);

    protected abstract void setMinAndMax(Double min, Double max);

    public void paint(Graphics g) {
        super.paint(g);
        plot(g);
        drawAxes(g);
    }

    // draw axes
    private void drawAxes(Graphics g) {
        // set draw color
        g.setColor(AXIS_COLOR);

        // draw axis lines
        g.drawLine(FRAME_PADDING - 1, 0, FRAME_PADDING - 1, _height + PLOT_PADDING);
        g.drawLine(FRAME_PADDING, _height + PLOT_PADDING, _width + PADDING, _height + PLOT_PADDING);

        // draw ticks (on y- then x-axis)
        g.drawLine(FRAME_PADDING - TICK_SIZE - 1, 0, FRAME_PADDING - 1, 0);
        g.drawLine(FRAME_PADDING - TICK_SIZE - 1, _height - 1, FRAME_PADDING - 1, _height - 1);
        g.drawLine(PADDING, _height + PLOT_PADDING, PADDING, _height + PLOT_PADDING + TICK_SIZE);
        g.drawLine(_width + PADDING - 1, _height + PLOT_PADDING, _width + PADDING - 1, _height + PLOT_PADDING +
                                                                                       TICK_SIZE);

        if (_width > FunctionPlot.PLOT_1D_WIDTH)
            g.drawChars((SCALE_TEXT + new DecimalFormat("0.00").format(_max)).toCharArray(), 0,
                        SCALE_TEXT.length() + 4, _width + PADDING - 70, 10);
    }

    /* Copyright (c) 1996 by Groupe Bull. All Rights Reserved */
    @Override
    public void update(Graphics g) {
        Graphics offgc;
        Image offscreen = null;
        Rectangle box = g.getClipBounds();

        // create the offscreen buffer and associated Graphics
        offscreen = createImage(box.width, box.height);
        offgc = offscreen.getGraphics();

        // clear the exposed area
        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, box.width, box.height);
        offgc.setColor(getForeground());

        // do normal redraw
        paint(offgc);

        // transfer offscreen to window
        g.drawImage(offscreen, 0, 0, this);
    }

    // shaded lemon-lime color scheme
    protected static Color toDensityColor(double num) {
        if (num > 1.0)
            return OVERFLOW_COLOUR;
        return doubleToGrayScale(num);
        // return new Color((int) ((191 + 64 * num) * num), (int) ((255 - 45 * num) * num), (int) (48 * num));
    }

    // bright lemon-lime color scheme
    protected static Color toFunctionColor(double num) {
        if (num > 1.0)
            return OVERFLOW_COLOUR;
        return Color.LIGHT_GRAY;
        // return new Color((int) (191 + 64 * num), (int) (255 - 45 * num), 48);
    }

    // transforms a linear number scale to a rainbow color scale
    protected static Color doubleToRainbowColor(double num) {
        if (num < 0.0 || num > 1.0)
            return OVERFLOW_COLOUR;
        double freq = 5.5;
        double phase = 0.2;
        int scale = 127;
        int red = (int) (scale * (1 + Math.sin(freq * num + (phase + 3.7) * Math.PI / 3)));
        int green = (int) (scale * (1 + Math.sin(freq * num + (phase + 4.7) * Math.PI / 3)));
        int blue = (int) (scale * (1 + Math.sin(freq * num + phase * Math.PI / 3)));
        return new Color(red, green, blue);
    }

    // transforms a linear number scale to gray scale
    protected static Color doubleToGrayScale(double num) {
        if (num > 1.0)
            return OVERFLOW_COLOUR;
        int scaled = (int) (255 * num);
        return new Color(scaled, scaled, scaled);
    }

    // rescales the plot dynamically to avoid tiny bars and dark densities
    protected void rescale(double highest) {
        
        // don't rescale if 1D plot
        if (_width <= FunctionPlot.PLOT_1D_WIDTH)
            return;

        // if outside rescaling limits, add to rescaling counter
        if (highest > RESCALE_UPPER_REL_LIM)
            _rescaleScore += (int)(SCORE_SCALE*(highest-RESCALE_UPPER_REL_LIM));
        else if(highest < RESCALE_LOWER_REL_LIM)
            _rescaleScore += (int)(SCORE_SCALE*(RESCALE_LOWER_REL_LIM-highest));
        else
            _rescaleScore = 0;

        // if counter has passed the threshold, rescale (find new _max)
        if (_rescaleScore > RESCALE_AT_SCORE) {
            // reconstruct data value
            double newmax = ((_max - _min) * highest + _min) * RESCALE_TO_MULTIPLE;
            if (newmax > RESCALE_UPPER_ABS_LIM)
                newmax = RESCALE_UPPER_ABS_LIM;
            else if (newmax < RESCALE_LOWER_ABS_LIM)
                newmax = RESCALE_LOWER_ABS_LIM;
            setMinAndMax(_min, newmax);
            _rescaleScore = 0;
        }
    }
}
