package uk.ac.cam.cal56.graphics;

import uk.ac.cam.cal56.qft.WavePacket;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;

//@formatter:off
public class Preset {
    
    // definitions of presets, NOTE: THE 2nd ARGUMENT AND THE 1st WAVEPACKET ARGUMENT MUST BE EQUAL
    public static final Preset VACUUM       = new Preset("Interacting vacuum", 16, 3, 1e-1, 1, 3e-3, 10, 1.0, 1.0, null, 
                                                         WavePacket.getVacuum(16));
    public static final Preset FREE_1P_SLOW = new Preset("1 free slow particle", 128, 1, 1e-1, 1, 1e-3, 100, null, null, null,
                                                         new MomentumWavePacket(64, new int[] { 4 }, new double[] { 0 }));
    public static final Preset FREE_1P_FAST = new Preset("1 free fast particle", 128, 1, 1e-1, 1, 1e-3, 256, null, null, null,
                                                         new MomentumWavePacket(128, new int[] { 110 }, new double[] { 1 }, 0.55));
    public static final Preset FREE_1P_FASTER = new Preset("1 free faster particle", 128, 1, 1e-1, 1, 1e-3, 256, null, null, null,
                                                         new MomentumWavePacket(128, new int[] { 125 }, new double[] { 1 }, 0.24));
    public static final Preset FREE_2P_FAST = new Preset("2 free particles collide", 32, 2, 1e-1, 1, 3.7e-3, 25, null, null, null,
                                                         new MomentumWavePacket(32, new int[] { 28, 4 }, new double[] { 0, 0 }));
    public static final Preset INT_2P_3VERTEX  = new Preset("2 particles colliding (3-vertex)", 32, 2, 1e-1, 1, 7.4e-4, 25, null, 2.7e0, null,
                                                         new MomentumWavePacket(32, new int[] { 27, 5 }, new double[] { 0, 0 }));
    public static final Preset INT_2P_4VERTEX  = new Preset("2 particles colliding (4-vertex)", 24, 2, 1e-1, 1, 7.4e-4, 25, null, null, 2.7,
                                                           new MomentumWavePacket(24, new int[] { 3, 20 }, new double[] { 0, 0 }));
    public static final Preset HIGGS_LIKE_1P  = new Preset("Higgs-like particle?", 16, 2, 1e-1, 1, 7.4e-4, 50, -1.3, null, 13.0,
                                                         new MomentumWavePacket(16, new int[] { 2 }, new double[] { 0 },0.8));
    
    // list of all presets
    public static final Preset[] all = new Preset[] { VACUUM, FREE_1P_SLOW, FREE_1P_FAST,FREE_1P_FASTER, FREE_2P_FAST, INT_2P_3VERTEX, INT_2P_4VERTEX, HIGGS_LIKE_1P };

    // @formatter:on
    // preset values
    public final String          name;
    public final int             N;
    public final int             Pmax;
    public final double          dx;
    public final double          m;
    public final double          dt;
    public final int             steps;
    public final Double          lambda2;
    public final Double          lambda3;
    public final Double          lambda4;
    public final WavePacket      wavepacket;

    // Singleton constructor
    private Preset(String name, int N, int Pmax, double dx, double m, double dt, int steps, Double lambda2,
            Double lambda3, Double lambda4, WavePacket wavepacket) {
        this.name = name;
        this.N = N;
        this.Pmax = Pmax;
        this.dx = dx;
        this.m = m;
        this.dt = dt;
        this.steps = steps;
        this.lambda2 = lambda2;
        this.lambda3 = lambda3;
        this.lambda4 = lambda4;
        this.wavepacket = wavepacket;
    }

    // needed to show up as text in preset selector
    public String toString() {
        return name;
    }
}
