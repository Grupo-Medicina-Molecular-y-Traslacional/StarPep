/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;

/**
 *
 * @author loge
 */
public class StructureData {

    private final Peptide peptide;
    private final List<String> structures;
    private final HashMap<String, MetadataNode> metadataNodes;

    public StructureData(Peptide peptide) {
        this.peptide = peptide;
        structures = new LinkedList<>();        
        String[] crossRefs = peptide.getAnnotationValues(StarPepAnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        String db, code;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            db = tokenizer.nextToken();
            if (db.equals("PDB")) {
                code = tokenizer.nextToken().trim();
                structures.add(code);
            }
        }
        
        metadataNodes = new HashMap<>();
        String name;
        NodeIterable iter = peptide.getNeighbors(StarPepAnnotationType.CROSSREF);
        for (Node neighbor : iter.toArray()) {
            name = (String) neighbor.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME);
            if (name.startsWith("PDB:")) {
                tokenizer = new StringTokenizer(name, ":");
                if (tokenizer.countTokens() == 2) {
                    tokenizer.nextToken();
                    code = tokenizer.nextToken().trim();
                    metadataNodes.put(code, new MetadataNode(peptide.getEdge(neighbor, StarPepAnnotationType.CROSSREF)));
                }
            }
        }
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public String[] getStructures() {
        return structures.toArray(new String[0]);
    }
    
    public MetadataNode[] getMetadataNode(String structure){
        if (structure == null){
            return metadataNodes.values().toArray(new MetadataNode[0]);
        }
        if (metadataNodes.containsKey(structure)){
            return new MetadataNode[]{metadataNodes.get(structure)};
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.peptide);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureData other = (StructureData) obj;
        if (!Objects.equals(this.peptide, other.peptide)) {
            return false;
        }
        return true;
    }

}
