/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import com.connectina.swing.fontchooser.JFontChooser;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.gephi.graph.api.Node;
import org.gephi.ui.components.JColorButton;
import org.gephi.visualization.VizController;
import org.gephi.visualization.VizModel;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.apiimpl.GraphIO;
import org.gephi.ui.components.JPopupButton;
import org.gephi.visualization.apiimpl.VizEvent;
import org.gephi.visualization.apiimpl.VizEventListener;
import org.gephi.visualization.text.SizeMode;
import org.gephi.visualization.text.TextManager;
import org.gephi.visualization.text.TextModelImpl;
import org.jdesktop.swingx.JXBusyLabel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class NeoGraphScene extends JPanel implements MultiViewElement, WorkspaceEventListener, PropertyChangeListener, VizEventListener {

    protected final ProjectManager pc;
    protected final InstanceContent content;
    protected final Lookup lookup;
    private MultiViewElementCallback callback;
    private final JToolBar toolbar = new JToolBar();
    private final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
    private final JPanel graphPanel = new JPanel();
    // Global
    final JColorButton backgroundButton = new JColorButton(Color.BLACK);
    //Node
    final JToggleButton showNodeLabelsButton = new JToggleButton();
    final JButton nodeFontButton = new JButton();
//    final JColorButton nodeColorButton = new JColorButton(Color.BLACK);
    final JPopupButton labelSizeModeButton = new JPopupButton();
    final JSlider nodeSizeSlider = new JSlider();
    //Edge
    final JToggleButton showEdgeButton = new JToggleButton();
    final JToggleButton edgeHasNodeColorButton = new JToggleButton();
//    final JColorButton edgeColorButton = new JColorButton(Color.BLACK);
    final JSlider edgeScaleSlider = new JSlider();
    final JToggleButton showEdgeLabelsButton = new JToggleButton();
    final JButton edgeFontButton = new JButton();
    final JSlider edgeSizeSlider = new JSlider();

    static {
        UIManager.put("Slider.paintValue", false);
    }

    public NeoGraphScene() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        GraphDrawable drawable = VizController.getInstance().getDrawable();
        graphPanel.add(drawable.getGraphComponent(), BorderLayout.CENTER);
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
    }

    private void initComponents() {
        setLayout(new CardLayout());

        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.busyLabel.text"));
        add(busyLabel, "busyCard");

        graphPanel.setLayout(new BorderLayout());
        add(graphPanel, "graphCard");

        // Toolbar setup
        toolbar.setFloatable(false);

        // Global settings
        toolbar.addSeparator();

        // Background
        backgroundButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.backgroundButton.toolTipText"));
        backgroundButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Color backgroundColor = (Color) evt.getNewValue();
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (!vizModel.getBackgroundColor().equals(backgroundColor)) {
                    vizModel.setBackgroundColor(backgroundColor);

                    TextModelImpl textModel = VizController.getInstance().getVizModel().getTextModel();
                    boolean isDarkBackground = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3 < 128;
                    textModel.setNodeColor(isDarkBackground ? Color.WHITE : Color.BLACK);
                }
            }
        });
        toolbar.add(backgroundButton);

        //Node settings
        toolbar.addSeparator();

        //Show node labels
        showNodeLabelsButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showNodeLabelsButton.toolTipText"));
        showNodeLabelsButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/showNodeLabels.png", false));
        showNodeLabelsButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (vizModel.getTextModel().isShowNodeLabels() != showNodeLabelsButton.isSelected()) {
                    vizModel.getTextModel().setShowNodeLabels(showNodeLabelsButton.isSelected());
                }
                setNodeLabelButton(showNodeLabelsButton.isSelected());
            }
        });
        setNodeLabelButton(showNodeLabelsButton.isSelected());
        toolbar.add(showNodeLabelsButton);

        // Label size mode
        labelSizeModeButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/labelSizeMode.png", false));
        labelSizeModeButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.labelSizeModeButton.toolTipText"));
        labelSizeModeButton.setChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SizeMode sm = (SizeMode) e.getSource();
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (!vizModel.getTextModel().getSizeMode().equals(sm)) {
                    vizModel.getTextModel().setSizeMode(sm);
                }
            }
        });
        toolbar.add(labelSizeModeButton);

        // Node Font
        nodeFontButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/font.png", false));
        nodeFontButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                Font font = JFontChooser.showDialog(WindowManager.getDefault().getMainWindow(), model.getNodeFont());
                if (font != null && font != model.getNodeFont()) {
                    model.setNodeFont(font);
                    nodeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.nodeFontButton.ToolTipText", font.getFontName() + ", " + font.getSize()));
                }
            }
        });
        toolbar.add(nodeFontButton);
        //Node color
//        nodeColorButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
//                if (!model.getNodeColor().equals(nodeColorButton.getColor())) {
//                    model.setNodeColor(nodeColorButton.getColor());
//                }
//
//            }
//        });
//        toolbar.add(nodeColorButton);
        //Font Size
        nodeSizeSlider.setPreferredSize(new Dimension(100, 20));
        nodeSizeSlider.setMaximumSize(nodeSizeSlider.getPreferredSize());
        nodeSizeSlider.setMinimumSize(nodeSizeSlider.getPreferredSize());
        nodeSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                if (model.getNodeSizeFactor() != nodeSizeSlider.getValue() / 100f) {
                    model.setNodeSizeFactor(nodeSizeSlider.getValue() / 100f);
                }
            }
        });
        toolbar.add(nodeSizeSlider);

        //Edge settings
        toolbar.addSeparator();

        //Show edges
        showEdgeButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showEdgeButton.toolTipText"));
        showEdgeButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/showEdges.png", false));
        showEdgeButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (vizModel.isShowEdges() != showEdgeButton.isSelected()) {
                    vizModel.setShowEdges(showEdgeButton.isSelected());
                }
                setEdgeButton(showEdgeButton.isSelected());
            }
        });
        setEdgeButton(showEdgeButton.isSelected());
        toolbar.add(showEdgeButton);

//        Edge color mode
        edgeHasNodeColorButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeHasNodeColorButton.ToolTipText"));
        edgeHasNodeColorButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/edgeNodeColor.png", false));
        edgeHasNodeColorButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (vizModel.isEdgeHasUniColor() == edgeHasNodeColorButton.isSelected()) {
                    vizModel.setEdgeHasUniColor(!edgeHasNodeColorButton.isSelected());
                }
            }
        });
        toolbar.add(edgeHasNodeColorButton);

        //Edge color
//        edgeColorButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeColorButton.ToolTipText"));
//        edgeColorButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
////                float[] edgeColorArray = edgeColorButton.getColorArray();
////                VizModel vizModel = VizController.getInstance().getVizModel();
////                if (!Arrays.equals(vizModel.getEdgeUniColor(), edgeColorArray)) {
////                    vizModel.setEdgeUniColor(edgeColorArray);
////                }
//                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
//                if (!model.getEdgeColor().equals(edgeColorButton.getColor())) {
//                    model.setEdgeColor(edgeColorButton.getColor());
//                }
//            }
//        });
//        toolbar.add(edgeColorButton);

        //EdgeScale slider
        edgeScaleSlider.setMinimum(0);
        edgeScaleSlider.setMaximum(100);
        edgeScaleSlider.setPreferredSize(new Dimension(100, 23));
        edgeScaleSlider.setMaximumSize(edgeScaleSlider.getPreferredSize());
        edgeScaleSlider.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeScaleSlider.toolTipText"));
        edgeScaleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (vizModel.getEdgeScale() != (edgeScaleSlider.getValue() / 10f + 0.1f)) {
                    vizModel.setEdgeScale(edgeScaleSlider.getValue() / 10f + 0.1f);
                }
            }
        });
        toolbar.add(edgeScaleSlider);

        //Show edge labels
        showEdgeLabelsButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showEdgeLabelsButton.toolTipText"));
        showEdgeLabelsButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/showEdgeLabels.png", false));
        showEdgeLabelsButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                if (vizModel.getTextModel().isShowEdgeLabels() != showEdgeLabelsButton.isSelected()) {
                    vizModel.getTextModel().setShowEdgeLabels(showEdgeLabelsButton.isSelected());
                }
                setEdgeLabelButton(showEdgeLabelsButton.isSelected());
            }
        });
        setEdgeLabelButton(showEdgeLabelsButton.isSelected());
        toolbar.add(showEdgeLabelsButton);

        //Edge Font
        edgeFontButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/font.png", false));
        edgeFontButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                Font font = JFontChooser.showDialog(WindowManager.getDefault().getMainWindow(), model.getEdgeFont());
                if (font != null && font != model.getEdgeFont()) {
                    model.setEdgeFont(font);
                    edgeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeFontButton.ToolTipText", font.getFontName() + ", " + font.getSize()));
                }
            }
        });
        toolbar.add(edgeFontButton);
        // Edge size slider       
        edgeSizeSlider.setPreferredSize(new Dimension(100, 23));
        edgeSizeSlider.setMaximumSize(nodeSizeSlider.getPreferredSize());
        edgeSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                if (model.getEdgeSizeFactor() != edgeSizeSlider.getValue() / 100f) {
                    model.setEdgeSizeFactor(edgeSizeSlider.getValue() / 100f);
                }
            }
        });
        toolbar.add(edgeSizeSlider);

        //Zoom
        toolbar.addSeparator();
        // Reset Zoom 
        JButton resetZoomButton = new JButton();
        resetZoomButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/centerOnGraph.png", false));
        resetZoomButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.resetZoomButton.toolTipText"));
        resetZoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphIO io = VizController.getInstance().getGraphIO();
                io.centerOnGraph();
            }
        });
        toolbar.add(resetZoomButton);

        // Plus Zoom
        JButton plusButton = new JButton();
        plusButton.setText("+");
        plusButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.plusButton.toolTipText"));
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cameraDistance = (int) VizController.getInstance().getVizModel().getCameraDistance();
                GraphIO io = VizController.getInstance().getGraphIO();
                io.setCameraDistance(cameraDistance - 1000);
            }
        });
        toolbar.add(plusButton);

        // Minus Zoom
        JButton minusButton = new JButton();
        minusButton.setText("-");
        minusButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.minusButton.toolTipText"));
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cameraDistance = (int) VizController.getInstance().getVizModel().getCameraDistance();
                GraphIO io = VizController.getInstance().getGraphIO();
                io.setCameraDistance(cameraDistance + 1000);
            }
        });
        toolbar.add(minusButton);

        //Advaced button
        toolbar.addSeparator();

        final ExtendedBar extendedBar = new ExtendedBar();
//        extendedPanel.setLayout(new BorderLayout());
//        extendedPanel.add(new JLabel("Hello"), BorderLayout.CENTER);
        graphPanel.add(extendedBar, BorderLayout.PAGE_START);

        final JToggleButton extendButton = new JToggleButton();
        extendButton.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.extendButton.text"));
        if (extendButton.isSelected()) {
            extendButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowDown.png", false)); // NOI18N
            extendButton.setRolloverIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowDown_rollover.png", false)); // NOI18N

        } else {
            extendButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowUp.png", false)); // NOI18N
            extendButton.setRolloverIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowUp_rollover.png", false)); // NOI18N
        }
        extendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (extendButton.isSelected()) {
                    extendButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowDown.png", false)); // NOI18N
                    extendButton.setRolloverIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowDown_rollover.png", false)); // NOI18N
                } else {
                    extendButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowUp.png", false)); // NOI18N
                    extendButton.setRolloverIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/arrowUp_rollover.png", false)); // NOI18N
                }
                extendedBar.setVisible(extendButton.isSelected());
            }
        });
        extendedBar.setVisible(extendButton.isSelected());
        toolbar.add(extendButton);

    }

    public void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphScene.this, busy ? "busyCard" : "graphCard");
        busyLabel.setBusy(busy);
        callback.getTopComponent().makeBusy(busy);
    }

    private void initToolBarComponents() {
        VizModel vizModel = VizController.getInstance().getVizModel();
        TextManager textManager = VizController.getInstance().getTextManager();
        TextModelImpl textModel = vizModel.getTextModel();

        // Background color
        backgroundButton.setColor(vizModel.getBackgroundColor());

        //Show node labels
        showNodeLabelsButton.setSelected(vizModel.getTextModel().isShowNodeLabels());

        //Label size mode
        labelSizeModeButton.removeItems();
        for (final SizeMode sm : textManager.getSizeModes()) {
            labelSizeModeButton.addItem(sm, sm.getIcon());
        }
        labelSizeModeButton.setSelectedItem(textModel.getSizeMode());

        //Font
        Font nodeFont = textModel.getNodeFont();
        nodeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.nodeFontButton.ToolTipText", nodeFont.getFontName() + ", " + nodeFont.getSize()));
        //Node color
//        nodeColorButton.setColor(textModel.getNodeColor());
        //Font Size
        if (nodeSizeSlider.getValue() / 100f != textModel.getNodeSizeFactor()) {
            nodeSizeSlider.setValue((int) (textModel.getNodeSizeFactor() * 100f));
        }

        //Show edges
        showEdgeButton.setSelected(vizModel.isShowEdges());

        //Edge color mode
        edgeHasNodeColorButton.setSelected(!vizModel.isEdgeHasUniColor());
        
        //Edge color
//        float[] edgeColorArray = vizModel.getEdgeUniColor();
//        edgeColorButton.setColor(new Color(edgeColorArray[0], edgeColorArray[1], edgeColorArray[2], edgeColorArray[3]));
//        edgeColorButton.setColor(textModel.getEdgeColor());
        //EdgeScale slider
        if (vizModel.getEdgeScale() != (edgeScaleSlider.getValue() / 10f + 0.1f)) {
            edgeScaleSlider.setValue((int) ((vizModel.getEdgeScale() - 0.1f) * 10));
        }

        //Show edge labels
        showEdgeLabelsButton.setSelected(textModel.isShowEdgeLabels());

        //Edge font
        Font edgeFont = textModel.getEdgeFont();
        edgeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeFontButton.ToolTipText", edgeFont.getFontName() + ", " + edgeFont.getSize()));
        //Edge size slider
        if (edgeSizeSlider.getValue() / 100f != textModel.getEdgeSizeFactor()) {
            edgeSizeSlider.setValue((int) (textModel.getEdgeSizeFactor() * 100f));
        }
    }

    private void setNodeLabelButton(boolean enabled) {
        labelSizeModeButton.setEnabled(enabled);
        nodeFontButton.setEnabled(enabled);
        nodeSizeSlider.setEnabled(enabled);
    }

    private void setEdgeButton(boolean enabled) {
//        edgeHasNodeColorButton.setEnabled(enabled);
        edgeScaleSlider.setEnabled(enabled);
        showEdgeLabelsButton.setEnabled(enabled);
        edgeSizeSlider.setEnabled(showEdgeLabelsButton.isSelected() && enabled);
    }

    private void setEdgeLabelButton(boolean enabled) {
        edgeFontButton.setEnabled(showEdgeButton.isSelected() && enabled);
        edgeSizeSlider.setEnabled(showEdgeButton.isSelected() && enabled);
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
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
        VizController.getInstance().getVizModel().addPropertyChangeListener(this);
        VizController.getInstance().getVizEventManager().addListener(this);
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void componentClosed() {
        VizController.getInstance().getVizModel().removePropertyChangeListener(this);
        VizController.getInstance().getVizEventManager().removeListener(this);
        pc.removeWorkspaceEventListener(this);
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
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

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            QueryModel oldQueryModel = pc.getQueryModel(oldWs);
            oldQueryModel.removePropertyChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);
        setBusy(queryModel.isRunning());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof QueryModel) {
            if (evt.getPropertyName().equals(QueryModel.RUNNING)) {
                setBusy(((QueryModel) evt.getSource()).isRunning());
            }
        } else if (evt.getSource() instanceof VizModel) {
            if (evt.getPropertyName().equals("init")) {
                initToolBarComponents();
            }
        }
    }

    @Override
    public void handleEvent(VizEvent event) {        
        Collection<? extends NodePropertiesWrapper> oldNodes = lookup.lookupAll(NodePropertiesWrapper.class);
        for(NodePropertiesWrapper node: oldNodes){
            content.remove(node);
        }
        Node[] selectedNodes = (Node[]) event.getData();
        for (Node node : selectedNodes) {
            content.add(new NodePropertiesWrapper(node));
        }
    }

    @Override
    public VizEvent.Type getType() {
        return VizEvent.Type.NODE_LEFT_CLICK;
    }

}
