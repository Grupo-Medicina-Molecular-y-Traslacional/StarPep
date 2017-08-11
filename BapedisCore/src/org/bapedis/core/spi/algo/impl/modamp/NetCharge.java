/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;

/**
 *
 * @author Home
 */
public class NetCharge extends AbstractModamp{
    protected double pH;
    protected Map<String, Double> pKscale;

    public NetCharge(AlgorithmFactory factory) {
        super(factory);
    }
        
    @Override
    public void compute(Peptide peptide) {
        Map<String, Double> Nj = new HashMap<>();
        Nj.put("Y", 0.0);
        Nj.put("D", 0.0);
        Nj.put("E", 0.0);
        Nj.put("C", 0.0);
        Nj.put("COOH", 1.0);
        //positive
        Map<String, Double> Ni = new HashMap<>();
        Ni.put("R", 0.0);
        Ni.put("H", 0.0);
        Ni.put("K", 0.0);
        Ni.put("NH2", 1.0);

        String seq = peptide.getSequence();
        //count number of acidic and basic amino acids
        for (int i = 0; i < seq.length(); i++) {
            String aa = seq.substring(i, i + 1);
            if (Nj.containsKey(aa)) {
                Nj.replace(aa, Nj.get(aa) + 1);
            } else if (Ni.containsKey(aa)) {
                Ni.replace(aa, Ni.get(aa) + 1);
            }
        }

        double pos = 0;
        Iterator<String> it = Ni.keySet().iterator();

        while (it.hasNext()) {
            String ni = it.next();
            Double count = Ni.get(ni);
            double pKai = pKscale.get(ni);
            if (count != 0) {
                pos += (count * (1 / (1 + Math.pow(10, pH - pKai))));
            }
        }
        double neg = 0;
        it = Nj.keySet().iterator();
        while (it.hasNext()) {
            String nj = it.next();
            Double count = Nj.get(nj);
            double pKai = pKscale.get(nj);
            if (count != 0) {
                neg += (count * (-1 / (1 + Math.pow(10, pKai - pH))));
            }
        }

//        return pos + neg;
    }
    
}
