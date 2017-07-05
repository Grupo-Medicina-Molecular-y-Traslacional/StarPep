/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.neo4j.graphdb.Label;

/**
 *
 * @author loge
 */
public class QueryModel {
    private final Label[] labels;
    private final Metadata[] metadatas;

    public QueryModel(Label[] labels, Metadata[] metadatas) {
        this.labels = labels;
        this.metadatas = metadatas;
    }

    public Label[] getLabels() {
        return labels;
    }

    public Metadata[] getMetadatas() {
        return metadatas;
    }
        
}
