/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.scales;

import java.util.HashMap;
import java.util.Map;
//obtain by : http://isoelectric.ovh.org/theory.html
/**
 *
 * @author beltran
 */
public class PkaValues {
    
    
    public static Map<String, Double> EMBOSS() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("C", 8.5);
        AAH.put("D", 3.9);
        AAH.put("E", 4.1);
        AAH.put("H", 6.5);
        AAH.put("K", 10.8);
        AAH.put("R", 12.5);
        AAH.put("Y", 10.2);
        AAH.put("NH2", 8.6);
        AAH.put("COOH", 3.6);
        return AAH;
    }
    
    public static Map<String, Double> Bjellqvist(){
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("C", 9.0);
        AAH.put("D", 4.05);
        AAH.put("E", 4.45);
        AAH.put("H", 5.98);
        AAH.put("K", 10.0);
        AAH.put("R", 12.0);
        AAH.put("Y", 10.0);
        AAH.put("NH2",  7.5);
        AAH.put("COOH", 3.55);
        return AAH;
    }
    
    //for short protein and peptide use IPC
    public static Map<String, Double> IPC_protein(){
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("C", 7.555);
        AAH.put("D", 3.872);
        AAH.put("E", 4.412);
        AAH.put("H", 5.637);
        AAH.put("K", 9.052);
        AAH.put("R", 11.84);
        AAH.put("Y", 10.85);
        AAH.put("NH2",  9.094);
        AAH.put("COOH", 2.869);
        return AAH;
    }
    
    
        //for short protein and peptide use IPC
    public static Map<String, Double> Lehninger(){
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("C", 8.33);
        AAH.put("D", 3.86);
        AAH.put("E", 4.25);
        AAH.put("H", 6.0);
        AAH.put("K", 10.5);
        AAH.put("R", 12.4 );
        AAH.put("Y", 10.0);
        AAH.put("NH2",  9.69);
        AAH.put("COOH", 2.34 );
        return AAH;
         		 	 	 	 	 		
    }
    
       public static  Map<String, Double> IPC_peptide(){
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("C", 8.297);
        AAH.put("D", 3.887);
        AAH.put("E", 4.317);
        AAH.put("H", 6.018);
        AAH.put("K", 10.517);
        AAH.put("R", 12.503);
        AAH.put("Y", 10.071);
        AAH.put("NH2",  9.094);
        AAH.put("COOH", 2.383);
        return AAH;
    }
    
}
