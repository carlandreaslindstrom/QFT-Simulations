package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.cam.cal56.graphics.impl.FermionDisplayPanel;
import uk.ac.cam.cal56.graphics.impl.ScalarDisplayPanel;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public abstract class SimulatorFrame extends JFrame {

    /***** VARIABLES *****/

    /* STATIC VARIABLES */
    protected static final String                SELECTOR_DEFAULT      = "Select a preset...";

    protected static final String                BUTTON_CALCULATE      = "Calculate";
    protected static final String                BUTTON_PLAY           = "Play";
    protected static final String                BUTTON_STOP           = "Stop";
    protected static final String                BUTTON_RESET          = "Reset";
    protected static final String                BUTTON_GROUNDSTATE    = "Set to ground state";

    protected static int                         _recalculateBeforeRow;
    private int                                  _controlPanelRowAdder = 1;

    /* QUANTUM STATE VARIABLES */
    // quantum state and wavepacket it starts as
    public State                                 state;

    /* ANIMATION VARIABLES */
    // Animation parameters and objects
    protected double                             _framerate            = 30.0;
    protected Animator                           _animator             = new Animator();

    /* FRAME SETUP VARIABLES */
    // Panels
    protected JPanel                             controlPanel          = new JPanel();
    protected DisplayPanel                       displayPanel;

    // Sliders
    protected JSlider                            _NSlider              = new JSlider(getNMin(), getNMax());
    protected JSlider                            _PmaxSlider           = new JSlider(getPmaxMin(), getPmaxMax());
    protected JSlider                            _dxSlider             = new JSlider(encode(getDxMin()),
                                                                           encode(getDxMax()));
    protected JSlider                            _mSlider              = new JSlider(encode(getMMin()),
                                                                           encode(getMMax()));
    protected JSlider                            _dtSlider             = new JSlider(encode(getDtMin()),
                                                                           encode(getDtMax()));
    public JSlider                               stepsSlider           = new JSlider(getStepsMin(), getStepsMax());

    protected JRadioButton                       _scalarButton         = new JRadioButton("Scalars");
    protected JRadioButton                       _fermionButton        = new JRadioButton("Fermions");
    protected JRadioButton                       _bothButton           = new JRadioButton("Both");

    // Buttons
    protected JButton                            _calculateButton      = new JButton(BUTTON_CALCULATE);
    protected JButton                            _playButton           = new JButton(BUTTON_PLAY);
    protected JButton                            _resetButton          = new JButton(BUTTON_RESET);
    protected JButton                            _groundStateButton    = new JButton(BUTTON_GROUNDSTATE);

    // Interaction sliders and checkboxes
    protected static Map<Interaction, JCheckBox> _checkBoxes           = new HashMap<Interaction, JCheckBox>();
    protected static Map<Interaction, JSlider>   _interactionSliders   = new HashMap<Interaction, JSlider>();
    protected static Map<Interaction, String>    _interactionToolTips  = new HashMap<Interaction, String>();
    protected static Map<Interaction, JCheckBox> _negativeCheckBoxes   = new HashMap<Interaction, JCheckBox>();

    /**** ABSTRACT METHODS ****/

    // quantum state and plots representing it
    protected abstract void setupQuantumState(WavePacket wavePacket);

    // @formatter:off
    protected abstract String getFrameTitle();
    protected abstract int getFrameWidth();
    protected abstract int getFrameHeight();
    protected abstract int getNMin();
    protected abstract int getNMax();
    protected abstract int getPmaxMin();
    protected abstract int getPmaxMax();
    protected abstract double getDxMin();
    protected abstract double getDxMax();
    protected abstract double getMMin();
    protected abstract double getMMax();
    protected abstract double getDtMin();
    protected abstract double getDtMax();
    protected abstract int getStepsMin();
    protected abstract int getStepsMax();
    protected abstract double getLambdaMin();
    protected abstract double getLambdaMax();
    protected abstract Preset getDefaultPreset();
    // @formatter:on

    /***** FUNCTIONS *****/

    // Constructor
    public SimulatorFrame() {
        setupInteractions();
        setupFrame();
        applyPreset(getDefaultPreset());
    }

    protected void setupInteractions() {
        _checkBoxes.put(Interaction.PHI_SQUARED, new JCheckBox("<html>&Phi;<sup>2</sup></html>"));
        _checkBoxes.put(Interaction.PHI_CUBED, new JCheckBox("<html>&Phi;<sup>3</sup></html>"));
        _checkBoxes.put(Interaction.PHI_FOURTH, new JCheckBox("<html>&Phi;<sup>4</sup></html>"));

        _interactionSliders.put(Interaction.PHI_SQUARED, new JSlider(encode(getLambdaMin()), encode(getLambdaMax())));
        _interactionSliders.put(Interaction.PHI_CUBED, new JSlider(encode(getLambdaMin()), encode(getLambdaMax())));
        _interactionSliders.put(Interaction.PHI_FOURTH, new JSlider(encode(getLambdaMin()), encode(getLambdaMax())));

        _interactionToolTips.put(Interaction.PHI_SQUARED, "2-vertex interaction strength");
        _interactionToolTips.put(Interaction.PHI_CUBED, "3-vertex interaction strength");
        _interactionToolTips.put(Interaction.PHI_FOURTH, "4-vertex interaction strength");

        _negativeCheckBoxes.put(Interaction.PHI_SQUARED, new JCheckBox());
        _negativeCheckBoxes.put(Interaction.PHI_CUBED, new JCheckBox());
        _negativeCheckBoxes.put(Interaction.PHI_FOURTH, new JCheckBox());
    }

    protected void setupFrame() {
        setTitle(getFrameTitle());
        setBounds(0, 0, getFrameWidth(), getFrameHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout(0, 0));
        setupControlPanel();
        setupDisplayPanel();
    }

    protected void setupDisplayPanel() {
        if (_scalarButton.isSelected())
            displayPanel = new ScalarDisplayPanel(this);
        else if (_fermionButton.isSelected())
            displayPanel = new FermionDisplayPanel(this);
        getContentPane().add(displayPanel, BorderLayout.CENTER);
    }

    protected void setupControlPanel() {
        // make it pretty
        controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(controlPanel, BorderLayout.EAST);

        // form layout @formatter:off
        controlPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("40px"),
                ColumnSpec.decode("175px:grow"),
                ColumnSpec.decode("74px"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
            new RowSpec[] {
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,}));// @formatter:on

        // setup preset selector
        setupPresetSelector();

        // set up scalar-fermion radiobuttons
        setupScalarFermionRadioButtions();

        // setup sliders
        setupSlidersAndButtons();

        // setup buttons (calculate, play, reset)
        setupButtons();

    }

    protected void setupScalarFermionRadioButtions() {
        // make button group
        ButtonGroup group = new ButtonGroup();
        group.add(_scalarButton);
        group.add(_fermionButton);
        group.add(_bothButton);

        // set default
        _scalarButton.setSelected(true);
        _bothButton.setEnabled(false); // disable the "both"-button for the moment

        // add change listener
        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _calculateButton.setEnabled(true);
            }
        };
        _scalarButton.addChangeListener(cl);
        _fermionButton.addChangeListener(cl);
        _bothButton.addChangeListener(cl);

        controlPanel.add(_scalarButton, "1, " + (_controlPanelRowAdder) + ", 3, 1, left, center");
        controlPanel.add(_fermionButton, "1, " + (_controlPanelRowAdder) + ", 3, 1, center, center");
        controlPanel.add(_bothButton, "1, " + (_controlPanelRowAdder++) + ", 3, 1, right, center");
    }

    protected void setupPresetSelector() {
        final JComboBox presetSelector = new JComboBox();

        // add presets
        presetSelector.addItem(SELECTOR_DEFAULT);
        for (Preset preset : Preset.all)
            presetSelector.addItem(preset);

        // time step update listener
        presetSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object item = presetSelector.getSelectedItem();
                if (item.getClass() == Preset.class)
                    applyPreset((Preset) item);
            }
        });

        // add to control panel
        controlPanel.add(presetSelector, "1, " + (_controlPanelRowAdder++) + ", 3, 1, fill, default");
    }

    private void applyPreset(Preset preset) {
        _NSlider.setValue(preset.N);
        _PmaxSlider.setValue(preset.Pmax);
        _dxSlider.setValue(encode(preset.dx));
        _mSlider.setValue(encode(preset.m));
        _dtSlider.setValue(encode(preset.dt));
        stepsSlider.setValue(preset.steps);

        _checkBoxes.get(Interaction.PHI_SQUARED).setSelected(preset.lambda2 != null);
        if (preset.lambda2 != null)
            _interactionSliders.get(Interaction.PHI_SQUARED).setValue(encode(preset.lambda2));

        _checkBoxes.get(Interaction.PHI_CUBED).setSelected(preset.lambda3 != null);
        if (preset.lambda3 != null)
            _interactionSliders.get(Interaction.PHI_CUBED).setValue(encode(preset.lambda3));

        _checkBoxes.get(Interaction.PHI_FOURTH).setSelected(preset.lambda4 != null);
        if (preset.lambda4 != null)
            _interactionSliders.get(Interaction.PHI_FOURTH).setValue(encode(preset.lambda4));

        calculate(preset.wavepacket);
    }

    protected void setupSlidersAndButtons() {
        // add calculate sliders
        setupGeneralSlider(_NSlider, getNMin(), getNMax(), int.class, null, "Number of lattice points");
        setupGeneralSlider(_PmaxSlider, getPmaxMin(), getPmaxMax(), int.class, null, "Number of particles considered");
        setupGeneralSlider(_dxSlider, encode(getDxMin()), encode(getDxMax()), double.class, null,
                           "Lattice point separation");
        setupGeneralSlider(_mSlider, encode(getMMin()), encode(getMMax()), double.class, null, "Particle mass");

        setupCheckboxes();
        _recalculateBeforeRow = _controlPanelRowAdder++;

        // add buttons to control panel
        controlPanel.add(_calculateButton, "2, " + (_controlPanelRowAdder++));
        controlPanel.add(Box.createVerticalStrut(20), "2, " + (_controlPanelRowAdder++));

        // add real time sliders...
        setupGeneralSlider(_dtSlider, encode(getDtMin()), encode(getDtMax()), double.class, null, "Time step");

        // ... with a real time update listener...
        _dtSlider.addChangeListener(new ChangeListener() { // update time step
            public void stateChanged(ChangeEvent e) {
                if (state != null)
                    state.setTimeStep(decode(_dtSlider.getValue()));
            }
        });
        setupGeneralSlider(stepsSlider, getStepsMin(), getStepsMax(), int.class, null, "Steps calculated per frame");

        // ... including interaction sliders (with change listeners)
        Interaction[] interactionsInOrder = new Interaction[] { Interaction.PHI_SQUARED, Interaction.PHI_CUBED,
            Interaction.PHI_FOURTH };
        for (final Interaction interaction : interactionsInOrder)
            setupGeneralSlider(_interactionSliders.get(interaction), encode(getLambdaMin()), encode(getLambdaMax()),
                               double.class, interaction, _interactionToolTips.get(interaction));

        // separator
        controlPanel.add(Box.createVerticalStrut(20), "2, " + _controlPanelRowAdder++);

        // play and reset buttons
        controlPanel.add(_playButton, "2, " + _controlPanelRowAdder++);
        controlPanel.add(_resetButton, "2, " + _controlPanelRowAdder++);
        controlPanel.add(_groundStateButton, "2, " + _controlPanelRowAdder++);

    }

    private void setupCheckboxes() {
        controlPanel.add(_checkBoxes.get(Interaction.PHI_SQUARED), "2, " + _controlPanelRowAdder + ", left, top");
        controlPanel.add(_checkBoxes.get(Interaction.PHI_CUBED), "2, " + _controlPanelRowAdder + ", center, top");
        controlPanel.add(_checkBoxes.get(Interaction.PHI_FOURTH), "2, " + _controlPanelRowAdder + ", right, top");
        ChangeListener calculateButtonEnabler = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _calculateButton.setEnabled(true);
            }
        };
        for (JCheckBox checkBox : _checkBoxes.values())
            checkBox.addChangeListener(calculateButtonEnabler);
    }

    protected void setupGeneralSlider(final JSlider slider, int min, int max, final Class<?> type,
                                      final Interaction interaction, String toolTip) {
        final int row = _controlPanelRowAdder;

        if (type == double.class) {
            Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
            labelTable.put(min, new JLabel(decodeText(min)));
            labelTable.put(max, new JLabel(decodeText(max)));
            slider.setLabelTable(labelTable);
        }
        slider.setMajorTickSpacing(max - min);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel icon = new JLabel("");
        icon.setIcon(new ImageIcon(getClass().getResource("icons/" + row + ".png")));
        icon.setToolTipText(toolTip);

        final JLabel value = new JLabel();

        // add negative value checkboxes
        if (interaction != null)
            controlPanel.add(_negativeCheckBoxes.get(interaction), "1, " + row + ", left, top");

        controlPanel.add(icon, "1, " + row + ", center, center");
        controlPanel.add(slider, "2, " + row + ", left, top");
        controlPanel.add(value, "3, " + row + ", center, center");

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (interaction != null) {
                    final JCheckBox checkbox = _negativeCheckBoxes.get(interaction);
                    ChangeListener cl = new ChangeListener() { // update interaction strength
                        public void stateChanged(ChangeEvent e) {
                            if (state != null) {
                                int negativeFactor = checkbox.isSelected() ? -1 : 1;
                                state.setInteractionStrength(interaction, negativeFactor * decode(slider.getValue()));
                            }
                            boolean negative = _negativeCheckBoxes.get(interaction).isSelected();
                            value.setText(decodeText(slider.getValue(), negative));
                        }
                    };

                    slider.addChangeListener(cl);
                    checkbox.addChangeListener(cl);
                }
                else if (type == double.class) {
                    value.setText(decodeText(slider.getValue()));
                }
                else if (type == int.class)
                    value.setText(slider.getValue() + "");

                // if the slider necessitates recalculation, enable the calculate button (if not already)
                if (row < _recalculateBeforeRow)
                    _calculateButton.setEnabled(true);
            }
        });

        // trigger events initially to show values
        ChangeEvent ce = new ChangeEvent(slider);
        for (ChangeListener cl : slider.getChangeListeners())
            cl.stateChanged(ce);

        _controlPanelRowAdder++; // increment row
    }

    // add buttons to the control panel
    protected void setupButtons() {
        _resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        _playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_playButton.getText() == BUTTON_PLAY)
                    start();
                else if (_playButton.getText() == BUTTON_STOP)
                    stop();
            }
        });
        _groundStateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
                state.setToGroundState();
                start();
            }
        });
        // add appropriate action listeners
        _calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculate(new MomentumWavePacket(_NSlider.getValue()));
            }
        });

        // initially disable the play and reset buttons
        _playButton.setEnabled(false);
        _resetButton.setEnabled(false);
    }

    protected void calculate(WavePacket wavePacket) {
        // stop animation and update buttons
        _animator.stopAnimation();
        _playButton.setEnabled(false);
        _playButton.setText(BUTTON_PLAY);

        // set up the requested quantum state
        setupQuantumState(wavePacket);

        // redraw plots and labels
        getContentPane().remove(displayPanel);
        setupDisplayPanel();
        displayPanel.drawPlotsAndLabels();

        // update interaction sliders
        for (Interaction interaction : _interactionSliders.keySet()) {
            _interactionSliders.get(interaction).setEnabled(_checkBoxes.get(interaction).isSelected());
            _negativeCheckBoxes.get(interaction).setEnabled(_checkBoxes.get(interaction).isSelected());
        }

        // update buttons and start animation
        _playButton.setEnabled(true);
        _resetButton.setEnabled(false);
        _calculateButton.setEnabled(false);
        start();
    }

    protected void start() {
        _animator.startAnimation();
        _playButton.setText(BUTTON_STOP);
        _resetButton.setEnabled(true);
        displayPanel.frameUpdate();
    }

    protected void stop() {
        _animator.stopAnimation();
        _playButton.setText(BUTTON_PLAY);
        displayPanel.frameUpdate();
    }

    protected void reset() {
        _resetButton.setEnabled(false);
        _animator.stopAnimation();
        _playButton.setText(BUTTON_PLAY);
        if (state != null)
            state.reset(); // reset quantum state
        displayPanel.frameUpdate();
    }

    /**** functions to encode doubles as slider values (integers) ****/

    protected static Integer encode(double d) {
        return (int) (100.0 * (Math.log10(d)));
    }

    protected static double decode(int encoded) {
        return Math.pow(10, encoded / 100.0);
    }

    // converts to scientific notation
    protected static String decodeText(int encoded) {
        return decodeText(encoded, false);
    }

    protected static String decodeText(int encoded, boolean negative) {
        double number = decode(encoded);
        int exponent = (int) Math.floor(Math.log10(number));
        double mantissa = number / Math.pow(10, exponent);
        String digit = (new DecimalFormat("#.#").format(mantissa));
        return "<html>" + (negative ? "-" : "") + (digit.equals("1") ? "" : digit + "x") + "10<sup>" + exponent +
               "</sup></html>";
    }

    /***** ANIMATION INNER CLASS *****/

    protected class Animator implements ActionListener {
        // takes care of animation
        private Timer   _timer;
        private boolean _frozen = true;
        private int     delay   = (int) (1000.0 / _framerate);

        public Animator() {
            _timer = new Timer(delay, this);
            _timer.setCoalesce(true);
        }

        public void startAnimation() {
            _timer.start();
            _frozen = false;
        }

        public void stopAnimation() {
            _timer.stop();
            _frozen = true;
        }

        // fired by timer
        public void actionPerformed(ActionEvent e) {
            if (!_frozen)
                displayPanel.frameUpdate();
        }
    }
}
