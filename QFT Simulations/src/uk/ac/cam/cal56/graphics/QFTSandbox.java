package uk.ac.cam.cal56.graphics;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class QFTSandbox extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
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

    /**
     * Create the frame.
     */
    public QFTSandbox() {
        setTitle("QFT Sandbox");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel controls = new JPanel();
        controls.setBounds(394, 5, 240, 447);
        controls.setBackground(UIManager.getColor("CheckBox.background"));
        contentPane.add(controls);
        controls.setLayout(null);

        JSlider slider = new JSlider();
        slider.setBounds(26, 139, 158, 29);
        controls.add(slider);

        JLabel lblLatticePoints = new JLabel("N");
        lblLatticePoints.setBounds(16, 139, 19, 29);
        controls.add(lblLatticePoints);

        JSpinner spinner = new JSpinner();
        spinner.setBounds(182, 139, 52, 28);
        controls.add(spinner);

        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(6, 6, 228, 27);
        controls.add(comboBox);

        JButton btnRun = new JButton("Run");
        btnRun.setBounds(6, 412, 85, 29);
        controls.add(btnRun);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(103, 412, 81, 29);
        controls.add(progressBar);

        JLabel label = new JLabel("100%");
        label.setBounds(192, 417, 42, 16);
        controls.add(label);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(149, 35, 85, 29);
        controls.add(btnReset);

        JSlider slider_1 = new JSlider();
        slider_1.setBounds(26, 174, 158, 29);
        controls.add(slider_1);

        JLabel lblNewLabel = new JLabel("dt");
        lblNewLabel.setBounds(16, 174, 32, 29);
        controls.add(lblNewLabel);

        JSeparator separator = new JSeparator();
        separator.setBounds(6, 401, 228, 12);
        controls.add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(6, 64, 228, 12);
        controls.add(separator_1);

        JLabel lblNewLabel_1 = new JLabel("1e-7");
        lblNewLabel_1.setBounds(192, 180, 42, 16);
        controls.add(lblNewLabel_1);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(6, 6, 384, 446);
        contentPane.add(tabbedPane);

        JPanel probabilities = new JPanel();
        tabbedPane.addTab("Probabilities", null, probabilities, null);

        JPanel visualizer = new JPanel();
        tabbedPane.addTab("Visualizer", null, visualizer, null);

        JPanel statistics = new JPanel();
        tabbedPane.addTab("Statistics", null, statistics, null);
        /*
        int maxdim = 354, N = 118;
        double[][] d = new double[N][N];
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double z1 = 5.0 * (i - N / 2) / N;
                double z2 = 5.0 * (j - 2*N / 3) / N;
                double z3 = 2.0 * (i - N / 4) / N;
                double z4 = 3.0 * (j - N / 6) / N;
                double value = Math.exp(-z1 * z1 - z2 * z2) + Math.exp(-z3 * z3 - z4 * z4);
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
                d[i][j] = value;
            }
        }
        DensityPlot dp = new DensityPlot(maxdim, N, min, max);
        dp.setBounds(0, 0, dp.getDimension(), dp.getDimension());
        probabilities.add(dp);
        dp.update(d);
        */
    }
}
