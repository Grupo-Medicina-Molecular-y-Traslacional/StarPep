/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;

/**
 *
 * @author loge
 */
public class FastaExporter implements Exporter {

    protected final AttributesModel attrModel;

    public FastaExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
    }

    @Override
    public void exportTo(File file) throws Exception {
        List<ProteinSequence> sequences = new LinkedList<>();
        ProteinSequence seq;
        for (Peptide pept : attrModel.getPeptides()) {
            seq = new ProteinSequence(pept.getSequence());
            seq.setOriginalHeader(pept.getId());
            sequences.add(seq);
        }
        FastaWriterHelper.writeProteinSequence(file, sequences);
    }

}
