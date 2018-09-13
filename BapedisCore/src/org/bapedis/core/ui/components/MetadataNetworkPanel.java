/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.GraphViz;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MetadataNetworkPanel extends JPanel {

    private MetadataRadioButton[] metadataOptions;
    private final JLabel metadataInfoLabel;
    private final JPanel centerPanel;

    public MetadataNetworkPanel(GraphViz graphViz) {
        super(new GridBagLayout());
        setMinimumSize(new Dimension(440, 220));
        setPreferredSize(new Dimension(440, 220));

        GridBagConstraints gridBagConstraints;

        // Label
        metadataInfoLabel = new JLabel(NbBundle.getMessage(MetadataNetworkPanel.class, "MetadataNetworkPanel.label"));
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
        initMetadataRadioButtons(graphViz);
    }

    private void initMetadataRadioButtons(GraphViz graphViz) {
        GridBagConstraints gridBagConstraints;
        AnnotationType[] arr = AnnotationType.values();
        metadataOptions = new MetadataRadioButton[arr.length];
        MetadataRadioButton mrb;
        JRadioButton rb;
        for (int i = 0; i < arr.length; i++) {
            mrb = new MetadataRadioButton(arr[i]);
            metadataOptions[i] = mrb;
            rb = mrb.getRadioButton();
            rb.setFocusable(false);
            rb.setSelected(graphViz.isDisplayedMetadata(arr[i]));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 5, 0, 0);
            centerPanel.add(rb, gridBagConstraints);
        }
    }

    public MetadataRadioButton[] getMetadataOptions() {
        return metadataOptions;
    }

    public static class MetadataRadioButton {

        private final JRadioButton radioButton;
        private final AnnotationType aType;

        public MetadataRadioButton(AnnotationType aType) {
            this.aType = aType;
            radioButton = new JRadioButton(aType.getDisplayName());
        }

        public JRadioButton getRadioButton() {
            return radioButton;
        }

        public AnnotationType getAnnotationType() {
            return aType;
        }

    }
}
