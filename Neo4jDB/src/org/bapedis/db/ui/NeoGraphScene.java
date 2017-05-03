/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.gephi.ui.components.JColorButton;
import org.gephi.visualization.VizController;
import org.gephi.visualization.VizModel;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.component.JPopupButton;
import org.gephi.visualization.component.VizBarController;
import org.gephi.visualization.text.SizeMode;
import org.gephi.visualization.text.TextManager;
import org.gephi.visualization.text.TextModelImpl;
import org.jdesktop.swingx.JXBusyLabel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class NeoGraphScene extends JPanel implements MultiViewElement {

    private MultiViewElementCallback callback;
    private final JToolBar toolbar = new JToolBar();
    private final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
    private final JPanel graphPanel = new JPanel();
    // Background button
    final JColorButton backgroundButton = new JColorButton(Color.BLACK);
    //Show node labels
    final JToggleButton showNodeLabelsButton = new JToggleButton();

    public NeoGraphScene() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new CardLayout());

        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.busyLabel.text"));
        add(busyLabel, "busyCard");

        graphPanel.setLayout(new BorderLayout());
        add(graphPanel, "graphCard");

        // Toolbar setup
        // Global settings
        toolbar.addSeparator();

        // Background
        backgroundButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.backgroundButton.toolTipText"));
        backgroundButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Color backgroundColor = (Color) evt.getNewValue();
                VizController.getInstance().getVizModel().setBackgroundColor(backgroundColor);

                TextModelImpl textModel = VizController.getInstance().getVizModel().getTextModel();
                boolean isDarkBackground = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3 < 128;
                textModel.setNodeColor(isDarkBackground ? Color.WHITE : Color.BLACK);
            }
        });
        toolbar.add(backgroundButton);
    }

    public void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphScene.this, busy ? "busyCard" : "graphCard");
        busyLabel.setBusy(busy);
    }

    private void toolbarSetup() {
        VizModel vizModel = VizController.getInstance().getVizModel();
//        TextModelImpl textModel = vizModel.getTextModel();

        vizModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("init")) {
                    initToolBarComponents();
                }
            }
        });

        //Nodes
        toolbar.addSeparator();

        showNodeLabelsButton.setSelected(vizModel.getTextModel().isShowNodeLabels());
        showNodeLabelsButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showNodeLabelsButton.toolTipText"));
        showNodeLabelsButton.setIcon(new ImageIcon(getClass().getResource("/org/bapedis/db/resources/showNodeLabels.png")));
        showNodeLabelsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vizModel.getTextModel().setShowNodeLabels(showNodeLabelsButton.isSelected());
            }
        });
        toolbar.add(showNodeLabelsButton);

        //Mode
        final JPopupButton labelSizeModeButton = new JPopupButton();
        labelSizeModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/labelSizeMode.png")));
        labelSizeModeButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.labelSizeModeButton.toolTipText"));
        for (final SizeMode sm : textManager.getSizeModes()) {
            labelSizeModeButton.addItem(sm, sm.getIcon());
        }
        final TextModelImpl textModel = vizModel.getTextModel();
        labelSizeModeButton.setSelectedItem(textModel.getSizeMode());
        labelSizeModeButton.setChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SizeMode sm = (SizeMode) e.getSource();
                textModel.setSizeMode(sm);
            }
        });
        toolbar.add(labelSizeModeButton);

        //Font Size
        final JSlider nodeSizeSlider = new JSlider();
        nodeSizeSlider.setPreferredSize(new Dimension(100, 23));
        nodeSizeSlider.setMaximumSize(nodeSizeSlider.getPreferredSize());
        if (nodeSizeSlider.getValue() / 100f != textModel.getNodeSizeFactor()) {
            nodeSizeSlider.setValue((int) (textModel.getNodeSizeFactor() * 100f));
        }
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

        //Edges
        toolbar.addSeparator();
        //Show edges
        final JToggleButton showEdgeButton = new JToggleButton();
        showEdgeButton.setSelected(vizModel.isShowEdges());
        showEdgeButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showEdgeButton.toolTipText"));
        showEdgeButton.setIcon(new ImageIcon(getClass().getResource("/org/bapedis/db/resources/showEdges.png")));
        showEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vizModel.setShowEdges(showEdgeButton.isSelected());
            }
        });
        toolbar.add(showEdgeButton);

        //Edge color mode
        final JToggleButton edgeHasNodeColorButton = new JToggleButton();
        edgeHasNodeColorButton.setSelected(!vizModel.isEdgeHasUniColor());
        edgeHasNodeColorButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeHasNodeColorButton.ToolTipText"));
        edgeHasNodeColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/edgeNodeColor.png")));
        edgeHasNodeColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vizModel.setEdgeHasUniColor(!edgeHasNodeColorButton.isSelected());
            }
        });
        toolbar.add(edgeHasNodeColorButton);

        //EdgeScale slider
        final JSlider edgeScaleSlider = new JSlider(0, 100, (int) ((vizModel.getEdgeScale() - 0.1f) * 10));
        edgeScaleSlider.setPreferredSize(new Dimension(100, 20));
        edgeScaleSlider.setMaximumSize(new Dimension(100, 20));
        edgeScaleSlider.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.edgeScaleSlider.toolTipText"));
        if (vizModel.getEdgeScale() != (edgeScaleSlider.getValue() / 10f + 0.1f)) {
            edgeScaleSlider.setValue((int) ((vizModel.getEdgeScale() - 0.1f) * 10));
        }
        edgeScaleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (vizModel.getEdgeScale() != (edgeScaleSlider.getValue() / 10f + 0.1f)) {
                    vizModel.setEdgeScale(edgeScaleSlider.getValue() / 10f + 0.1f);
                }
            }
        });
        toolbar.add(edgeScaleSlider);

        //Show edge labels
        final JToggleButton showEdgeLabelsButton = new JToggleButton();
        showEdgeLabelsButton.setSelected(vizModel.getTextModel().isShowEdgeLabels());
        showEdgeLabelsButton.setToolTipText(NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.showEdgeLabelsButton.toolTipText"));
        showEdgeLabelsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/showEdgeLabels.png")));
        showEdgeLabelsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vizModel.getTextModel().setShowEdgeLabels(showEdgeLabelsButton.isSelected());
            }
        });
        toolbar.add(showEdgeLabelsButton);

    }

    private void initToolBarComponents() {
        VizModel vizModel = VizController.getInstance().getVizModel();
        TextModelImpl textModel = vizModel.getTextModel();

        // Background color
        backgroundButton.setBackground(vizModel.getBackgroundColor());
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
        return Lookups.singleton(this);
    }

    @Override
    public void componentOpened() {
        setBusy(true);

        SwingWorker worker = new SwingWorker<GraphDrawable, Void>() {
            @Override
            protected GraphDrawable doInBackground() throws Exception {
                GraphDrawable drawable = VizController.getInstance().getDrawable();
                createToolBarComponents(VizController.getInstance().getVizModel(),
                        VizController.getInstance().getTextManager());
                return drawable;
            }

            @Override
            protected void done() {
                try {
                    graphPanel.add(get().getGraphComponent(), BorderLayout.CENTER);
                    setBusy(false);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        };
        worker.execute();
    }

    @Override
    public void componentClosed() {
        // Destroy JOGL
        VizController.getInstance().destroy();
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

}
