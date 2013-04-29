package uk.ac.cam.cal56.graphics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import uk.ac.cam.cal56.qft.WavePacket;

public abstract class PlotListener extends MouseAdapter {

    private SimulatorFrame _sandbox;

    protected int          N;
    protected int          width;
    protected int          height;

    protected Integer      x_init;
    protected Integer      y_init;
    protected Integer      p1_or_x1_init;
    protected Integer      p2_or_x2_init;
    protected Double       peakProb_init;

    protected WavePacket   wavePacket;

    public PlotListener(SimulatorFrame sandbox, Plot plot) {
        _sandbox = sandbox;
        N = sandbox.state.getN();
        width = plot._width;
        height = plot._height;
    }

    public void mousePressed(MouseEvent e) {
        _sandbox.stop();

        // remember parameters
        if (x_init == null) {
            x_init = e.getX();
            y_init = e.getY();
            p1_or_x1_init = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
            p2_or_x2_init = (int) (N * (1.0 - 1.0 * e.getY() / height));
            peakProb_init = 1.0 - 1.0 * e.getY() / height;
        }

        setWavePacket(e);
        _sandbox.state.setWavePacket(wavePacket);
        _sandbox.displayPanel.frameUpdate();
    }

    public void mouseDragged(MouseEvent e) {
        setWavePacket(e);
        _sandbox.state.setWavePacket(wavePacket);
        _sandbox.displayPanel.frameUpdate();
    }

    public void mouseReleased(MouseEvent e) {
        // delete memory of inital parameters
        x_init = null;
        y_init = null;
        p1_or_x1_init = null;
        p2_or_x2_init = null;
        peakProb_init = null;

        _sandbox.start();
    }

    public abstract void setWavePacket(MouseEvent e);
}
