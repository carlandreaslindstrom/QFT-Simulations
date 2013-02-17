package uk.ac.cam.cal56.maths;

public interface FourierTransform {
    
    public Complex[] transform(Complex[] f);

    public Complex[] inversetransform(Complex[] F);

    public Complex[][] transform2D(Complex[][] f);
    
    public Complex[][] inversetransform2D(Complex[][] F);

}
