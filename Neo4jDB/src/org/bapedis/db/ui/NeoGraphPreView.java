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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.gephi.desktop.preview.PreviewSketch;
import org.gephi.desktop.preview.api.PreviewUIModel;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.jdesktop.swingx.JXBusyLabel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.Mnemonics;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class NeoGraphPreView extends JPanel implements MultiViewElement {

    private final PreviewController previewController;
    private G2DTarget target;
    private PreviewSketch sketch;
    private final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
    private final JPanel graphPanel = new JPanel();

    private MultiViewElementCallback callback;
    private final JToolBar toolbar = new JToolBar();

    public NeoGraphPreView() {
        initComponents();
        previewController = Lookup.getDefault().lookup(PreviewController.class);
        initTarget(previewController.getModel());
    }

    private void initComponents() {
        setLayout(new CardLayout());

        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Mnemonics.setLocalizedText(busyLabel, org.openide.util.NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.busyLabel.text")); // NOI18N
        add(busyLabel, "busyCard");

        graphPanel.setLayout(new BorderLayout());
        add(graphPanel, "graphCard");

        JButton refreshButton = new JButton(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/refresh.png")));
        refreshButton.setText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.refreshButton.text"));
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        previewController.refreshPreview();
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
                setBusy(true);
                worker.execute();
            }
        });
        
        toolbar.add(refreshButton);

    }

    private void initTarget(PreviewModel previewModel) {
        // inits the preview applet
        Color background = previewModel.getProperties().getColorValue(PreviewProperty.BACKGROUND_COLOR);
        if (background != null) {
//                graphPanel.setBackgroundColor(background);
        }

        Dimension dimensions = getSketchDimensions();
        previewModel.getProperties().putValue("width", (int) dimensions.getWidth());
        previewModel.getProperties().putValue("height", (int) dimensions.getHeight());

        target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
        if (target != null) {
            sketch = new PreviewSketch(target, isRetina());
            graphPanel.add(sketch, BorderLayout.CENTER);
        }
    }
    

    public void setBusy(boolean busy) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(NeoGraphPreView.this, busy ? "busyCard" : "graphCard");
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
        callback = mvec;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    /**
     * Returns true if the default screen is in retina display (high dpi).
     *
     * @return true if retina, false otherwise
     */
    private boolean isRetina() {

        boolean isRetina = false;
        try {
            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Field field = graphicsDevice.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(graphicsDevice);
                if (scale instanceof Integer && ((Integer) scale).intValue() == 2) {
                    isRetina = true;
                }
            }
        } catch (Exception e) {
            //Ignore
        }
        return isRetina;
    }

    protected Dimension getSketchDimensions() {
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        if (width > 1 && height > 1) {
            if (isRetina()) {
                width = (int) (width * 2.0);
                height = (int) (height * 2.0);
            }
            return new Dimension(width, height);
        }
        return new Dimension(1, 1);
    }

}
