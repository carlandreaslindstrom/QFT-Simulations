package uk.ac.cam.cal56.qft;

import java.util.Map;

public interface Hamiltonian {

    public void calculateElements();
    
    public Double get(int n, int m);
    
    public Map<Integer, Double> getRow(int n);
}
