package uk.ac.cam.cal56.qft.util;

public class Complex {

	private final double _real;
	private final double _imag;
	
	// constructor for Cartesian coordinates
	public Complex(double real, double imag) {
		_real = real;
		_imag = imag;
	}
	
	// constructor for polar coordinates
	public static Complex polar(double mod, double arg) { 
		return new Complex(mod*Math.cos(arg),mod*Math.sin(arg)); 
	}
	
	// constructor for special case e^(i*arg)
	public static Complex expi(double arg) { return Complex.polar(1.0, arg); }
	
	// real and imaginary part getters
	public double real() { return _real; }
	public double imag() { return _imag; }
	
	// modulus and argument getters
	public double modSquared() { return _real*_real + _imag*_imag; }
	public double mod() { return Math.sqrt(modSquared()); }
	public double arg() { return Math.atan2(_imag, _real); };
	
	// complex conjugate
	public Complex conj() { return new Complex(_real, -_imag); }
	
	// addition and subtraction
	public Complex plus(Complex z) { return new Complex(_real + z.real(), _imag + z.imag()); }
	public Complex minus(Complex z) { return new Complex(_real - z.real(), _imag - z.imag()); }
	
	// multiplication and division
	public Complex times(Complex z) { return Complex.polar(mod()*z.mod(), arg()+z.arg()); }
	public Complex divide(Complex z) { return Complex.polar(mod()/z.mod(), arg()-z.arg()); }
	
	// equals operator
	public Boolean equals(Complex z) { return (z.real()==_real && z.imag()==_imag); }
	
	// toString() override
	public String toString() { return "("+_real+","+_imag+")"; }
	
}
