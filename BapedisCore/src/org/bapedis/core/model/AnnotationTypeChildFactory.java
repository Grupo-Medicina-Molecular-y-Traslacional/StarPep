/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.List;
import org.bapedis.core.spi.data.MetadataDAO;
import org.gephi.graph.api.GraphModel;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Home
 */
public class AnnotationTypeChildFactory extends ChildFactory<Metadata> {

    protected final MetadataDAO metadataDAO;
    protected final AnnotationType annotationType;

    public AnnotationTypeChildFactory(AnnotationType annotationType) {
        this.annotationType = annotationType;
        metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
    }

    @Override
    protected boolean createKeys(List<Metadata> list) {
        list.addAll(metadataDAO.getMetadata(annotationType));
        return true;
    }

    @Override
    protected Node createNodeForKey(Metadata key) {
        return new MetadataNode(key);
    }

}
