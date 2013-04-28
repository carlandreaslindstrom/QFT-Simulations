package uk.ac.cam.cal56.graphics;

import java.awt.Color;
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
    protected Color getDisplayColor()   { return Color.BLACK; }
    protected Color getLabelColor()     { return Color.GRAY; }
    protected int getFrameWidth()       { return 1100; }
    protected int getFrameHeight()      { return 775; }
    protected int getPlotWidth()        { return 256; }
    protected int getPlotHeight()       { return getPlotWidth(); }
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
    protected Preset getDefaultPreset() { return Preset.INT_2P_FAST; }
    // @formatter:on

    // quantum state and plots representing it
    protected void setupQuantumState(WavePacket wavePacket) {

        // set up interaction strengths from sliders
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        for (Entry<Interaction, JSlider> entry : _interactionSliders.entrySet()) {
            Interaction interaction = entry.getKey();
            JSlider slider = entry.getValue();
            JCheckBox checkBox = _checkBoxes.get(interaction);
            if (checkBox.isSelected())
                lambdas.put(interaction, decode(slider.getValue()));
        }

        // make scalar quantum state
        if (_scalarButton.isSelected())
            _state = new ScalarState(_NSlider.getValue(), _PmaxSlider.getValue(), decode(_mSlider.getValue()),
                decode(_dxSlider.getValue()), decode(_dtSlider.getValue()), lambdas, wavePacket);

        // or a fermion quantum state
        else if (_fermionButton.isSelected())
            _state = new FermionState(_NSlider.getValue(), _PmaxSlider.getValue(), decode(_mSlider.getValue()),
                decode(_dxSlider.getValue()), decode(_dtSlider.getValue()), lambdas, wavePacket);
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