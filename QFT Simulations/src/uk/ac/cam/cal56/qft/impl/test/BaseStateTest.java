package uk.ac.cam.cal56.qft.impl.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.ac.cam.cal56.qft.Interaction;
import uk.ac.cam.cal56.qft.State;
import uk.ac.cam.cal56.qft.impl.MomentumWavePacket;
import uk.ac.cam.cal56.qft.scalars.ScalarState;

public class BaseStateTest {

    private final int                _N       = 5;
    private final int                _Pmax    = 3;
    private final double             _m       = 1.0;
    private final double             _dx      = 0.1;
    private final double             _dt      = 0.01;
    private double                   _lambda  = 1;

    private Map<Interaction, Double> _lambdas = new HashMap<Interaction, Double>();

    @Test
    public void testGetTotalEnergy() {
        _lambdas.put(Interaction.PHI_CUBED, _lambda);
        State state = new ScalarState(_N, _Pmax, _dt, _dx, _m, _lambdas, new MomentumWavePacket(_N));
        
        state.setToGroundState();
        System.out.println(state.getTotalEnergy());
    }

}
