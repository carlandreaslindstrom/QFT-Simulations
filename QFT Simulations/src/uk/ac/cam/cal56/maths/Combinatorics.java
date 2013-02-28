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

    // Number of distinct Fock states with N lattice points and maximally P particles
    // S(N,P) = Sum(Choose(N+m-1,m), {m,0,P})
    public static int S(int N, int P) {
        int sum = 0;
        for (int m = 0; m <= P; m++)
            sum += sigma(N, m);
        return sum;
    }

    // number of states with a given particle number m if N lattice points
    public static int sigma(int N, int m) {
        return choose(N + m - 1, m);
    }

    // Combinatoric factor from a given momentum from sandwiching a state of l particles
    // in that momentum with n creation operators and m annihilation operators. Complete
    // combinatoric factor is obtained by the product these for all momenta.
    // F_p(l,n,m) = sqrt( (Product[a=1->n](l-m+a)) * (Product[b=1->m](l+1-b) )
    public static double F_p(int l_p, int n_p, int m_p) {
        if (m_p > l_p)
            return 0;
        int squaredProduct = 1;
        for (int a = 1; a <= n_p; a++)
            squaredProduct *= (l_p - m_p + a);
        for (int b = 1; b <= m_p; b++)
            squaredProduct *= (l_p + 1 - b);
        return Math.sqrt(squaredProduct);
    }

}
