package uk.ac.cam.cal56.graphics.impl;

import java.awt.Color;

import javax.swing.JLabel;

import uk.ac.cam.cal56.graphics.DisplayPanel;
import uk.ac.cam.cal56.graphics.Plot;
import uk.ac.cam.cal56.graphics.SimulatorFrame;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class ScalarFieldPanel extends DisplayPanel {

    private Plot _phiPlot;    // phi : field value
    private Plot _piPlot; // pi : conjugate momentum

    // @formatter:off
    protected int getPlotWidth() { return 640; }
    protected int getPlotHeight() { return 256; }
    // @formatter:on

    protected final JLabel titleLabel       = new JLabel("<html><strong>Fields</strong></html>");
    protected final JLabel xPhiLabel        = new JLabel("<html>x<sub>n</sub></html>");
    protected final JLabel xPiLabel         = new JLabel(xPhiLabel.getText());
    protected final JLabel phiLabel         = new JLabel("<html>&phi;<sub>n</sub></html>");
    protected final JLabel piLabel          = new JLabel("<html>&pi;<sub>n</sub></html>");
    protected final JLabel fieldPlotLabel   = new JLabel(
                                                "<html>Field value <em>&phi;<sub>n</sub></em> at positions <em>x<sub>n</sub></em></html>");
    protected final JLabel conjMomPlotLabel = new JLabel(
                                                "<html>Conjuagate momentum <em>&pi;<sub>n</sub></em> at positions <em>x<sub>n</sub></em></html>");

    public ScalarFieldPanel(SimulatorFrame frame) {
        super(frame);
    }

    @Override
    protected void setup() {
        setBackground(DISPLAY_BG_COLOR);

        // set layout @formatter:off
        setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("15px"),
                ColumnSpec.decode("30px"),
                ColumnSpec.decode("20px"),
                ColumnSpec.decode((getPlotWidth()+15)+"px"), // col #4
                ColumnSpec.decode("20px"),
                ColumnSpec.decode("40px"),},
            new RowSpec[] {
                RowSpec.decode("10px"),
                RowSpec.decode("30px"),
                RowSpec.decode("20px"),
                RowSpec.decode((getPlotHeight()+15)+"px"), // row #4
                RowSpec.decode("20px"),
                RowSpec.decode("30px"),
                RowSpec.decode("20px"),
                RowSpec.decode((getPlotHeight()+15)+"px"), // row #8
                RowSpec.decode("20px"),
                RowSpec.decode("30px"),
                RowSpec.decode("30px"),})); // @formatter:on
    }

    @Override
    public void drawPlotsAndLabels() {
        // define plots
        _phiPlot = new FunctionPlot(((ScalarState) frame.state).getPhi(), 0.0, Double.MAX_VALUE, getPlotWidth(),
            getPlotHeight());
        _piPlot = new FunctionPlot(((ScalarState) frame.state).getPi(), 0.0, Double.MAX_VALUE, getPlotWidth(),
            getPlotHeight());

        // add to panel
        add(_piPlot, "4, 4, center, center");
        add(_phiPlot, "4, 8, center, center");

        // add labels
        setupLabels();

        // update to show
        frameUpdate();
    }

    private void setupLabels() {

        // set font color on labels
        JLabel[] labels = new JLabel[] { titleLabel, timeLabel, totalProbLabel, totalEnergyLabel, fieldPlotLabel,
            conjMomPlotLabel, phiLabel, piLabel, xPhiLabel, xPiLabel };
        for (JLabel label : labels)
            label.setForeground(DISPLAY_LABEL_COLOR);
        rescalingInfoLabel.setForeground(Color.DARK_GRAY); // tips are darker

        // title
        add(titleLabel, "2, 2, 3, 1, left, top");

        // tips
        add(rescalingInfoLabel, "5, 2, 2, 1, right, top");

        // plot labels
        add(fieldPlotLabel, "4, 7, center, bottom");
        add(conjMomPlotLabel, "4, 3, center, bottom");
        add(phiLabel, "3, 8, right, center");
        add(piLabel, "3, 4, right, center");
        add(xPhiLabel, "4, 9, center, top");
        add(xPiLabel, "4, 5, center, top");

        // time label
        totalEnergyLabel.setText("");
        add(totalEnergyLabel, "3, 10, 2, 1, left, top");

        // total probability label
        totalProbLabel.setText("");
        add(totalProbLabel, "4, 10, center, top");

        // total energy label
        timeLabel.setText("");
        add(timeLabel, "4, 10, 2, 1, right, top");

        setupColorLegend();

    }

    private void setupColorLegend() {

        JLabel positive = new JLabel("Positive/");
        JLabel negative = new JLabel("Negative values");
        positive.setForeground(Plot.toFunctionColor(0.5, 0));
        negative.setForeground(Plot.toFunctionColor(0.5, Math.PI));
        add(positive, "1, 11, 3, 1, right, center");
        add(negative, "4, 11, left, center");
    }

    @Override
    public void frameUpdate() {

        ScalarState state = ((ScalarState) (frame.state));

        // exit if state is not set up
        if (state == null)
            return;
        updateStateStats();

        // plot coefficients
        _piPlot.update(((ScalarState) state).getPi());
        _phiPlot.update(((ScalarState) state).getPhi());

        // step state
        state.step(frame.stepsSlider.getValue());
    }

}
