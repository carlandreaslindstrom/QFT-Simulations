package uk.ac.cam.cal56.qft.interactingtheory;

import java.util.Map;

import uk.ac.cam.cal56.qft.util.Complex;

public class InteractionHamiltonian {

    private Map<Integer, Map<Integer, Complex>> _elements;

    public InteractionHamiltonian(int N, double dx) {
        // calculate the elements
    }

    public Complex get(int n, int m) {
        return _elements.get(n).get(m);
    }

    public Map<Integer, Complex> getRow(int n) {
        return _elements.get(n);
    }

}
