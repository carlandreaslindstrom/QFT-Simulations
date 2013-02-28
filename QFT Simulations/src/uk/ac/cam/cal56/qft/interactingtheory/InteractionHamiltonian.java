package uk.ac.cam.cal56.qft.interactingtheory;

import java.util.Map;

public interface InteractionHamiltonian {

    public void calculateElements();
    
    public Double get(int n, int m);
    
    public Map<Integer, Double> getRow(int n);
}
