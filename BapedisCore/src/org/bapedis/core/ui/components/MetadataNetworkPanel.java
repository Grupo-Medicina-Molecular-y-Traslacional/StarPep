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
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MetadataNetworkPanel extends JPanel {

    private MetadataRadioButton[] metadataOptions;
    private final JLabel metadataInfoLabel;
    private final JPanel centerPanel;
    private final ButtonGroup group;
    private final Workspace workspace;

    public MetadataNetworkPanel(GraphVizSetting graphViz, Workspace workspace) {
        super(new GridBagLayout());
        this.workspace = workspace;
        setMinimumSize(new Dimension(440, 220));
        setPreferredSize(new Dimension(440, 220));

        group = new ButtonGroup();

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
        initMetadataOptions(graphViz);
    }

    private void initMetadataOptions(GraphVizSetting graphViz) {
        StarPepAnnotationType[] arr = StarPepAnnotationType.values();
        metadataOptions = new MetadataRadioButton[arr.length + 1];
        MetadataRadioButton mrb = new MetadataRadioButton(); // Default option     
        metadataOptions[0] = mrb;
        addOption(mrb, 0);
        JRadioButton rb, none;
        none = mrb.getRadioButton();
        none.setSelected(true);        
        for (int i = 0; i < arr.length; i++) {
            mrb = new MetadataRadioButton(arr[i]);
            metadataOptions[i + 1] = mrb;
            addOption(mrb, i + 1);
            rb = mrb.getRadioButton();
            rb.setSelected(graphViz.isDisplayedMetadata(arr[i]));
            if (workspace.isBusy()) {
                rb.setEnabled(rb.isSelected());
            }
        }
        if(workspace.isBusy()){
            none.setEnabled(none.isSelected());
        }
    }

    private void addOption(MetadataRadioButton mrb, int i) {
        JRadioButton rb;
        GridBagConstraints gridBagConstraints;
        rb = mrb.getRadioButton();
        rb.setFocusable(false);
        group.add(rb);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = i + 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 0, 0);
        centerPanel.add(rb, gridBagConstraints);

    }

    public MetadataRadioButton[] getMetadataOptions() {
        return metadataOptions;
    }

    public static class MetadataRadioButton {

        private final JRadioButton radioButton;
        private final StarPepAnnotationType aType;

        public MetadataRadioButton() {
            this.aType = null;
            radioButton = new JRadioButton(NbBundle.getMessage(MetadataNetworkPanel.class, "MetadataNetworkPanel.RadioButton.none"));
        }

        public MetadataRadioButton(StarPepAnnotationType aType) {
            this.aType = aType;
            radioButton = new JRadioButton(aType.getLabelName());
            radioButton.setToolTipText(aType.getDescription());
        }

        public JRadioButton getRadioButton() {
            return radioButton;
        }

        public StarPepAnnotationType getAnnotationType() {
            return aType;
        }

    }
}
