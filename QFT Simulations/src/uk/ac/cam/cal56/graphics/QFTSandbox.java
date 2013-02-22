package uk.ac.cam.cal56.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.cam.cal56.maths.Complex;
import uk.ac.cam.cal56.maths.FFT;
import uk.ac.cam.cal56.maths.FourierTransform;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class QFTSandbox extends JFrame {

    private static final int    LATTICE_NUMBER_EXPONENT_MIN     = 0;
    private static final int    LATTICE_NUMBER_EXPONENT_MAX     = 8;
    private static final int    LATTICE_NUMBER_EXPONENT_DEFAULT = 6;

    private static final String TIME_STEP_PREFIX                = "<html>10<sup>";
    private static final String TIME_STEP_SUFFIX                = "</sup></html>";
    private static final int    TIME_STEP_EXPONENT_MIN          = -10;
    private static final int    TIME_STEP_EXPONENT_MAX          = -1;
    private static final int    TIME_STEP_EXPONENT_DEFAULT      = -7;

    private static final String BUTTON_TEXT_START               = "Start";
    private static final String BUTTON_TEXT_STOP                = "Stop";
    private static final String BUTTON_TEXT_RESET               = "Reset";

    // quantum system parameters
    private int                 _N                              = (int) Math.pow(2, LATTICE_NUMBER_EXPONENT_DEFAULT);                  // lattice
    // number
    private int                 _dt_exponent                    = TIME_STEP_EXPONENT_DEFAULT;                                          // time
                                                                                                                                        // step

    private int                 _number                         = 0;

    private Animator            _animator;
    private double              _framerate                      = 30.0;

    // Labels
    private JLabel              _latticePointsValue             = new JLabel(
                                                                    Integer.toString((int) Math.pow(2,
                                                                                                    LATTICE_NUMBER_EXPONENT_DEFAULT)));
    private JLabel              _latticePointsLabel             = new JLabel("Lattice points [N]:");
    private JLabel              _timeStepValue                  = new JLabel(
                                                                    TIME_STEP_PREFIX +
                                                                            Integer.toString(TIME_STEP_EXPONENT_DEFAULT) +
                                                                            TIME_STEP_SUFFIX);
    private JLabel              _timeStepLabel                  = new JLabel("Time step [dt]:");

    // Buttons
    private JButton             _startStopButton                = new JButton(BUTTON_TEXT_START);
    private JButton             _resetButton                    = new JButton(BUTTON_TEXT_RESET);

    // Sliders
    private JSlider             _timeStepSlider                 = new JSlider(TIME_STEP_EXPONENT_MIN,
                                                                    TIME_STEP_EXPONENT_MAX, TIME_STEP_EXPONENT_DEFAULT);
    private JSlider             _latticePointsSlider            = new JSlider(LATTICE_NUMBER_EXPONENT_MIN,
                                                                    LATTICE_NUMBER_EXPONENT_MAX,
                                                                    LATTICE_NUMBER_EXPONENT_DEFAULT);

    // Momentum space plots
    private FourierTransform    _ft                              = new FFT();
    private Plot                _momPlotVacuum                  = new Plot(g(), 0.0, 1.0, 20, 256);
    private Plot                _momPlot1P                      = new Plot(_ft.transform(f()), 0.0, 1.0, 256, 256);
    private DensityPlot         _momDensityPlot2P               = new DensityPlot(_ft.transform2D(k()), 0.0, 0.5, 256,
                                                                    256);

    // Position space plots
    private Plot                _posPlotVacuum                  = new Plot(g(), 0.0, 1.0, 20, 256);
    private Plot                _posPlot1P                      = new Plot(f(), 0.0, 1.0, 256, 256);
    private DensityPlot         _posDensityPlot2P               = new DensityPlot(k(), 0.0, 1.0, 256, 256);

    public QFTSandbox() {
        setupFrame();
        setupControlPanel();
        setupDisplayPanel();
        _animator = new Animator();
    }

    private void setupFrame() {
        setTitle("QFT Sandbox");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1068, 700);
        getContentPane().setBackground(Color.GREEN);
        getContentPane().setLayout(new BorderLayout(0, 0));

    }

    private void setupControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        getContentPane().add(controlPanel, BorderLayout.EAST);

        // FORM LAYOUT
        controlPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            ColumnSpec.decode("175px"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            ColumnSpec.decode("left:max(24dlu;default)"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, }));

        // LATTICE POINTS SLIDER & LABEL
        Hashtable<Integer, JLabel> labelTable1 = new Hashtable<Integer, JLabel>();
        labelTable1.put(new Integer(LATTICE_NUMBER_EXPONENT_MIN), new JLabel(
            Integer.toString((int) Math.pow(2, LATTICE_NUMBER_EXPONENT_MIN))));
        labelTable1.put(new Integer(LATTICE_NUMBER_EXPONENT_MAX), new JLabel(
            Integer.toString((int) Math.pow(2, LATTICE_NUMBER_EXPONENT_MAX))));
        _latticePointsSlider.setLabelTable(labelTable1);
        _latticePointsSlider.setMajorTickSpacing(LATTICE_NUMBER_EXPONENT_MAX - LATTICE_NUMBER_EXPONENT_MIN);
        _latticePointsSlider.setPaintTicks(true);
        _latticePointsSlider.setPaintLabels(true);
        _latticePointsSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _N = (int) Math.pow(2, _latticePointsSlider.getValue());
                _latticePointsValue.setText(Integer.toString(_N));
            }
        });
        controlPanel.add(_latticePointsLabel, "2, 2");
        controlPanel.add(_latticePointsSlider, "2, 3, left, top");
        controlPanel.add(_latticePointsValue, "4, 3, center, top");

        // TIME STEP SLIDER & LABEL TABLE
        Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
        labelTable2.put(new Integer(TIME_STEP_EXPONENT_MIN), new JLabel(TIME_STEP_PREFIX +
                                                                        Integer.toString(TIME_STEP_EXPONENT_MIN) +
                                                                        TIME_STEP_SUFFIX));
        labelTable2.put(new Integer(TIME_STEP_EXPONENT_MAX), new JLabel(TIME_STEP_PREFIX +
                                                                        Integer.toString(TIME_STEP_EXPONENT_MAX) +
                                                                        TIME_STEP_SUFFIX));
        _timeStepSlider.setLabelTable(labelTable2);
        controlPanel.add(_timeStepLabel, "2, 5");
        _timeStepSlider.setMajorTickSpacing(TIME_STEP_EXPONENT_MAX - TIME_STEP_EXPONENT_MIN);
        _timeStepSlider.setPaintTicks(true);
        _timeStepSlider.setPaintLabels(true);
        controlPanel.add(_timeStepSlider, "2, 6, left, top");
        controlPanel.add(_timeStepValue, "4, 6, center, top");
        _timeStepSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                _dt_exponent = slider.getValue();
                _timeStepValue.setText(TIME_STEP_PREFIX + Integer.toString(_dt_exponent) + TIME_STEP_SUFFIX);
            }
        });

        // START & STOP BUTTON
        controlPanel.add(_startStopButton, "2, 8");
        _startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_startStopButton.getText() == BUTTON_TEXT_START) {
                    _animator.startAnimation();
                    _startStopButton.setText(BUTTON_TEXT_STOP);
                }
                else if (_startStopButton.getText() == BUTTON_TEXT_STOP) {
                    _animator.stopAnimation();
                    _startStopButton.setText(BUTTON_TEXT_START);
                }
            }
        });

        // RESET BUTTON
        controlPanel.add(_resetButton, "2, 10");
        _resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _number = 0;
                _animator.stopAnimation();
                _startStopButton.setText(BUTTON_TEXT_START);
                updatePlots();
            }
        });

    }

    private void setupDisplayPanel() {
        JPanel displayPanel = new JPanel();
        getContentPane().add(displayPanel, BorderLayout.CENTER);
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.UNRELATED_GAP_COLSPEC,
            ColumnSpec.decode("112px"), FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(154dlu;default)"),
            FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("max(166dlu;default)"), }, new RowSpec[] {
            FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("314px"), FormFactory.UNRELATED_GAP_ROWSPEC,
            RowSpec.decode("314px"), FormFactory.UNRELATED_GAP_ROWSPEC, }));

        // add momentum plots
        displayPanel.add(_momPlotVacuum, "2, 2, center, center");
        displayPanel.add(_momPlot1P, "4, 2, center, center");
        displayPanel.add(_momDensityPlot2P, "6, 2, center, center");

        // add position plots
        displayPanel.add(_posPlotVacuum, "2, 4, center, center");
        displayPanel.add(_posPlot1P, "4, 4, center, center");
        displayPanel.add(_posDensityPlot2P, "6, 4, center, center");

    }

    private Complex[] f() {
        Complex[] cdata = new Complex[_N];
        for (int i = 0; i < _N; i++) {
            double arg = 20.0*(i - 0.5*_N*Math.sin(2.0*_number/_N) - _N / 2) / _N;
            cdata[i] = Complex.one().times(Math.cos(Math.PI*_number/_N)*Math.exp(-arg * arg));
            //if (i < _N * 0.1 * (1.2 + Math.cos(2 * Math.PI * _number / _N)))
            //    cdata[i] = Complex.one();
            //else
            //    cdata[i] = Complex.zero();
        }
        return cdata;
    }

    private double g() {
        double value = Math.cos(Math.PI * _number / _N);
        return value * value;
    }

    @SuppressWarnings("unused")
    private double[][] h() {
        double[][] data = new double[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                double value = Math.cos(Math.PI * (i + _number) / _N) * Math.cos(Math.PI * (j - _number) / _N);
                data[i][j] = value * value;
            }
        }
        return data;
    }

    private Complex[][] k() {
        Complex[][] cdata = new Complex[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                double arg = 20.0*(i - 0.4*_N*Math.sin(2.0*_number/_N) - _N / 2) / _N;
                double arg2 = 15.0*(j - 0.3*_N*Math.cos(3.0*_number/_N) - _N / 2) / _N;
                cdata[i][j] = Complex.one().times(Math.cos(Math.PI*_number/_N)*Math.exp(-arg * arg - arg2*arg2));
                
                //double value = Math.cos(Math.PI * (i + _number) / _N) * Math.cos(Math.PI * (j - _number) / _N);
                //cdata[i][j] = Complex.one().times(value);
                
                // if (i < _N * 0.1 * (1.2 + Math.cos(2 * Math.PI * _number / _N)) && j < _N * 0.2 * (1.2 + Math.cos(2 *
                // Math.PI * _number / _N)))
                // cdata[i][j] = Complex.one();
                // else
                // cdata[i][j] = Complex.zero();
            }
        }
        return cdata;
    }

    private void updatePlots() {

        // momentum plots
        _momPlotVacuum.update(g());
        _momPlot1P.update(_ft.transform(f()));
        _momDensityPlot2P.update(_ft.transform2D(k()));

        _momPlotVacuum.repaint();
        _momPlot1P.repaint();
        _momDensityPlot2P.repaint();

        // momentum plots
        _posPlotVacuum.update(g());
        _posPlot1P.update(f());
        _posDensityPlot2P.update(k());

        _posPlotVacuum.repaint();
        _posPlot1P.repaint();
        _posDensityPlot2P.repaint();
    }

    // inner Animator class, taking care of animation
    private class Animator implements ActionListener {

        private Timer   _timer;
        private boolean _frozen = true;
        private int     delay   = (int) (1000.0 / _framerate);

        public Animator() {
            _timer = new Timer(delay, this);
            _timer.setCoalesce(true);
        }

        // fired by timer
        public void actionPerformed(ActionEvent e) {
            if (!_frozen) {
                _number++;
                updatePlots();
            }
        }

        public void startAnimation() {
            _timer.start();
            _frozen = false;
        }

        public void stopAnimation() {
            _timer.stop();
            _frozen = true;
        }

    }

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
