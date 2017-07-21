/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.desktop.preview.PreviewSketch;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.ui.components.JColorButton;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class NeoGraphPreView extends JPanel implements MultiViewElement {

    private final PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
    private PreviewSketch sketch;

    private MultiViewElementCallback callback;
    private final JToolBar toolbar = new JToolBar();

    public NeoGraphPreView() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new CardLayout());

        JLabel info = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/info.png")));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.infoLabel.text"));
        add(info, "infoCard");

        // Tool bar
        toolbar.addSeparator();

        // Background button
        JColorButton backgroundButton = new JColorButton((Color) previewController.getModel().getProperties().getValue(PreviewProperty.BACKGROUND_COLOR));
        backgroundButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreView.backgroundButton.toolTipText"));
        backgroundButton.addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                previewController.getModel().getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, (Color) evt.getNewValue());
                sketch.refresh();
            }
        });
        toolbar.add(backgroundButton);

        toolbar.addSeparator();
        // Visibility Ratio
        float val = previewController.getModel().getProperties().getValue(PreviewProperty.VISIBILITY_RATIO);
        final NumberFormat formatter = NumberFormat.getPercentInstance();

        final JLabel ratioLabel = new JLabel();
        ratioLabel.setFont(new Font("Serif", Font.PLAIN, 11));
        ratioLabel.setPreferredSize(new Dimension(35, 20));
        ratioLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ratioLabel.setMaximumSize(ratioLabel.getPreferredSize());

        final JSlider ratioSlider = new JSlider();
        ratioSlider.setMaximum(100);
        ratioSlider.setMinimum(1);
        ratioSlider.setPreferredSize(new Dimension(120, 23));
        ratioSlider.setMaximumSize(ratioSlider.getPreferredSize());

        ratioSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float val = ratioSlider.getValue() / 100f;
                ratioSlider.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.visibilityRatio.text", formatter.format(val)));
                ratioLabel.setText(formatter.format(val));
                ratioLabel.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.visibilityRatio.text", formatter.format(val)));
                previewController.getModel().getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, val);
            }
        });
        ratioSlider.setValue(Math.round(val) * 100);
        toolbar.add(ratioSlider);
        toolbar.add(ratioLabel);

        // Refresh button
        JButton refreshButton = new JButton(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/refresh.png")));
        refreshButton.setToolTipText(NbBundle.getMessage(NeoGraphPreView.class, "NeoGraphPreview.refreshButton.text"));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) getLayout();
                cl.show(NeoGraphPreView.this, "graphCard");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
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

        resetZoomButton.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/db/resources/centerOnGraph.png")));
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

        plusButton.setText(
                "+");
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

        minusButton.setText(
                "-");
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
