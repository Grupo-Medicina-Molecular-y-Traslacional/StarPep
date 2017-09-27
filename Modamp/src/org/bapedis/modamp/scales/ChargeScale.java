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
public class ChargeScale {

    /**
     * Database: AAindex Entry: KLEP840101 LinkDB: KLEP840101 All links * H
     * KLEP840101 D Net charge (Klein et al., 1984) R LIT:1008055 PMID:6547351 A
     * Klein, P., Kanehisa, M. and DeLisi, C. T Prediction of protein function
     * from sequence properties: Discriminant analysis of a data base J Biochim.
     * Biophys. Acta 787, 221-226 (1984) C ZIMJ680104 0.941 I * A/L R/K N/M D/F
     * C/P Q/S E/T G/W H/Y I/V 0. 1. 0. -1. 0. 0. -1. 0. 0. 0. 0. 1. 0. 0. 0. 0.
     * 0. 0. 0. 0.
     *
     * @return NetCharge according to Klein 1984
     */
    public static Map<String, Double> klein_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.0);
        AAH.put("L", 0.0);
        AAH.put("R", 1.0);
        AAH.put("K", 1.0);
        AAH.put("N", 0.0);
        AAH.put("M", 0.0);
        AAH.put("D", -1.0);
        AAH.put("F", 0.0);
        AAH.put("C", 0.0);
        AAH.put("P", 0.0);
        AAH.put("Q", 0.0);
        AAH.put("S", 0.0);
        AAH.put("E", -1.0);
        AAH.put("T", 0.0);
        AAH.put("G", 0.0);
        AAH.put("W", 0.0);
        AAH.put("H", 0.0);
        AAH.put("Y", 0.0);
        AAH.put("I", 0.0);
        AAH.put("V", 0.0);
        return AAH;
    }

    /**
     * H CHAM830107 D A parameter of charge transfer capability
     * (Charton-Charton, 1983) R LIT:0907093b PMID:6876837 A Charton, M. and
     * Charton, B. T The dependence of the Chou-Fasman parameters on amino acid
     * side chain structure J J. Theor. Biol. 111, 447-450 (1983) (Pro !) C I
     * A/L R/K N/M D/F C/P Q/S E/T G/W H/Y I/V 0. 0. 1. 1. 0. 0. 1. 1. 0. 0. 0.
     * 0. 0. 0. 0. 0. 0. 0. 0. 0. //
     *
     * @return
     */
    public static Map<String, Double> charton_ctc_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("D", 1.0);
        AAH.put("E", 1.0);
        AAH.put("K", 0.0);
        AAH.put("R", 0.0);
        AAH.put("H", 0.0);
        AAH.put("Y", 0.0);
        AAH.put("W", 0.0);
        AAH.put("F", 0.0);
        AAH.put("C", 0.0);
        AAH.put("M", 0.0);
        AAH.put("S", 0.0);
        AAH.put("T", 0.0);
        AAH.put("N", 1.0);
        AAH.put("Q", 0.0);
        AAH.put("G", 1.0);
        AAH.put("A", 0.0);
        AAH.put("V", 0.0);
        AAH.put("L", 0.0);
        AAH.put("I", 0.0);
        AAH.put("P", 0.0);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CHAM830108 LinkDB: CHAM830108 H CHAM830108 D A
     * parameter of charge transfer donor capability (Charton-Charton, 1983) R
     * LIT:0907093b PMID:6876837 A Charton, M. and Charton, B. T The dependence
     * of the Chou-Fasman parameters on amino acid side chain structure J J.
     * Theor. Biol. 111, 447-450 (1983) (Pro !) C I A/L R/K N/M D/F C/P Q/S E/T
     * G/W H/Y I/V 0. 1. 1. 0. 1. 1. 0. 0. 1. 0. 0. 1. 1. 1. 0. 0. 0. 1. 1. 0.
     * //
     *
     *
     * @return
     */
    public static Map<String, Double> charton_ctdc_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("D", 0.0);
        AAH.put("E", 0.0);
        AAH.put("K", 1.0);
        AAH.put("R", 1.0);
        AAH.put("H", 1.0);
        AAH.put("Y", 1.0);
        AAH.put("W", 1.0);
        AAH.put("F", 1.0);
        AAH.put("C", 1.0);
        AAH.put("M", 1.0);
        AAH.put("S", 0.0);
        AAH.put("T", 0.0);
        AAH.put("N", 1.0);
        AAH.put("Q", 1.0);
        AAH.put("G", 0.0);
        AAH.put("A", 0.0);
        AAH.put("V", 0.0);
        AAH.put("L", 0.0);
        AAH.put("I", 0.0);
        AAH.put("P", 0.0);
        return AAH;
    }

}
