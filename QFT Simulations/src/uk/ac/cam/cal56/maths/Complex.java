package uk.ac.cam.cal56.maths;

import java.text.DecimalFormat;

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

    // constructor for 0
    public static Complex zero() {
        return new Complex(0.0, 0.0);
    }

    // constructor for 1
    public static Complex one() {
        return new Complex(1.0, 0.0);
    }

    // constructor for i
    public static Complex i() {
        return new Complex(0.0, 1.0);
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

    public double arg() {
        if (Math.abs(_imag) < EPSILON && Math.abs(_real) < EPSILON)
            return 0.0;
        return Math.atan2(_imag, _real);
    };

    // complex conjugate
    public Complex conj() {
        return new Complex(_real, -_imag);
    }

    public Complex negative() {
        return new Complex(-_real, -_imag);
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

    public Complex timesi() {
        return new Complex(-_imag, _real);
    }

    public Complex timesi(double a) {
        return new Complex(-a * _imag, a * _real);
    }

    public Complex timesexpi(double arg) {
        return new Complex(Math.cos(arg) * _real - Math.sin(arg) * _imag, Math.sin(arg) * _real + Math.cos(arg) * _imag);
    }

    // equals operator
    public boolean equals(Complex z) {
        return (Math.abs(z.real() - _real) < EPSILON && Math.abs(z.imag() - _imag) < EPSILON);
    }

    // zero bool operator
    public boolean isZero() {
        return equals(Complex.zero());
    }

    // toString() override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return "(" + df.format(_real) + "," + df.format(_imag) + ")";
    }

    // static dot product of two complex vectors
    public static Complex dotProduct(Complex[] a, Complex[] b) {
        if (a.length != b.length)
            return null;
        Complex sum = Complex.zero();
        for (int n = 0; n < a.length; n++)
            sum = sum.plus(a[n].conj().times(b[n]));
        return sum;
    }

    // norm squared of complex vector
    public static double normSquared(Complex[] a) {
        double sum = 0;
        for (int n = 0; n < a.length; n++)
            sum += a[n].modSquared();
        return sum;
    }

    // norm of complex vector
    public static double norm(Complex[] a) {
        return Math.sqrt(normSquared(a));
    }

    // normalise complex vector
    public static void normalise(Complex[] a) {
        double norm = Complex.norm(a);
        for (int n = 0; n < a.length; n++)
            a[n] = a[n].divide(norm);
    }

}
