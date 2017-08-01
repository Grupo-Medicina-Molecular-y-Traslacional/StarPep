/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.data.MetadataDAO;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
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
    protected boolean showAll;

    public AnnotationTypeChildFactory(AnnotationType annotationType, boolean showAll) {
        this.annotationType = annotationType;
        metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
        this.showAll = showAll;

    }

    @Override
    protected boolean createKeys(List<Metadata> list) {
        List<Metadata> metadatas = metadataDAO.getMetadata(annotationType);
        if (showAll) {
            list.addAll(metadatas);
        } else {
            GraphModel graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel();
            GraphView view = graphModel.getVisibleView();
            Graph graph = graphModel.getGraph(view);
            org.gephi.graph.api.Node node;
            graph.readLock();
            try {
                LinkedList<Metadata> list2 = new LinkedList();
                for (Metadata m : metadatas) {
                    node = graph.getNode(m.getUnderlyingNodeID());
                    if (node != null) {
                        m.setNode(node);
                        list2.add(m);
                    }
                }
                list.addAll(list2);
            } finally {
                graph.readUnlock();
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Metadata key) {
        return new MetadataNode(key);
    }

}
