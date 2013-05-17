package uk.ac.cam.cal56.graphics.impl;

import java.awt.Color;

import javax.swing.JLabel;

import uk.ac.cam.cal56.graphics.CoefficientPanel;
import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.graphics.SimulatorFrame;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.qft.fermions.FermionState;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class FermionCoefficientPanel extends CoefficientPanel {

    /* PLOT DATA VARIABLES */
    // Momentum space plots
    protected Plot _momPlotVacuum;
    protected Plot _momPlot1P;
    protected Plot _momPlot1AntiP;
    protected Plot _momDensityPlot2P;
    protected Plot _momDensityPlot1P1AntiP;
    protected Plot _momDensityPlot2AntiP;
    protected Plot _momPlotRest;

    // Position space plots
    protected Plot _posPlotVacuum;
    protected Plot _posPlot1P;
    protected Plot _posPlot1AntiP;
    protected Plot _posDensityPlot2P;
    protected Plot _posDensityPlot1P1AntiP;
    protected Plot _posDensityPlot2AntiP;
    protected Plot _posPlotRest;

    public FermionCoefficientPanel(SimulatorFrame frame) {
        super(frame);
    }

    // @formatter:off
    protected int getPlotWidth() { return 128; }
    protected int getDPlotHeight() { return getPlotWidth(); }
    protected int getPlotHeight() { return 2 * getPlotWidth() + 15;  }
    // @formatter:on

    @Override
    protected void setup() {
        setBackground(DISPLAY_BG_COLOR);

        // set layout @formatter:off
        setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("20px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("60px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode((getPlotHeight()+15)+"px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode((getPlotWidth()+15)+"px"),
                ColumnSpec.decode((getPlotWidth()+15)+"px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("30px"),},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("30px"),
                RowSpec.decode("25px"),
                RowSpec.decode((getDPlotHeight()+15)+"px"),
                RowSpec.decode((getDPlotHeight()+15)+"px"),
                RowSpec.decode("20px"),
                RowSpec.decode("25px"),
                RowSpec.decode("25px"),
                RowSpec.decode((getDPlotHeight()+15)+"px"),
                RowSpec.decode((getDPlotHeight()+15)+"px"),
                RowSpec.decode("25px"),
                RowSpec.decode("15px"),})); // @formatter:on
    }

    @Override
    public void drawPlotsAndLabels() {

        // clean display panel
        removeAll();

        FermionState state = ((FermionState) (frame.state));

        // get coefficients
        Complex c0p = state.getVacuum();
        Complex[] c1p = state.get1PMom();
        Complex[] c1Ap = state.get1AntiPMom();
        Complex[][] c2p = state.get2PMom();
        Complex[][] c1p1Ap = state.get1P1AntiPMom();
        Complex[][] c2Ap = state.get2AntiPMom();
        Double rest = state.getRemainingProbability();

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        add(_momPlotVacuum, "3, 4, 1, 2, left, top");
        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        add(_posPlotVacuum, "3, 9, 1, 2, left, top");

        if (c1p != null) {
            _momPlot1P = new FunctionPlot(c1p, 0.0, 1.0, 2 * getPlotWidth(), getDPlotHeight());
            add(_momPlot1P, "5, 4, left, top");
            _posPlot1P = new FunctionPlot(_ft.transform(c1p), 0.0, 1.0, 2 * getPlotWidth(), getDPlotHeight());
            add(_posPlot1P, "5, 9, left, top");

            _momPlot1AntiP = new FunctionPlot(c1Ap, 0.0, 1.0, 2 * getPlotWidth(), getDPlotHeight());
            add(_momPlot1AntiP, "5, 5, left, top");
            _posPlot1AntiP = new FunctionPlot(_ft.transform(c1Ap), 0.0, 1.0, 2 * getPlotWidth(), getDPlotHeight());
            add(_posPlot1AntiP, "5, 10, left, top");
        }

        if (c2p != null) {
            _momDensityPlot2P = new DensityPlot(c2p, 0.0, 1.0, getPlotWidth(), getDPlotHeight());
            add(_momDensityPlot2P, "7, 4, left, top");
            _posDensityPlot2P = new DensityPlot(_ft.transform2D(c2p), 0.0, 1.0, getPlotWidth(), getDPlotHeight());
            add(_posDensityPlot2P, "7, 9, left, top");

            _momDensityPlot1P1AntiP = new DensityPlot(c1p1Ap, 0.0, 1.0, getPlotWidth(), getDPlotHeight());
            add(_momDensityPlot1P1AntiP, "7, 5, left, top");
            _posDensityPlot1P1AntiP = new DensityPlot(_ft.transform2D(c1p1Ap), 0.0, 1.0, getPlotWidth(),
                getDPlotHeight());
            add(_posDensityPlot1P1AntiP, "7, 10, left, top");

            _momDensityPlot2AntiP = new DensityPlot(c2p, 0.0, 1.0, getPlotWidth(), getDPlotHeight());
            add(_momDensityPlot2AntiP, "8, 4, left, top");
            _posDensityPlot2AntiP = new DensityPlot(_ft.transform2D(c2Ap), 0.0, 1.0, getPlotWidth(), getDPlotHeight());
            add(_posDensityPlot2AntiP, "8, 9, left, top");
        }

        if (rest != null) {
            _momPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            add(_momPlotRest, "10, 4, 1, 2, left, top");
            _posPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            add(_posPlotRest, "10, 9, 1, 2, left, top");
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
        add(timeLabel, "9, 12, 2, 1, right, top");

        // total probability label
        totalProbLabel.setText("");
        add(totalProbLabel, "7, 12, 2, 1, left, top");

        // total energy label
        totalEnergyLabel.setText("");
        add(totalEnergyLabel, "3, 12, 3, 1, right, top");

        // tip labels
        add(lblClickAndDrag, "2, 12, 3, 1, left, top");
        add(rescalingInfoLabel, "8, 2, 3, 1, right, top");

        // PLOT LABELS
        // titles
        add(lblMomentumSpace, "2, 2, 3, 1, left, center"); // momentum space title
        add(lblPositionSpace, "2, 7, 3, 1, left, center"); // position space title

        // 0 particles = vacuum
        add(lblMomVacuum, "2, 3, 2, 1, center, center"); // momentum title
        add(prob0Pmom, "2, 4, 1, 2, left, center"); // momentum y-label
        add(lblPosVacuum, "2, 8, 2, 1, center, center"); // position title
        add(prob0Ppos, "2, 9, 1, 2, left, center"); // position y-label

        if (show1P) { // 1 particle
            add(lblMom1P, "4, 3, 2, 1, center, center"); // momentum title
            add(prob1Pmom, "4, 4, left, center"); // momentum y-label
            add(plbl, "5, 5, center, top"); // momentum x-label

            add(lblPos1P, "4, 8, 2, 1, center, center"); // position title
            add(prob1Ppos, "4, 9, left, center"); // position y-label
            add(xlbl, "5, 11, center, top"); // position x-label
        }

        if (show2P) { // 2 particles
            add(lblMom2P, "7, 3, 2, 1, center, center"); // momentum title
            add(p2lbl, "6, 4, left, center"); // momentum y-label
            add(p2Albl, "6, 5, left, center"); // momentum y-label
            add(p1lbl, "7, 6, center, top"); // momentum x-label

            add(lblPos2P, "7, 8, 2, 1, center, center"); // position title
            add(x2lbl, "6, 9, left, center"); // position y-label
            add(x1lbl, "7, 11, center, top"); // position x-label
        }
        if (showRest) { // 3+ particles
            add(lblMomRest, "9, 3, 2, 1, center, center"); // momentum title
            add(prob3Pmom, "9, 4, 1, 2, left, center"); // momentum y-label

            add(lblPosRest, "9, 8, 2, 1, center, center"); // position title
            add(prob3Ppos, "9, 9, 1, 2, left, center"); // position y-label
        }
    }

    /**** INTERACTIVE PLOTS ****/
    @Override
    protected void setupInteractivePlots() {

        setupVacuumPlots(_momPlotVacuum, _posPlotVacuum);

        setup1ParticlePlots(_momPlot1P, _posPlot1P);
        setup1ParticlePlots(_momPlot1AntiP, _posPlot1AntiP);

        setup2ParticlePlots(_momDensityPlot2P, _posDensityPlot2P);
        setup2ParticlePlots(_momDensityPlot1P1AntiP, _posDensityPlot1P1AntiP);
        setup2ParticlePlots(_momDensityPlot2AntiP, _posDensityPlot2AntiP);

    }

    @Override
    public void frameUpdate() {

        FermionState state = ((FermionState) (frame.state));

        // exit if state is not set up
        if (state == null)
            return;

        updateStateStats();

        // get coefficients
        Complex c0p = state.getVacuum();
        Complex[] c1p = state.get1PMom();
        Complex[] c1Ap = state.get1AntiPMom();
        Complex[][] c2p = state.get2PMom();
        Complex[][] c1p1Ap = state.get1P1AntiPMom();
        Complex[][] c2Ap = state.get2AntiPMom();
        Double rest = state.getRemainingProbability();

        // plot coefficients if they exist
        _momPlotVacuum.update(c0p);
        _posPlotVacuum.update(c0p);
        if (c1p != null) {
            _momPlot1P.update(c1p);
            _posPlot1P.update(_ft.transform(c1p));

            _momPlot1AntiP.update(c1Ap);
            _posPlot1AntiP.update(_ft.transform(c1Ap));
        }
        if (c2p != null) {
            _momDensityPlot2P.update(c2p);
            _posDensityPlot2P.update(_ft.transform2D(c2p));

            _momDensityPlot1P1AntiP.update(c1p1Ap);
            _posDensityPlot1P1AntiP.update(_ft.transform2D(c1p1Ap));

            _momDensityPlot2AntiP.update(c2Ap);
            _posDensityPlot2AntiP.update(_ft.transform2D(c2Ap));
        }
        if (rest != null) {
            Complex restCoeff = Complex.one().times(Math.sqrt(rest));
            _momPlotRest.update(restCoeff);
            _posPlotRest.update(restCoeff);
        }

        state.step(frame.stepsSlider.getValue());
    }

}
