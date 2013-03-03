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
    private static final int    N_MAX              = 64;

    private static final int    PMAX_MIN           = 2;
    private static final int    PMAX_DEFAULT       = 3;
    private static final int    PMAX_MAX           = 5;

    private static final double DX_MIN             = 1.0e-3;
    private static final double DX_DEFAULT         = 1.0;
    private static final double DX_MAX             = 10;

    private static final double M_MIN              = 1.0e-3;
    private static final double M_DEFAULT          = 1.0;
    private static final double M_MAX              = 10;

    private static final double DT_MIN             = 1.0e-5;
    private static final double DT_DEFAULT         = 1.0e-3;
    private static final double DT_MAX             = 1.0;

    private static final double LAMBDA_MIN         = 1.0e-3;
    private static final double LAMBDA_DEFAULT     = 1.0e1;
    private static final double LAMBDA_MAX         = 1.0e3;

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
    private DensityPlot         _momDensityPlot2P;

    // Position space plots
    private Plot                _posPlotVacuum;
    private Plot                _posPlot1P;
    private DensityPlot         _posDensityPlot2P;

    /* ANIMATION VARIABLES */
    // Animation parameters and objects
    private double              _framerate         = 30.0;
    private Animator            _animator          = new Animator();

    /* FRAME SETUP VARIABLES */
    // Panels
    private JPanel              _controlPanel      = new JPanel();
    private JPanel              _displayPanel      = new JPanel();
    private boolean             _plotsVisible      = false;

    private final Component     _controlPanelStrut = Box.createVerticalStrut(100);

    // Value Labels
    private JLabel              _NValue            = new JLabel(N_DEFAULT + "");
    private JLabel              _PmaxValue         = new JLabel(PMAX_DEFAULT + "");
    private JLabel              _dxValue           = new JLabel(format(DX_DEFAULT));
    private JLabel              _mValue            = new JLabel(format(M_DEFAULT));
    private JLabel              _dtValue           = new JLabel(format(DT_DEFAULT));
    private JLabel              _lambdaValue       = new JLabel(format(LAMBDA_DEFAULT));

    private JLabel              _NLabel            = new JLabel("Lattice points [N]:");
    private JLabel              _PmaxLabel         = new JLabel("Max particles considered:");
    private JLabel              _dxLabel           = new JLabel("Lattice spacing [dx]:");
    private JLabel              _mLabel            = new JLabel("Particle mass [m]:");
    private JLabel              _dtLabel           = new JLabel("Time step [dt]:");
    private JLabel              _lambdaLabel       = new JLabel("Interaction strength:");
    private final JLabel        _timeLabel         = new JLabel();

    // Sliders
    private JSlider             _NSlider           = new JSlider(N_MIN, N_MAX, N_DEFAULT);
    private JSlider             _PmaxSlider        = new JSlider(PMAX_MIN, PMAX_MAX, PMAX_DEFAULT);
    private JSlider             _dxSlider          = new JSlider(log10(DX_MIN), log10(DX_MAX), log10(DX_DEFAULT));
    private JSlider             _mSlider           = new JSlider(log10(M_MIN), log10(M_MAX), log10(M_DEFAULT));
    private JSlider             _dtSlider          = new JSlider(log10(DT_MIN), log10(DT_MAX), log10(DT_DEFAULT));
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
        drawPlots();
    }

    protected void drawPlots() {
        // make new plots if not visible
        if (!_plotsVisible) {
            _momPlotVacuum = new Plot(_state.get0P(), 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
            _momPlot1P = new Plot(_state.get1PMomenta(), 0.0, 1.0, PLOT_WIDTH, PLOT_HEIGHT);
            _momDensityPlot2P = new DensityPlot(_state.get2PMomenta(), 0.0, 0.5, PLOT_WIDTH, PLOT_HEIGHT);
            _posPlotVacuum = new Plot(_state.get0P(), 0.0, 1.0, PLOT_1D_WIDTH, PLOT_HEIGHT);
            _posPlot1P = new Plot(_state.get1PPositions(), 0.0, 0.2, PLOT_WIDTH, PLOT_HEIGHT);
            _posDensityPlot2P = new DensityPlot(_state.get2PPositions(), 0.0, 0.5, PLOT_WIDTH, PLOT_HEIGHT);
            _plotsVisible = true;
        }

        // clean display panel
        _displayPanel.removeAll();

        // add timer to frame
        _displayPanel.add(_timeLabel, "2, 2");

        // add plots to frame
        _displayPanel.add(_momPlotVacuum, "2, 4, center, center");
        _displayPanel.add(_momPlot1P, "4, 4, center, center");
        _displayPanel.add(_momDensityPlot2P, "6, 4, center, center");
        _displayPanel.add(_posPlotVacuum, "2, 6, center, center");
        _displayPanel.add(_posPlot1P, "4, 6, center, center");
        _displayPanel.add(_posDensityPlot2P, "6, 6, center, center");
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
            ColumnSpec.decode("112px"), FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(154dlu;default)"),
            FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(166dlu;default)"), }, new RowSpec[] {
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
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));

        // setup sliders
        setupSlider(_NSlider, _NValue, _NLabel, N_MIN, N_MAX, 2, false);
        setupSlider(_PmaxSlider, _PmaxValue, _PmaxLabel, PMAX_MIN, PMAX_MAX, 5, false);
        setupSlider(_dxSlider, _dxValue, _dxLabel, log10(DX_MIN), log10(DX_MAX), 8, true);
        setupSlider(_mSlider, _mValue, _mLabel, log10(M_MIN), log10(M_MAX), 11, true);
        setupSlider(_dtSlider, _dtValue, _dtLabel, log10(DT_MIN), log10(DT_MAX), 15, true);
        setupSlider(_lambdaSlider, _lambdaValue, _lambdaLabel, log10(LAMBDA_MIN), log10(LAMBDA_MAX), 18, true);
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
                _state.setTimeStep(_dt); // update time step
            }
        });
        _lambdaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _lambda = Math.pow(10, _lambdaSlider.getValue());
                _lambdaValue.setText(format(_lambda));
                _state.setInteractionStrength(_lambda); // update interaction strength
            }
        });
    }

    private void setupCalculateButton() {
        _controlPanel.add(_calculateButton, "2, 21");
        _calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _animator.stopAnimation();
                _playButton.setText(BUTTON_PLAY);
                _playButton.setEnabled(false);
                _resetButton.setEnabled(false);
                setupQuantumState();
                _playButton.setEnabled(true);
                frameUpdate();
            }
        });
    }

    private void setupPlayButton() {
        _playButton.setEnabled(false);
        _controlPanel.add(_playButton, "2, 23");
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
        _controlPanel.add(_resetButton, "2, 25");
        _resetButton.setEnabled(false);
        _resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _resetButton.setEnabled(false);
                _animator.stopAnimation();
                _playButton.setText(BUTTON_PLAY);
                if (_state != null) {
                    _state.reset(); // reset quantum state
                }
                frameUpdate();
            }
        });
    }

    // function which is fired every time frame is updated
    private void frameUpdate() {

        if (_state == null)
            return; // only if state is set up

        _timeLabel.setText("t = " + _state.getTime());

        // momentum plots
        _momPlotVacuum.update(_state.get0P());
        _momPlot1P.update(_state.get1PMomenta());
        _momDensityPlot2P.update(_state.get2PMomenta());

        // momentum plots
        _posPlotVacuum.update(_state.get0P());
        _posPlot1P.update(_state.get1PPositions());
        _posDensityPlot2P.update(_state.get2PPositions());

        _state.step();
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
