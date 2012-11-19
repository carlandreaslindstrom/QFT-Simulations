package uk.ac.cam.cal56.qft.util;

public class Complex {

    private static final double EPSILON = 1.0e-10;

    private final double        _real;
    private final double        _imag;

    // constructor for Cartesian coordinates
    public Complex(double real, double imag) {
        _real = real;
        _imag = imag;
    }

    // constructor for polar coordinates
    public static Complex polar(double mod, double arg) {
        return new Complex(mod * Math.cos(arg), mod * Math.sin(arg));
    }

    // constructor for special case e^(i*arg)
    public static Complex expi(double arg) {
        return Complex.polar(1.0, arg);
    }

    // constructor for zero
    public static Complex zero() {
        return new Complex(0.0, 0.0);
    }

    // real and imaginary part getters
    public double real() {
        return _real;
    }

    public double imag() {
        return _imag;
    }

    // modulus and argument getters
    public double modSquared() {
        return _real * _real + _imag * _imag;
    }

    public double mod() {
        return Math.sqrt(modSquared());
    }

    public double arg() throws IllegalArgumentException {
        if (_imag < EPSILON && _real < EPSILON)
            throw new IllegalArgumentException();
        return Math.atan2(_imag, _real);
    };

    // complex conjugate
    public Complex conj() {
        return new Complex(_real, -_imag);
    }

    // addition and subtraction
    public Complex plus(Complex z) {
        return new Complex(_real + z.real(), _imag + z.imag());
    }

    public Complex minus(Complex z) {
        return new Complex(_real - z.real(), _imag - z.imag());
    }

    // multiplication and division by real scalar
    public Complex times(double a) {
        return new Complex(a * _real, a * _imag);
    }

    public Complex divide(double a) {
        return times(1 / a);
    }

    // multiplication and division by complex numbers
    public Complex times(Complex z) {
        if (mod() * z.mod() < EPSILON)
            return Complex.zero();
        else
            return Complex.polar(mod() * z.mod(), arg() + z.arg());
    }

    public Complex divide(Complex z) {
        if (mod() < EPSILON || z.mod() > EPSILON)
            return Complex.zero();
        else
            return Complex.polar(mod() / z.mod(), arg() - z.arg());
    }

    // equals operator
    public Boolean equals(Complex z) {
        return (Math.abs(z.real() - _real) < EPSILON && Math.abs(z.imag() - _imag) < EPSILON);
    }

    // toString() override
    public String toString() {
        return "(" + _real + "," + _imag + ")";
    }

}
