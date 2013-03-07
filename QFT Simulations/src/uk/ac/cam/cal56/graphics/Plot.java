package uk.ac.cam.cal56.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

@SuppressWarnings("serial")
public abstract class Plot extends Canvas {

    protected static Color OVERFLOW_COLOUR = Color.RED;

    protected int          _width;
    protected int          _height;
    protected int          _sampling;
    protected int          _pointsize;
    protected double       _min            = Double.MAX_VALUE;
    protected double       _max            = Double.MIN_VALUE;

    protected abstract void plot(Graphics g);

    public abstract void update(Object data);

    protected abstract void setHeightAndWidth(int width, int height);

    protected abstract void setPointSizeAndSampling(int width, int height);

    protected abstract void setMinAndMax(Double min, Double max);

    public void paint(Graphics g) {
        super.paint(g);
        plot(g);
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
        if (num > 1.0 || num < 0.0)
            return OVERFLOW_COLOUR;
        return new Color((int) ((191 + 64 * num) * num), (int) ((255 - 45 * num) * num), (int) (48 * num));
    }

    // bright lemon-lime color scheme
    protected static Color toFunctionColor(double num) {
        if (num > 1.0 || num < 0.0)
            return OVERFLOW_COLOUR;
        else
            return new Color((int) (191 + 64 * num), (int) (255 - 45 * num), 48);
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

}
