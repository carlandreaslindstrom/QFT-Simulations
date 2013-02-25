package uk.ac.cam.cal56.qft.interactingtheory;

import uk.ac.cam.cal56.graphics.DensityPlot;
import uk.ac.cam.cal56.graphics.Plot;

public interface State {

    public void setInteractionStrength(double lambda);

    public void step();

    public void reset();

    public double getTime();

    public double get0P();

    public double[] get1PMomenta();

    public double[] get1PPositions();
    
    public double[][] get2PMomenta();
    
    public double[][] get2PPositions();

    public void updatePlots(Plot p0m, Plot p0p, Plot p1m, Plot p1p, DensityPlot p2m, DensityPlot p2p);
}
