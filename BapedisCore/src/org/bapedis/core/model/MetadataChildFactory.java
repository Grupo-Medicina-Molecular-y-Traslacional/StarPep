/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class MetadataChildFactory extends ChildFactory<Metadata> {

    protected final Metadata metadata;

    public MetadataChildFactory(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    protected boolean createKeys(List<Metadata> list) {
        list.addAll(metadata.getChilds());
        return true;
    }

    @Override
    protected Node createNodeForKey(Metadata key) {
        return new MetadataNode(key);
    }

}
