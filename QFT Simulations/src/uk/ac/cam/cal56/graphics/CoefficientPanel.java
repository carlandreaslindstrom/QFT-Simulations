package uk.ac.cam.cal56.graphics;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import uk.ac.cam.cal56.maths.FourierTransform;
import uk.ac.cam.cal56.maths.impl.FFT;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.impl.PositionWavePacket;

@SuppressWarnings("serial")
public abstract class CoefficientPanel extends DisplayPanel {

    /* FOURIER TRANSFORM */
    protected FourierTransform _ft              = new FFT();

    // Explanatory labels
    protected final JLabel     lblMomentumSpace = new JLabel("<html><strong>Momentum Space:</strong></html>");
    protected final JLabel     lblMomVacuum     = new JLabel("Vacuum");
    protected final JLabel     lblMom1P         = new JLabel("<html>1 particle of momentum <em>p</em></html>");
    protected final JLabel     lblMom2P         = new JLabel(
                                                    "<html>2 particles of momenta <em>p<sub>1</sub></em> and <em>p<sub>2</sub></em></html>");
    protected final JLabel     lblMomRest       = new JLabel("3+ particles");
    protected final JLabel     lblPositionSpace = new JLabel("<html><strong>Position Space:</strong></html>");
    protected final JLabel     lblPosVacuum     = new JLabel(lblMomVacuum.getText());
    protected final JLabel     lblPos1P         = new JLabel("<html>1 particle of position <em>x</em></html>");
    protected final JLabel     lblPos2P         = new JLabel(
                                                    "<html>2 particles of positions <em>x<sub>1</sub></em> and <em>x<sub>2</sub></em></html>");
    protected final JLabel     lblPosRest       = new JLabel(lblMomRest.getText());

    protected final JLabel     prob0Pmom        = new JLabel("<html>&phi;<sup>2</sup></html>");
    protected final JLabel     prob0Ppos        = new JLabel(prob0Pmom.getText());
    protected final JLabel     prob1Pmom        = new JLabel(prob0Pmom.getText());
    protected final JLabel     plbl             = new JLabel("p");
    protected final JLabel     p1lbl            = new JLabel("<html>p<sub>1</sub></html>");
    protected final JLabel     p2lbl            = new JLabel("<html>p<sub>2</sub></html>");
    protected final JLabel     prob3Pmom        = new JLabel(prob0Pmom.getText());
    protected final JLabel     prob1Ppos        = new JLabel(prob0Pmom.getText());
    protected final JLabel     xlbl             = new JLabel("x");
    protected final JLabel     x1lbl            = new JLabel("<html>x<sub>1</sub></html>");
    protected final JLabel     x2lbl            = new JLabel("<html>x<sub>2</sub></html>");
    protected final JLabel     prob3Ppos        = new JLabel(prob0Pmom.getText());

    protected final JLabel     prob1AntiPmom    = new JLabel(prob0Pmom.getText());
    protected final JLabel     p2Albl           = new JLabel(p1lbl.getText());

    protected final JLabel     lblClickAndDrag  = new JLabel("(Click on plots to place particles)");

    public CoefficientPanel(SimulatorFrame frame) {
        super(frame);
    }

    protected abstract void setupInteractivePlots();

    // momentum and position vacuum mouse clickability (sets to vacuum)
    protected void setupVacuumPlots(Plot momPlot, Plot posPlot) {
        if (momPlot == null)
            return;
        PlotListener vacuumListener = new PlotListener(frame, momPlot) {
            public void setWavePacket(MouseEvent e) {
                wavePacket = new MomentumWavePacket(N);
            }
        };
        momPlot.addMouseListener(vacuumListener);
        posPlot.addMouseListener(vacuumListener);
    }

    protected void setup1ParticlePlots(Plot momPlot, Plot posPlot) {
        if (momPlot == null)
            return;
        // momentum 1 particle mouse clickability (sets to wave packet peaking at click)
        PlotListener mom1PListener = new PlotListener(frame, momPlot) {
            public void setWavePacket(MouseEvent e) {
                if (!e.isShiftDown()) {
                    double phase = 2 * Math.PI * (e.getX() - x_init) / width;
                    wavePacket = new MomentumWavePacket(N, new int[] { p1_or_x1_init }, new double[] { phase },
                        peakProb_init);
                }
                else {
                    int p = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                    double peakProb = 1.0 - 1.0 * e.getY() / height;
                    wavePacket = new MomentumWavePacket(N, new int[] { p }, new double[] { 0 }, peakProb);
                }
            }
        };
        momPlot.addMouseListener(mom1PListener);
        momPlot.addMouseMotionListener(mom1PListener);

        // position 1 particle mouse clickability (sets to wave packet peaking at click)
        PlotListener pos1PListener = new PlotListener(frame, posPlot) {
            public void setWavePacket(MouseEvent e) {
                if (!e.isShiftDown()) {
                    double phase = 2 * Math.PI * (e.getX() - x_init) / width;
                    wavePacket = new PositionWavePacket(N, new int[] { p1_or_x1_init }, new double[] { phase },
                        peakProb_init);
                }
                else {
                    int x = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                    double peakProb = 1.0 - 1.0 * e.getY() / height;
                    wavePacket = new PositionWavePacket(N, new int[] { x }, new double[] { 0 }, peakProb);
                }
            }
        };
        posPlot.addMouseListener(pos1PListener);
        posPlot.addMouseMotionListener(pos1PListener);
    }

    protected void setup2ParticlePlots(Plot momPlot, Plot posPlot) {
        if (momPlot == null)
            return;
        // momentum 2 particle mouse clickability (sets to wave packet peaking at click)
        PlotListener mom2PListener = new PlotListener(frame, momPlot) {
            public void setWavePacket(MouseEvent e) {
                if (!e.isShiftDown()) {
                    double phase1 = 2 * Math.PI * (e.getX() - x_init) / width;
                    double phase2 = 2 * Math.PI * (1.0 - 1.0 * e.getY() / height);
                    wavePacket = new MomentumWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init }, new double[] {
                        phase1, phase2 });
                }
                else {
                    int p1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                    int p2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                    wavePacket = new MomentumWavePacket(N, new int[] { p1, p2 }, new double[] { 0, 0 });
                }
            }
        };
        momPlot.addMouseListener(mom2PListener);
        momPlot.addMouseMotionListener(mom2PListener);

        PlotListener pos2PListener = new PlotListener(frame, posPlot) {
            public void setWavePacket(MouseEvent e) {
                if (!e.isShiftDown()) {
                    double phase1 = 2 * Math.PI * (e.getX() - x_init) / width;
                    double phase2 = 2 * Math.PI * (1.0 - 1.0 * e.getY() / height);
                    wavePacket = new PositionWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init }, new double[] {
                        phase1, phase2 });
                }
                else {
                    int x1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                    int x2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                    wavePacket = new PositionWavePacket(N, new int[] { x1, x2 }, new double[] { 0, 0 });
                }
            }
        };
        posPlot.addMouseListener(pos2PListener);
        posPlot.addMouseMotionListener(pos2PListener);
    }

}
