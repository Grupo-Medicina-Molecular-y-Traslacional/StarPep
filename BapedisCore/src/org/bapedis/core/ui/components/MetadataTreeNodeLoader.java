/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.spi.data.MetadataDAO;
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
    private int entriesloaded;

    public MetadataTreeNodeLoader(StarPepAnnotationType annotationType) {
        this.annotationType = annotationType;
        rootNode = new DefaultMutableTreeNode();
        entriesloaded = 0;
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
        }
//        GraphModel graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel();
//        Graph graph = graphModel.getGraphVisible();
//        org.gephi.graph.api.Node node;
//        graph.readLock();
//        try {
//            for (Metadata m : allMetadata) {
//                node = graph.getNode(m.getUnderlyingNodeID());
//                if (node != null) {
//                    m.setGraphNode(node);
//                    count += createNode(m, rootNode);
//                }
//            }
//        } finally {
//            graph.readUnlock();
//        }
        return count;
    }

    @Override
    protected void done() {
        try {
            entriesloaded = get();            
        } catch (InterruptedException | ExecutionException ex) {
            entriesloaded = 0;
            Exceptions.printStackTrace(ex);
        } finally{
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
            }
        }
        return count;
    }

}
