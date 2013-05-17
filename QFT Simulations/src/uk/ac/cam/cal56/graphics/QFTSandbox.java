package uk.ac.cam.cal56.graphics;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JSlider;

import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.fermions.FermionState;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

@SuppressWarnings("serial")
public class QFTSandbox extends SimulatorFrame {

    // @formatter:off
    protected String getFrameTitle()    { return "Quantum Field Theory on a Ring"; }
    protected int getFrameWidth()       { return 1120; }
    protected int getFrameHeight()      { return 820; }
    protected int getNMin()             { return 2; }
    protected int getNMax()             { return 128; }
    protected int getPmaxMin()          { return 1; }
    protected int getPmaxMax()          { return 7; }
    protected double getDxMin()         { return 1.0e-3; }
    protected double getDxMax()         { return 10.0; }
    protected double getMMin()          { return 1.0e-3; }
    protected double getMMax()          { return 10.0; }
    protected double getDtMin()         { return 1.0e-5; }
    protected double getDtMax()         { return 1.0e-1; }
    protected int getStepsMin()         { return 1; }
    protected int getStepsMax()         { return 256; }
    protected double getLambdaMin()     { return 1.0e-7; }
    protected double getLambdaMax()     { return 1.0e2; }
    protected Preset getDefaultPreset() { return Preset.INT_2P_3VERTEX; }
    // @formatter:on

    // quantum state and plots representing it
    protected void setupQuantumState(WavePacket wavePacket) {

        // set up interaction strengths from sliders
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        for (Entry<Interaction, JSlider> entry : interactionSliders.entrySet()) {
            Interaction interaction = entry.getKey();
            JSlider slider = entry.getValue();
            JCheckBox checkBox = interactionCheckBoxes.get(interaction);
            if (checkBox.isSelected())
                lambdas.put(interaction, decode(slider.getValue()));
        }

        // make scalar quantum state
        if (scalarButton.isSelected())
            state = new ScalarState(NSlider.getValue(), PmaxSlider.getValue(), decode(dtSlider.getValue()),
                decode(dxSlider.getValue()), decode(mSlider.getValue()), lambdas, wavePacket);

        // or a fermion quantum state
        else if (fermionButton.isSelected())
            state = new FermionState(NSlider.getValue(), PmaxSlider.getValue(), decode(dtSlider.getValue()),
                decode(dxSlider.getValue()), decode(mSlider.getValue()), lambdas, wavePacket);
    }

    /**** MAIN RUN FUNCTION ****/
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    QFTSandbox frame = new QFTSandbox();
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}