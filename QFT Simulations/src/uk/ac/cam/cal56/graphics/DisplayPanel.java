package uk.ac.cam.cal56.graphics;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class DisplayPanel extends JPanel {

    protected static final String LABEL_TIME          = "Time: ";
    protected static final String LABEL_TOTAL_PROB    = "Total probability: ";
    protected static final String LABEL_TOTAL_ENERGY  = "Total energy: ";

    public static final Color     DISPLAY_BG_COLOR    = Color.BLACK;
    protected static final Color  DISPLAY_LABEL_COLOR = Color.GRAY;

    // Value Labels
    protected JLabel              timeLabel           = new JLabel();
    protected JLabel              totalProbLabel      = new JLabel();
    protected JLabel              totalEnergyLabel    = new JLabel();
    protected JLabel              rescalingInfoLabel  = new JLabel(
                                                          "(Hold Alt + drag up/down to rescale plots / Hold AltGr + click to autorescale)");

    protected SimulatorFrame      frame;

    public DisplayPanel(SimulatorFrame frame) {
        this.frame = frame;
        setup();
    }

    // update time, total probability, total energy
    protected void updateStateStats() {
        totalEnergyLabel.setText(LABEL_TOTAL_ENERGY +
                                 new DecimalFormat("#.#E0 GeV").format(frame.state.getTotalEnergy()));
        totalProbLabel.setText(LABEL_TOTAL_PROB + new DecimalFormat("##0%").format(frame.state.getModSquared()));
        timeLabel.setText("<html>" + LABEL_TIME + new DecimalFormat("0.000").format(frame.state.getTime()) +
                          " GeV<sup>-1</sup></html>");
    }

    protected abstract void setup();

    public abstract void drawPlotsAndLabels();

    public abstract void frameUpdate();

}
