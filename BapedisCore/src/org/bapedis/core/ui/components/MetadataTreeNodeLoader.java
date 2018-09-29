/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.data.MetadataDAO;
import org.gephi.graph.api.Graph;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class MetadataTreeNodeLoader extends SwingWorker<Integer, Object> {

    public static String FINISH = "finish";
    private final StarPepAnnotationType annotationType;
    private final DefaultMutableTreeNode rootNode;
    private final static MetadataDAO metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
    private final Graph graph;
    private int entriesloaded;
    private static Comparator<DefaultMutableTreeNode> tnc = Comparator.comparing(n -> n.getUserObject().toString());

    public MetadataTreeNodeLoader(StarPepAnnotationType annotationType) {
        this.annotationType = annotationType;
        rootNode = new DefaultMutableTreeNode();
        entriesloaded = 0;
        graph = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getGraph();
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public int getEntriesloaded() {
        return entriesloaded;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        int count = 0;

        List<Metadata> allMetadata = metadataDAO.getMetadata(annotationType);
        for (Metadata m : allMetadata) {
            count += createNode(m, rootNode);
            m.setGraphNode(graph.getNode(m.getID()));
        }
        sort(rootNode);
        return count;
    }

    private static void sort(DefaultMutableTreeNode parent) {
        int n = parent.getChildCount();
        List<DefaultMutableTreeNode> children = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            children.add((DefaultMutableTreeNode) parent.getChildAt(i));
        }

        Collections.sort(children, tnc);
        parent.removeAllChildren();
        for (MutableTreeNode node : children) {
            parent.add(node);
        }
    }

    @Override
    protected void done() {
        try {
            entriesloaded = get();
        } catch (InterruptedException | ExecutionException ex) {
            entriesloaded = 0;
            Exceptions.printStackTrace(ex);
        } finally {
            firePropertyChange(FINISH, false, true);
        }
    }

    private int createNode(Metadata metadata, DefaultMutableTreeNode parentNode) {
        int count = 1;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(metadata);
        parentNode.add(node);
        if (metadata.hasChilds()) {
            for (Metadata m : metadata.getChilds()) {
                count += createNode(m, node);
                sort(node);                
                m.setGraphNode(graph.getNode(m.getID()));
            }
        }        
        return count;
    }

}
