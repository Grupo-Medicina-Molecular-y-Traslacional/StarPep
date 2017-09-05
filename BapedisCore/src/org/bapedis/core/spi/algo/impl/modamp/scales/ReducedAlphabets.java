/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp.scales;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author beltran
 */
public class ReducedAlphabets {

    public static ReduceAlphabet stdAminoAcids() {
        Map<String, Double> counter = new HashMap<>(20);
        String ALPHABET = "ACDEFGHIKLMNPQRSTVWY";
        //Inicializar
        for (int i = 0; i < ALPHABET.length(); i++) {
            counter.put(ALPHABET.substring(i, i + 1), 0.0);
        }
        return new ReduceAlphabet("std", counter);
    }

    /**
     * property: hydrophobicity cite: Rose et al., 1985 Reduce: class [CFLMVWI],
     * [AG], [HP], [DEKR], [NQSTY]
     *
     * @return
     */
    public static ReduceAlphabet ra_hydrop_Rose() {
        Map<String, Double> count = new HashMap<>(5);
        count.put("CFLMVWI", 0.0);
        count.put("AG", 0.0);
        count.put("HP", 0.0);
        count.put("DEKR", 0.0);
        count.put("NQSTY", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "AG");
        AAH.put("L", "CFLMVWI");
        AAH.put("R", "DEKR");
        AAH.put("K", "DEKR");
        AAH.put("N", "NQSTY");
        AAH.put("M", "CFLMVWI");
        AAH.put("D", "DEKR");
        AAH.put("F", "CFLMVWI");
        AAH.put("C", "CFLMVWI");
        AAH.put("P", "HP");
        AAH.put("Q", "NQSTY");
        AAH.put("S", "NQSTY");
        AAH.put("E", "DEKR");
        AAH.put("T", "NQSTY");
        AAH.put("G", "AG");
        AAH.put("W", "CFLMVWI");
        AAH.put("H", "HP");
        AAH.put("Y", "NQSTY");
        AAH.put("I", "CFLMVWI");
        AAH.put("V", "CFLMVWI");
        return new ReduceAlphabet("hyR",count, AAH);
    }

    /**
     * property: Blosum50 Reference: Murphy et al., 2000 Reduce class [FWY],
     * [CLVIM], [H], [AG], [ST], [DENQ], [KR], [ P]
     *
     * @return
     */
    public static ReduceAlphabet ra_Blosum50_Murphy() {
        Map<String, Double> reduce_alphabet = new HashMap<>(8);
        reduce_alphabet.put("FWY", 0.0);
        reduce_alphabet.put("CLVIM", 0.0);
        reduce_alphabet.put("H", 0.0);
        reduce_alphabet.put("AG", 0.0);
        reduce_alphabet.put("ST", 0.0);
        reduce_alphabet.put("DENQ", 0.0);
        reduce_alphabet.put("KR", 0.0);
        reduce_alphabet.put("P", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "AG");
        AAH.put("L", "CLVIM");
        AAH.put("R", "KR");
        AAH.put("K", "KR");
        AAH.put("N", "DENQ");
        AAH.put("M", "CLVIM");
        AAH.put("D", "DENQ");
        AAH.put("F", "FWY");
        AAH.put("C", "CLVIM");
        AAH.put("P", "P");
        AAH.put("Q", "DENQ");
        AAH.put("S", "ST");
        AAH.put("E", "DENQ");
        AAH.put("T", "ST");
        AAH.put("G", "AG");
        AAH.put("W", "FWY");
        AAH.put("H", "H");
        AAH.put("Y", "FWY");
        AAH.put("I", "CLVIM");
        AAH.put("V", "CLVIM");
        return new ReduceAlphabet("b50",reduce_alphabet, AAH);
    }

    /**
     * property: conformational Similarity Reference: Chakrabarti and Pal, 2001
     * Reduce class [SCMEKRL], [DN], [HFYW], [VIT], [A], [G], [P]
     *
     * @return
     */
    public static ReduceAlphabet ra_cSimilarity_chakrabarty() {
        Map<String, Double> reduce_alphabet = new HashMap<>(7);
        reduce_alphabet.put("SCMEQKRL", 0.0);
        reduce_alphabet.put("DN", 0.0);
        reduce_alphabet.put("HFYW", 0.0);
        reduce_alphabet.put("VIT", 0.0);
        reduce_alphabet.put("A", 0.0);
        reduce_alphabet.put("G", 0.0);
        reduce_alphabet.put("P", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "A");
        AAH.put("L", "SCMEQKRL");
        AAH.put("R", "SCMEQKRL");
        AAH.put("K", "SCMEQKRL");
        AAH.put("N", "DN");
        AAH.put("M", "SCMEQKRL");
        AAH.put("D", "DN");
        AAH.put("F", "HFYW");
        AAH.put("C", "SCMEQKRL");
        AAH.put("P", "P");
        AAH.put("Q", "SCMEQKRL");
        AAH.put("S", "SCMEQKRL");
        AAH.put("E", "SCMEQKRL");
        AAH.put("T", "VIT");
        AAH.put("G", "G");
        AAH.put("W", "HFYW");
        AAH.put("H", "HFYW");
        AAH.put("Y", "HFYW");
        AAH.put("I", "VIT");
        AAH.put("V", "VIT");
        return new ReduceAlphabet("cs",reduce_alphabet, AAH);
    }

    /**
     * property: Hydrophobicity Reference: Tomii and Kanehisa, 1996; Li et al.,
     * 2006 Reduce class [RKEDQN], [GASTPHY], [CLVIMFW]
     *
     *
     * @return
     */
    public static ReduceAlphabet ra_Hydrophobicity_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("RKEDQN", 0.0);
        reduce_alphabet.put("GASTPHY", 0.0);
        reduce_alphabet.put("CLVIMFW", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "GASTPHY");
        AAH.put("L", "CLVIMFW");
        AAH.put("R", "RKEDQN");
        AAH.put("K", "RKEDQN");
        AAH.put("N", "RKEDQN");
        AAH.put("M", "CLVIMFW");
        AAH.put("D", "RKEDQN");
        AAH.put("F", "CLVIMFW");
        AAH.put("C", "CLVIMFW");
        AAH.put("P", "GASTPHY");
        AAH.put("Q", "RKEDQN");
        AAH.put("S", "GASTPHY");
        AAH.put("E", "RKEDQN");
        AAH.put("T", "GASTPHY");
        AAH.put("G", "GASTPHY");
        AAH.put("W", "CLVIMFW");
        AAH.put("H", "GASTPHY");
        AAH.put("Y", "GASTPHY");
        AAH.put("I", "CLVIMFW");
        AAH.put("V", "CLVIMFW");
        return new ReduceAlphabet("hydT",reduce_alphabet, AAH);
    }

    /**
     * property: Normalized van der Waals volume Reference: Tomii and Kanehisa,
     * 1996; Li et al., 2006 Reduce class [GASTCPD], [NVEQIL], [MHKFRYW]
     *
     *
     *
     * @return
     */
    public static ReduceAlphabet ra_NormVW_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("GASTCPD", 0.0);
        reduce_alphabet.put("NVEQIL", 0.0);
        reduce_alphabet.put("MHKFRYW", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "GASTCPD");
        AAH.put("L", "NVEQIL");
        AAH.put("R", "MHKFRYW");
        AAH.put("K", "MHKFRYW");
        AAH.put("N", "NVEQIL");
        AAH.put("M", "MHKFRYW");
        AAH.put("D", "GASTCPD");
        AAH.put("F", "MHKFRYW");
        AAH.put("C", "GASTCPD");
        AAH.put("P", "GASTCPD");
        AAH.put("Q", "NVEQIL");
        AAH.put("S", "GASTCPD");
        AAH.put("E", "NVEQIL");
        AAH.put("T", "GASTCPD");
        AAH.put("G", "GASTCPD");
        AAH.put("W", "MHKFRYW");
        AAH.put("H", "MHKFRYW");
        AAH.put("Y", "MHKFRYW");
        AAH.put("I", "NVEQIL");
        AAH.put("V", "NVEQIL");
        return new ReduceAlphabet("vw",reduce_alphabet, AAH);
    }

    /**
     * property: Polarity Reference: Tomii and Kanehisa, 1996; Li et al., 2006
     * Reduce class [LIFWCMVY], [PATGS], [HQRKNED]
     *
     * @return
     */
    public static ReduceAlphabet ra_Polarity_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(5);
        reduce_alphabet.put("LIFWCMVY", 0.0);
        reduce_alphabet.put("PATGS", 0.0);
        reduce_alphabet.put("HQRKNED", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "PATGS");
        AAH.put("L", "LIFWCMVY");
        AAH.put("R", "HQRKNED");
        AAH.put("K", "HQRKNED");
        AAH.put("N", "HQRKNED");
        AAH.put("M", "LIFWCMVY");
        AAH.put("D", "HQRKNED");
        AAH.put("F", "LIFWCMVY");
        AAH.put("C", "LIFWCMVY");
        AAH.put("P", "PATGS");
        AAH.put("Q", "HQRKNED");
        AAH.put("S", "PATGS");
        AAH.put("E", "HQRKNED");
        AAH.put("T", "PATGS");
        AAH.put("G", "PATGS");
        AAH.put("W", "LIFWCMVY");
        AAH.put("H", "HQRKNED");
        AAH.put("Y", "LIFWCMVY");
        AAH.put("I", "LIFWCMVY");
        AAH.put("V", "LIFWCMVY");
        return new ReduceAlphabet("pol",reduce_alphabet, AAH);
    }

    /**
     * property: Polarizability Reference: Tomii and Kanehisa, 1996; Li et al.,
     * 2006 Reduce class [GASDT], [CPNVEQIL], [KMHFRYW]
     *
     * @return
     */
    public static ReduceAlphabet ra_Polarizability_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("GASDT", 0.0);
        reduce_alphabet.put("CPNVEQIL", 0.0);
        reduce_alphabet.put("KMHFRYW", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "GASDT");
        AAH.put("L", "CPNVEQIL");
        AAH.put("R", "KMHFRYW");
        AAH.put("K", "KMHFRYW");
        AAH.put("N", "CPNVEQIL");
        AAH.put("M", "KMHFRYW");
        AAH.put("D", "GASDT");
        AAH.put("F", "KMHFRYW");
        AAH.put("C", "CPNVEQIL");
        AAH.put("P", "CPNVEQIL");
        AAH.put("Q", "CPNVEQIL");
        AAH.put("S", "GASDT");
        AAH.put("E", "CPNVEQIL");
        AAH.put("T", "GASDT");
        AAH.put("G", "GASDT");
        AAH.put("W", "KMHFRYW");
        AAH.put("H", "KMHFRYW");
        AAH.put("Y", "KMHFRYW");
        AAH.put("I", "CPNVEQIL");
        AAH.put("V", "CPNVEQIL");
        return new ReduceAlphabet("polz",reduce_alphabet, AAH);
    }

    /**
     * property: Charge Reference: Tomii and Kanehisa, 1996; Li et al., 2006
     * Reduce class [KR], [ANCQGHILMFPSTWYV], [DE]
     *
     * @return
     */
    public static ReduceAlphabet ra_Charge_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("KR", 0.0);
        reduce_alphabet.put("ANCQGHILMFPSTWYV", 0.0);
        reduce_alphabet.put("DE", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "ANCQGHILMFPSTWYV");
        AAH.put("L", "ANCQGHILMFPSTWYV");
        AAH.put("R", "KR");
        AAH.put("K", "KR");
        AAH.put("N", "ANCQGHILMFPSTWYV");
        AAH.put("M", "ANCQGHILMFPSTWYV");
        AAH.put("D", "DE");
        AAH.put("F", "ANCQGHILMFPSTWYV");
        AAH.put("C", "ANCQGHILMFPSTWYV");
        AAH.put("P", "ANCQGHILMFPSTWYV");
        AAH.put("Q", "ANCQGHILMFPSTWYV");
        AAH.put("S", "ANCQGHILMFPSTWYV");
        AAH.put("E", "DE");
        AAH.put("T", "ANCQGHILMFPSTWYV");
        AAH.put("G", "ANCQGHILMFPSTWYV");
        AAH.put("W", "ANCQGHILMFPSTWYV");
        AAH.put("H", "ANCQGHILMFPSTWYV");
        AAH.put("Y", "ANCQGHILMFPSTWYV");
        AAH.put("I", "ANCQGHILMFPSTWYV");
        AAH.put("V", "ANCQGHILMFPSTWYV");
        return new ReduceAlphabet("chrg",reduce_alphabet, AAH);
    }

    /**
     * property: Secondary structure Reference: Tomii and Kanehisa, 1996; Li et
     * al., 2006 Reduce class [EALMQKRH], [VIYCWFT], [GNPSD]
     *
     * @return
     */
    public static ReduceAlphabet ra_secondaryStructure_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("EALMQKRH", 0.0);
        reduce_alphabet.put("VIYCWFT", 0.0);
        reduce_alphabet.put("GNPSD", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "EALMQKRH");
        AAH.put("L", "EALMQKRH");
        AAH.put("R", "EALMQKRH");
        AAH.put("K", "EALMQKRH");
        AAH.put("N", "GNPSD");
        AAH.put("M", "EALMQKRH");
        AAH.put("D", "GNPSD");
        AAH.put("F", "VIYCWFT");
        AAH.put("C", "VIYCWFT");
        AAH.put("P", "GNPSD");
        AAH.put("Q", "EALMQKRH");
        AAH.put("S", "GNPSD");
        AAH.put("E", "EALMQKRH");
        AAH.put("T", "VIYCWFT");
        AAH.put("G", "GNPSD");
        AAH.put("W", "VIYCWFT");
        AAH.put("H", "EALMQKRH");
        AAH.put("Y", "VIYCWFT");
        AAH.put("I", "VIYCWFT");
        AAH.put("V", "VIYCWFT");
        return new ReduceAlphabet("ss",reduce_alphabet, AAH);
    }

    /**
     * property: Solvent accessibility Reference: Tomii and Kanehisa, 1996; Li
     * et al., 2006 Reduce class [ALFCGIVW], [RKQEND], [MPSTHY]
     *
     * @return
     */
    public static ReduceAlphabet ra_solventAccessibility_Tomii() {
        Map<String, Double> reduce_alphabet = new HashMap<>(3);
        reduce_alphabet.put("ALFCGIVW", 0.0);
        reduce_alphabet.put("RKQEND", 0.0);
        reduce_alphabet.put("MPSTHY", 0.0);

        Map<String, String> AAH = new HashMap<>(20);
        AAH.put("A", "ALFCGIVW");
        AAH.put("L", "ALFCGIVW");
        AAH.put("R", "RKQEND");
        AAH.put("K", "RKQEND");
        AAH.put("N", "RKQEND");
        AAH.put("M", "MPSTHY");
        AAH.put("D", "RKQEND");
        AAH.put("F", "ALFCGIVW");
        AAH.put("C", "ALFCGIVW");
        AAH.put("P", "MPSTHY");
        AAH.put("Q", "RKQEND");
        AAH.put("S", "MPSTHY");
        AAH.put("E", "RKQEND");
        AAH.put("T", "MPSTHY");
        AAH.put("G", "ALFCGIVW");
        AAH.put("W", "ALFCGIVW");
        AAH.put("H", "MPSTHY");
        AAH.put("Y", "MPSTHY");
        AAH.put("I", "ALFCGIVW");
        AAH.put("V", "ALFCGIVW");
        return new ReduceAlphabet("sa",reduce_alphabet, AAH);
    }

    
    
    
}
