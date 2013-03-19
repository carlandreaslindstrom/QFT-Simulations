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
import uk.ac.cam.cal56.qft.scalars.impl.ScalarState;

@SuppressWarnings("serial")
public class QFTSandbox extends SimulatorFrame {
    public QFTSandbox() {
    }

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