package uk.ac.cam.cal56.graphics;

public class Preset {

    // definitions of presets
    public static final Preset   VACUUM       = new Preset("Interacting vacuum", 24, 3, 1e-1, 1, 3e-3, 10, 1);
    public static final Preset   FREE_1P_SLOW = new Preset("Non-interacting slow particle", 64, 1, 1e-1, 1, 1e-3, 100,
                                                  0, 4);
    public static final Preset   FREE_1P_FAST = new Preset("Non-interacting fast particle", 64, 1, 1e-1, 1, 1e-3, 256,
                                                  0, 32);

    // list of all presets
    public static final Preset[] all          = new Preset[] { VACUUM, FREE_1P_SLOW, FREE_1P_FAST };

    // preset values
    public final String          name;
    public final int             N;
    public final int             Pmax;
    public final double          dx;
    public final double          m;
    public final double          dt;
    public final int             steps;
    public final double          lambda;
    public final int[]           particleMomenta;

    // Singleton constructor
    private Preset(String name, int N, int Pmax, double dx, double m, double dt, int steps, double lambda,
            int... particleMomenta) {
        this.name = name;
        this.N = N;
        this.Pmax = Pmax;
        this.dx = dx;
        this.m = m;
        this.dt = dt;
        this.steps = steps;
        this.lambda = lambda;
        this.particleMomenta = particleMomenta;
    }

    // needed to show up as text in preset selector
    public String toString() {
        return name;
    }
}
