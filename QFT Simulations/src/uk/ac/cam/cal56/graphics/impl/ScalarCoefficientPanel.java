package uk.ac.cam.cal56.graphics.impl;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import uk.ac.cam.cal56.graphics.CoefficientPanel;
import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.graphics.PlotListener;
import uk.ac.cam.cal56.graphics.SimulatorFrame;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.impl.PositionWavePacket;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class ScalarCoefficientPanel extends CoefficientPanel {

    public ScalarCoefficientPanel(SimulatorFrame frame) {
        super(frame);
    }

    /* PLOT DATA VARIABLES */
    // Momentum space plots
    protected Plot _momPlotVacuum;
    protected Plot _momPlot1P;
    protected Plot _momDensityPlot2P;
    protected Plot _momPlotRest;

    // Position space plots
    protected Plot _posPlotVacuum;
    protected Plot _posPlot1P;
    protected Plot _posDensityPlot2P;
    protected Plot _posPlotRest;

    // @formatter:off
    protected int getPlotWidth() { return 256; }
    protected int getPlotHeight() { return getPlotWidth(); }
    // @formatter:on

    @Override
    protected void setup() {
        setBackground(DISPLAY_BG_COLOR);

        // set layout @formatter:off
        setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("30px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("60px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode((getPlotWidth()+15)+"px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode((getPlotWidth()+15)+"px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("60px"),},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("30px"),
                RowSpec.decode("25px"),
                RowSpec.decode((getPlotHeight()+15)+"px"),
                RowSpec.decode("25px"),
                RowSpec.decode("30px"),
                RowSpec.decode("25px"),
                RowSpec.decode((getPlotHeight()+15)+"px"),
                RowSpec.decode("25px"),
                RowSpec.decode("15px"),})); // @formatter:on
    }

    @Override
    public void drawPlotsAndLabels() {

        // clean display panel
        removeAll();

        // get coefficients
        Complex c0p = frame.state.getVacuum();
        Complex[] c1p = frame.state.get1PMom();
        Complex[][] c2p = frame.state.get2PMom();
        Double rest = frame.state.getRemainingProbability();

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        add(_momPlotVacuum, "3, 4, left, top");

        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        add(_posPlotVacuum, "3, 8, left, top");

        if (c1p != null) {
            _momPlot1P = new FunctionPlot(c1p, 0.0, 1.0, getPlotWidth(), getPlotHeight());
            add(_momPlot1P, "5, 4, left, top");

            _posPlot1P = new FunctionPlot(_ft.transform(c1p), 0.0, 1.0, getPlotWidth(), getPlotHeight());
            add(_posPlot1P, "5, 8, left, top");
        }

        if (c2p != null) {
            _momDensityPlot2P = new DensityPlot(c2p, 0.0, 1.0, getPlotWidth(), getPlotHeight());
            add(_momDensityPlot2P, "7, 4, left, top");

            _posDensityPlot2P = new DensityPlot(_ft.transform2D(c2p), 0.0, 1.0, getPlotWidth(), getPlotHeight());
            add(_posDensityPlot2P, "7, 8, left, top");
        }
        if (rest != null) {
            _momPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            add(_momPlotRest, "9, 4, left, top");

            _posPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            add(_posPlotRest, "9, 8, left, top");
        }

        // make plots interactive
        setupInteractivePlots();

        // make plot labels
        setupPlotLabels(c1p != null, c2p != null, rest != null);

        // update the frame
        frameUpdate();
    }

    private void setupPlotLabels(boolean show1P, boolean show2P, boolean showRest) {

        // set font color on labels
        JLabel[] labels = new JLabel[] { prob0Pmom, prob0Ppos, prob1Pmom, plbl, prob1Ppos, xlbl, p2lbl, p1lbl, x2lbl,
            x1lbl, prob3Pmom, prob3Ppos, timeLabel, totalProbLabel, totalEnergyLabel, lblMomentumSpace, lblMom1P,
            lblMom2P, lblMomRest, lblMomVacuum, lblPositionSpace, lblPos1P, lblPos2P, lblPosRest, lblPosVacuum };
        for (JLabel label : labels)
            label.setForeground(DISPLAY_LABEL_COLOR);
        lblClickAndDrag.setForeground(Color.DARK_GRAY); // tips are darker
        rescalingInfoLabel.setForeground(Color.DARK_GRAY);
        
        // time label
        timeLabel.setText("");
        add(timeLabel, "8, 10, 2, 1, right, top");

        // total probability label
        totalProbLabel.setText("");
        add(totalProbLabel, "7, 10, 2, 1, left, top");

        // total energy label
        totalEnergyLabel.setText("");
        add(totalEnergyLabel, "3, 10, 3, 1, right, top");

        // tip labels
        add(lblClickAndDrag, "2, 10, 3, 1, left, top");
        add(rescalingInfoLabel, "7, 2, 3, 1, right, top");

        // PLOT LABELS
        // titles
        add(lblMomentumSpace, "2, 2, 3, 1, left, center"); // momentum space title
        add(lblPositionSpace, "2, 6, 3, 1, left, center"); // position space title

        // 0 particles = vacuum
        add(lblMomVacuum, "2, 3, 2, 1, center, center"); // momentum title
        add(prob0Pmom, "2, 4, left, center"); // momentum y-label
        add(lblPosVacuum, "2, 7, 2, 1, center, center"); // position title
        add(prob0Ppos, "2, 8, left, center"); // position y-label

        if (show1P) { // 1 particle
            add(lblMom1P, "4, 3, 2, 1, center, center"); // momentum title
            add(prob1Pmom, "4, 4, left, center"); // momentum y-label
            add(plbl, "5, 5, center, top"); // momentum x-label

            add(lblPos1P, "4, 7, 2, 1, center, center"); // position title
            add(prob1Ppos, "4, 8, left, center"); // position y-label
            add(xlbl, "5, 9, center, top"); // position x-label
        }

        if (show2P) { // 2 particles
            add(lblMom2P, "6, 3, 2, 1, center, center"); // momentum title
            add(p2lbl, "6, 4, left, center"); // momentum y-label
            add(p1lbl, "7, 5, center, top"); // momentum x-label

            add(lblPos2P, "6, 7, 2, 1, center, center"); // position title
            add(x2lbl, "6, 8, left, center"); // position y-label
            add(x1lbl, "7, 9, center, top"); // position x-label
        }
        if (showRest) { // 3+ particles
            add(lblMomRest, "8, 3, 2, 1, center, center"); // momentum title
            add(prob3Pmom, "8, 4, left, center"); // momentum y-label

            add(lblPosRest, "8, 7, 2, 1, center, center"); // position title
            add(prob3Ppos, "8, 8, left, center"); // position y-label
        }
    }

    /**** INTERACTIVE PLOTS ****/
    @Override
    protected void setupInteractivePlots() {

        // momentum and position vacuum mouse clickability (sets to vacuum)
        if (_momPlotVacuum != null) {
            PlotListener vacuumListener = new PlotListener(frame, _momPlotVacuum) {
                public void setWavePacket(MouseEvent e) {
                    wavePacket = new MomentumWavePacket(N);
                }
            };
            _momPlotVacuum.addMouseListener(vacuumListener);
            _posPlotVacuum.addMouseListener(vacuumListener);
        }

        if (_momPlot1P != null) {
            // momentum 1 particle mouse clickability (sets to wave packet peaking at click)
            PlotListener mom1PListener = new PlotListener(frame, _momPlot1P) {
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
            _momPlot1P.addMouseListener(mom1PListener);
            _momPlot1P.addMouseMotionListener(mom1PListener);

            // position 1 particle mouse clickability (sets to wave packet peaking at click)
            PlotListener pos1PListener = new PlotListener(frame, _posPlot1P) {
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
            _posPlot1P.addMouseListener(pos1PListener);
            _posPlot1P.addMouseMotionListener(pos1PListener);
        }

        if (_momDensityPlot2P != null) {
            // momentum 2 particle mouse clickability (sets to wave packet peaking at click)
            PlotListener mom2PListener = new PlotListener(frame, _momDensityPlot2P) {
                public void setWavePacket(MouseEvent e) {
                    if (!e.isShiftDown()) {
                        double phase1 = 2 * Math.PI * (e.getX() - x_init) / width;
                        double phase2 = 2 * Math.PI * (1.0 - 1.0 * e.getY() / height);
                        wavePacket = new MomentumWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init },
                            new double[] { phase1, phase2 });
                    }
                    else {
                        int p1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        int p2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                        wavePacket = new MomentumWavePacket(N, new int[] { p1, p2 }, new double[] { 0, 0 });
                    }
                }
            };
            _momDensityPlot2P.addMouseListener(mom2PListener);
            _momDensityPlot2P.addMouseMotionListener(mom2PListener);

            PlotListener pos2PListener = new PlotListener(frame, _posDensityPlot2P) {
                public void setWavePacket(MouseEvent e) {
                    if (!e.isShiftDown()) {
                        double phase1 = 2 * Math.PI * (e.getX() - x_init) / width;
                        double phase2 = 2 * Math.PI * (1.0 - 1.0 * e.getY() / height);
                        wavePacket = new PositionWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init },
                            new double[] { phase1, phase2 });
                    }
                    else {
                        int x1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        int x2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                        wavePacket = new PositionWavePacket(N, new int[] { x1, x2 }, new double[] { 0, 0 });
                    }
                }
            };
            _posDensityPlot2P.addMouseListener(pos2PListener);
            _posDensityPlot2P.addMouseMotionListener(pos2PListener);
        }
    }

    // FIRED EVERY TIME FRAME
    @Override
    public void frameUpdate() {

        // exit if state is not set up
        if (frame.state == null)
            return;

        updateStateStats();

        // get coefficients
        Complex c0p = frame.state.getVacuum();
        Complex[] c1p = frame.state.get1PMom();
        Complex[][] c2p = frame.state.get2PMom();
        Double rest = frame.state.getRemainingProbability();

        // plot coefficients if they exist
        _momPlotVacuum.update(c0p);
        _posPlotVacuum.update(c0p);
        if (c1p != null) {
            _momPlot1P.update(c1p);
            _posPlot1P.update(_ft.transform(c1p));
        }
        if (c2p != null) {
            _momDensityPlot2P.update(c2p);
            _posDensityPlot2P.update(_ft.transform2D(c2p));
        }
        if (rest != null) {
            Complex restCoeff = Complex.one().times(Math.sqrt(rest));
            _momPlotRest.update(restCoeff);
            _posPlotRest.update(restCoeff);
        }

        frame.state.step(frame.stepsSlider.getValue());
    }
}
