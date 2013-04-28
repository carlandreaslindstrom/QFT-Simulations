package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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

import uk.ac.cam.cal56.graphics.impl.DensityPlot;
import uk.ac.cam.cal56.graphics.impl.FunctionPlot;
import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FourierTransform;
import uk.ac.cam.cal56.maths.impl.FFT;
import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.impl.PositionWavePacket;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public abstract class SimulatorFrame extends JFrame {

    /***** VARIABLES *****/

    /* STATIC VARIABLES */

    protected static final String                LABEL_TIME            = "Time: ";
    protected static final String                LABEL_TOTAL_PROB      = "Total probability: ";
    protected static final String                LABEL_TOTAL_ENERGY    = "Total energy: ";

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
    protected State                              _state;

    /* PLOT DATA VARIABLES */
    // Momentum space plots
    protected Plot                               _momPlotVacuum;
    protected Plot                               _momPlot1P;
    protected Plot                               _momDensityPlot2P;
    protected Plot                               _momPlotRest;

    // Position space plots
    protected Plot                               _posPlotVacuum;
    protected Plot                               _posPlot1P;
    protected Plot                               _posDensityPlot2P;
    protected Plot                               _posPlotRest;

    /* FOURIER TRANSFORM */
    protected FourierTransform                   _ft                   = new FFT();

    /* ANIMATION VARIABLES */
    // Animation parameters and objects
    protected double                             _framerate            = 30.0;
    protected Animator                           _animator             = new Animator();

    /* FRAME SETUP VARIABLES */
    // Panels
    protected JPanel                             _controlPanel         = new JPanel();
    protected JPanel                             _displayPanel         = new JPanel();

    // Value Labels
    protected JLabel                             _timeLabel            = new JLabel();
    protected JLabel                             _totalProbLabel       = new JLabel();
    protected JLabel                             _totalEnergyLabel     = new JLabel();

    // Sliders
    protected JSlider                            _NSlider              = new JSlider(getNMin(), getNMax());
    protected JSlider                            _PmaxSlider           = new JSlider(getPmaxMin(), getPmaxMax());
    protected JSlider                            _dxSlider             = new JSlider(encode(getDxMin()),
                                                                           encode(getDxMax()));
    protected JSlider                            _mSlider              = new JSlider(encode(getMMin()),
                                                                           encode(getMMax()));
    protected JSlider                            _dtSlider             = new JSlider(encode(getDtMin()),
                                                                           encode(getDtMax()));
    protected JSlider                            _stepsSlider          = new JSlider(getStepsMin(), getStepsMax());

    protected JRadioButton                       _scalarButton         = new JRadioButton("Scalars");
    protected JRadioButton                       _fermionButton        = new JRadioButton("Fermions");
    protected JRadioButton                       _bothButton           = new JRadioButton("Both");

    // Explanatory labels
    protected final JLabel                       lblMomentumSpace      = new JLabel(
                                                                           "<html><strong>Momentum Space:</strong></html>");
    protected final JLabel                       lblMomVacuum          = new JLabel("Vacuum");
    protected final JLabel                       lblMom1P              = new JLabel(
                                                                           "<html>1 particle of momentum <em>p</em></html>");
    protected final JLabel                       lblMom2P              = new JLabel(
                                                                           "<html>2 particles of momenta <em>p<sub>1</sub></em> and <em>p<sub>2</sub></em></html>");
    protected final JLabel                       lblMomRest            = new JLabel("3+ particles");
    protected final JLabel                       lblPositionSpace      = new JLabel(
                                                                           "<html><strong>Position Space:</strong></html>");
    protected final JLabel                       lblPosVacuum          = new JLabel(lblMomVacuum.getText());
    protected final JLabel                       lblPos1P              = new JLabel(
                                                                           "<html>1 particle of position <em>x</em></html>");
    protected final JLabel                       lblPos2P              = new JLabel(
                                                                           "<html>2 particles of positions <em>x<sub>1</sub></em> and <em>x<sub>2</sub></em></html>");
    protected final JLabel                       lblPosRest            = new JLabel(lblMomRest.getText());

    protected final JLabel                       prob0Pmom             = new JLabel("<html>&phi;<sup>2</sup></html>");
    protected final JLabel                       prob0Ppos             = new JLabel(prob0Pmom.getText());
    protected final JLabel                       prob1Pmom             = new JLabel(prob0Pmom.getText());
    protected final JLabel                       plbl                  = new JLabel("p");
    protected final JLabel                       p1lbl                 = new JLabel("<html>p<sub>1</sub></html>");
    protected final JLabel                       p2lbl                 = new JLabel("<html>p<sub>2</sub></html>");
    protected final JLabel                       prob3Pmom             = new JLabel(prob0Pmom.getText());
    protected final JLabel                       prob1Ppos             = new JLabel(prob0Pmom.getText());
    protected final JLabel                       xlbl                  = new JLabel("x");
    protected final JLabel                       x1lbl                 = new JLabel("<html>x<sub>1</sub></html>");
    protected final JLabel                       x2lbl                 = new JLabel("<html>x<sub>2</sub></html>");
    protected final JLabel                       prob3Ppos             = new JLabel(prob0Pmom.getText());

    protected final JLabel                       lblTime               = new JLabel(LABEL_TIME);
    protected final JLabel                       lblClickAndDrag       = new JLabel(
                                                                           "(Click on plots to place particles)");

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
    protected abstract Color getDisplayColor();
    protected abstract Color getLabelColor();
    protected abstract int getFrameWidth();
    protected abstract int getFrameHeight();
    protected abstract int getPlotWidth();
    protected abstract int getPlotHeight();
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
        setupControlPanel();
        setupDisplayPanel();
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

    // FIRED EVERY TIME FRAME
    protected void frameUpdate() {

        // exit if state is not set up
        if (_state == null)
            return;

        // update time, total probability, total energy
        _totalEnergyLabel.setText(LABEL_TOTAL_ENERGY + new DecimalFormat("#.#E0 GeV").format(_state.getTotalEnergy()));
        _totalProbLabel.setText(LABEL_TOTAL_PROB + new DecimalFormat("##0%").format(_state.getModSquared()));
        _timeLabel.setText("<html>" + new DecimalFormat("0.000").format(_state.getTime()) + " GeV<sup>-1</sup></html>");

        // get coefficients
        Complex c0p = _state.getVacuum();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Double rest = _state.getRemainingProbability();

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

        _state.step(_stepsSlider.getValue());
    }

    protected void setupFrame() {
        setTitle(getFrameTitle());
        setBounds(0, 0, getFrameWidth(), getFrameHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
    }

    protected void setupDisplayPanel() {
        // make display panel
        getContentPane().add(_displayPanel, BorderLayout.CENTER);
        _displayPanel.setBackground(getDisplayColor());

        // set layout @formatter:off
        _displayPanel.setLayout(new FormLayout(new ColumnSpec[] {
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

    protected void setupControlPanel() {
        // make it pretty
        _controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(_controlPanel, BorderLayout.EAST);

        // form layout @formatter:off
        _controlPanel.setLayout(new FormLayout(new ColumnSpec[] {
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
        _scalarButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _calculateButton.setEnabled(true);
            }
        });
        _fermionButton.addChangeListener(_scalarButton.getChangeListeners()[0]); // reuse change listener
        _bothButton.addChangeListener(_scalarButton.getChangeListeners()[0]); // reuse change listener

        _controlPanel.add(_scalarButton, "1, " + (_controlPanelRowAdder) + ", 3, 1, left, center");
        _controlPanel.add(_fermionButton, "1, " + (_controlPanelRowAdder) + ", 3, 1, center, center");
        _controlPanel.add(_bothButton, "1, " + (_controlPanelRowAdder++) + ", 3, 1, right, center");
    }

    // draw plots after calculation
    protected void drawPlotsAndLabels() {

        // clean display panel
        _displayPanel.removeAll();

        // get coefficients
        Complex c0p = _state.getVacuum();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Double rest = _state.getRemainingProbability();

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        _displayPanel.add(_momPlotVacuum, "3, 4, left, top");

        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, getPlotHeight());
        _displayPanel.add(_posPlotVacuum, "3, 8, left, top");

        if (c1p != null) {
            _momPlot1P = new FunctionPlot(c1p, 0.0, 1.0, getPlotWidth(), getPlotHeight());
            _displayPanel.add(_momPlot1P, "5, 4, left, top");

            _posPlot1P = new FunctionPlot(_ft.transform(c1p), 0.0, 1.0, getPlotWidth(), getPlotHeight());
            _displayPanel.add(_posPlot1P, "5, 8, left, top");
        }

        if (c2p != null) {
            _momDensityPlot2P = new DensityPlot(c2p, 0.0, 1.0, getPlotWidth(), getPlotHeight());
            _displayPanel.add(_momDensityPlot2P, "7, 4, left, top");

            _posDensityPlot2P = new DensityPlot(_ft.transform2D(c2p), 0.0, 1.0, getPlotWidth(), getPlotHeight());
            _displayPanel.add(_posDensityPlot2P, "7, 8, left, top");
        }
        if (rest != null) {
            _momPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            _displayPanel.add(_momPlotRest, "9, 4, left, top");

            _posPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, getPlotHeight());
            _displayPanel.add(_posPlotRest, "9, 8, left, top");
        }

        // make plots interactive
        setupInteractivePlots();

        // make plot labels
        setupPlotLabels(c1p != null, c2p != null, rest != null);

        // update the frame
        frameUpdate();
    }

    protected void setupPlotLabels(boolean show1P, boolean show2P, boolean showRest) {

        // set font color on labels
        JLabel[] labels = new JLabel[] { prob0Pmom, prob0Ppos, prob1Pmom, plbl, prob1Ppos, xlbl, p2lbl, p1lbl, x2lbl,
            x1lbl, prob3Pmom, prob3Ppos, _timeLabel, lblTime, _totalProbLabel, _totalEnergyLabel, lblMomentumSpace,
            lblMom1P, lblMom2P, lblMomRest, lblMomVacuum, lblPositionSpace, lblPos1P, lblPos2P, lblPosRest,
            lblPosVacuum };
        for (JLabel label : labels)
            label.setForeground(getLabelColor());
        lblClickAndDrag.setForeground(Color.DARK_GRAY); // tips are darker

        // time label
        _timeLabel.setText("");
        _displayPanel.add(lblTime, "7, 10, right, top");
        _displayPanel.add(_timeLabel, "8, 10, 2, 1, left, top");

        // total probability label
        _totalProbLabel.setText("");
        _displayPanel.add(_totalProbLabel, "7, 10, 2, 1, left, top");

        // total energy label
        _totalEnergyLabel.setText("");
        _displayPanel.add(_totalEnergyLabel, "3, 10, 3, 1, right, top");

        // tip labels
        _displayPanel.add(lblClickAndDrag, "2, 10, 3, 1, left, top");

        // PLOT LABELS
        // titles
        _displayPanel.add(lblMomentumSpace, "2, 2, 3, 1, left, center"); // momentum space title
        _displayPanel.add(lblPositionSpace, "2, 6, 3, 1, left, center"); // position space title

        // 0 particles = vacuum
        _displayPanel.add(lblMomVacuum, "2, 3, 2, 1, center, center"); // momentum title
        _displayPanel.add(prob0Pmom, "2, 4, left, center"); // momentum y-label
        _displayPanel.add(lblPosVacuum, "2, 7, 2, 1, center, center"); // position title
        _displayPanel.add(prob0Ppos, "2, 8, left, center"); // position y-label

        if (show1P) { // 1 particle
            _displayPanel.add(lblMom1P, "4, 3, 2, 1, center, center"); // momentum title
            _displayPanel.add(prob1Pmom, "4, 4, left, center"); // momentum y-label
            _displayPanel.add(plbl, "5, 5, center, top"); // momentum x-label

            _displayPanel.add(lblPos1P, "4, 7, 2, 1, center, center"); // position title
            _displayPanel.add(prob1Ppos, "4, 8, left, center"); // position y-label
            _displayPanel.add(xlbl, "5, 9, center, top"); // position x-label
        }

        if (show2P) { // 2 particles
            _displayPanel.add(lblMom2P, "6, 3, 2, 1, center, center"); // momentum title
            _displayPanel.add(p2lbl, "6, 4, left, center"); // momentum y-label
            _displayPanel.add(p1lbl, "7, 5, center, top"); // momentum x-label

            _displayPanel.add(lblPos2P, "6, 7, 2, 1, center, center"); // position title
            _displayPanel.add(x2lbl, "6, 8, left, center"); // position y-label
            _displayPanel.add(x1lbl, "7, 9, center, top"); // position x-label
        }
        if (showRest) { // 3+ particles
            _displayPanel.add(lblMomRest, "8, 3, 2, 1, center, center"); // momentum title
            _displayPanel.add(prob3Pmom, "8, 4, left, center"); // momentum y-label

            _displayPanel.add(lblPosRest, "8, 7, 2, 1, center, center"); // position title
            _displayPanel.add(prob3Ppos, "8, 8, left, center"); // position y-label
        }
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
        _controlPanel.add(presetSelector, "1, " + (_controlPanelRowAdder++) + ", 3, 1, fill, default");
    }

    private void applyPreset(Preset preset) {
        _NSlider.setValue(preset.N);
        _PmaxSlider.setValue(preset.Pmax);
        _dxSlider.setValue(encode(preset.dx));
        _mSlider.setValue(encode(preset.m));
        _dtSlider.setValue(encode(preset.dt));
        _stepsSlider.setValue(preset.steps);

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
        _controlPanel.add(_calculateButton, "2, " + (_controlPanelRowAdder++));
        _controlPanel.add(Box.createVerticalStrut(20), "2, " + (_controlPanelRowAdder++));

        // add real time sliders...
        setupGeneralSlider(_dtSlider, encode(getDtMin()), encode(getDtMax()), double.class, null, "Time step");

        // ... with a real time update listener...
        _dtSlider.addChangeListener(new ChangeListener() { // update time step
            public void stateChanged(ChangeEvent e) {
                if (_state != null)
                    _state.setTimeStep(decode(_dtSlider.getValue()));
            }
        });
        setupGeneralSlider(_stepsSlider, getStepsMin(), getStepsMax(), int.class, null, "Steps calculated per frame");

        // ... including interaction sliders (with change listeners)
        Interaction[] interactionsInOrder = new Interaction[] { Interaction.PHI_SQUARED, Interaction.PHI_CUBED,
            Interaction.PHI_FOURTH };
        for (final Interaction interaction : interactionsInOrder)
            setupGeneralSlider(_interactionSliders.get(interaction), encode(getLambdaMin()), encode(getLambdaMax()),
                               double.class, interaction, _interactionToolTips.get(interaction));

        // separator
        _controlPanel.add(Box.createVerticalStrut(20), "2, " + _controlPanelRowAdder++);

        // play and reset buttons
        _controlPanel.add(_playButton, "2, " + _controlPanelRowAdder++);
        _controlPanel.add(_resetButton, "2, " + _controlPanelRowAdder++);
        _controlPanel.add(_groundStateButton, "2, " + _controlPanelRowAdder++);

    }

    private void setupCheckboxes() {
        _controlPanel.add(_checkBoxes.get(Interaction.PHI_SQUARED), "2, " + _controlPanelRowAdder + ", left, top");
        _controlPanel.add(_checkBoxes.get(Interaction.PHI_CUBED), "2, " + _controlPanelRowAdder + ", center, top");
        _controlPanel.add(_checkBoxes.get(Interaction.PHI_FOURTH), "2, " + _controlPanelRowAdder + ", right, top");
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
            _controlPanel.add(_negativeCheckBoxes.get(interaction), "1, " + row + ", left, top");

        _controlPanel.add(icon, "1, " + row + ", center, center");
        _controlPanel.add(slider, "2, " + row + ", left, top");
        _controlPanel.add(value, "3, " + row + ", center, center");

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (interaction != null) {
                    final JCheckBox checkbox = _negativeCheckBoxes.get(interaction);
                    ChangeListener cl = new ChangeListener() { // update interaction strength
                        public void stateChanged(ChangeEvent e) {
                            if (_state != null) {
                                int negativeFactor = checkbox.isSelected() ? -1 : 1;
                                _state.setInteractionStrength(interaction, negativeFactor * decode(slider.getValue()));
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
                _state.setToGroundState();
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
        drawPlotsAndLabels();

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
        frameUpdate();
    }

    protected void stop() {
        _animator.stopAnimation();
        _playButton.setText(BUTTON_PLAY);
        frameUpdate();
    }

    protected void reset() {
        _resetButton.setEnabled(false);
        _animator.stopAnimation();
        _playButton.setText(BUTTON_PLAY);
        if (_state != null)
            _state.reset(); // reset quantum state
        frameUpdate();
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

    /**** INTERACTIVE PLOTS ****/

    protected void setupInteractivePlots() {

        // momentum and position vacuum mouse clickability (sets to vacuum)
        if (_momPlotVacuum != null) {
            PlotListener vacuumListener = new PlotListener(this, _momPlotVacuum) {
                public void setWavePacket(MouseEvent e) {
                    wavePacket = new MomentumWavePacket(N);
                }
            };
            _momPlotVacuum.addMouseListener(vacuumListener);
            _posPlotVacuum.addMouseListener(vacuumListener);
        }

        if (_momPlot1P != null) {
            // momentum 1 particle mouse clickability (sets to wave packet peaking at click)
            PlotListener mom1PListener = new PlotListener(this, _momPlot1P) {
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
            PlotListener pos1PListener = new PlotListener(this, _posPlot1P) {
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
            PlotListener mom2PListener = new PlotListener(this, _momDensityPlot2P) {
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

            PlotListener pos2PListener = new PlotListener(this, _posDensityPlot2P) {
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
                frameUpdate();
        }
    }
}
