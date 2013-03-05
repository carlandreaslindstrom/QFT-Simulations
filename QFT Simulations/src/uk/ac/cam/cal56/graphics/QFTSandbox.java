package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JButton;
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
    private static final String FRAME_TITLE        = "QFT Sandbox";
    private static final int    FRAME_WIDTH        = 1068;
    private static final int    FRAME_HEIGHT       = 700;

    private static final int    PLOT_1D_WIDTH      = 20;
    private static final int    PLOT_WIDTH         = 256;
    private static final int    PLOT_HEIGHT        = 256;

    private static final int    N_MIN              = 2;
    private static final int    N_DEFAULT          = 16;
    private static final int    N_MAX              = 128;

    private static final int    PMAX_MIN           = 1;
    private static final int    PMAX_DEFAULT       = 3;
    private static final int    PMAX_MAX           = 7;

    private static final double DX_MIN             = 1.0e-3;
    private static final double DX_DEFAULT         = 1.0;
    private static final double DX_MAX             = 10;

    private static final double M_MIN              = 1.0e-3;
    private static final double M_DEFAULT          = 1.0;
    private static final double M_MAX              = 10;

    private static final double DT_MIN             = 1.0e-5;
    private static final double DT_DEFAULT         = 1.0e-3;
    private static final double DT_MAX             = 1.0;

    private static final int    STEPS_MIN          = 1;
    private static final int    STEPS_DEFAULT      = 10;
    private static final int    STEPS_MAX          = 1000;

    private static final double LAMBDA_MIN         = 1.0e-9;
    private static final double LAMBDA_DEFAULT     = 1.0e1;
    private static final double LAMBDA_MAX         = 1.0e2;

    private static final String BUTTON_CALCULATE   = "Calculate";
    private static final String BUTTON_PLAY        = "Play";
    private static final String BUTTON_STOP        = "Stop";
    private static final String BUTTON_RESET       = "Reset";

    /* QUANTUM STATE VARIABLES */
    // quantum state
    protected State             _state;

    // system parameters
    protected int               _N                 = N_DEFAULT;
    protected int               _Pmax              = PMAX_DEFAULT;
    protected double            _dx                = DX_DEFAULT;
    protected double            _m                 = M_DEFAULT;
    protected double            _dt                = DT_DEFAULT;
    protected double            _lambda            = LAMBDA_DEFAULT;

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
    private FourierTransform    _ft                = new FFT();

    /* ANIMATION VARIABLES */
    // Animation parameters and objects
    private double              _framerate         = 30.0;
    private int                 _steps             = STEPS_DEFAULT;
    private Animator            _animator          = new Animator();

    /* FRAME SETUP VARIABLES */
    // Panels
    private JPanel              _controlPanel      = new JPanel();
    private JPanel              _displayPanel      = new JPanel();

    private final Component     _controlPanelStrut = Box.createVerticalStrut(20);

    // Value Labels
    private JLabel              _NValue            = new JLabel(N_DEFAULT + "");
    private JLabel              _PmaxValue         = new JLabel(PMAX_DEFAULT + "");
    private JLabel              _dxValue           = new JLabel(format(DX_DEFAULT));
    private JLabel              _mValue            = new JLabel(format(M_DEFAULT));
    private JLabel              _dtValue           = new JLabel(format(DT_DEFAULT));
    private JLabel              _stepsValue        = new JLabel(STEPS_DEFAULT + "");
    private JLabel              _lambdaValue       = new JLabel(format(LAMBDA_DEFAULT));

    private JLabel              _NLabel            = new JLabel("Lattice points [N]:");
    private JLabel              _PmaxLabel         = new JLabel("Max particles considered:");
    private JLabel              _dxLabel           = new JLabel("Lattice spacing [dx]:");
    private JLabel              _mLabel            = new JLabel("Particle mass [m]:");
    private JLabel              _dtLabel           = new JLabel("Time step [dt]:");
    private JLabel              _stepsLabel        = new JLabel("Steps per frame:");
    private JLabel              _lambdaLabel       = new JLabel("Interaction strength:");
    private JLabel              _timeLabel         = new JLabel();

    // Sliders
    private JSlider             _NSlider           = new JSlider(N_MIN, N_MAX, N_DEFAULT);
    private JSlider             _PmaxSlider        = new JSlider(PMAX_MIN, PMAX_MAX, PMAX_DEFAULT);
    private JSlider             _dxSlider          = new JSlider(log10(DX_MIN), log10(DX_MAX), log10(DX_DEFAULT));
    private JSlider             _mSlider           = new JSlider(log10(M_MIN), log10(M_MAX), log10(M_DEFAULT));
    private JSlider             _dtSlider          = new JSlider(log10(DT_MIN), log10(DT_MAX), log10(DT_DEFAULT));
    private JSlider             _stepsSlider       = new JSlider(STEPS_MIN, STEPS_MAX, STEPS_DEFAULT);
    private JSlider             _lambdaSlider      = new JSlider(log10(LAMBDA_MIN), log10(LAMBDA_MAX),
                                                       log10(LAMBDA_DEFAULT));

    // Buttons
    private JButton             _calculateButton   = new JButton(BUTTON_CALCULATE);
    private JButton             _playButton        = new JButton(BUTTON_PLAY);
    private JButton             _resetButton       = new JButton(BUTTON_RESET);

    /***** FUNCTIONS *****/

    // Constructor
    public QFTSandbox() {
        setupFrame();
        setupControlPanel();
        setupDisplayPanel();
    }

    // quantum state and plots representing it
    protected void setupQuantumState() {
        _state = new SecondOrderSymplecticState(_N, _Pmax, _m, _dx, _dt, _lambda);
    }

    protected void drawPlots() {

        // clean display panel
        _displayPanel.removeAll();

        _timeLabel.setText("");
        _displayPanel.add(_timeLabel, "2, 2, center, center");

        // get coefficients
        Complex c0p = _state.get0P();
        Complex[] c1p = _state.get1PMom();
        Complex[][] c2p = _state.get2PMom();
        Complex rest = Complex.one().times(Math.sqrt(_state.getRemainingProbability()));

        // make and add plots (only if they exist)
        _momPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
        _posPlotVacuum = new FunctionPlot(c0p, 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
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

        // set layout
        _displayPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.UNRELATED_GAP_COLSPEC,
            ColumnSpec.decode("84px"), FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(149dlu;default)"),
            FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(155dlu;default)"),
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(30dlu;default)"), }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.UNRELATED_GAP_ROWSPEC,
            RowSpec.decode("311px"), FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("309px"),
            FormFactory.UNRELATED_GAP_ROWSPEC, }));
        _timeLabel.setForeground(Color.WHITE);

    }

    private void setupControlPanel() {
        _controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(_controlPanel, BorderLayout.EAST);

        // form layout
        _controlPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            ColumnSpec.decode("175px"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            ColumnSpec.decode("left:max(24dlu;default)"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));

        // setup sliders
        setupSlider(_NSlider, _NValue, _NLabel, N_MIN, N_MAX, 2, false);
        setupSlider(_PmaxSlider, _PmaxValue, _PmaxLabel, PMAX_MIN, PMAX_MAX, 5, false);
        setupSlider(_dxSlider, _dxValue, _dxLabel, log10(DX_MIN), log10(DX_MAX), 8, true);
        setupSlider(_mSlider, _mValue, _mLabel, log10(M_MIN), log10(M_MAX), 11, true);
        setupSlider(_dtSlider, _dtValue, _dtLabel, log10(DT_MIN), log10(DT_MAX), 15, true);
        setupSlider(_stepsSlider, _stepsValue, _stepsLabel, STEPS_MIN, STEPS_MAX, 18, false);
        setupSlider(_lambdaSlider, _lambdaValue, _lambdaLabel, log10(LAMBDA_MIN), log10(LAMBDA_MAX), 21, true);
        setupSliderListeners();

        // setup separating strut
        _controlPanel.add(_controlPanelStrut, "2, 13");

        // setup buttons
        setupCalculateButton();
        setupPlayButton();
        setupResetButton();
    }

    private void setupSlider(JSlider slider, JLabel val, JLabel label, int min, int max, int row, boolean exp) {
        if (exp) {
            Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
            labelTable.put(min, new JLabel(tenToThe(min)));
            labelTable.put(max, new JLabel(tenToThe(max)));
            slider.setLabelTable(labelTable);
        }
        slider.setMajorTickSpacing(max - min);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        _controlPanel.add(label, "2, " + row);
        _controlPanel.add(slider, "2, " + (row + 1) + ", left, top");
        _controlPanel.add(val, "4, " + (row + 1) + ", center, top");
    }

    private void setupSliderListeners() {
        _NSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _N = _NSlider.getValue();
                _NValue.setText(Integer.toString(_N));
            }
        });
        _PmaxSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _Pmax = _PmaxSlider.getValue();
                _PmaxValue.setText(Integer.toString(_Pmax));
            }
        });
        _dxSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _dx = Math.pow(10, _dxSlider.getValue());
                _dxValue.setText(format(_dx));
            }
        });
        _mSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _m = Math.pow(10, _mSlider.getValue());
                _mValue.setText(format(_m));
            }
        });
        _dtSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _dt = Math.pow(10, _dtSlider.getValue());
                _dtValue.setText(format(_dt));
                if (_state != null)
                    _state.setTimeStep(_dt); // update time step
            }
        });
        _stepsSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _steps = _stepsSlider.getValue();
                _stepsValue.setText(Integer.toString(_steps));
            }
        });
        _lambdaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _lambda = Math.pow(10, _lambdaSlider.getValue());
                _lambdaValue.setText(format(_lambda));
                if (_state != null)
                    _state.setInteractionStrength(_lambda); // update interaction strength
            }
        });
    }

    private void setupCalculateButton() {
        _controlPanel.add(_calculateButton, "2, 24");
        _calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _animator.stopAnimation();
                _playButton.setEnabled(false);
                _playButton.setText(BUTTON_PLAY);
                setupQuantumState();
                drawPlots();
                _playButton.setEnabled(true);
                _resetButton.setEnabled(false);
            }
        });
    }

    private void setupPlayButton() {
        _playButton.setEnabled(false);
        _controlPanel.add(_playButton, "2, 26");
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
    }

    private void setupResetButton() {
        _controlPanel.add(_resetButton, "2, 28");
        _resetButton.setEnabled(false);
        _resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _resetButton.setEnabled(false);
                _animator.stopAnimation();
                _playButton.setText(BUTTON_PLAY);
                if (_state != null)
                    _state.reset(); // reset quantum state
                frameUpdate();
            }
        });
    }

    // function which is fired every time frame is updated
    private void frameUpdate() {

        if (_state == null)
            return; // only if state is set up

        _timeLabel.setText("Time = " + _state.getTime());

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

        _state.step(_steps);

    }

    // [log10,tenToThe,format] are helper functions for slider input
    private Integer log10(double d) {
        return (int) Math.log10(d);
    }

    private String tenToThe(int dlog10) {
        return "<html>10<sup>" + dlog10 + "</sup></html>";
    }

    private String format(double d) {
        return tenToThe(log10(d));
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
