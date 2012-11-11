package uk.ac.cam.cal56.qft;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cal56.qft.util.Complex;

public class FockState {
	
	private List<Complex> _coeffs;
	
	public FockState() {
		_coeffs = new ArrayList<Complex>();
		_coeffs.add(0, new Complex(1/Math.sqrt(2.0), 0.0));
		_coeffs.add(1, new Complex(0.0, 1/Math.sqrt(2.0)));
	}
	
	public Complex getCoeff(int n) { return _coeffs.get(n); }
	
	public double mod() {
		double sumOfCoeffModSquares = 0.0;
		for(Complex z : _coeffs) sumOfCoeffModSquares += z.modSquared();
		return Math.sqrt(sumOfCoeffModSquares);
	}
	
}
