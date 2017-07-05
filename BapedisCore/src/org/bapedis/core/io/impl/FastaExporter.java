/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.data.PeptideDAO;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;

/**
 *
 * @author loge
 */
public class FastaExporter implements Exporter {

    protected List<PeptideAttribute> attributes;
    protected Peptide[] peptides;

    public FastaExporter(Peptide[] peptides, List<PeptideAttribute> attributes) {
        this.peptides = peptides;
        this.attributes = attributes;
    }

    @Override
    public void exportTo(File file) throws Exception {
        List<ProteinSequence> sequences = new LinkedList<>();
        ProteinSequence seq;
        StringBuilder header;
        Object objValue;
        String strValue;
        for (Peptide pept : peptides) {
            seq = new ProteinSequence(pept.getSequence());
            header = new StringBuilder(pept.getId());
            for (PeptideAttribute attr : attributes) {
                if (!(attr.equals(PeptideDAO.ID) || attr.equals(PeptideDAO.SEQ))) {
                    objValue = pept.getAttributeValue(attr);
                    strValue = objValue.getClass().isArray() ? Arrays.toString((Object[]) objValue) : objValue.toString();
                    header.append(String.format("|%s=%s", attr.getDisplayName(),strValue));
                }
            }
            seq.setOriginalHeader(header.toString());
            sequences.add(seq);
        }
        FastaWriterHelper.writeProteinSequence(file, sequences);
    }

}
