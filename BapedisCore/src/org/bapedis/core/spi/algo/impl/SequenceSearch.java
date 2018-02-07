/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class SequenceSearch implements Algorithm {

    private final ProjectManager pc;
    private ProteinSequence query;
    private Peptide[] targets;
    private List<Peptide> resultList;
    private ProgressTicket ticket;
    private boolean stopRun;
    private SequenceAlignmentModel alignmentModel;
    private final SequenceSearchFactory factory;
    protected static final int MAX_REJECTS = 16;

    public SequenceSearch(SequenceSearchFactory factory) {
        this.factory = factory;
        alignmentModel = new SequenceAlignmentModel();
        pc = Lookup.getDefault().lookup(ProjectManager.class);        
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }    

    public ProteinSequence getQuery() {
        return query;
    }

    public void setQuery(ProteinSequence query) {
        this.query = query;
    }    

    public Peptide[] getTargets() {
        return targets;
    }

    public void setTargets(Peptide[] targets) {
        this.targets = targets;
    }

    public List<Peptide> getResultList() {
        return resultList;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        if (targets == null) {
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            targets = attrModel.getPeptides().toArray(new Peptide[0]);
        }
        ticket = progressTicket;
        resultList = new LinkedList<>();
    }

    @Override
    public void endAlgo() {
        targets = null;
        ticket = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        if (targets != null && query != null) {            
            
            // Sort by decreasing common words
            Arrays.sort(targets, new CommonKMersComparator(query.getSequenceAsString()));

            // Assign peptide from targets to result list
            // Stop if max rejects ocurred
            float identityScore = alignmentModel.getIndentityScore();
            int rejections = 0;
            for (int i = 0; i < targets.length && rejections < MAX_REJECTS; i++) {
                try {
                    if (PairwiseSequenceAlignment.computeSequenceIdentity(query, targets[i].getBiojavaSeq(), alignmentModel) >= identityScore) {
                        resultList.add(targets[i]);
                    } else {
                        rejections++;
                    }
                } catch (CompoundNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }
    }

}

class SeqLengthComparator implements Comparator<Peptide> {

    @Override
    public int compare(Peptide o1, Peptide o2) {
        return o2.getLength() - o1.getLength();
    }
}

class CommonKMersComparator implements Comparator<Peptide> {

    private final Set<String> set;
    private final int k;

    public CommonKMersComparator(String query) {
        k = 6;
        set = new HashSet<>();
        for (int i = 0; i <= query.length() - k; i++) {
            set.add(query.substring(i, i + k - 1));
        }
    }

    @Override
    public int compare(Peptide o1, Peptide o2) {
        int c1 = countCommonWords(o1);
        int c2 = countCommonWords(o2);
        return c2 - c1;
    }

    private int countCommonWords(Peptide peptide) {
        String seq = peptide.getSequence();
        int count = 0;
        for (int i = 0; i <= seq.length() - k; i++) {
            if (set.contains(seq.substring(i, i + k - 1))) {
                count++;
            }
        }
        return count;
    }

}
