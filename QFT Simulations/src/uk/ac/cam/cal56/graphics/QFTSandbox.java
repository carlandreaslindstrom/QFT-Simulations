package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import uk.ac.cam.cal56.qft.interactingtheory.Interaction;
import uk.ac.cam.cal56.qft.interactingtheory.State;
import uk.ac.cam.cal56.qft.interactingtheory.WavePacket;
import uk.ac.cam.cal56.qft.interactingtheory.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.interactingtheory.impl.PositionWavePacket;
import uk.ac.cam.cal56.qft.interactingtheory.impl.SecondOrderSymplecticState;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class QFTSandbox extends JFrame {

    /***** VARIABLES *****/

    /* STATIC VARIABLES */
    public static final String  FRAME_TITLE          = "QFT Sandbox";
    public static final Color   BACKGROUND_COLOR     = Color.BLACK;
    private static final int    FRAME_WIDTH          = 1110;
    private static final int    FRAME_HEIGHT         = 750;

    private static final int    PLOT_WIDTH           = 256;
    private static final int    PLOT_HEIGHT          = 256;

    private static final int    N_MIN                = 2;
    private static final int    N_DEFAULT            = 16;
    private static final int    N_MAX                = 128;

    private static final int    PMAX_MIN             = 1;
    private static final int    PMAX_DEFAULT         = 3;
    private static final int    PMAX_MAX             = 7;

    private static final double DX_MIN               = 1.0e-3;
    private static final double DX_DEFAULT           = 1.0;
    private static final double DX_MAX               = 10.0;

    private static final double M_MIN                = 1.0e-3;
    private static final double M_DEFAULT            = 1.0;
    private static final double M_MAX                = 10.0;

    private static final double DT_MIN               = 1.0e-5;
    private static final double DT_DEFAULT           = 3.0e-4;
    private static final double DT_MAX               = 1.0e-1;

    private static final int    STEPS_MIN            = 1;
    private static final int    STEPS_DEFAULT        = 16;
    private static final int    STEPS_MAX            = 256;

    private static final double LAMBDA_MIN           = 1.0e-7;
    private static final double LAMBDA_DEFAULT       = 1.0e1;
    private static final double LAMBDA_MAX           = 1.0e2;

    // Explanatory labels
    private static final String LABEL_MOM_SPACE      = "<html><strong>Momentum Space:</strong></html>";
    private static final String LABEL_POS_SPACE      = "<html><strong>Position Space:</strong></html>";
    private static final String LABEL_VACUUM         = "Vacuum";
    private static final String LABEL_1P_MOM         = "<html>1 particle of momentum <em>p</em></html>";
    private static final String LABEL_1P_POS         = "<html>1 particle of position <em>x</em></html>";
    private static final String LABEL_2P_MOM         = "<html>2 particles of momenta <em>p<sub>1</sub></em> and <em>p<sub>2</sub></em></html>";
    private static final String LABEL_2P_POS         = "<html>2 particles of positions <em>x<sub>1</sub></em> and <em>x<sub>2</sub></em></html>";
    private static final String LABEL_3P_PLUS        = "3+ particles";

    private static final String LABEL_PROBABILITY    = "<html>&phi;<sup>2</sup></html>";
    private static final String LABEL_MOMENTUM_P     = "p";
    private static final String LABEL_MOMENTUM_P1    = "<html>p<sub>1</sub></html>";
    private static final String LABEL_MOMENTUM_P2    = "<html>p<sub>2</sub></html>";
    private static final String LABEL_POSITION_X     = "x";
    private static final String LABEL_POSITION_X1    = "<html>x<sub>1</sub></html>";
    private static final String LABEL_POSITION_X2    = "<html>x<sub>2</sub></html>";

    private static final String LABEL_TIME           = "Time: ";
    private static final String LABEL_TOTAL_PROB     = "Total probability: ";
    private static final String LABEL_CLICK_AND_DRAG = "(Tip: Click the plots to place particles)";

    private static final String SELECTOR_DEFAULT     = "Select a preset...";

    private static final String BUTTON_CALCULATE     = "Calculate";
    private static final String BUTTON_PLAY          = "Play";
    private static final String BUTTON_STOP          = "Stop";
    private static final String BUTTON_RESET         = "Reset";

    private static final Color  DEFAULT_LABEL_COLOR  = Color.GRAY;

    /* QUANTUM STATE VARIABLES */
    // quantum state
    protected State             _state;
    protected WavePacket        _wavepacket;

    /* PLOT DATA VARIABLES */
    // Momentum space plots
    private Plot                _momPlotVacuum;
    private Plot                _momPlot1P;
    private Plot                _momDensityPlot2P;
    private Plot                _momPlotRest;

    // Position space plots
    private Plot                _posPlotVacuum;
    private Plot                _posPlot1P;
    private Plot                _posDensityPlot2P;
    private Plot                _posPlotRest;

    /* FOURIER TRANSFORM */
    private FourierTransform    _ft                  = new FFT();

    /* ANIMATION VARIABLES */
    // Animation parameters and objects
    private double              _framerate           = 30.0;
    private Animator            _animator            = new Animator();

    /* FRAME SETUP VARIABLES */
    // Panels
    private JPanel              _controlPanel        = new JPanel();
    private JPanel              _displayPanel        = new JPanel();

    // Value Labels
    private JLabel              _NValue              = new JLabel(N_DEFAULT + "");
    private JLabel              _PmaxValue           = new JLabel(PMAX_DEFAULT + "");
    private JLabel              _dxValue             = new JLabel(format(DX_DEFAULT));
    private JLabel              _mValue              = new JLabel(format(M_DEFAULT));
    private JLabel              _dtValue             = new JLabel(format(DT_DEFAULT));
    private JLabel              _stepsValue          = new JLabel(STEPS_DEFAULT + "");
    private JLabel              _lambdaSquaredValue  = new JLabel(format(LAMBDA_DEFAULT));
    private JLabel              _lambdaCubedValue    = new JLabel(format(LAMBDA_DEFAULT));

    private JLabel              _timeLabel           = new JLabel();
    private JLabel              _totalProbLabel      = new JLabel();

    // Preset Selector
    private final JComboBox     _presetSelector      = new JComboBox();

    // Sliders
    protected JSlider           _NSlider             = new JSlider(N_MIN, N_MAX, N_DEFAULT);
    protected JSlider           _PmaxSlider          = new JSlider(PMAX_MIN, PMAX_MAX, PMAX_DEFAULT);
    protected JSlider           _dxSlider            = new JSlider(encode(DX_MIN), encode(DX_MAX), encode(DX_DEFAULT));
    protected JSlider           _mSlider             = new JSlider(encode(M_MIN), encode(M_MAX), encode(M_DEFAULT));
    protected JSlider           _dtSlider            = new JSlider(encode(DT_MIN), encode(DT_MAX), encode(DT_DEFAULT));
    protected JSlider           _stepsSlider         = new JSlider(STEPS_MIN, STEPS_MAX, STEPS_DEFAULT);
    protected JSlider           _lambdaSquaredSlider = new JSlider(encode(LAMBDA_MIN), encode(LAMBDA_MAX),
                                                         encode(LAMBDA_DEFAULT));
    protected JSlider           _lambdaCubedSlider   = new JSlider(encode(LAMBDA_MIN), encode(LAMBDA_MAX),
                                                         encode(LAMBDA_DEFAULT));
    // Separators
    private final Component     _separator           = Box.createVerticalStrut(50);
    private final int           SEPARATOR_ROW        = 6;
    private final Component     _separator2          = Box.createVerticalStrut(50);

    // Buttons
    private JButton             _calculateButton     = new JButton(BUTTON_CALCULATE);
    private JButton             _playButton          = new JButton(BUTTON_PLAY);
    private JButton             _resetButton         = new JButton(BUTTON_RESET);

    /***** FUNCTIONS *****/

    // Constructor
    public QFTSandbox() {
        setupFrame();
        setupControlPanel();
        setupDisplayPanel();
        calculate();
    }

    // quantum state and plots representing it
    protected void setupQuantumState() {
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_SQUARED, decode(_lambdaSquaredSlider.getValue()));
        lambdas.put(Interaction.PHI_CUBED, decode(_lambdaCubedSlider.getValue()));
        int N = _NSlider.getValue();
        if (_wavepacket == null)
            _wavepacket = new MomentumWavePacket(N);
        _state = new SecondOrderSymplecticState(N, _PmaxSlider.getValue(), decode(_mSlider.getValue()),
            decode(_dxSlider.getValue()), decode(_dtSlider.getValue()), lambdas, _wavepacket);
    }

    // fired every time frame is updated
    protected void frameUpdate() {

        if (_state == null)
            return; // only if state is set up

        _timeLabel.setText(new DecimalFormat("#.#######").format(_state.getTime()));
        _totalProbLabel.setText(LABEL_TOTAL_PROB + new DecimalFormat("##0%").format(_state.getModSquared()));

        // get coefficients
        Complex c0p = _state.get0P();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Double rest = _state.getRemainingProbability();

        // plots
        _momPlotVacuum.update(c0p);
        _posPlotVacuum.update(c0p);
        if (c1p != null) {
            _momPlot1P.update(c1p);
            _posPlot1P.update(_ft.transform(c1p));
        }
        if (c2p != null) {
            _momDensityPlot2P.update(c2p);
            _posDensityPlot2P.update(_ft.transform2D(c2p)); // TODO: check if symmetry is right
        }
        if (rest != null) {
            Complex restCoeff = Complex.one().times(Math.sqrt(rest));
            _momPlotRest.update(restCoeff);
            _posPlotRest.update(restCoeff);
        }

        _state.step(_stepsSlider.getValue());

    }

    protected void setupFrame() {
        setTitle(FRAME_TITLE);
        setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
    }

    protected void setupDisplayPanel() {
        // make display panel
        getContentPane().add(_displayPanel, BorderLayout.CENTER);
        _displayPanel.setBackground(BACKGROUND_COLOR);

        // set layout @formatter:off
        _displayPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("30px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("60px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("275px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("275px"),
                ColumnSpec.decode("25px"),
                ColumnSpec.decode("60px"),},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("30px"),
                RowSpec.decode("25px"),
                RowSpec.decode("265px"),
                RowSpec.decode("25px"),
                RowSpec.decode("30px"),
                RowSpec.decode("25px"),
                RowSpec.decode("265px"),
                RowSpec.decode("25px"),
                RowSpec.decode("15px"),})); // @formatter:on
    }

    protected void setupControlPanel() {
        // make it pretty
        _controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(_controlPanel, BorderLayout.EAST);

        // form layout @formatter:off
        _controlPanel.setLayout(new FormLayout(
            new ColumnSpec[] { 
                ColumnSpec.decode("40px"),
                ColumnSpec.decode("175px:grow"), 
                ColumnSpec.decode("64px"), 
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC
            }, new RowSpec[] { 
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
                FormFactory.MIN_ROWSPEC 
            }));// @formatter:on

        setupPresetSelector();

        // setup sliders
        setupSliders();

        // setup separating struts
        _controlPanel.add(_separator, "2, " + SEPARATOR_ROW);
        _controlPanel.add(_separator2, "2, 11");

        // setup buttons (calculate, play, reset)
        setupButtons();
    }

    // draw plots after calculation
    protected void drawPlotsAndLabels() {

        // clean display panel
        _displayPanel.removeAll();

        // get coefficients
        Complex c0p = _state.get0P();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Double rest = _state.getRemainingProbability();

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_HEIGHT);
        _displayPanel.add(_momPlotVacuum, "3, 4, left, top");

        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_HEIGHT);
        _displayPanel.add(_posPlotVacuum, "3, 8, left, top");

        if (c1p != null) {
            _momPlot1P = new FunctionPlot(c1p, 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_momPlot1P, "5, 4, left, top");

            _posPlot1P = new FunctionPlot(_ft.transform(c1p), 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_posPlot1P, "5, 8, left, top");
        }

        if (c2p != null) {
            _momDensityPlot2P = new DensityPlot(c2p, 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_momDensityPlot2P, "7, 4, left, top");

            _posDensityPlot2P = new DensityPlot(_ft.transform2D(c2p), 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_posDensityPlot2P, "7, 8, left, top");
        }
        if (rest != null) {
            _momPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, PLOT_HEIGHT);
            _displayPanel.add(_momPlotRest, "9, 4, left, top");

            _posPlotRest = new FunctionPlot(Complex.one().times(Math.sqrt(rest)), 0.0, 1.0, PLOT_HEIGHT);
            _displayPanel.add(_posPlotRest, "9, 8, left, top");
        }

        // make plots interactive
        setupInteractivePlots();

        // make plot labels
        setupPlotLabels(c1p != null, c2p != null, rest != null);

        // update the frame
        frameUpdate();
    }

    private void setupPlotLabels(boolean show1P, boolean show2P, boolean showRest) {

        // define plot labels (if plots exist)
        JLabel lblMomentumSpace = new JLabel(LABEL_MOM_SPACE);
        JLabel lblMomVacuum = new JLabel(LABEL_VACUUM);
        JLabel lblMom1P = new JLabel(LABEL_1P_MOM);
        JLabel lblMom2P = new JLabel(LABEL_2P_MOM);
        JLabel lblMomRest = new JLabel(LABEL_3P_PLUS);
        JLabel lblPositionSpace = new JLabel(LABEL_POS_SPACE);
        JLabel lblPosVacuum = new JLabel(LABEL_VACUUM);
        JLabel lblPos1P = new JLabel(LABEL_1P_POS);
        JLabel lblPos2P = new JLabel(LABEL_2P_POS);
        JLabel lblPosRest = new JLabel(LABEL_3P_PLUS);

        JLabel prob0Pmom = new JLabel(LABEL_PROBABILITY);
        JLabel prob0Ppos = new JLabel(LABEL_PROBABILITY);
        JLabel prob1Pmom = new JLabel(LABEL_PROBABILITY);
        JLabel plbl = new JLabel(LABEL_MOMENTUM_P);
        JLabel p1lbl = new JLabel(LABEL_MOMENTUM_P1);
        JLabel p2lbl = new JLabel(LABEL_MOMENTUM_P2);
        JLabel prob3Pmom = new JLabel(LABEL_PROBABILITY);
        JLabel prob1Ppos = new JLabel(LABEL_PROBABILITY);
        JLabel xlbl = new JLabel(LABEL_POSITION_X);
        JLabel x1lbl = new JLabel(LABEL_POSITION_X1);
        JLabel x2lbl = new JLabel(LABEL_POSITION_X2);
        JLabel prob3Ppos = new JLabel(LABEL_PROBABILITY);

        JLabel lblTime = new JLabel(LABEL_TIME);
        JLabel lblTotalProb = new JLabel(LABEL_TOTAL_PROB);
        JLabel lblClickAndDrag = new JLabel(LABEL_CLICK_AND_DRAG);

        // set font color on labels
        JLabel[] labels = new JLabel[] { prob0Pmom, prob0Ppos, prob1Pmom, plbl, prob1Ppos, xlbl, p2lbl, p1lbl, x2lbl,
            x1lbl, prob3Pmom, prob3Ppos, _timeLabel, lblTime, _totalProbLabel, lblTotalProb, lblMomentumSpace,
            lblMom1P, lblMom2P, lblMomRest, lblMomVacuum, lblPositionSpace, lblPos1P, lblPos2P, lblPosRest,
            lblPosVacuum };
        for (JLabel label : labels)
            label.setForeground(DEFAULT_LABEL_COLOR);
        lblClickAndDrag.setForeground(Color.DARK_GRAY); // tips are darker

        // time label
        _timeLabel.setText("");
        _displayPanel.add(lblTime, "7, 10, right, top");
        _displayPanel.add(_timeLabel, "8, 10, 2, 1, left, top");

        // total probability label
        _totalProbLabel.setText("");
        _displayPanel.add(_totalProbLabel, "7, 10, 2, 1, left, top");

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

        // add presets
        _presetSelector.addItem(SELECTOR_DEFAULT);
        for (Preset preset : Preset.all) {
            _presetSelector.addItem(preset);
        }

        _presetSelector.addActionListener(new ActionListener() { // update time step
            public void actionPerformed(ActionEvent e) {
                Object item = _presetSelector.getSelectedItem();
                if (item.getClass() == Preset.class) {
                    Preset preset = (Preset) item;
                    _NSlider.setValue(preset.N);
                    _PmaxSlider.setValue(preset.Pmax);
                    _dxSlider.setValue(encode(preset.dx));
                    _mSlider.setValue(encode(preset.m));
                    _dtSlider.setValue(encode(preset.dt));
                    _stepsSlider.setValue(preset.steps);
                    _lambdaSquaredSlider.setValue(encode(preset.lambda2));
                    _lambdaCubedSlider.setValue(encode(preset.lambda3));
                    _wavepacket = preset.wavepacket;
                    calculate();
                }
            }
        });

        // add to control panel
        _controlPanel.add(_presetSelector, "1, 1, 3, 1, fill, default");
    }

    protected void setupSliders() {
        // add calculate sliders
        setupGeneralSlider(_NSlider, _NValue, N_MIN, N_MAX, 2, int.class, "Number of lattice points");
        setupGeneralSlider(_PmaxSlider, _PmaxValue, PMAX_MIN, PMAX_MAX, 3, int.class, "Number of particles considered");
        setupGeneralSlider(_dxSlider, _dxValue, encode(DX_MIN), encode(DX_MAX), 4, double.class,
                           "Lattice point separation");
        setupGeneralSlider(_mSlider, _mValue, encode(M_MIN), encode(M_MAX), 5, double.class, "Particle mass");
        // add real time sliders
        setupGeneralSlider(_dtSlider, _dtValue, encode(DT_MIN), encode(DT_MAX), 7, double.class, "Time step");
        setupGeneralSlider(_stepsSlider, _stepsValue, STEPS_MIN, STEPS_MAX, 8, int.class, "Steps calculated per frame");
        setupGeneralSlider(_lambdaSquaredSlider, _lambdaSquaredValue, encode(LAMBDA_MIN), encode(LAMBDA_MAX), 9,
                           double.class, "2-vertex interaction strength");
        setupGeneralSlider(_lambdaCubedSlider, _lambdaCubedValue, encode(LAMBDA_MIN), encode(LAMBDA_MAX), 10,
                           double.class, "3-vertex interaction strength");

        // then add real time update listeners (time step and interaction strength)
        _dtSlider.addChangeListener(new ChangeListener() { // update time step
            public void stateChanged(ChangeEvent e) {
                if (_state != null)
                    _state.setTimeStep(decode(_dtSlider.getValue()));
            }
        });
        _lambdaSquaredSlider.addChangeListener(new ChangeListener() { // update interaction strength
            public void stateChanged(ChangeEvent e) {
                if (_state != null)
                    _state.setInteractionStrength(Interaction.PHI_SQUARED, decode(_lambdaSquaredSlider.getValue()));
            }
        });
        _lambdaCubedSlider.addChangeListener(new ChangeListener() { // update interaction strength
            public void stateChanged(ChangeEvent e) {
                if (_state != null)
                    _state.setInteractionStrength(Interaction.PHI_CUBED, decode(_lambdaCubedSlider.getValue()));
            }
        });
    }

    protected void setupGeneralSlider(final JSlider slider, final JLabel value, int min, int max, final int row,
                                      final Class<?> type, String toolTip) {
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

        _controlPanel.add(icon, "1, " + row + ", center, center");
        _controlPanel.add(slider, "2, " + row + ", left, top");
        _controlPanel.add(value, "3, " + row + ", center, center");

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (type == double.class)
                    value.setText(decodeText(slider.getValue()));
                else if (type == int.class)
                    value.setText(slider.getValue() + "");
                // if the slider necessitates recalculation, enable the calculate button (if not already)
                if (row < SEPARATOR_ROW)
                    _calculateButton.setEnabled(true);
            }
        });
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

        // add appropriate action listeners
        _calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _wavepacket = null;
                calculate();
            }
        });

        // add buttons to control panel
        _controlPanel.add(_calculateButton, "2, 12");
        _controlPanel.add(_playButton, "2, 13");
        _controlPanel.add(_resetButton, "2, 14");

        // initially disable the play and reset buttons
        _playButton.setEnabled(false);
        _resetButton.setEnabled(false);
    }

    protected void calculate() {
        _animator.stopAnimation();
        _playButton.setEnabled(false);
        _playButton.setText(BUTTON_PLAY);
        setupQuantumState();
        drawPlotsAndLabels();
        _playButton.setEnabled(true);
        _resetButton.setEnabled(false);
        _calculateButton.setEnabled(false);
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

    protected Integer encode(double d) {
        return (int) (100.0 * (Math.log10(d)));
    }

    protected double decode(int encoded) {
        return Math.pow(10, encoded / 100.0);
    }

    // converts to scientific notation
    protected String decodeText(int encoded) {
        double number = decode(encoded);
        int power = (int) Math.floor(Math.log10(number));
        double mantissa = number / Math.pow(10, power);
        String digit = (new DecimalFormat("#.#").format(mantissa));
        return "<html>" + (digit.equals("1") ? "" : digit + "x") + "10<sup>" + power + "</sup></html>";
    }

    protected String format(double d) {
        return decodeText(encode(d));
    }

    /**** INTERACTIVE PLOTS ****/

    protected void setupInteractivePlots() {

        // TODO: add drag option for placing in the other space

        // momentum and position vacuum mouse clickability (sets to vacuum)
        if (_momPlotVacuum != null) {
            PlotListener vacuumListener = new PlotListener(this, _momPlotVacuum) {
                public void setWavePacket(MouseEvent e) {
                    _wavepacket = new MomentumWavePacket(_state.getN());
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
                        _wavepacket = new MomentumWavePacket(N, new int[] { p1_or_x1_init }, new double[] { phase },
                            peakProb_init);
                    }
                    else {
                        int p = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        double peakProb = 1.0 - 1.0 * e.getY() / height;
                        _wavepacket = new MomentumWavePacket(N, new int[] { p }, new double[] { 0 }, peakProb);
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
                        _wavepacket = new PositionWavePacket(N, new int[] { p1_or_x1_init }, new double[] { phase },
                            peakProb_init);
                    }
                    else {
                        int x = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        double peakProb = 1.0 - 1.0 * e.getY() / height;
                        _wavepacket = new PositionWavePacket(N, new int[] { x }, new double[] { 0 }, peakProb);
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
                        _wavepacket = new MomentumWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init },
                            new double[] { phase1, phase2 });
                    }
                    else {
                        int p1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        int p2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                        _wavepacket = new MomentumWavePacket(N, new int[] { p1, p2 }, new double[] { 0, 0 });
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
                        _wavepacket = new PositionWavePacket(N, new int[] { p1_or_x1_init, p2_or_x2_init },
                            new double[] { phase1, phase2 });
                    }
                    else {
                        int x1 = (int) (1.0 * N * (e.getX() - Plot.PADDING) / width);
                        int x2 = (int) (N * (1.0 - 1.0 * e.getY() / height));
                        _wavepacket = new PositionWavePacket(N, new int[] { x1, x2 }, new double[] { 0, 0 });
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

    /***** MAIN FUNCTION *****/

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
