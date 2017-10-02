/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bapedis.modamp.scales.ReduceAlphabet;
import org.bapedis.modamp.scales.ReducedAlphabets;
import org.bapedis.modamp.scales.otherScales;
import org.bapedis.modamp.scales.PkaValues;
import org.bapedis.modamp.scales.HydrophobicityScale;

/**
 *
 * @author beltran
 */
public class MD {
    
    private static final String ALPHABET = "ACDEFGHIKLMNPQRSTVWY";
    private static final double GRP[][] = {
        {1.0, 44.94, -7.49, 1.0, 1.0, 1.0, -7.49, 1.0, 1.0, 1.0, 1.0, 1.0, 20.26, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
        {1.0, 1.0, 20.26, 1.0, 1.0, 1.0, 33.6, 1.0, 1.0, 20.26, 33.6, 1.0, 20.26, -6.54, 1.0, 1.0, 33.6, -6.54, 24.68, 1.0},
        {1.0, 1.0, 1.0, 1.0, -6.54, 1.0, 1.0, 1.0, -7.49, 1.0, 1.0, 1.0, 1.0, 1.0, -6.54, 20.26, -14.03, 1.0, 1.0, 1.0},
        {1.0, 44.94, 20.26, 33.6, 1.0, 1.0, -6.54, 20.26, 1.0, 1.0, 1.0, 1.0, 20.26, 20.26, 1.0, 20.26, 1.0, 1.0, -14.03, 1.0},
        {1.0, 1.0, 13.34, 1.0, 1.0, 1.0, 1.0, 1.0, -14.03, 1.0, 1.0, 1.0, 20.26, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 33.6},
        {-7.49, 1.0, 1.0, -6.54, 1.0, 13.34, 1.0, -7.49, -7.49, 1.0, 1.0, -7.49, 1.0, 1.0, 1.0, 1.0, -7.49, 1.0, 13.34, -7.49},
        {1.0, 1.0, 1.0, 1.0, -9.37, -9.37, 1.0, 44.94, 24.68, 1.0, 1.0, 24.68, -1.88, 1.0, 1.0, 1.0, -6.54, 1.0, -1.88, 44.94},
        {1.0, 1.0, 1.0, 44.94, 1.0, 1.0, 13.34, 1.0, -7.49, 20.26, 1.0, 1.0, -1.88, 1.0, 1.0, 1.0, 1.0, -7.49, 1.0, 1.0},
        {1.0, 1.0, 1.0, 1.0, 1.0, -7.49, 1.0, -7.49, 1.0, -7.49, 33.6, 1.0, -6.54, 24.68, 33.6, 1.0, 1.0, -7.49, 1.0, 1.0},
        {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -7.49, 1.0, 1.0, 1.0, 20.26, 33.6, 20.26, 1.0, 1.0, 1.0, 24.68, 1.0},
        {13.34, 1.0, 1.0, 1.0, 1.0, 1.0, 58.28, 1.0, 1.0, 1.0, -1.88, 1.0, 44.94, -6.54, -6.54, 44.94, -1.88, 1.0, 1.0, 24.68},
        {1.0, -1.88, 1.0, 1.0, -14.03, -14.03, 1.0, 44.94, 24.68, 1.0, 1.0, 1.0, -1.88, -6.54, 1.0, 1.0, -7.49, 1.0, -9.37, 1.0},
        {20.26, -6.54, -6.54, 18.38, 20.26, 1.0, 1.0, 1.0, 1.0, 1.0, -6.54, 1.0, 20.26, 20.26, -6.54, 20.26, 1.0, 20.26, -1.88, 1.0},
        {1.0, -6.54, 20.26, 20.26, -6.54, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 20.26, 20.26, 1.0, 44.94, 1.0, -6.54, 1.0, -6.54},
        {1.0, 1.0, 1.0, 1.0, 1.0, -7.49, 20.26, 1.0, 1.0, 1.0, 1.0, 13.34, 20.26, 20.26, 58.28, 44.94, 1.0, 1.0, 58.28, -6.54},
        {1.0, 33.6, 1.0, 20.26, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 44.94, 20.26, 20.26, 20.26, 1.0, 1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0, 20.26, 13.34, -7.49, 1.0, 1.0, 1.0, 1.0, 1.0, -14.03, 1.0, -6.54, 1.0, 1.0, 1.0, 1.0, -14.03, 1.0},
        {1.0, 1.0, -14.03, 1.0, 1.0, -7.49, 1.0, 1.0, -1.88, 1.0, 1.0, 1.0, 20.26, 1.0, 1.0, 1.0, -7.49, 1.0, 1.0, -6.54},
        {-14.03, 1.0, 1.0, 1.0, 1.0, -9.37, 24.68, 1.0, 1.0, 13.34, 24.68, 13.34, 1.0, 1.0, 1.0, 1.0, -14.03, -7.49, 1.0, 1.0},
        {24.68, 1.0, 26.68, -6.54, 1.0, -7.49, 13.34, 1.0, 1.0, 1.0, 44.94, 1.0, 13.34, 1.0, -15.91, 1.0, -7.49, 1.0, -9.37, 13.34}};

    /**
     * Compute the molecular weight of a peptide sequences, the molecular weight
     * M of a peptide may be estimated by calculating M = M_N + Mc + \sum_i N_i
     * M_i where Ni are the number of aminoacids, and Mi average reside
     * molecular weight, of aminoacids. M_N + M_C are added to the total in
     * odrder to account for the termini: H at the N-terminus and OH at the
     * c-terminus.
     *
     * we used as a reference point the results obtained by ExPASy pI/Mw tool.
     *
     * @param seq sequences of amino acid residues
     * @return Molecular weight (real number) given in Dalton (DA)
     */
    public static double mw(String seq) {
        Map<String, Double> aa_hash = otherScales.molecularWeight();
        double molW = 0.0;
        
        for (int i = 0; i < seq.length(); i++) {
            molW += aa_hash.getOrDefault(seq.subSequence(i, i + 1), 0.0);
        }
        
        double H = 1.00797;
        double OH = 17.00738;
        return H + molW + OH;
    }

    /**
     * Compute the net charge Z of a peptide sequences at a certain pH and a
     * default pka scale (IPC_Protein) Note: The resulting net charge depends on
     * what pKa values the algorithm uses.
     *
     * the net charge can be calculated as a follow \sum_{ai\in positive charge
     * residues} N_{ai} * \fracc{1}{1 + 10^{pH-pka_i}} + \sum_{aj \in negative
     * charge residues} N_{aj} * \fracc{-1}{1+ 10^{pka_j - pH}}
     *
     * where pka_i and pka_j is the acid dissociation constant of positively and
     * negatively charge amino acids, respectively. positive charge residues and
     * group are R, H, k and N'terminus. Negative charge residues and group are
     * Y, D , E ,C , C'terminus.
     *
     * @param seq peptide sequences
     * @param pH
     * @param pKscale pKaValues.EMBOSS(), pkaValues.Bjellqvist(),
     * pkaValues.IPC_protein(), pkaValues.IPC_peptide().
     * @return netcharge
     */
    public static double netCharge(String seq, double pH, Map<String, Double> pKscale) {
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
        
        return pos + neg;
    }

    /**
     * Compute the net charge Z of a peptide sequences at neutral pH (7.0) In
     * order to verify the calculations, we used as a reference point the
     * following web pages http://pepcalc.com/ (INNOVAGEN)
     * http://www.bachem.com/service-support/peptide-calculator/
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @return netCharge
     */
    public static double netCharge(String seq) {
        return netCharge(seq, 7.0, PkaValues.Lehninger());
    }

    /**
     * Compute the isoelectric point of a peptide sequences. Isoelectric point
     * (pI) is a pH in which net charge of protein is zero. References web
     * pages: http://isoelectric.ovh.org/theory.html
     * http://web.expasy.org/compute_pi/ (note: the ExPASy web page used the
     * Bjellqvist pkaScale)
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @param pKscale pKaValues.EMBOSS(), pkaValues.Bjellqvist(),
     * @return
     */
    public static double isoelectricPoint(String seq, Map<String, Double> pKscale) {
        double z = 0;
        double pH = 7;
        double pHprev = 0.0;
        double pHnext = 14.0;
        double E = 0.0001;
        double temp = 0.0;
        
        while (true) {
            z = netCharge(seq, pH, pKscale); //pkA_EMBOSS()
            if (pH >= 14) {
                System.out.println("Error");
                break;
            }
            if (z < 0) {
                temp = pH;
                pH -= (pH - pHprev) / 2;
                pHnext = temp;
            } else {
                temp = pH;
                pH += (pHnext - pH) / 2;
                pHprev = temp;
            }
            
            if ((pH - pHprev < E) && (pHnext - pH < E)) //terminal condition, finding isoelectric point with given precision
            {
                break;
            }
        }
        
        return pH;
    }

    /**
     * Compute the isoelectric point of a peptide sequences at deafult pk scale
     * (IPC_protein) http://isoelectric.ovh.org/theory.html
     *
     * @param seq
     * @return
     */
    public static double isoelectricPoint(String seq) {
        return isoelectricPoint(seq, PkaValues.IPC_protein());
    }

    /**
     * Number of amino acid residues
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @return number of amino acid residues
     */
    public static int length(String seq) {
        return seq.length();
    }

    /**
     * Boman index is the sum of the free energies of the respective side chains
     * for transfer from cyclohexane to water taken from Radzeka and Wolfenden
     * and divided by the total number of the residues of an antimicrobial
     * peptide. The calculated values are negative ( except for the hybrid
     * peptide), but the + and - are reversed. A more hydrophobic peptide tends
     * to have a negative index, while a more hydrophilic peptide tends to have
     * a more positive index [Boman, H.G.(2003) J.Inter.Med.254:197-215].
     * references web pages: http://aps.unmc.edu/AP/glossary_wang.php
     *
     * This function computes the potential protein interaction index proposed
     * by Boman (2003) based in the amino acid sequence of a protein. The index
     * is equal to the sum of the solubility values for all residues in a
     * sequence, it might give an overall estimate of the potential of a peptide
     * to bind to membranes or other proteins as receptors, to normalize it is
     * divided by the number of residues. A protein have high binding potential
     * if the index value is higher than 2.48.
     *
     * Reference point: Boman, H. G. (2003). Antibacterial peptides: basic facts
     * and emerging concepts. Journal of internal medicine, 254(3), 197-215.
     *
     * Examples: NAME seq index Human LL-37 (PDB:2K6O)
     * LLGDFFRKSKEKIGKEFKRIVQRIKDFLRNLVPRTES 3.00 Pig PR-39
     * RRRPRPPYLPRPRPPPFFPPRLPPRIPPGFPPRFPPRFP 3.04 Frog Maganing 2
     * GIGKFLHSAKKFGKAFVGEIMNS 0.42
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @return boman index
     */
    public static double boman(String seq) {
        Map<String, Double> BI = otherScales.bomanIndex();
        double bi = 0.0;
        
        for (int i = 0; i < seq.length(); i++) {
            bi += BI.getOrDefault(seq.subSequence(i, i + 1), 0.0);
        }
        
        return -1 * (bi / seq.length());
    }

    /**
     * compute the max averaged hydrophobic moment. The averaged hydrophobic
     * moment are calculated using a rectangular window of size n. The averaged
     * hydrophobic momen is equal to:
     *
     * \fracc{1}{n}{[\sum_{i=1}^n h_i sin(i*\angle)]^2 + [\sum_{i=1}^n h_i
     * cos(i*\angle)]^2 }^{1/2} where i is the position of the amino acid
     * residue a_i in the sequences (Seq)
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @param angle a peptide rotational angle (for alpha-helix and i suggest
     * @param window size of windows Defaullt
     * hydrophobicityScale.eisenberg_hydrov_hash()
     * @return
     */
    public static double hMoment(String seq, int angle, int window) {
        return hMoment(seq, angle, window, HydrophobicityScale.normalized_eisenberg_hydrov_hash());
    }

    /**
     * compute the max averaged hydrophobic moment. The averaged hydrophobic
     * moment are calculated using a rectangular window of size n. The averaged
     * hydrophobic momen is equal to:
     *
     * \fracc{1}{n}{[\sum_{i=1}^n h_i sin(i*\angle)]^2 + [\sum_{i=1}^n h_i
     * cos(i*\angle)]^2 }^{1/2} where i is the position of the amino acid
     * residue a_i in the sequences (Seq)
     *
     * @param seq sequences of amino acids residues, this there are represented
     * in one letter code.
     * @param angle a peptide rotational angle (for alpha-helix and i suggest
     * @param window size of windows
     * @param hydrophobicityScale HydrophobicityScale.
     * kyte_doolittle_hydrov_hash()
     * @return
     */
    public static double hMoment(String seq, int angle, int window, Map<String, Double> hydrophobicityScale) {
        
        double angRad = Math.toRadians(angle);
        double sumHmSin = 0.0;
        double sumHmCos = 0.0;
        double hMMax = Double.NEGATIVE_INFINITY;
        double hM;
        int pos = 0;
        if (window > seq.length()) {
            return -1;
        }
        
        for (int i = 0; i < (seq.length() - window + 1); i++) {
            String subseq = seq.substring(i, window + i);
            
            for (int j = 0; j < subseq.length(); j++) {
                double hv = hydrophobicityScale.getOrDefault(subseq.subSequence(j, j + 1), 0.0);
                sumHmSin += hv * Math.sin(Math.toRadians(angle * (j + i + 1)));
                sumHmCos += hv * Math.cos(Math.toRadians(angle * (j + i + 1)));
                //System.out.println("C="+sumHmCos + "\n" + "S="+sumHmSin);
            }
            
            hM = Math.sqrt(Math.pow(sumHmSin, 2) + Math.pow(sumHmCos, 2)) / window;
            //System.out.println(subseq + "\t" + hM );

            if (hM > hMMax) {
                hMMax = hM;
            }
            hM = 0;
            sumHmSin = 0;
            sumHmCos = 0;
        }
        
        return hMMax;
    }

    /**
     * The Grand average of hydropathicity GRAVY value for a peptide or protein
     * is calculated as the sum of hydropathy values [9] of all the amino acids,
     * divided by the number of residues in the sequence. reference point:
     * http://web.expasy.org/protparam/ Expassy used the default scale
     * kyte_doolittle_hydrov_hash()
     *
     * @param seq
     * @param hydro_scale kuhn_hydrov_hash(), hopp_Woods_hydrov_hash()
     * @return GRAVY
     */
    public static double gravy(String seq, Map<String, Double> hydro_scale) {
        double gravy = 0.0;
        for (int i = 0; i < seq.length(); i++) {
            gravy += hydro_scale.getOrDefault(seq.subSequence(i, i + 1), 0.0); //sum
        }
        gravy /= seq.length();
        return gravy;
    }

    /**
     * The Grand average of hydropathicity GRAVY value for a peptide or protein
     * is calculated as the sum of hydropathy values [9] of all the amino acids,
     * divided by the number of residues in the sequence. reference point:
     * http://web.expasy.org/protparam/ Expassy used the default scale
     * kyte_doolittle_hydrov_hash()
     *
     * @param seq
     * @return GRAVY
     */
    public static double gravy(String seq) {
        return gravy(seq, HydrophobicityScale.kyte_doolittle_hydrov_hash());
    }

    /**
     * compute the max averaged hydrophobicity. The averaged hydrophobicity
     * moment are calculated using a rectangular window of size n. The averaged
     * hydrophobicity can be calculated as a follow:
     *
     * H = \frac{1}{n} \sum_{i=1}^n H_i
     *
     * where n is the windows length and H_i is the hydrophobicity of the i
     * amino acid in the windows.
     *
     * @param seq
     * @param window
     * @param hydrophobicityScale
     * @return
     */
    public static double maxMeanHydrophobicity(String seq, int window, Map<String, Double> hydrophobicityScale) {
        double hmax = Double.NEGATIVE_INFINITY;
        double h;
        if (window > seq.length()) {
            return -1;
        }
        
        for (int i = 0; i < (seq.length() - window + 1); i++) {
            String subseq = seq.substring(i, window + i);
            h = gravy(subseq, hydrophobicityScale);
            // System.out.println(subseq + "\t" + h);
            if (h > hmax) {
                hmax = h;
            }
        }
        return hmax;
    }

    /**
     * compute the max averaged hydrophobicity. The averaged hydrophobicity
     * moment are calculated using a rectangular window of size n. The averaged
     * hydrophobicity can be calculated as a follow:
     *
     * H = \frac{1}{n} \sum_{i=1}^n H_i
     *
     * where n is the windows length and H_i is the hydrophobicity of the i
     * amino acid in the windows.
     *
     * @param seq
     * @param window
     * @return
     */
    public static double maxMeanHydrophobicity(String seq, int window) {
        return maxMeanHydrophobicity(seq, window, HydrophobicityScale.normalized_eisenberg_hydrov_hash());
    }
    
    public static Map<String, Double> aaComposition(String seq) {
        return compositionReducedAlphabet(seq, ReducedAlphabets.stdAminoAcids());
    }

    /**
     * The aliphatic index of a protein is defined as the relative volume
     * occupied by aliphatic side chains (alanine, valine, isoleucine, and
     * leucine). It may be regarded as a positive factor for the increase of
     * thermostability of globular proteins. The aliphatic index of a protein is
     * calculated according to the following formula [8]: Ikai, A.J. (1980)
     * Thermostability and aliphatic index of globular proteins. J. Biochem. 88,
     * 1895-1898. [PubMed: 7462208] Aliphatic index = X(Ala) + a * X(Val) + b*
     * (X(Ile) + X(Leu)) where X(Ala), X(Val), X(Ile), and X(Leu) are mole
     * percent (100 x mole fraction) The coefficients a and b are the relative
     * volumen of valine side chain (a=2.9) and of Leu/Ile side chains (b = 3.9)
     * to the side chain of alanine. String.format(%.2f,input) references point
     * http://web.expasy.org/protparam/
     *
     * @param seq
     * @return aliphaticIndex
     */
    public static double aliphaticIndex(String seq) {
        Map<String, Double> aminoAcidComposition = aaComposition(seq);
        Double ala, val, ile, leu;
        ala = aminoAcidComposition.containsKey("A") ? (double) aminoAcidComposition.get("A") : 0.0;
        val = aminoAcidComposition.containsKey("V") ? (double) aminoAcidComposition.get("V") : 0.0;
        ile = aminoAcidComposition.containsKey("I") ? (double) aminoAcidComposition.get("I") : 0.0;
        leu = aminoAcidComposition.containsKey("L") ? (double) aminoAcidComposition.get("L") : 0.0;
        
        return ala + 2.9 * val + 3.9 * (ile + leu);
    }

    /**
     * The instability index provides and estimate of the stability of yout
     * protein in a test tube Statistical analysis of 12 unstable and 32 stable
     * protein has revealed that there are certain dipeptides the occurence of
     * wich is significantly different in the unstable proteins compared with
     * those in the stables one. The authors of this method have assigned a
     * weight value of instability to each of the 400 different dippetides
     * (DIWV). Using these wight values it is possible to compute an instability
     * indx which is defined as: II = (10/L) "Sum_{i=1}^{i=L-1} DIWV(X[i]
     * X[i+1]) where: L is the length of sequences DIWV(x[i]x[i+1) is the
     * instability weight value for the dipeptide starting in position i A
     * protien whose instabilit index is smaller than 40 is predicted as stable,
     * a value above 40 predicts that the protein may be unstable.
     *
     *
     * @param seq
     * @return
     */
    public static double inestabilityIndex(String seq) {
        //Matrix of conditon-based instability values for 400 possible dipeptides
        double iIndex = 0;
        for (int i = 0; i < seq.length() - 1; i++) {
            int x_i = ALPHABET.indexOf(seq.charAt(i));
            int x_i1 = ALPHABET.indexOf(seq.charAt(i + 1));
            if (x_i != -1 && x_i1 != -1) {
                iIndex += GRP[x_i][x_i1];
            }
        }
        
        return (10.0 / seq.length()) * iIndex;
    }

    /**
     * A protein whose instability index is smaller than 40 is predicted as
     * stable, a value above 40 predicts that the protein may be unstable
     *
     * @param seq
     * @return 1 if the protein is stable: 0 otherwise.
     */
    public static int isProteinStable(String seq) {
        return inestabilityIndex(seq) < 40 ? 1 : 0;
    }
    
    public static int isProteinStable(double inestabilityIndex) {
        return inestabilityIndex < 40 ? 1 : 0;
    }

    /**
     * Periodicity in the occurence of hydrophobic residues A(M) We consider the
     * distribution of hydrophobic residues in a protein sequences of length L
     * and begin by looking for the amplitude of 3.6 residues periodicity within
     * a block of length m m might be the length of a typical \alpha helix.
     * References: Klein et al (1984). Prediction of protein Function from
     * sequences properties
     *
     * @param seq
     * @param windows
     * @return
     */
    public static double A_m(String seq, int windows) {

        /*
            Possible better fix for situations whether the windows is greater than the sequence (division by zero)
         */
        if (seq.length() < windows) {
            return 0;
        }
        
        Map<String, Double> hydro_NOZY = HydrophobicityScale.hydro_NOZY();
        double Am = 0.0;
        double Amk = 0.0;
        double cos = 2 * Math.PI / 3.6;
        for (int i = 0; i < seq.length() - windows + 1; i++) {
            String subseq = seq.substring(i, windows + i);
            double GRAVY = gravy(subseq, hydro_NOZY);
            for (int j = 0; j < subseq.length(); j++) {
                double hi = hydro_NOZY.getOrDefault(subseq.subSequence(j, j + 1), 0.0) - GRAVY;
                Amk += hi * Math.cos(cos * (j));
            }
            Am += Amk;
            Amk = 0.0;
            //then the average amplitude of the periodicity for the entire sequences of length L
            //is defiened as
        }
        
        return (1.0 / seq.length() - windows + 1) * Am;
    }

    /**
     *
     * @param seq
     * @param aa_hash
     * @return A[0]=sum, A[1]=average
     */
    public static double[] sumAndAvg(String seq, Map<String, Double> aa_hash) {
        double[] feature = {0.0, 0.0};
        for (int i = 0; i < seq.length(); i++) {
            feature[0] += aa_hash.getOrDefault(seq.subSequence(i, i + 1), 0.0); //sum
            //feature[1] += aa_hash.get(seq.subSequence(i, i + 1)) / seq.length();
        }
        feature[1] = feature[0] / seq.length();
        return feature;
    }

    /**
     * compute the frequency for each amino acid in the sequences. The
     * composition describe the global composition of a given amino acid
     * property (i.e. Reduce alphabet) in a protein. References: Dubchak, I.,
     * Muchnik, I., Holbrook, S. R., & Kim, S. H. (1995). Prediction of protein
     * folding class using global description of amino acid sequence.
     * Proceedings of the National Academy of Sciences, 92(19), 8700-8704.
     *
     * @param seq
     * @param ra alphabeth
     * @return Map with the frequency for each amino acid.
     */
    public static Map<String, Double> compositionReducedAlphabet(String seq, ReduceAlphabet ra) {
        ra.init();
        Map<String, Double> count = ra.getCount();
        
        for (int i = 0; i < seq.length(); i++) {
            String rClass = ra.getrAlphabet() == null ? seq.substring(i, i + 1) : ra.getrAlphabet().get(seq.substring(i, i + 1));
            if (count.get(rClass) != null) {
                count.replace(rClass, count.get(rClass) + 1);
            }
        }

        /**
         * Calcular el porcentaje de la frecuencia
         */
        Iterator<String> it = count.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Double percent = (count.get(key) / seq.length()) * 100;
            count.replace(key, percent);
        }
        
        return count;
    }

    /**
     * The transition descriptor T characterize the percent frequency with which
     * an element A \in Reduce Alphabet is followed by another B element in
     * Reduce Alphabet or vice-versa. for example:
     *
     * Sequences AB BA B BAB BBB -- -- --- In this case, there are 7 transition,
     * that is (7/11)*100=63%
     *
     *
     * References: Dubchak, I., Muchnik, I., Holbrook, S. R., & Kim, S. H.
     * (1995). Prediction of protein folding class using global description of
     * amino acid sequence. Proceedings of the National Academy of Sciences,
     * 92(19), 8700-8704.
     *
     * @param seq
     * @param ra
     * @return
     */
    public static Map<String, Double> transitionReducedAlphabet(String seq, ReduceAlphabet ra) {
        Map<String, Double> trans = new HashMap<>(); //Trasition from one group to another based on reduced alphabets

        Object[] keys = ra.getCount().keySet().toArray();

        /*
         * Initialize the percent frequency with which a element a \in reduceAlphabets is followed by
         * another element b \in reduceAlphabet such as a !=b.
         */
        for (int i = 0; i < keys.length - 1; i++) {
            String key1 = keys[i].toString();
            for (int j = i; j < keys.length; j++) {
                String key2 = keys[j].toString();
                if (!key1.equalsIgnoreCase(key2)) {
                    String newKey = key1 + "->" + key2;
                    trans.put(newKey, 0.0);
                }
            }
        }


        /*
         *para cada substring de seq s(i,i+1) hacer los siguiente 
         */
        Map<String, String> rAlphabet = ra.getrAlphabet();
        for (int i = 0; i < seq.length() - 1; i++) {
            String key1 = rAlphabet.getOrDefault(seq.subSequence(i, i + 1), "");
            String key2 = rAlphabet.getOrDefault(seq.subSequence(i + 1, i + 2), "");
            if (key1 != null && key2 != null && !key1.equalsIgnoreCase(key2)) {
                String newKey = key1 + "->" + key2;
                if (!trans.containsKey(newKey)) {
                    newKey = key2 + "->" + key1;
                }
                if (trans.containsKey(newKey)) {
                    trans.replace(newKey, trans.get(newKey) + 1);
                }
            }
        }

        /**
         * Calcular el porcentaje de la frecuencia
         */
        Iterator<String> it = trans.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Double percent = (trans.get(key) / (seq.length() - 1)) * 100;
            trans.replace(key, percent);
        }
        
        return trans;
    }

    /**
     * The distribution descriptor D. For a given Reduce Alphabet, the
     * distribution property along the peptide sequences is described by five
     * chain lengths (in percent), wihin which the first, 25%, 50%, 75% and 100%
     * of the amino acids with a certain property are contained (reduceAlphabet)
     * For example Sequences: ABBBABBAB
     *
     * the first residue A coincides with the begining of the sequences, so the
     * first number of D descripor is equal to 0.0.
     *
     * En otras palabras, la distribucion me dice cual donde esta contenido el
     * tanto porciento de una letra del alfabeto en la sequences
     *
     * @param seq
     * @param ra
     * @param percent
     * @return
     */
    public static Map<String, Double> distributionReducedAlphabet(String seq, ReduceAlphabet ra, int percent) {
        //composition
        Map<String, Double> composition = compositionReducedAlphabet(seq, ra);
        
        Map<String, String> rAlphabet = ra.getrAlphabet();
        Iterator<String> it = composition.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            int nRa = 0;
            if (percent > 0) {
                nRa = (int) Math.round((composition.get(key) * seq.length()) / 100.0);
                nRa = (int) Math.round((nRa * percent) / 100.0);
            }
            int aux = (nRa == 0 && percent == 0) ? -1 : 0;
            if (nRa == 0 && percent > 0) {
                composition.replace(key, 0.0);
            } else {
                for (int i = 0; i < seq.length(); i++) {
                    String k = rAlphabet.get(seq.subSequence(i, i + 1));
                    aux += key.equalsIgnoreCase(k) ? 1 : 0;
                    if (nRa == aux) {
                        double perSeq = ((i + 1.0) / seq.length()) * 100;
                        composition.replace(key, perSeq);
                        break;
                    }
                }
            }
        }
        
        return composition;
    }
    
    public static Map<String, Double> dipeptideComposition(String seq, ReduceAlphabet ra) {
        Map<String, Double> trans = new HashMap<>(); //Trasition from one grou to another based on reduced alphabets
        Set<String> keySet = ra.getCount().keySet();

        /*
        * Initialize the percent frequency with which a element a \in reduceAlphabets is followed by
        * another element b \in reduceAlphabet such as a !=b.
         */
        for (String key1 : keySet) {
            for (String key2 : keySet) {
                String newKey = "[" + key1 + "][" + key2 + "]";
                trans.put(newKey, 0.0);
            }
        }

        /*
         *para cada substring de seq s(i,i+1) hacer los siguiente 
         */
        Map<String, String> rAlphabet = ra.getrAlphabet();
        String key1, key2;
        for (int i = 0; i < seq.length() - 1; i++) {
            key1 = rAlphabet.get(seq.subSequence(i, i + 1));
            key2 = rAlphabet.get(seq.subSequence(i + 1, i + 2));
            if (key1 != null && key2 != null) {
                String newKey = "[" + key1 + "][" + key2 + "]";
                trans.replace(newKey, trans.get(newKey) + 1);
            }
        }

        /**
         * Calcular el porcentaje de la frecuencia
         */
        Iterator<String> it = trans.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Double percent = (trans.get(key) / (seq.length() - 1)) * 100;
            trans.replace(key, percent);
        }
        
        return trans;
    }
    
    public static Map<String, Double> tripeptideComposition(String seq, ReduceAlphabet ra) {
        Map<String, Double> trans = new HashMap<>(); //Trasition from one grou to another based on reduced alphabets
        Set<String> keySet = ra.getCount().keySet();

        /*
        * Initialize the percent frequency with which a element a \in reduceAlphabets is followed by
        * another element b \in reduceAlphabet such as a !=b.
         */
        for (String key1 : keySet) {
            for (String key2 : keySet) {
                for (String key3 : keySet) {
                    String newKey = "[" + key1 + "][" + key2 + "][" + key3 + "]";
                    trans.put(newKey, 0.0);
                }
            }
        }


        /*
         *para cada substring de seq s(i,i+1) hacer los siguiente 
         */
        Map<String, String> rAlphabet = ra.getrAlphabet();
        for (int i = 0; i < seq.length() - 2; i++) {
            String key1 = rAlphabet.get(seq.subSequence(i, i + 1));
            String key2 = rAlphabet.get(seq.subSequence(i + 1, i + 2));
            String key3 = rAlphabet.get(seq.subSequence(i + 2, i + 3));
            String newKey = "[" + key1 + "][" + key2 + "][" + key3 + "]";
            if (trans.containsKey(newKey)) {
                trans.replace(newKey, trans.get(newKey) + 1);
            }
        }

        /**
         * Calcular el porcentaje de la frecuencia
         */
        Iterator<String> it = trans.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Double percent = (trans.get(key) / (seq.length() - 2)) * 100;
            trans.replace(key, percent);
        }
        
        return trans;
    }
    
}
