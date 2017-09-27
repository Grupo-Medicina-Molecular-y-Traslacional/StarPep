package org.bapedis.modamp.scales;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author beltran
 */
public class HydrophilicityScale {

    /**
     * Database: AAindex Entry: KUHL950101 LinkDB: KUHL950101 H KUHL950101 D
     * Hydrophilicity scale (Kuhn et al., 1995) R PMID:8749849 A Kuhn, L.A.,
     * Swanson, C.A., Pique, M.E., Tainer, J.A. and Getzoff, E.D. T Atomic and
     * residue hydrophilicity in the context of folded protein structures J
     * Proteins 23, 536-547 (1995)
     *
     * @return kunh_hydrov_hash
     */
    public static Map<String, Double> kuhn_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.78);
        AAH.put("L", 0.56);
        AAH.put("R", 1.58);
        AAH.put("K", 1.10);
        AAH.put("N", 1.20);
        AAH.put("M", 0.66);
        AAH.put("D", 1.35);
        AAH.put("F", 0.47);
        AAH.put("C", 0.55);
        AAH.put("P", 0.69);
        AAH.put("Q", 1.19);
        AAH.put("S", 1.00);
        AAH.put("E", 1.45);
        AAH.put("T", 1.05);
        AAH.put("G", 0.68);
        AAH.put("W", 0.70);
        AAH.put("H", 0.99);
        AAH.put("Y", 1.00);
        AAH.put("I", 0.47);
        AAH.put("V", 0.51);
        return AAH;
    }
    
       /**
     * Database: AAindex Entry: HOPT810101 LinkDB: HOPT810101 H HOPT810101 D
     * Hydrophilicity value (Hopp-Woods, 1981) R LIT:0707598 PMID:6167991 A
     * Hopp, T.P. and Woods, K.R. T Prediction of protein antigenic determinants
     * from amino acid sequecces J Proc. Natl. Acad. Sci. USA 78, 3824-3828
     * (1981)
     *
     * @return hoop_wood_hydro_
     */
    public static Map<String, Double> hopp_Woods_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.5);
        AAH.put("L", -1.8);
        AAH.put("R",  3.0);
        AAH.put("K",  3.0);
        AAH.put("N",  0.2);
        AAH.put("M", -1.3);
        AAH.put("D",  3.0);
        AAH.put("F", -2.5);
        AAH.put("C", -1.0);
        AAH.put("P",  0.0);
        AAH.put("Q",  0.2);
        AAH.put("S",  0.3);
        AAH.put("E",  3.0);
        AAH.put("T", -0.4);
        AAH.put("G",  0.0);
        AAH.put("W", -3.4);
        AAH.put("H", -0.5);
        AAH.put("Y", -2.3);
        AAH.put("I", -1.8);
        AAH.put("V", -1.5);
        return AAH;
    }
    
    
    
}
