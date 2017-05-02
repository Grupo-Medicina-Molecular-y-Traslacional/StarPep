/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.gephi.ui.components.JColorButton;
import org.gephi.visualization.VizController;
import org.gephi.visualization.VizModel;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.component.VizBarController;
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
    }

    public void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphScene.this, busy ? "busyCard" : "graphCard");
        busyLabel.setBusy(busy);
    }

    private void createToolBarComponent() {
        VizModel vizModel = VizController.getInstance().getVizModel();
//        JColorButton backgroundButton = new JColorButton
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
                return VizController.getInstance().getDrawable();
            }

            @Override
            protected void done() {
                try {
                    GraphDrawable drawable = get();
                    graphPanel.add(drawable.getGraphComponent(), BorderLayout.CENTER);
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
