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
public class HydrophobicityScale {

    /**
     * Database: AAindex Entry: KYTJ820101 LinkDB: KYTJ820101 H KYTJ820101 D
     * Hydropathy index (Kyte-Doolittle, 1982) R LIT:0807099 PMID:7108955 A
     * Kyte, J. and Doolittle, R.F. T A simple method for displaying the
     * hydropathic character of a protein J J. Mol. Biol. 157, 105-132 (1982)
     *
     * @return Kyte and Doolittle , R.F.
     */
    public static  Map<String, Double> kyte_doolittle_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 1.8);
        AAH.put("L", 3.8);
        AAH.put("R", -4.5);
        AAH.put("K", -3.9);
        AAH.put("N", -3.5);
        AAH.put("M", 1.9);
        AAH.put("D", -3.5);
        AAH.put("F", 2.8);
        AAH.put("C", 2.5);
        AAH.put("P", -1.6);
        AAH.put("Q", -3.5);
        AAH.put("S", -0.8);
        AAH.put("E", -3.5);
        AAH.put("T", -0.7);
        AAH.put("G", -0.4);
        AAH.put("W", -0.9);
        AAH.put("H", -3.2);
        AAH.put("Y", -1.3);
        AAH.put("I", 4.5);
        AAH.put("V", 4.2);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CIDH920101 LinkDB: CIDH920105
     *
     * H CIDH920101 D Normalized hydrophobicity scales for alpha-proteins (Cid
     * et al., 1992) 1992) R LIT:1817105b PMID:1518784 A Cid, H., Bunster, M.,
     * Canales, M. and Gazitua, F. T Hydrophobicity and structural classes in
     * proteins J Protein Engineering 5, 373-375 (1992)
     *
     * @return Cid_hydrov_hash
     */
    public static Map<String, Double> cid1_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.45);
        AAH.put("L", 1.29);
        AAH.put("R", -0.24);
        AAH.put("K", -0.36);
        AAH.put("N", -0.20);
        AAH.put("M", 1.37);
        AAH.put("D", -1.52);
        AAH.put("F", 1.48);
        AAH.put("C", 0.79);
        AAH.put("P", -0.12);
        AAH.put("Q", -0.99);
        AAH.put("S", -0.98);
        AAH.put("E", -0.80);
        AAH.put("T", -0.70);
        AAH.put("G", -1.00);
        AAH.put("W", 1.38);
        AAH.put("H", 1.07);
        AAH.put("Y", 1.49);
        AAH.put("I", 0.76);
        AAH.put("V", 1.26);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CIDH920102 LinkDB: CIDH920102
     *
     * H CIDH920102 D Normalized hydrophobicity scales for beta-proteins (Cid et
     * al., 1992) 1992) R LIT:1817105b PMID:1518784 A Cid, H., Bunster, M.,
     * Canales, M. and Gazitua, F. T Hydrophobicity and structural classes in
     * proteins J Protein Engineering 5, 373-375 (1992)
     *
     * @return Cid2_hydrov_hash
     */
    public static Map<String, Double> cid2_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.08);
        AAH.put("L", 1.24);
        AAH.put("R", -0.09);
        AAH.put("K", -0.09);
        AAH.put("N", -0.70);
        AAH.put("M", 1.27);
        AAH.put("D", -0.71);
        AAH.put("F", 1.53);
        AAH.put("C", 0.76);
        AAH.put("P", -0.01);
        AAH.put("Q", -0.40);
        AAH.put("S", -0.93);
        AAH.put("E", -1.31);
        AAH.put("T", -0.59);
        AAH.put("G", -0.84);
        AAH.put("W", 2.25);
        AAH.put("H", 0.43);
        AAH.put("Y", 1.53);
        AAH.put("I", 1.39);
        AAH.put("V", 1.09);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CIDH920103 LinkDB: CIDH920103
     *
     * H CIDH920103 D Normalized hydrophobicity scales for alpha+beta-proteins
     * (Cid et al., 1992) 1992) R LIT:1817105b PMID:1518784 A Cid, H., Bunster,
     * M., Canales, M. and Gazitua, F. T Hydrophobicity and structural classes
     * in proteins J Protein Engineering 5, 373-375 (1992)
     *
     * @return Cid3_hydrov_hash
     */
    public static Map<String, Double> cid3_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.36);
        AAH.put("L", 1.18);
        AAH.put("R", -0.52);
        AAH.put("K", -0.56);
        AAH.put("N", -0.90);
        AAH.put("M", 1.21);
        AAH.put("D", -1.09);
        AAH.put("F", 1.01);
        AAH.put("C", 0.70);
        AAH.put("P", -0.06);
        AAH.put("Q", -1.05);
        AAH.put("S", -0.60);
        AAH.put("E", -0.83);
        AAH.put("T", -1.20);
        AAH.put("G", -0.82);
        AAH.put("W", 1.31);
        AAH.put("H", 0.16);
        AAH.put("Y", 1.05);
        AAH.put("I", 2.17);
        AAH.put("V", 1.21);
        return AAH;
    }

    /**
     *
     * Database: AAindex Entry: CIDH920104 LinkDB: CIDH920104
     *
     * H CIDH920104 D Normalized hydrophobicity scales for alpha/beta-proteins
     * (Cid et al., 1992) R LIT:1817105b PMID:1518784 A Cid, H., Bunster, M.,
     * Canales, M. and Gazitua, F. T Hydrophobicity and structural classes in
     * proteins J Protein Engineering 5, 373-375 (1992)
     *
     * @return CIDH920104
     */
    public static Map<String, Double> cid4_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.17);
        AAH.put("L", 0.96);
        AAH.put("R", -0.70);
        AAH.put("K", -0.62);
        AAH.put("N", -0.90);
        AAH.put("M", 0.60);
        AAH.put("D", -1.05);
        AAH.put("F", 1.29);
        AAH.put("C", 1.24);
        AAH.put("P", -0.21);
        AAH.put("Q", -1.20);
        AAH.put("S", -0.83);
        AAH.put("E", -1.19);
        AAH.put("T", -0.62);
        AAH.put("G", -0.57);
        AAH.put("W", 1.51);
        AAH.put("H", -0.25);
        AAH.put("Y", 0.66);
        AAH.put("I", 2.06);
        AAH.put("V", 1.21);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CIDH920105 LinkDB: CIDH920105
     *
     * H CIDH920105 D Normalized average hydrophobicity scales (Cid et al.,
     * 1992) R LIT:1817105b PMID:1518784 A Cid, H., Bunster, M., Canales, M. and
     * Gazitua, F. T Hydrophobicity and structural classes in proteins J Protein
     * Engineering 5, 373-375 (1992)
     *
     * @return Cid_hydrov_hash
     */
    public static Map<String, Double> cid5_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.02);
        AAH.put("L", 1.14);
        AAH.put("R", -0.42);
        AAH.put("K", -0.41);
        AAH.put("N", -0.77);
        AAH.put("M", 1.00);
        AAH.put("D", -1.04);
        AAH.put("F", 1.35);
        AAH.put("C", 0.77);
        AAH.put("P", -0.09);
        AAH.put("Q", -1.10);
        AAH.put("S", -0.97);
        AAH.put("E", -1.14);
        AAH.put("T", -0.77);
        AAH.put("G", -0.80);
        AAH.put("W", 1.71);
        AAH.put("H", 0.26);
        AAH.put("Y", 1.11);
        AAH.put("I", 1.81);
        AAH.put("V", 1.13);
        return AAH;
    }

    /**
     *
     * Database: AAindex Entry: EISD840101 LinkDB: EISD840101
     *
     * H EISD840101 D Consensus normalized hydrophobicity scale (Eisenberg,
     * 1984) R LIT:2004004a PMID:6383201 A Eisenberg, D. T Three-dimensional
     * structure of membrane and surface proteins J Ann. Rev. Biochem. 53,
     * 595-623 (1984) Original references: Eisenberg, D., Weiss, R.M.,
     * Terwilliger, T.C. and Wilcox, W. Faraday Symp. Chem. Soc. 17, 109-120
     * (1982) Eisenberg, D., Weiss, R.M. and Terwilliger, T.C. The hydrophobic
     * moment detects periodicity in protein hydrophobicity Proc. Natl. Acad.
     * Sci. USA 81, 140-144 (1984)
     *
     * @return eisenberg_hydrov_hash
     */
    public static Map<String, Double> eisenberg_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.25);
        AAH.put("L", 0.53);
        AAH.put("R", -1.76);
        AAH.put("K", -1.10);
        AAH.put("N", -0.64);
        AAH.put("M", 0.26);
        AAH.put("D", -0.72);
        AAH.put("F", 0.61);
        AAH.put("C", 0.04);
        AAH.put("P", -0.07);
        AAH.put("Q", -0.69);
        AAH.put("S", -0.26);
        AAH.put("E", -0.62);
        AAH.put("T", -0.18);
        AAH.put("G", 0.16);
        AAH.put("W", 0.37);
        AAH.put("H", -0.40);
        AAH.put("Y", 0.02);
        AAH.put("I", 0.73);
        AAH.put("V", 0.54);

        return AAH;
    }
    
    public static Map<String, Double> normalized_eisenberg_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A",  0.62);
        AAH.put("L",  1.10);
        AAH.put("R", -2.50);
        AAH.put("K", -1.50);
        AAH.put("N", -0.78);
        AAH.put("M",  0.64);
        AAH.put("D", -0.90);
        AAH.put("F",  1.20);
        AAH.put("C",  0.29);
        AAH.put("P",  0.12);
        AAH.put("Q", -0.85);
        AAH.put("S", -0.18);
        AAH.put("E", -0.74);
        AAH.put("T", -0.05);
        AAH.put("G",  0.48);
        AAH.put("W",  0.81);
        AAH.put("H", -0.40);
        AAH.put("Y",  0.26);
        AAH.put("I",  1.40);
        AAH.put("V",  1.10);

        return AAH;
    }

    /**
     *
     * Database: AAindex Entry: GOLD730101 LinkDB: GOLD73010 H GOLD730101 D
     * Hydrophobicity factor (Goldsack-Chalifoux, 1973) R LIT:2004110b
     * PMID:4354159 A Goldsack, D.E. and Chalifoux, R.C. T Contribution of the
     * free energy of mixing of hydrophobic side chains to the stability of the
     * tertiary structure J J. Theor. Biol. 39, 645-651 (1973) (Asn Gln !)
     *
     * @return
     */
    public  static Map<String, Double> golsack_Chalifoux_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.75);
        AAH.put("L", 2.40);
        AAH.put("R", 0.75);
        AAH.put("K", 1.50);
        AAH.put("N", 0.69);
        AAH.put("M", 1.30);
        AAH.put("D", 0.00);
        AAH.put("F", 2.65);
        AAH.put("C", 1.00);
        AAH.put("P", 2.60);
        AAH.put("Q", 0.59);
        AAH.put("S", 0.00);
        AAH.put("E", 0.00);
        AAH.put("T", 0.45);
        AAH.put("G", 0.00);
        AAH.put("W", 3.00);
        AAH.put("H", 0.00);
        AAH.put("Y", 2.85);
        AAH.put("I", 2.95);
        AAH.put("V", 1.70);
        return AAH;
    }

    /**
     *
     * Database: AAindex Entry: JOND750101 LinkDB: JOND750101 H JOND750101 D
     * Hydrophobicity (Jones, 1975) R PMID:1127956 A Jones, D.D. T Amino acid
     * properties and side-chain orientation in proteins: A cross correlation
     * approach J J. Theor. Biol. 50, 167-183 (1975)
     *
     * @return jones_hydrov_hash
     */
    public static Map<String, Double> jones_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.87);
        AAH.put("L", 2.17);
        AAH.put("R", 0.85);
        AAH.put("K", 1.64);
        AAH.put("N", 0.09);
        AAH.put("M", 1.67);
        AAH.put("D", 0.66);
        AAH.put("F", 2.87);
        AAH.put("C", 1.52);
        AAH.put("P", 2.77);
        AAH.put("Q", 0.00);
        AAH.put("S", 0.07);
        AAH.put("E", 0.67);
        AAH.put("T", 0.07);
        AAH.put("G", 0.10);
        AAH.put("W", 3.77);
        AAH.put("H", 0.87);
        AAH.put("Y", 2.67);
        AAH.put("I", 3.15);
        AAH.put("V", 1.87);
        return AAH;
    }

    /**
     * Database: AAindex Entry: MANP780101 LinkDB: MANP780101 H MANP780101 D
     * Average surrounding hydrophobicity (Manavalan-Ponnuswamy, 1978) R
     * LIT:0411088 PMID:703834 A Manavalan, P. and Ponnuswamy, P.K. T
     * Hydrophobic character of amino acid residues in globular proteins
     *
     * @return manavalan_Ponnuswamy_hydrov_hash
     */
    public static Map<String, Double> manavalan_Ponnuswamy_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 12.97);
        AAH.put("L", 14.90);
        AAH.put("R", 11.72);
        AAH.put("K", 11.36);
        AAH.put("N", 11.42);
        AAH.put("M", 14.39);
        AAH.put("D", 10.85);
        AAH.put("F", 14.00);
        AAH.put("C", 14.63);
        AAH.put("P", 11.37);
        AAH.put("Q", 11.76);
        AAH.put("S", 11.23);
        AAH.put("E", 11.89);
        AAH.put("T", 11.69);
        AAH.put("G", 12.43);
        AAH.put("W", 13.93);
        AAH.put("H", 12.16);
        AAH.put("Y", 13.42);
        AAH.put("I", 15.67);
        AAH.put("V", 15.71);
        return AAH;
    }

    /**
     * H PONP800101 D Surrounding hydrophobicity in folded form (Ponnuswamy et
     * al., 1980) R LIT:0608056 PMID:7397216 A Ponnuswamy, P.K., Prabhakaran, M.
     * and Manavalan, P. T Hydrophobic packing and spatial arrangement of amino
     * acid residues in globular proteins J Biochim. Biophys. Acta 623, 301-316
     * (1980)
     */
    public static Map<String, Double> Ponnuswamy1_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 12.28);
        AAH.put("L", 14.10);
        AAH.put("R", 11.49);
        AAH.put("K", 10.80);
        AAH.put("N", 11.00);
        AAH.put("M", 14.33);
        AAH.put("D", 10.97);
        AAH.put("F", 13.43);
        AAH.put("C", 14.93);
        AAH.put("P", 11.19);
        AAH.put("Q", 11.28);
        AAH.put("S", 11.26);
        AAH.put("E", 11.19);
        AAH.put("T", 11.65);
        AAH.put("G", 12.01);
        AAH.put("W", 12.95);
        AAH.put("H", 12.84);
        AAH.put("Y", 13.29);
        AAH.put("I", 14.77);
        AAH.put("V", 15.07);
        return AAH;
    }

    /**
     * H PONP800104 D Surrounding hydrophobicity in alpha-helix (Ponnuswamy et
     * al., 1980) R LIT:0608056 PMID:7397216 A Ponnuswamy, P.K., Prabhakaran, M.
     * and Manavalan, P. T Hydrophobic packing and spatial arrangement of amino
     * acid residues in globular proteins J Biochim. Biophys. Acta 623, 301-316
     * (1980)
     */
    public static Map<String, Double> Ponnuswamy4_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 13.65);
        AAH.put("L", 14.01);
        AAH.put("R", 11.28);
        AAH.put("K", 11.96);
        AAH.put("N", 12.24);
        AAH.put("M", 13.40);
        AAH.put("D", 10.98);
        AAH.put("F", 14.08);
        AAH.put("C", 14.49);
        AAH.put("P", 11.51);
        AAH.put("Q", 11.30);
        AAH.put("S", 11.26);
        AAH.put("E", 12.55);
        AAH.put("T", 13.00);
        AAH.put("G", 15.36);
        AAH.put("W", 12.06);
        AAH.put("H", 11.59);
        AAH.put("Y", 12.64);
        AAH.put("I", 14.63);
        AAH.put("V", 12.88);
        return AAH;
    }

    /**
     * H PONP800105 D Surrounding hydrophobicity in beta-sheet (Ponnuswamy et
     * al., 1980) R LIT:0608056 PMID:7397216 A Ponnuswamy, P.K., Prabhakaran, M.
     * and Manavalan, P. T Hydrophobic packing and spatial arrangement of amino
     * acid residues in globular proteins J Biochim. Biophys. Acta 623, 301-316
     * (1980)
     */
    public static Map<String, Double> Ponnuswamy5_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 14.60);
        AAH.put("L", 16.49);
        AAH.put("R", 13.24);
        AAH.put("K", 13.28);
        AAH.put("N", 11.79);
        AAH.put("M", 16.23);
        AAH.put("D", 13.78);
        AAH.put("F", 14.18);
        AAH.put("C", 15.90);
        AAH.put("P", 14.10);
        AAH.put("Q", 12.02);
        AAH.put("S", 13.36);
        AAH.put("E", 13.59);
        AAH.put("T", 14.50);
        AAH.put("G", 14.18);
        AAH.put("W", 13.90);
        AAH.put("H", 15.35);
        AAH.put("Y", 14.76);
        AAH.put("I", 14.10);
        AAH.put("V", 16.30);
        return AAH;
    }

    /**
     * H PONP800106 D Surrounding hydrophobicity in turn (Ponnuswamy et al.,
     * 1980) R LIT:0608056 PMID:7397216 A Ponnuswamy, P.K., Prabhakaran, M. and
     * Manavalan, P. T Hydrophobic packing and spatial arrangement of amino acid
     * residues in globular proteins J Biochim. Biophys. Acta 623, 301-316
     * (1980)
     */
    public static Map<String, Double> Ponnuswamy6_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 10.67);
        AAH.put("L", 13.07);
        AAH.put("R", 11.05);
        AAH.put("K", 09.93);
        AAH.put("N", 10.85);
        AAH.put("M", 15.00);
        AAH.put("D", 10.21);
        AAH.put("F", 13.27);
        AAH.put("C", 14.15);
        AAH.put("P", 10.62);
        AAH.put("Q", 11.71);
        AAH.put("S", 11.18);
        AAH.put("E", 11.71);
        AAH.put("T", 10.53);
        AAH.put("G", 10.95);
        AAH.put("W", 11.41);
        AAH.put("H", 12.07);
        AAH.put("Y", 11.52);
        AAH.put("I", 12.95);
        AAH.put("V", 13.86);
        return AAH;
    }

    /**
     * Database: AAindex Entry: PRAM900101 LinkDB: PRAM900101 H PRAM900101 D
     * Hydrophobicity (Prabhakaran, 1990) R LIT:1614053b PMID:2390062 A
     * Prabhakaran, M. T The distribution of physical, chemical and
     * conformational properties in signal and nascent peptides J Biochem. J.
     * 269, 691-696 (1990) Original references: Engelman, D.M., Steitz, T.A. and
     * Terwilliger, T.C. Annu. Rev. Biophys. Chem. 15, 321-353 (1986)
     */
    public static Map<String, Double> prabhakaran_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -06.70);
        AAH.put("L", -11.70);
        AAH.put("R", 51.50);
        AAH.put("K", 36.80);
        AAH.put("N", 20.10);
        AAH.put("M", -14.20);
        AAH.put("D", 38.50);
        AAH.put("F", -15.50);
        AAH.put("C", -08.40);
        AAH.put("P", 00.80);
        AAH.put("Q", 17.20);
        AAH.put("S", -02.50);
        AAH.put("E", 34.30);
        AAH.put("T", -05.00);
        AAH.put("G", -04.20);
        AAH.put("W", -07.90);
        AAH.put("H", 12.60);
        AAH.put("Y", 2.90);
        AAH.put("I", -13.00);
        AAH.put("V", -10.90);
        return AAH;
    }

    /**
     * Database: AAindex Entry: SWER830101 LinkDB: SWER830101 All links * H
     * SWER830101 D Optimal matching hydrophobicity (Sweet-Eisenberg, 1983) R
     * LIT:2004095b PMID:6663622 A Sweet, R.M. and Eisenberg, D. T Correlation
     * of sequence hydrophobicities measures similarity in three-dimensional
     * protein structure J J. Mol. Biol. 171, 479-488 (1983)
     *
     */
    public static Map<String, Double> sweet_Eisenberg_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.40);
        AAH.put("L", 1.22);
        AAH.put("R", -0.59);
        AAH.put("K", -0.67);
        AAH.put("N", -0.92);
        AAH.put("M", 1.02);
        AAH.put("D", -1.31);
        AAH.put("F", 1.92);
        AAH.put("C", 0.17);
        AAH.put("P", -0.49);
        AAH.put("Q", -0.91);
        AAH.put("S", -0.55);
        AAH.put("E", -1.22);
        AAH.put("T", -0.28);
        AAH.put("G", -0.67);
        AAH.put("W", 0.50);
        AAH.put("H", -0.64);
        AAH.put("Y", 1.67);
        AAH.put("I", 1.25);
        AAH.put("V", 0.91);
        return AAH;
    }

    /**
     *
     * Database: AAindex Entry: ZIMJ680101 LinkDB: ZIMJ680101
     *
     * H ZIMJ680101 D Hydrophobicity (Zimmerman et al., 1968) R LIT:2004109b
     * PMID:5700434 A Zimmerman, J.M., Eliezer, N. and Simha, R. T The
     * characterization of amino acid sequences in proteins by statistical
     * methods J J. Theor. Biol. 21, 170-201 (1968)
     *
     * @return zimmerman_hydro
     */
    public static Map<String, Double> zimmerman_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.83);
        AAH.put("L", 2.52);
        AAH.put("R", 0.83);
        AAH.put("K", 1.60);
        AAH.put("N", 0.09);
        AAH.put("M", 1.40);
        AAH.put("D", 0.64);
        AAH.put("F", 2.75);
        AAH.put("C", 1.48);
        AAH.put("P", 2.70);
        AAH.put("Q", 0.00);
        AAH.put("S", 0.14);
        AAH.put("E", 0.65);
        AAH.put("T", 0.54);
        AAH.put("G", 0.10);
        AAH.put("W", 0.31);
        AAH.put("H", 1.10);
        AAH.put("Y", 2.97);
        AAH.put("I", 3.07);
        AAH.put("V", 1.79);
        return AAH;
    }

    /**
     * Database: AAindex Entry: JURD980101 LinkDB: JURD980101
     *
     *
     * H JURD980101 D Modified Kyte-Doolittle hydrophobicity scale (Juretic et
     * al., 1998) R A Juretic, D., Lucic, B., Zucic, D. and Trinajstic, N. T
     * Protein transmembrane structure: recognition and prediction by using
     * hydrophobicity scales through preference functions J Theoretical and
     * Computational Chemistry, 5, 405-445 (1998)
     */
    public static Map<String, Double> juretic_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 1.10);
        AAH.put("L", 3.80);
        AAH.put("R", -5.10);
        AAH.put("K", -4.11);
        AAH.put("N", -3.50);
        AAH.put("M", 1.90);
        AAH.put("D", -3.60);
        AAH.put("F", 2.80);
        AAH.put("C", 2.50);
        AAH.put("P", -1.90);
        AAH.put("Q", -3.68);
        AAH.put("S", -0.50);
        AAH.put("E", -3.20);
        AAH.put("T", -0.70);
        AAH.put("G", -0.64);
        AAH.put("W", -0.46);
        AAH.put("H", -3.20);
        AAH.put("Y", -1.30);
        AAH.put("I", 4.50);
        AAH.put("V", 4.20);
        return AAH;
    }

    /**
     * Database: AAindex Entry: WOLR790101 LinkDB: WOLR790101 H WOLR790101 D
     * Hydrophobicity index (Wolfenden et al., 1979) R PMID:493962 A Wolfenden,
     * R.V., Cullis, P.M. and Southgate, C.C.F. T Water, protein folding, and
     * the genetic code J Science 206, 575-577 (1979)
     *
     * @return wolfenden_hydrov_hash
     */
    public static Map<String, Double> wolfenden_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 1.12);
        AAH.put("L", 1.18);
        AAH.put("R", -2.55);
        AAH.put("K", -0.80);
        AAH.put("N", -0.83);
        AAH.put("M", 0.55);
        AAH.put("D", -0.83);
        AAH.put("F", 0.67);
        AAH.put("C", 0.59);
        AAH.put("P", 0.54);
        AAH.put("Q", -0.78);
        AAH.put("S", -0.05);
        AAH.put("E", -0.92);
        AAH.put("T", -0.02);
        AAH.put("G", 1.20);
        AAH.put("W", -0.19);
        AAH.put("H", -0.93);
        AAH.put("Y", -0.23);
        AAH.put("I", 1.16);
        AAH.put("V", 1.13);
        return AAH;
    }

    /**
     * Database: AAindex Entry: KIDA850101 LinkDB: KIDA850101 H KIDA850101 D
     * Hydrophobicity-related index (Kidera et al., 1985) R * A Kidera, A.,
     * Konishi, Y., Oka, M., Ooi, T. and Scheraga, A. T Statistical Analysis of
     * the Physical Properties of the 20 Naturally Occuring Amino Acids J J.
     * Prot. Chem. 4, 23-55 (1985)
     *
     * @return
     */
    public static Map<String, Double> kidera_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.27);
        AAH.put("L", -1.10);
        AAH.put("R", 1.87);
        AAH.put("K", 1.70);
        AAH.put("N", 0.81);
        AAH.put("M", -0.73);
        AAH.put("D", 0.81);
        AAH.put("F", -1.43);
        AAH.put("C", -1.05);
        AAH.put("P", -0.75);
        AAH.put("Q", 1.10);
        AAH.put("S", 0.42);
        AAH.put("E", 1.17);
        AAH.put("T", 0.63);
        AAH.put("G", -0.16);
        AAH.put("W", -1.57);
        AAH.put("H", 0.28);
        AAH.put("Y", -0.56);
        AAH.put("I", -0.77);
        AAH.put("V", -0.40);
        return AAH;
    }

    /**
     * Database: AAindex Entry: CASG920101 LinkDB: CASG920101 H CASG920101 D
     * Hydrophobicity scale from native protein structures (Casari-Sippl, 1992)
     * R PMID:1569551 A Casari, G. and Sippl, M. T Structure-derived Hydrophobic
     * Potential. Hydrophobic Potential Derived from X-ray Structures of
     * Globular Proteins is able to Identify Native Folds J J. Mol. Biol. 224,
     * 725-732 (1992)
     *
     * @return
     */
    public static  Map<String, Double> casari_Sippl_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.20);
        AAH.put("L", 0.50);
        AAH.put("R", -0.70);
        AAH.put("K", -1.60);
        AAH.put("N", -0.50);
        AAH.put("M", 0.50);
        AAH.put("D", -1.40);
        AAH.put("F", 1.00);
        AAH.put("C", 1.90);
        AAH.put("P", -1.00);
        AAH.put("Q", -1.10);
        AAH.put("S", -0.70);
        AAH.put("E", -1.30);
        AAH.put("T", -0.40);
        AAH.put("G", -0.10);
        AAH.put("W", 1.60);
        AAH.put("H", 0.40);
        AAH.put("Y", 0.50);
        AAH.put("I", 1.40);
        AAH.put("V", 0.70);
        return AAH;
    }

    /**
     * Database: AAindex Entry: ENGD860101 LinkDB: ENGD860101 H ENGD860101 D
     * Hydrophobicity index (Engelman et al., 1986) R PMID:3521657 A Engelman,
     * D.M., Steitz, T.A. and Goldman, A. T Identifying Nonpolar Transbilayer
     * Helices in Amino Acid Sequences of Membrane Proteins J
     * Ann.Rev.Biophys.Biophys.Chem. 15, 321-353 (1986)
     *
     * @return engleman_hydrov_hash
     */
    public static  Map<String, Double> engleman_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -1.60);
        AAH.put("L", -2.80);
        AAH.put("R", 12.30);
        AAH.put("K", 8.80);
        AAH.put("N", 4.80);
        AAH.put("M", -3.40);
        AAH.put("D", 9.20);
        AAH.put("F", -3.70);
        AAH.put("C", -2.00);
        AAH.put("P", 0.20);
        AAH.put("Q", 4.10);
        AAH.put("S", -0.60);
        AAH.put("E", 8.20);
        AAH.put("T", -1.20);
        AAH.put("G", -1.00);
        AAH.put("W", -1.90);
        AAH.put("H", 3.00);
        AAH.put("Y", 0.70);
        AAH.put("I", -3.10);
        AAH.put("V", -2.60);
        return AAH;
    }

    /**
     * Database: AAindex Entry: FASG890101 LinkDB: FASG890101
     *
     * H FASG890101 D Hydrophobicity index (Fasman, 1989) R A Fasman, G.D. T
     * Prediction of Protein Structure and the Principles of Protein
     * Conformation J Plenum, New York 1989, page 457, Table XVII
     *
     * @return fasman_hydrov_hash
     */
    public static Map<String, Double> fasman_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -0.21);
        AAH.put("L", -4.68);
        AAH.put("R", 2.11);
        AAH.put("K", 3.88);
        AAH.put("N", 0.96);
        AAH.put("M", -3.66);
        AAH.put("D", 1.36);
        AAH.put("F", -4.65);
        AAH.put("C", -6.04);
        AAH.put("P", 0.75);
        AAH.put("Q", 1.52);
        AAH.put("S", 1.74);
        AAH.put("E", 2.30);
        AAH.put("T", 0.78);
        AAH.put("G", 0.00);
        AAH.put("W", -3.32);
        AAH.put("H", -1.23);
        AAH.put("Y", -1.01);
        AAH.put("I", -4.81);
        AAH.put("V", -3.50);
        return AAH;
    }

    /**
     * Tossi et al. (2012) new consensus hydrophobicity scale extended to non-
     * proteinogenic amino acids. Peptides 2002. ettore Benedetti and Carlos
     * pedone
     *
     * @return tossi_hydrov_hash
     */
    public static Map<String, Double> tossi_hydrov_hash() {
        Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", -1.1);
        AAH.put("L", 9.7);
        AAH.put("R", -10.0);
        AAH.put("K", -9.9);
        AAH.put("N", -7.1);
        AAH.put("M", 4.6);
        AAH.put("D", -8.3);
        AAH.put("F", 10.00);
        AAH.put("C", -2.3);
        AAH.put("P", -0.20);
        AAH.put("Q", -6.0);
        AAH.put("S", -4.3);
        AAH.put("E", -8.3);
        AAH.put("T", -3.8);
        AAH.put("G", -2.4);
        AAH.put("W", 9.7);
        AAH.put("H", -3.8);
        AAH.put("Y", 2.5);
        AAH.put("I", 8.7);
        AAH.put("V", 4.1);
        return AAH;
    }
    
      public static Map<String, Double> hydro_NOZY(){
        
         Map<String, Double> AAH = new HashMap<>();
        AAH.put("A", 0.5);
        AAH.put("L", 1.8);
        AAH.put("R", 0.0);
        AAH.put("K", 0.0);
        AAH.put("N", 0.0);
        AAH.put("M", 1.3);
        AAH.put("D", 0.0);
        AAH.put("F", 2.5);
        AAH.put("C", 0.0);
        AAH.put("P", 0.0);
        AAH.put("Q", 0.0);
        AAH.put("S", 0.0);
        AAH.put("E", 0.0);
        AAH.put("T", 0.4);
        AAH.put("G", 0.0);
        AAH.put("W", 3.4);
        AAH.put("H", 0.5);
        AAH.put("Y", 2.3);
        AAH.put("I", 1.8);
        AAH.put("V", 1.5);
    return AAH;
    }

}
