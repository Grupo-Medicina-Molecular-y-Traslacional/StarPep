/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.gephi.desktop.preview.PreviewSketch;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.visualization.VizController;
import org.gephi.visualization.VizModel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class NeoGraphPreView extends JPanel implements MultiViewElement {

    private final PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
    protected final Lookup lookup;
    private PreviewSketch sketch;

    private MultiViewElementCallback callback;
    private final JToolBar toolbar = new JToolBar();

    public NeoGraphPreView() {
        initComponents();
        lookup = Lookups.singleton(new PreviewNode());
    }

    private void initComponents() {
        setLayout(new CardLayout());

        JLabel info = new JLabel(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/info.png", false));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.infoLabel.text"));
        add(info, "infoCard");

        toolbar.addSeparator();

        // Refresh button
        JButton refreshButton = new JButton(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/refresh.png", false));
        refreshButton.setText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.refreshButton.text"));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) getLayout();
                cl.show(NeoGraphPreView.this, "graphCard");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        VizModel vizModel = VizController.getInstance().getVizModel();
                        previewController.getModel().getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, vizModel.getBackgroundColor());
                        previewController.refreshPreview();
                        sketch.refresh();
                    }
                });
            }
        }
        );

        toolbar.add(refreshButton);

        toolbar.addSeparator();
        // Reset Zoom 
        JButton resetZoomButton = new JButton();

        resetZoomButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/centerOnGraph.png", false));
        resetZoomButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class,
                "NeoGraphPreview.resetZoomButton.toolTipText"));
        resetZoomButton.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sketch.resetZoom();
            }
        }
        );
        toolbar.add(resetZoomButton);

        // Plus Zoom
        JButton plusButton = new JButton();
        plusButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/magnifier--plus.png", false));
        plusButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class,
                "NeoGraphPreview.plusButton.toolTipText"));
        plusButton.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e
            ) {
                sketch.zoomPlus();
            }
        }
        );
        toolbar.add(plusButton);

        // Minus Zoom
        JButton minusButton = new JButton();
        minusButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/magnifier--minus.png", false));
        minusButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class,
                "NeoGraphPreview.minusButton.toolTipText"));
        minusButton.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e
            ) {
                sketch.zoomMinus();
            }
        }
        );
        toolbar.add(minusButton);

        toolbar.addSeparator();
        // Settings
        JButton settingsButton = new JButton();
        settingsButton.setText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.settings.text"));
        settingsButton.setIcon(ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/settings.png", false));
        settingsButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.settings.toolTipText"));
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TopComponent tc = WindowManager.getDefault().findTopComponent("properties"); // NOI18N
                if (tc != null) {
                    tc.open();
                    tc.requestActive();
                }
            }
        });
        
        toolbar.add(settingsButton);
        
        toolbar.addSeparator();
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
        PreviewModel previewModel = previewController.getModel();

        Dimension dimensions = getSketchDimensions();
        int width = (int) dimensions.getWidth();
        int height = (int) dimensions.getHeight();
        previewModel.getProperties().putValue("width", width);
        previewModel.getProperties().putValue("height", height);

        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);

        if (target.getWidth() != width || target.getHeight() != height) {
            target.resize(width, height);
        }
        sketch = new PreviewSketch(target, isRetina());
        add(sketch, "graphCard");
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
        int width = getWidth();
        int height = getHeight();
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
