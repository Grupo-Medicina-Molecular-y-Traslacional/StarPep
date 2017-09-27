/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.scales;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author beltran
 */
public class otherScales {
    
    /**
     * Protein Mw is calculated by the addition of average isotopic masses of amino acids in the protein and the average isotopic mass of one water molecule. Molecular weight values are given in Dalton (Da).
     */
     /**
     * H GASG760101 Database AAINDEX
     *
     * @return molecular weight (FASMAN 1976)
     */
    public static Map<String, Double> molecularWeight() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A",  71.0788);
        AAH.put("L", 113.1594);
        AAH.put("R", 156.1875);
        AAH.put("K", 128.1741);
        AAH.put("N", 114.1038);
        AAH.put("M", 131.1926);
        AAH.put("D", 115.0886);
        AAH.put("F", 147.1766);
        AAH.put("C", 103.1388);
        AAH.put("P",  97.1167);
        AAH.put("Q", 128.1307);
        AAH.put("S",  87.0782);
        AAH.put("E", 129.1155);
        AAH.put("T", 101.1051);
        AAH.put("G",  57.0519);
        AAH.put("W", 186.2132);
        AAH.put("H", 137.1411);
        AAH.put("Y", 163.1760);
        AAH.put("I", 113.1594);
        AAH.put("V", 99.1326);
        return AAH;
    }
    
     public static Map<String, Double> bomanIndex() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 1.81);
        AAH.put("L", 4.92);
        AAH.put("R", -14.92);
        AAH.put("K", -5.5);
        AAH.put("N", -6.64);
        AAH.put("M", 2.35);
        AAH.put("D", -8.72);
        AAH.put("F", 2.98);
        AAH.put("C", 1.28);
        AAH.put("P", 0.00);
        AAH.put("Q", -5.54);
        AAH.put("S", -3.40);
        AAH.put("E", -6.81);
        AAH.put("T", -2.57);
        AAH.put("G", 0.94);
        AAH.put("W", 2.33);
        AAH.put("H", -4.66);
        AAH.put("Y", -0.14);
        AAH.put("I", 4.92);
        AAH.put("V", 4.04);
        return AAH;
    }
}
