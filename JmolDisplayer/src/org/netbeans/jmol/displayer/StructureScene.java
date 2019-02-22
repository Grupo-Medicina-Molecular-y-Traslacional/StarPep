/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.displayer;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.StringTokenizer;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.jmol.api.JmolViewer;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

/**
 *
 * @author loge
 */
public class StructureScene extends JPanel implements MultiViewElement {

    private final Peptide peptide;
    private final String code;
    private final JPanel toolBarPanel;
    private final JPanel centerPanel;
    private JmolPanel jmolPanel;
    private MultiViewElementCallback callback;
    protected final Lookup lookup;
    protected final InstanceContent content;

    public StructureScene(Peptide peptide, String code) {
        this.peptide = peptide;
        this.code = code;

        setLayout(new BorderLayout());
        centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        StringTokenizer tokenizer;
        String name;

        NodeIterable iter = peptide.getNeighbors(StarPepAnnotationType.CROSSREF);
        for (Node neighbor : iter.toArray()) {
            name = (String) neighbor.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME);
            if (name.startsWith("PDB:")) {
                tokenizer = new StringTokenizer(name, ":");
                if (tokenizer.countTokens() == 2) {
                    tokenizer.nextToken();
                    if (tokenizer.nextToken().trim().equals(code)) {
                        content.add(new MetadataNode(peptide.getEdge(neighbor, StarPepAnnotationType.CROSSREF)));
                        break;
                    }
                }
            }
        }
        
        toolBarPanel = new JPanel(new BorderLayout());
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarPanel;
    }

    @Override
    public Action[] getActions() {
        if (callback != null) {
            return callback.createDefaultActions();
        }
        return new Action[]{};
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
        jmolPanel = new JmolPanel(null, null, centerPanel, 600, 600, "", new Point(200, 200));
        centerPanel.add(jmolPanel, BorderLayout.CENTER);
        toolBarPanel.add(jmolPanel.getToolbar(), BorderLayout.CENTER);

        JmolViewer viewer = jmolPanel.getViewer();
        viewer.script(getScript(code));

        //Cartoons
        viewer.script("select protein; cartoons only; color structure;");

        //Spacefill
//            viewer.script("select *; cartoons off; spacefill only; color cpk");
        //Wire
//            viewer.script("select *;cartoons off; wireframe -0.1; color cpk");
        //Ball and stick
//            viewer.script("select *; cartoons off; spacefill 23%; wireframe 0.15; color cpk");  

        centerPanel.revalidate();
        centerPanel.repaint();
        
        toolBarPanel.revalidate();
        toolBarPanel.repaint();
    }

    @Override
    public void componentDeactivated() {
        centerPanel.removeAll();
        toolBarPanel.removeAll();
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        this.callback = mvec;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    public static String getScript(String code) {
        return "var xid = _modelTitle; if (xid.length != 4) { xid = '"
                + code
                + "'};load @{'=' + xid}";
    }
}
