package uk.ac.cam.cal56.qft;

import uk.ac.cam.cal56.maths.Complex;

public interface State {

    public void step();

    public void step(int numSteps);

    public void reset();

    public void setWavePacket(WavePacket wavepacket);
    
    public void setToGroundState();
    
    public void setToFirstState();
    
    public void setToSecondState();

    public int getN();

    public double getTime();

    public double getModSquared();

    public Complex getVacuum();

    public Complex[] get1PMom();

    public Complex[][] get2PMom();

    public Double getRemainingProbability();
    
    public double getTotalEnergy();

    // remember to recalculate ALL the coefficients (not only current)
    public void setInteractionStrength(Interaction interaction, double lambda);

    // remember to recalculate ALL the coefficients (not only current)
    public void setTimeStep(double dt);
}
