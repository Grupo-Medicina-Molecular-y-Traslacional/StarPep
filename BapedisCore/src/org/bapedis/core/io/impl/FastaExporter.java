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
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;

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
        StringBuilder header;
        ProteinSequence seq;
        NodeIterable neighbors;
        Edge edge;
        for (Peptide pept : attrModel.getPeptides()) {
            header = new StringBuilder(pept.getId());
            neighbors = pept.getNeighbors(AnnotationType.DATABASE);
            for (Node neighbor : neighbors){
                edge = pept.getEdge(neighbor, AnnotationType.DATABASE);
                for(String db: (String[]) edge.getAttribute("xref")){
                    header.append("|");
                    header.append(db);
                }
            }
            seq = new ProteinSequence(pept.getSequence());
            seq.setOriginalHeader(header.toString());
            sequences.add(seq);
        }
        FastaWriterHelper.writeProteinSequence(file, sequences);
    }

}
