package uk.ac.cam.cal56.maths;

public class Combinatorics {

    // Number of combinations possible of r elements out of n choices
    // choose(n,r) = n!/(r!(n-r)!)
    public static int choose(int n, int r) {
        if (r < 0)
            return 0;
        int product = 1;
        int divFactor = r;
        for (int multfactor = n; multfactor > (n - r); multfactor--) {
            product *= multfactor;
            // continually divide by factors of dividing factorial to keep result from blowing up
            while (divFactor > 1 && product % divFactor == 0) {
                product /= divFactor;
                divFactor--;
            }
        }
        return product;
    }

    public static int factorial(int k) {
        int result = 1;
        for (int i = 1; i <= k; i++)
            result *= i;
        return result;
    }

}
