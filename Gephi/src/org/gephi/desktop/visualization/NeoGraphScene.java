/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import org.bapedis.core.model.GraphNodeWrapper;
import com.connectina.swing.fontchooser.JFontChooser;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.GraphElementNavigatorLookupHint;
import org.bapedis.core.ui.MetadataNavigatorLookupHint;
import org.gephi.graph.api.Node;
import org.gephi.ui.components.JColorBlackWhiteSwitcher;
import org.gephi.ui.components.JColorButton;
import org.gephi.visualization.VizController;
import org.gephi.visualization.VizModel;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.apiimpl.GraphIO;
import org.gephi.ui.components.JPopupButton;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.apiimpl.VizEvent;
import org.gephi.visualization.apiimpl.VizEventListener;
import org.gephi.visualization.text.ColorMode;
import org.gephi.visualization.text.SizeMode;
import org.gephi.visualization.text.TextManager;
import org.gephi.visualization.text.TextModelImpl;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXHyperlink;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.UndoRedo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.WindowManager;

public class NeoGraphScene extends JPanel implements MultiViewElement, WorkspaceEventListener, PropertyChangeListener, VizEventListener {

    protected final ProjectManager pc;
    protected final InstanceContent content;
    protected final Lookup lookup;
    private MultiViewElementCallback callback;
    private final JToolBar bottomToolbar = new JToolBar();
    private final JToolBar topToolbar = new JToolBar();
    private final ExtendedBar extendedBar = new ExtendedBar();
    private final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
    private final JPanel graphPanel = new JPanel();
    // Global
    private final JButton resetZoomButton = new JButton();
    private final JButton plusButton = new JButton();
    private final JButton minusButton = new JButton();
    private final JColorBlackWhiteSwitcher backgroundColorSwitcher = new JColorBlackWhiteSwitcher(Color.BLACK);
    private final JColorButton backgroundColorButton = new JColorButton(Color.BLACK);
    private final JXHyperlink configureLink = new JXHyperlink();
    private final JCheckBoxMenuItem autoSelectNeighborItem = new JCheckBoxMenuItem();
    private final JPopupMenu configurePopup = new JPopupMenu();
    private final JButton configureButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/configure.png", false), configurePopup);
    //Node
    private final JToggleButton showNodeLabelsButton = new JToggleButton();
    private final JButton nodeFontButton = new JButton();
//    final JColorButton nodeColorButton = new JColorButton(Color.BLACK);
    private final JPopupButton labelSizeModeButton = new JPopupButton();
    private final JPopupButton labelColorModeButton = new JPopupButton();
    private final JSlider nodeSizeSlider = new JSlider();
    private final JButton metadataButton = new JButton();
    //Edge
    private final JToggleButton showEdgeButton = new JToggleButton();
    private final JToggleButton edgeHasNodeColorButton = new JToggleButton();
//    final JColorButton edgeColorButton = new JColorButton(Color.BLACK);
    private final JSlider edgeScaleSlider = new JSlider();
    private final JToggleButton showEdgeLabelsButton = new JToggleButton();
    private final JButton edgeFontButton = new JButton();
    private final JSlider edgeSizeSlider = new JSlider();

    static {
        UIManager.put("Slider.paintValue", false);
    }

    public NeoGraphScene() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        GraphDrawable drawable = VizController.getInstance().getDrawable();
        graphPanel.add(drawable.getGraphComponent(), BorderLayout.CENTER);
        graphPanel.add(new BottomPanel(), BorderLayout.PAGE_END);
        content = new InstanceContent();
        lookup = new ProxyLookup(new AbstractLookup(content), Lookups.singleton(new GraphElementNavigatorLookupHint()));
    }

    private void initComponents() {
        setLayout(new CardLayout());

        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.busyLabel.text"));
        add(busyLabel, "busyCard");

        graphPanel.setLayout(new BorderLayout());
        add(graphPanel, "graphCard");

        // Toolbar setup
        initTopToolbar(); // Global settings
        initBottomToolbar(); // Node and edge settings
    }

    private void initTopToolbar() {
        topToolbar.setFloatable(false);
        topToolbar.addSeparator();

        // Background
        backgroundColorSwitcher.setFocusable(false);
        backgroundColorSwitcher.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.backgroundSwitcher.toolTipText"));
        backgroundColorSwitcher.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                Color backgroundColor = ((JColorBlackWhiteSwitcher) backgroundColorSwitcher).getColor();
                vizModel.setBackgroundColor(backgroundColor);
                backgroundColorButton.setColor(backgroundColor);

                TextModelImpl textModel = VizController.getInstance().getVizModel().getTextModel();
                boolean isDarkBackground = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3 < 128;
                textModel.setNodeColor(isDarkBackground ? Color.WHITE : Color.BLACK);
                textModel.setEdgeColor(isDarkBackground ? Color.WHITE : Color.BLACK);
            }
        });
        topToolbar.add(backgroundColorSwitcher);

        backgroundColorButton.setFocusable(false);
        backgroundColorButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.backgroundButton.toolTipText"));
        backgroundColorButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                Color bgColor = backgroundColorButton.getColor();
                if (vizModel.getBackgroundColor() != bgColor) {
                    vizModel.setBackgroundColor(bgColor);
                }
            }
        });
        topToolbar.add(backgroundColorButton);

        //Zoom
        topToolbar.addSeparator();

        // Reset Zoom
        resetZoomButton.setFocusable(false);
        resetZoomButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/centerOnGraph.png", false));
        resetZoomButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.resetZoomButton.toolTipText"));
        resetZoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphIO io = VizController.getInstance().getGraphIO();
                io.centerOnGraph();
            }
        });
        topToolbar.add(resetZoomButton);

        // Plus Zoom  
        plusButton.setFocusable(false);
        plusButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/magnifier--plus.png", false));
        plusButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.plusButton.toolTipText"));
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cameraDistance = Math.max((int) VizController.getInstance().getVizModel().getCameraDistance() - 1000, 100);
                GraphIO io = VizController.getInstance().getGraphIO();
                io.setCameraDistance(cameraDistance);
            }
        });
        topToolbar.add(plusButton);

        // Minus Zoom   
        minusButton.setFocusable(false);
        minusButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/magnifier--minus.png", false));
        minusButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.minusButton.toolTipText"));
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cameraDistance = Math.min((int) VizController.getInstance().getVizModel().getCameraDistance() + 1000, 10000);
                GraphIO io = VizController.getInstance().getGraphIO();
                io.setCameraDistance(cameraDistance);
            }
        });
        topToolbar.add(minusButton);

        topToolbar.addSeparator();
        // Mouse Selection        
        final ButtonGroup buttonGroup = new ButtonGroup();
        final JToggleButton mouseButton = new JToggleButton(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/mouse.png", false));
        mouseButton.setFocusable(false);
        mouseButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.selection.mouse.tooltip"));
        mouseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseButton.isSelected()) {
                    SelectionManager selectionManager = VizController.getInstance().getSelectionManager();
                    selectionManager.setDirectMouseSelection();
                    selectionManager.setDraggingMouseSelection();
                    selectionManager.resetSelection();
                    configureLink.setEnabled(true);
                }
            }
        });
        buttonGroup.add(mouseButton);
        buttonGroup.setSelected(mouseButton.getModel(), true);
        topToolbar.add(mouseButton);

        //Configure
        configureLink.setFocusable(false);
        configureLink.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.selection.configureLink.text")); // NOI18N
        configureLink.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.selection.configureLink.tooltip"));
        configureLink.setClickedColor(new java.awt.Color(0, 51, 255));
        configureLink.setDefaultCapable(false);
        configureLink.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N   
        configureLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu menu = createPopup();
                menu.show(configureLink, 0, configureLink.getHeight());
            }
        });
        topToolbar.add(configureLink);

        //Init events
        VizController.getInstance().getSelectionManager().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                SelectionManager selectionManager = VizController.getInstance().getSelectionManager();
                if (selectionManager.isBlocked() || !selectionManager.isSelectionEnabled()) {
                    buttonGroup.clearSelection();
                    configureLink.setEnabled(false);
                } else if (selectionManager.isDirectMouseSelection()) {
                    if (!buttonGroup.isSelected(mouseButton.getModel())) {
                        buttonGroup.setSelected(mouseButton.getModel(), true);
                        configureLink.setEnabled(true);
                    }
                }
            }
        });

        topToolbar.addSeparator();

        // Relationships
        metadataButton.setFocusable(false);
        metadataButton.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.metadataButton.text"));
        metadataButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.metadataButton.toolTipText"));
        metadataButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/relationships.png", false));
        metadataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphViz graphViz = pc.getGraphViz();
                MetadataPanel metadataPanel = new MetadataPanel(graphViz);
                DialogDescriptor dd = new DialogDescriptor(metadataPanel, NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.metadataPanel.title"));
                dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
                if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
                    for (MetadataCheckBox mcb : metadataPanel.getMetadataCheckBoxs()) {
                        if (mcb.getCheckBox().isSelected()) {
                            graphViz.addDisplayedMetadata(mcb.getAnnotationType());
                        } else {
                            graphViz.removeDisplayedMetadata(mcb.getAnnotationType());
                        }
                    }
                }
            }
        });
        topToolbar.add(metadataButton);

        topToolbar.addSeparator();

        //Configure Button
        configureButton.setFocusable(false);
        configureButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.configureButton.toolTipText"));
        configureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configurePopup.show(configureButton, 0, configureButton.getHeight());
            }
        });      
        
        configurePopup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                configureButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                configureButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });                
        
        //Configure Button - Auto select neighbor
        autoSelectNeighborItem.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.autoSelectNeigborCheckbox.text"));
        autoSelectNeighborItem.setFocusable(false);
        autoSelectNeighborItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = VizController.getInstance().getVizModel();
                vizModel.setAutoSelectNeighbor(autoSelectNeighborItem.isSelected());
            }
        });
        configurePopup.add(autoSelectNeighborItem);
        
        topToolbar.add(configureButton);        
    }

    private JPopupMenu createPopup() {
        SelectionManager manager = VizController.getInstance().getSelectionManager();
        final MouseSelectionPopupPanel popupPanel = new MouseSelectionPopupPanel();
        popupPanel.setDiameter(manager.getMouseSelectionDiameter());
        popupPanel.setProportionnalToZoom(manager.isMouseSelectionZoomProportionnal());
        popupPanel.setChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                SelectionManager manager = VizController.getInstance().getSelectionManager();
                manager.setMouseSelectionDiameter(popupPanel.getDiameter());
                manager.setMouseSelectionZoomProportionnal(popupPanel.isProportionnalToZoom());
            }
        });

        JPopupMenu menu = new JPopupMenu();
        menu.add(popupPanel);
        return menu;
    }

    private void initBottomToolbar() {
        bottomToolbar.setFloatable(false);
        bottomToolbar.addSeparator();

        //Node settings        
        //Show node labels
        showNodeLabelsButton.setFocusable(false);
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
        bottomToolbar.add(showNodeLabelsButton);

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
        bottomToolbar.add(labelSizeModeButton);

        //Color mode
        labelColorModeButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/labelColorMode.png", false));
        labelColorModeButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.labelColorModeButton.toolTipText"));
        labelColorModeButton.setChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ColorMode cm = (ColorMode) e.getSource();
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                model.setColorMode(cm);
            }
        });
        bottomToolbar.add(labelColorModeButton);

        // Node Font
        nodeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.nodeFontButton.ToolTipText"));
        nodeFontButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                Font font = JFontChooser.showDialog(WindowManager.getDefault().getMainWindow(), model.getNodeFont());
                if (font != null && font != model.getNodeFont()) {
                    model.setNodeFont(font);
                    nodeFontButton.setText(font.getFontName() + ", " + font.getSize());
                }
            }
        });
        bottomToolbar.add(nodeFontButton);
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
        bottomToolbar.add(nodeSizeSlider);

        //Edge settings
        bottomToolbar.addSeparator();

        //Show edges
        showEdgeButton.setFocusable(false);
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
        bottomToolbar.add(showEdgeButton);

//        Edge color mode
        edgeHasNodeColorButton.setFocusable(false);
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
        bottomToolbar.add(edgeHasNodeColorButton);

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
        bottomToolbar.add(edgeScaleSlider);

        //Show edge labels
        showEdgeLabelsButton.setFocusable(false);
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
        bottomToolbar.add(showEdgeLabelsButton);

        //Edge Font
        edgeFontButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeFontButton.ToolTipText"));
        edgeFontButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TextModelImpl model = VizController.getInstance().getVizModel().getTextModel();
                Font font = JFontChooser.showDialog(WindowManager.getDefault().getMainWindow(), model.getEdgeFont());
                if (font != null && font != model.getEdgeFont()) {
                    model.setEdgeFont(font);
                    edgeFontButton.setText(font.getFontName() + ", " + font.getSize());
                }
            }
        });
        bottomToolbar.add(edgeFontButton);
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
        bottomToolbar.add(edgeSizeSlider);

        //Advaced button
        bottomToolbar.addSeparator();

//        graphPanel.add(extendedBar, BorderLayout.PAGE_START);
        final JToggleButton extendButton = new JToggleButton();
        extendButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.extendButton.text"));
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
        bottomToolbar.add(extendButton);
    }

    public void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphScene.this, busy ? "busyCard" : "graphCard");
        busyLabel.setBusy(busy);
        for (Component c : topToolbar.getComponents()) {
            c.setEnabled(!busy);
        }
        callback.getTopComponent().makeBusy(busy);
    }

    private void initToolBarComponents() {
        VizModel vizModel = VizController.getInstance().getVizModel();
        TextManager textManager = VizController.getInstance().getTextManager();
        TextModelImpl textModel = vizModel.getTextModel();

        // Background color
        backgroundColorSwitcher.setColor(vizModel.getBackgroundColor());
        backgroundColorButton.setColor(vizModel.getBackgroundColor());

        //Auto select neighbor
        autoSelectNeighborItem.setSelected(vizModel.isAutoSelectNeighbor());

        //Show node labels
        showNodeLabelsButton.setSelected(vizModel.getTextModel().isShowNodeLabels());

        //Label size mode
        labelSizeModeButton.removeItems();
        for (final SizeMode sm : textManager.getSizeModes()) {
            labelSizeModeButton.addItem(sm, sm.getIcon());
        }
        labelSizeModeButton.setSelectedItem(textModel.getSizeMode());

        // Color mode
        labelColorModeButton.removeItems();
        for (final ColorMode cm : textManager.getColorModes()) {
            labelColorModeButton.addItem(cm, cm.getIcon());
        }
        labelColorModeButton.setSelectedItem(textManager.getModel().getColorMode());

        //Font
        Font nodeFont = textModel.getNodeFont();
        nodeFontButton.setText(nodeFont.getFontName() + ", " + nodeFont.getSize());

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
        edgeFontButton.setText(edgeFont.getFontName() + ", " + edgeFont.getSize());
        //Edge size slider
        if (edgeSizeSlider.getValue() / 100f != textModel.getEdgeSizeFactor()) {
            edgeSizeSlider.setValue((int) (textModel.getEdgeSizeFactor() * 100f));
        }
    }

    private void setNodeLabelButton(boolean enabled) {
        labelSizeModeButton.setEnabled(enabled);
        labelColorModeButton.setEnabled(enabled);
        nodeFontButton.setEnabled(enabled);
        nodeSizeSlider.setEnabled(enabled);
    }

    private void setEdgeButton(boolean enabled) {
        edgeHasNodeColorButton.setEnabled(enabled);
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
        return topToolbar;
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

            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            oldFilterModel.removePropertyChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        setBusy(queryModel.isRunning() || filterModel.isRunning());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof QueryModel) {
            if (evt.getPropertyName().equals(QueryModel.RUNNING)) {
                setBusy(((QueryModel) evt.getSource()).isRunning());
            }
        } else if (evt.getSource() instanceof FilterModel) {
            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
                setBusy(((FilterModel) evt.getSource()).isRunning());
            }
        } else if (evt.getSource() instanceof VizModel) {
            if (evt.getPropertyName().equals("init")) {
                initToolBarComponents();
            }
        }
    }

    @Override
    public void handleEvent(VizEvent event) {
        Collection<? extends GraphNodeWrapper> oldNodes = lookup.lookupAll(GraphNodeWrapper.class);
        for (GraphNodeWrapper node : oldNodes) {
            content.remove(node);
        }
        Node[] selectedNodes = (Node[]) event.getData();
        for (Node node : selectedNodes) {
            content.add(new GraphNodeWrapper(node));
        }
    }

    @Override
    public VizEvent.Type getType() {
        return VizEvent.Type.NODE_LEFT_CLICK;
    }

    private class BottomPanel extends JPanel {

        public BottomPanel() {
            setLayout(new BorderLayout());
            add(extendedBar, BorderLayout.CENTER);
            add(bottomToolbar, BorderLayout.SOUTH);
        }

    }

}

class MouseSelectionPopupPanel extends javax.swing.JPanel {

    private javax.swing.JSlider diameterSlider;
    private javax.swing.JLabel labelDiameter;
    private javax.swing.JLabel labelValue;
    private javax.swing.JCheckBox proportionnalZoomCheckbox;
    private ChangeListener changeListener;

    /**
     * Creates new form MouseSelectionPopupPanel
     */
    public MouseSelectionPopupPanel() {
        initComponents();

        diameterSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    fireChangeEvent(source);
                }
            }
        });

        proportionnalZoomCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fireChangeEvent(proportionnalZoomCheckbox);
            }
        });
    }

    public boolean isProportionnalToZoom() {
        return proportionnalZoomCheckbox.isSelected();
    }

    public void setProportionnalToZoom(boolean proportionnalToZoom) {
        proportionnalZoomCheckbox.setSelected(proportionnalToZoom);
    }

    public int getDiameter() {
        return diameterSlider.getValue();
    }

    public void setDiameter(int diameter) {
        diameterSlider.setValue(diameter);
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    private void fireChangeEvent(Object source) {
        if (changeListener != null) {
            ChangeEvent changeEvent = new ChangeEvent(source);
            changeListener.stateChanged(changeEvent);
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelDiameter = new javax.swing.JLabel();
        diameterSlider = new javax.swing.JSlider();
        labelValue = new javax.swing.JLabel();
        proportionnalZoomCheckbox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        labelDiameter.setText(NbBundle.getMessage(MouseSelectionPopupPanel.class, "NeoGraphScene.labelDiameter.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 8, 0);
        add(labelDiameter, gridBagConstraints);

        diameterSlider.setMaximum(1000);
        diameterSlider.setMinimum(1);
        diameterSlider.setFocusable(false);
        diameterSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!diameterSlider.getValueIsAdjusting()) {
                    labelValue.setText(String.valueOf(diameterSlider.getValue()));
                }
            }
        });
        diameterSlider.setValue(1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(diameterSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 8, 5);
        add(labelValue, gridBagConstraints);

        proportionnalZoomCheckbox.setText(NbBundle.getMessage(MouseSelectionPopupPanel.class, "NeoGraphScene.proportionnalZoomCheckbox.text")); // NOI18N
        proportionnalZoomCheckbox.setFocusable(false);
        proportionnalZoomCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 0);
        add(proportionnalZoomCheckbox, gridBagConstraints);

    }
}

class MetadataPanel extends JPanel {

    MetadataCheckBox[] metadataCheckBoxs;
    JLabel metadataInfoLabel;
    JPanel centerPanel;

    public MetadataPanel(GraphViz graphViz) {
        super(new GridBagLayout());
        setMinimumSize(new Dimension(440, 220));
        setPreferredSize(new Dimension(440, 220));

        GridBagConstraints gridBagConstraints;

        // Label
        metadataInfoLabel = new JLabel(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.metadataPanel.label"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        add(metadataInfoLabel, gridBagConstraints);

        // Center Panel
        centerPanel = new JPanel(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        add(centerPanel, gridBagConstraints);
        initMetadataCheckBoxs(graphViz);
    }

    private void initMetadataCheckBoxs(GraphViz graphViz) {
        GridBagConstraints gridBagConstraints;
        AnnotationType[] arr = AnnotationType.values();
        metadataCheckBoxs = new MetadataCheckBox[arr.length];
        MetadataCheckBox mcb;
        JCheckBox cb;
        for (int i = 0; i < arr.length; i++) {
            mcb = new MetadataCheckBox(arr[i]);
            metadataCheckBoxs[i] = mcb;
            cb = mcb.getCheckBox();
            cb.setFocusable(false);
            cb.setSelected(graphViz.isDisplayedMetadata(arr[i]));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 5, 0, 0);
            centerPanel.add(cb, gridBagConstraints);
        }
    }

    public MetadataCheckBox[] getMetadataCheckBoxs() {
        return metadataCheckBoxs;
    }

}

class MetadataCheckBox {

    private final JCheckBox cb;
    private final AnnotationType aType;

    public MetadataCheckBox(AnnotationType aType) {
        this.aType = aType;
        cb = new JCheckBox(aType.getDisplayName());
    }

    public JCheckBox getCheckBox() {
        return cb;
    }

    public AnnotationType getAnnotationType() {
        return aType;
    }

}
