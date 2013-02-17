package uk.ac.cam.cal56.graphics;

/* Copyright (c) 1996 by Groupe Bull. All Rights Reserved */
/* Author: Jean-Michel.Leon@sophia.inria.fr */

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

// Canvas with double buffered update (no flicker!)
@SuppressWarnings("serial")
public class DoubleBufferedCanvas extends Canvas {

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
}
