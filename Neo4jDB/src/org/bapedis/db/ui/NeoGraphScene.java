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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.gephi.visualization.VizController;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.jdesktop.swingx.JXBusyLabel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.Mnemonics;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

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
        setBusy(true);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                GraphDrawable drawable = VizController.getInstance().getDrawable();
                graphPanel.add(drawable.getGraphComponent(), BorderLayout.CENTER);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();            
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

    private void initComponents() {
        setLayout(new CardLayout());

        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Mnemonics.setLocalizedText(busyLabel, org.openide.util.NbBundle.getMessage(NeoGraphScene.class, "NeoGraphScene.busyLabel.text")); // NOI18N
        add(busyLabel, "busyCard");

        graphPanel.setLayout(new BorderLayout());
        add(graphPanel, "graphCard");
    }

    private void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphScene.this, busy ? "busyCard" : "graphCard");
        busyLabel.setBusy(busy);
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
