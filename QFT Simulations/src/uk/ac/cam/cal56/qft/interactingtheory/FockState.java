package uk.ac.cam.cal56.qft.interactingtheory;

import java.util.HashMap;
import java.util.Map;

import uk.ac.cam.cal56.qft.util.Combinatorics;

public class FockState {

    private Map<Integer, Integer> _exponents;
    private int                   _N;

    public FockState(int N) {
        _exponents = new HashMap<Integer, Integer>();
        _N = N;
    }

    // total particle number in Fock state
    public int particleNumber() {
        int sum = 0;
        for (int exponent : _exponents.values())
            sum += exponent;
        return sum;
    }

    // naive label of Fock State
    public long label() {
        long result = Combinatorics.S(_N, particleNumber() - 1);
        for (int k = 0; k < (_N - 1); k++) {
            long product = 1;
            int divFactor = 2; // k + 1;
            for (int m = 0; m <= k; m++) {
                int sum = 0;
                for (int n = _N - k - 1; n < _N; n++) {
                    Integer value = _exponents.get(n);
                    if (value != null)
                        sum += value;
                }
                product *= (sum + m);
                // continually divide by factors of dividing factorial to keep result from blowing up
                while (divFactor <= k + 1 && product % divFactor == 0) {
                    product /= divFactor;
                    divFactor++;
                }
            }
            result += product;
        }
        return result;
    }

    // create particle of momentum number p
    public void create(int p) {
        Integer exponent = _exponents.get(p);
        if (exponent == null)
            exponent = 0;
        _exponents.put(p, exponent + 1);
    }

    // annihilate particle of momentum number p, return false if resulting state is 0
    public boolean annihilate(int p) {
        Integer exponent = _exponents.get(p);
        if (exponent == null)
            return false;
        else if (exponent <= 0)
            _exponents.remove(p);
        else
            _exponents.put(p, exponent - 1);
        return true;
    }

    // gives label of the state which gives a nonzero contribution when
    // sandwiching a set of creation and annihilation operators.
    public Long sandwichLabel(Map<Integer, Integer> creationOps, Map<Integer, Integer> annihilationOps) {
        try {
            FockState bra = (FockState) this.clone();
            return bra.label();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
