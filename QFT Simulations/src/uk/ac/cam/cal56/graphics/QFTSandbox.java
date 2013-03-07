package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import uk.ac.cam.cal56.qft.interactingtheory.impl.SecondOrderSymplecticState;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class QFTSandbox extends JFrame {

    /***** VARIABLES *****/

    /* STATIC VARIABLES */
    private static final String FRAME_TITLE          = "QFT Sandbox";
    private static final int    FRAME_WIDTH          = 1120;
    private static final int    FRAME_HEIGHT         = 700;

    private static final int    PLOT_1D_WIDTH        = 20;
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
    private static final double DT_DEFAULT           = 1.0e-3;
    private static final double DT_MAX               = 1.0e-1;

    private static final int    STEPS_MIN            = 1;
    private static final int    STEPS_DEFAULT        = 16;
    private static final int    STEPS_MAX            = 256;

    private static final double LAMBDA_MIN           = 1.0e-7;
    private static final double LAMBDA_DEFAULT       = 1.0e1;
    private static final double LAMBDA_MAX           = 1.0e2;

    private static final String BUTTON_CALCULATE     = "Calculate";
    private static final String BUTTON_PLAY          = "Play";
    private static final String BUTTON_STOP          = "Stop";
    private static final String BUTTON_RESET         = "Reset";

    private static final String SELECTOR_DEFAULT     = "Select a preset...";

    /* QUANTUM STATE VARIABLES */
    // quantum state
    protected State             _state;

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

    // Preset Selector
    private final JComboBox     _presetSelector      = new JComboBox();
    private int[]               _particleMomenta     = new int[] {};

    // Sliders
    private JSlider             _NSlider             = new JSlider(N_MIN, N_MAX, N_DEFAULT);
    private JSlider             _PmaxSlider          = new JSlider(PMAX_MIN, PMAX_MAX, PMAX_DEFAULT);
    private JSlider             _dxSlider            = new JSlider(encode(DX_MIN), encode(DX_MAX), encode(DX_DEFAULT));
    private JSlider             _mSlider             = new JSlider(encode(M_MIN), encode(M_MAX), encode(M_DEFAULT));
    private JSlider             _dtSlider            = new JSlider(encode(DT_MIN), encode(DT_MAX), encode(DT_DEFAULT));
    private JSlider             _stepsSlider         = new JSlider(STEPS_MIN, STEPS_MAX, STEPS_DEFAULT);
    private JSlider             _lambdaSquaredSlider = new JSlider(encode(LAMBDA_MIN), encode(LAMBDA_MAX),
                                                         encode(LAMBDA_DEFAULT));
    private JSlider             _lambdaCubedSlider   = new JSlider(encode(LAMBDA_MIN), encode(LAMBDA_MAX),
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
    }

    // quantum state and plots representing it
    protected void setupQuantumState() {
        Map<Interaction, Double> lambdas = new HashMap<Interaction, Double>();
        lambdas.put(Interaction.PHI_SQUARED, 0.1);
        lambdas.put(Interaction.PHI_CUBED, decode(_lambdaSquaredSlider.getValue()));
        _state = new SecondOrderSymplecticState(_NSlider.getValue(), _PmaxSlider.getValue(),
            decode(_mSlider.getValue()), decode(_dxSlider.getValue()), decode(_dtSlider.getValue()), lambdas,
            _particleMomenta);
    }

    // fired every time frame is updated
    private void frameUpdate() {

        if (_state == null)
            return; // only if state is set up

        _timeLabel.setText("Time: " + (new DecimalFormat("#.#######").format(_state.getTime())));

        // get coefficients
        Complex c0p = _state.get0P();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Complex rest = Complex.one().times(Math.sqrt(_state.getRemainingProbability()));

        // plots
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
        _momPlotRest.update(rest);
        _posPlotRest.update(rest);

        _state.step(_stepsSlider.getValue());

    }

    // draw plots after calculation
    protected void drawPlots() {

        // clean display panel
        _displayPanel.removeAll();

        _timeLabel.setText("");
        _displayPanel.add(_timeLabel, "4, 2, left, center");

        // get coefficients
        Complex c0p = _state.get0P();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Complex rest = Complex.one().times(Math.sqrt(_state.getRemainingProbability()));

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
        _displayPanel.add(_momPlotVacuum, "2, 4, center, center");
        _displayPanel.add(_posPlotVacuum, "2, 6, center, center");

        if (c1p != null) {
            _momPlot1P = new FunctionPlot(c1p, 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _posPlot1P = new FunctionPlot(_ft.transform(c1p), 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_momPlot1P, "4, 4, center, center");
            _displayPanel.add(_posPlot1P, "4, 6, center, center");
        }

        if (c2p != null) {
            _momDensityPlot2P = new DensityPlot(c2p, 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _posDensityPlot2P = new DensityPlot(_ft.transform2D(c2p), 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _displayPanel.add(_momDensityPlot2P, "6, 4, center, center");
            _displayPanel.add(_posDensityPlot2P, "6, 6, center, center");
        }

        _momPlotRest = new FunctionPlot(rest, 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
        _posPlotRest = new FunctionPlot(rest, 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
        _displayPanel.add(_momPlotRest, "8, 4, center, center");
        _displayPanel.add(_posPlotRest, "8, 6, center, center");

        // update the frame
        frameUpdate();
    }

    private void setupFrame() {
        setTitle(FRAME_TITLE);
        setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
    }

    private void setupDisplayPanel() {
        // make display panel
        getContentPane().add(_displayPanel, BorderLayout.CENTER);
        _displayPanel.setBackground(Color.BLACK);
        _timeLabel.setForeground(Color.WHITE);

        // set layout
        _displayPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.UNRELATED_GAP_COLSPEC,
            ColumnSpec.decode("84px"), FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(149dlu;default)"),
            FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(155dlu;default)"),
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(30dlu;default)"), }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.UNRELATED_GAP_ROWSPEC,
            RowSpec.decode("311px"), FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("309px"),
            FormFactory.UNRELATED_GAP_ROWSPEC }));
    }

    private void setupControlPanel() {
        // make it pretty
        _controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(_controlPanel, BorderLayout.EAST);

        // form layout
        _controlPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("40px"),
            ColumnSpec.decode("175px:grow"), ColumnSpec.decode("64px"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
            new RowSpec[] { FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
                FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC }));

        setupPresetSelector();

        // setup sliders
        setupSliders();

        // setup separating struts
        _controlPanel.add(_separator, "2, " + SEPARATOR_ROW);
        _controlPanel.add(_separator2, "2, 11");

        // setup buttons (calculate, play, reset)
        setupButtons();
    }

    private void setupPresetSelector() {

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
                    _lambdaSquaredSlider.setValue(encode(preset.lambda));
                    _particleMomenta = preset.particleMomenta;
                    calculate();
                }
            }
        });

        // add to control panel
        _controlPanel.add(_presetSelector, "1, 1, 3, 1, fill, default");
    }

    private void setupSliders() {
        // add calculate sliders
        setupGeneralSlider(_NSlider, _NValue, N_MIN, N_MAX, 2, int.class, "Number of lattice points");
        setupGeneralSlider(_PmaxSlider, _PmaxValue, PMAX_MIN, PMAX_MAX, 3, int.class, "Number of particles considered");
        setupGeneralSlider(_dxSlider, _dxValue, encode(DX_MIN), encode(DX_MAX), 4, double.class, "Lattice point separation");
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

    private void setupGeneralSlider(final JSlider slider, final JLabel value, int min, int max, final int row,
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
        icon.setIcon(new ImageIcon(getClass().getResource(row + ".png")));
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
    private void setupButtons() {
        _resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _resetButton.setEnabled(false);
                _animator.stopAnimation();
                _playButton.setText(BUTTON_PLAY);
                if (_state != null)
                    _state.reset(_particleMomenta); // reset quantum state
                frameUpdate();
            }
        });
        _playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_playButton.getText() == BUTTON_PLAY) {
                    _animator.startAnimation();
                    _playButton.setText(BUTTON_STOP);
                    _resetButton.setEnabled(true);
                }
                else if (_playButton.getText() == BUTTON_STOP) {
                    _animator.stopAnimation();
                    _playButton.setText(BUTTON_PLAY);
                }
                frameUpdate();
            }
        });

        // add appropriate action listeners
        _calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

    private void calculate() {
        _animator.stopAnimation();
        _playButton.setEnabled(false);
        _playButton.setText(BUTTON_PLAY);
        setupQuantumState();
        drawPlots();
        _playButton.setEnabled(true);
        _resetButton.setEnabled(false);
        _calculateButton.setEnabled(false);
    }

    /**** functions to encode doubles as slider values (integers) ****/

    private Integer encode(double d) {
        return (int) (100.0 * (Math.log10(d)));
    }

    private double decode(int encoded) {
        return Math.pow(10, encoded / 100.0);
    }

    // converts to scientific notation
    private String decodeText(int encoded) {
        double number = decode(encoded);
        int power = (int) Math.floor(Math.log10(number));
        double mantissa = number / Math.pow(10, power);
        String digit = (new DecimalFormat("#.#").format(mantissa));
        return "<html>" + (digit.equals("1") ? "" : digit + "x") + "10<sup>" + power + "</sup></html>";
    }

    private String format(double d) {
        return decodeText(encode(d));
    }

    /***** ANIMATION INNER CLASS *****/

    private class Animator implements ActionListener {
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
