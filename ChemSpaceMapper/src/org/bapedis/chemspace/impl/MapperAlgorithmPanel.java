/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.chemspace.component.SpaceArffWritable;
import org.bapedis.chemspace.component.VisualizePanel3D;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.chemspace.model.PCATableModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.ui.components.JQuickHistogram;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.bapedis.core.util.ArffWriter;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import weka.core.Instances;

/**
 *
 * @author loge
 */
public class MapperAlgorithmPanel extends javax.swing.JPanel implements AlgorithmSetupUI, PropertyChangeListener {

    protected final JXHyperlink openWizardLink, scatter3DLink;
    protected MapperAlgorithm csMapper;
    protected final JToolBar toolBar;
    protected NetworkEmbedderAlg netEmbedder;
    private final DecimalFormat formatter;
    protected final JXBusyLabel busyLabel;
    protected final DefaultComboBoxModel<String> modelX, modelY;
    protected final JXTable pcaTable;

    static {
        UIManager.put("Slider.paintValue", false);
    }

    /**
     * Creates new form MapperAlgorithmPanel
     */
    public MapperAlgorithmPanel() {
        initComponents();

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        rightTopPanel.add(toolBar);

        scatter3DLink = new JXHyperlink();
//        configureScatter3DLink();
//        toolBar.add(scatter3DLink);

        toolBar.addSeparator();

        openWizardLink = new JXHyperlink();
        configureOpenWizardLink();
        toolBar.add(openWizardLink);

        Hashtable<Integer, JLabel> thresholdLabelTable = new Hashtable<>();
        thresholdLabelTable.put(0, new JLabel("0"));
        thresholdLabelTable.put(10, new JLabel("0.1"));
        thresholdLabelTable.put(20, new JLabel("0.2"));
        thresholdLabelTable.put(30, new JLabel("0.3"));
        thresholdLabelTable.put(40, new JLabel("0.4"));
        thresholdLabelTable.put(50, new JLabel("0.5"));
        thresholdLabelTable.put(60, new JLabel("0.6"));
        thresholdLabelTable.put(70, new JLabel("0.7"));
        thresholdLabelTable.put(80, new JLabel("0.8"));
        thresholdLabelTable.put(90, new JLabel("0.9"));
        thresholdLabelTable.put(100, new JLabel("1"));
        cutoffSlider.setLabelTable(thresholdLabelTable);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);

        busyLabel = new JXBusyLabel(new Dimension(12, 12));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setVisible(false);
        leftTopPanel.add(busyLabel);

        modelX = new DefaultComboBoxModel<>();
        modelY = new DefaultComboBoxModel<>();

        jXComboBox.setModel(modelX);
        jYComboBox.setModel(modelY);

        pcaTable = new JXTable();
        pcaTable.setGridColor(Color.LIGHT_GRAY);
        pcaTable.setHighlighters(HighlighterFactory.createAlternateStriping());
        pcaTable.setColumnControlVisible(false);
        pcaTable.setSortable(true);
        pcaTable.setAutoCreateRowSorter(true);
        pcaScrollPane.setViewportView(pcaTable);
    }

    private void configureOpenWizardLink() {
        openWizardLink.setIcon(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/wizard.png", false));
        openWizardLink.setText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.openWizardLink.text"));
        openWizardLink.setClickedColor(new java.awt.Color(0, 51, 255));
        openWizardLink.setFocusPainted(false);
        openWizardLink.setFocusable(false);
        openWizardLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (csMapper != null) {
                    WizardDescriptor wiz = MapperAlgorithmFactory.createWizardDescriptor(csMapper);

                    //The image in the left sidebar of the wizard is set like this:
                    //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
                    if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                        MapperAlgorithmFactory.setUp(csMapper, wiz);
                    }
                }
            }
        });
    }

    private void configureScatter3DLink() {
        scatter3DLink.setIcon(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/coordinates.png", false));
        scatter3DLink.setText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.scatter3DButton.text"));
        scatter3DLink.setToolTipText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.scatter3DButton.toolTipText"));
        scatter3DLink.setClickedColor(new java.awt.Color(0, 51, 255));
        scatter3DLink.setFocusPainted(false);
        scatter3DLink.setFocusable(false);
        scatter3DLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (csMapper != null & csMapper.getPCATransformer().getXYZSpace() != null) {
                    try {
                        SpaceArffWritable writable = new SpaceArffWritable(csMapper.getPCATransformer().getXYZSpace());
                        File f = ArffWriter.writeToArffFile(writable);
                        BufferedReader reader = new BufferedReader(new FileReader(f));
                        Instances data = new Instances(reader);

                        final VisualizePanel3D panel = new VisualizePanel3D();
                        panel.setInstances(data, 1, 2, 3, 4);

                        final JFrame frame = new JFrame("Scatter plot 3D");
                        frame.addWindowListener(new java.awt.event.WindowAdapter() {
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                panel.freeResources();
                                frame.dispose();
                                System.exit(1);
                            }
                        });
                        frame.setSize(800, 600);
                        frame.setContentPane(panel);
                        frame.setVisible(true);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.csMapper = (MapperAlgorithm) algo;
        netEmbedder = csMapper.getNetworkEmbedderAlg();

        setupAxis();
        setupThreshold();
        return this;
    }

    private void setupThreshold() {
        double similarityThreshold = netEmbedder.getSimilarityThreshold();
        jCutoffCurrentValue.setText(formatter.format(similarityThreshold));
        cutoffSlider.setValue((int) (similarityThreshold * 100));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);

        densityPanel.removeAll();
        if (netEmbedder.getDensityChart() != null) {
            densityPanel.add(netEmbedder.getDensityChart(), BorderLayout.CENTER);
        }

        densityPanel.revalidate();
        densityPanel.repaint();
    }

    private void setupAxis() {
        modelX.removeAllElements();
        modelY.removeAllElements();

        CoordinateSpace xyzSpace = csMapper.getPCATransformer().getXYZSpace();

        if (xyzSpace != null) {
            String[] axisLabels = xyzSpace.getAxisLabels();
            for (String axis : axisLabels) {
                modelX.addElement(axis);
                modelY.addElement(axis);
            }
            modelX.setSelectedItem(axisLabels[xyzSpace.getxAxis()]);
            modelY.setSelectedItem(axisLabels[xyzSpace.getyAxis()]);

            pcaTable.setModel(new PCATableModel(xyzSpace.getExplainedVariance()));
        }
    }

    private void setRunning(boolean running) {
        busyLabel.setVisible(running);
        busyLabel.setBusy(running);

        jXComboBox.setEnabled(!running);
        jYComboBox.setEnabled(!running);

        jCutoffToolBar.setEnabled(!running);
        jLessCutoffButton.setEnabled(!running);
        jMoreCutoffButton.setEnabled(!running);
        cutoffSlider.setEnabled(!running);

        if (running) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }

        scatter3DLink.setEnabled(!running);
        openWizardLink.setEnabled(!running);
        rightTopPanel.setEnabled(!running);
    }

    private void thresholdChanged(double value) {
        netEmbedder.setSimilarityThreshold(value);
        jCutoffCurrentValue.setText(formatter.format(value));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);
        jApplyThresholdButton.setEnabled(false);
    }

    public MapperAlgorithm getCheSMapper() {
        return csMapper;
    }

    @Override
    public void setEnabled(boolean enabled) {
        openWizardLink.setEnabled(enabled);

        //Node panel
        jXComboBox.setEnabled(enabled);
        jYComboBox.setEnabled(enabled);
        pcaTable.setEnabled(enabled);

        //Edges panel
        jCutoffCurrentLabel.setEnabled(enabled);
        jCutoffCurrentValue.setEnabled(enabled);
        jCutoffNewLabel.setEnabled(enabled);
        jCutoffNewValue.setEnabled(enabled);
        jCutoffToolBar.setEnabled(enabled);
        jLessCutoffButton.setEnabled(enabled);
        jMoreCutoffButton.setEnabled(enabled);
        densityPanel.setEnabled(enabled);
        cutoffSlider.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        leftTopPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        rightTopPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tab1 = new javax.swing.JPanel();
        jApplyCoordinateButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jXComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jYComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        twoDPanel = new javax.swing.JPanel();
        pcaScrollPane = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JPanel();
        jCutoffCurrentLabel = new javax.swing.JLabel();
        jCutoffCurrentValue = new javax.swing.JLabel();
        jCutoffNewLabel = new javax.swing.JLabel();
        jCutoffNewValue = new javax.swing.JLabel();
        jCutoffToolBar = new javax.swing.JToolBar();
        jLessCutoffButton = new javax.swing.JButton();
        cutoffSlider = new javax.swing.JSlider();
        jMoreCutoffButton = new javax.swing.JButton();
        densityPanel = new javax.swing.JPanel();
        jApplyThresholdButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        leftTopPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLabel5.text")); // NOI18N
        leftTopPanel.add(jLabel5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(leftTopPanel, gridBagConstraints);

        rightTopPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(rightTopPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.CardLayout());

        tab1.setLayout(new java.awt.GridBagLayout());

        jApplyCoordinateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyCoordinateButton, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jApplyCoordinateButton.text")); // NOI18N
        jApplyCoordinateButton.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jApplyCoordinateButton.toolTipText")); // NOI18N
        jApplyCoordinateButton.setEnabled(false);
        jApplyCoordinateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyCoordinateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        tab1.add(jApplyCoordinateButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab1.add(jLabel1, gridBagConstraints);

        jXComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        tab1.add(jXComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab1.add(jLabel2, gridBagConstraints);

        jYComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        tab1.add(jYComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab1.add(jLabel3, gridBagConstraints);

        twoDPanel.setLayout(new java.awt.BorderLayout());
        twoDPanel.add(pcaScrollPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        tab1.add(twoDPanel, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.tab1.TabConstraints.tabTitle"), tab1); // NOI18N

        tab2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jCutoffCurrentLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab2.add(jCutoffCurrentLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentValue, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jCutoffCurrentValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        tab2.add(jCutoffCurrentValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jCutoffNewLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab2.add(jCutoffNewLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewValue, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jCutoffNewValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        tab2.add(jCutoffNewValue, gridBagConstraints);

        jCutoffToolBar.setFloatable(false);
        jCutoffToolBar.setRollover(true);
        jCutoffToolBar.setPreferredSize(new java.awt.Dimension(420, 90));

        jLessCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessCutoffButton, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLessCutoffButton.text")); // NOI18N
        jLessCutoffButton.setFocusable(false);
        jLessCutoffButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLessCutoffButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLessCutoffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLessCutoffButtonActionPerformed(evt);
            }
        });
        jCutoffToolBar.add(jLessCutoffButton);

        cutoffSlider.setMajorTickSpacing(10);
        cutoffSlider.setMinorTickSpacing(5);
        cutoffSlider.setPaintLabels(true);
        cutoffSlider.setPaintTicks(true);
        cutoffSlider.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.cutoffSlider.toolTipText")); // NOI18N
        cutoffSlider.setValue(70);
        cutoffSlider.setMinimumSize(new java.awt.Dimension(360, 80));
        cutoffSlider.setPreferredSize(new java.awt.Dimension(360, 80));
        cutoffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cutoffSliderStateChanged(evt);
            }
        });
        jCutoffToolBar.add(cutoffSlider);

        jMoreCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/more.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jMoreCutoffButton, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jMoreCutoffButton.text")); // NOI18N
        jMoreCutoffButton.setFocusable(false);
        jMoreCutoffButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMoreCutoffButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jMoreCutoffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoreCutoffButtonActionPerformed(evt);
            }
        });
        jCutoffToolBar.add(jMoreCutoffButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        tab2.add(jCutoffToolBar, gridBagConstraints);

        densityPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        densityPanel.setOpaque(false);
        densityPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        densityPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        tab2.add(densityPanel, gridBagConstraints);

        jApplyThresholdButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyThresholdButton, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jApplyThresholdButton.text")); // NOI18N
        jApplyThresholdButton.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jApplyThresholdButton.toolTipText")); // NOI18N
        jApplyThresholdButton.setEnabled(false);
        jApplyThresholdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyThresholdButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        tab2.add(jApplyThresholdButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        tab2.add(jLabel4, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.tab2.TabConstraints.tabTitle"), tab2); // NOI18N

        centerPanel.add(jTabbedPane1, "tabCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jLessCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLessCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff > cutoffSlider.getMinimum()) {
            cutoffSlider.setValue(cutoff - 1);
        }
    }//GEN-LAST:event_jLessCutoffButtonActionPerformed

    private void cutoffSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cutoffSliderStateChanged
        float threshold = cutoffSlider.getValue() / 100.f;
        if (threshold != Float.parseFloat(jCutoffCurrentValue.getText())) {
            jCutoffNewLabel.setVisible(true);
            jCutoffNewValue.setVisible(true);
            jCutoffNewValue.setText(formatter.format(threshold));
            jApplyThresholdButton.setEnabled(netEmbedder != null);
        } else {
            jCutoffNewLabel.setVisible(false);
            jCutoffNewValue.setVisible(false);
            jApplyThresholdButton.setEnabled(false);
            jCutoffNewValue.setText("");
        }
    }//GEN-LAST:event_cutoffSliderStateChanged

    private void jMoreCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoreCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff < cutoffSlider.getMaximum()) {
            cutoffSlider.setValue(cutoff + 1);
        }
    }//GEN-LAST:event_jMoreCutoffButtonActionPerformed

    private void jApplyThresholdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyThresholdButtonActionPerformed
        if (netEmbedder != null) {
            double threshold = cutoffSlider.getValue() / 100.0;
            NetworkThresholdUpdater fnUpdater = new NetworkThresholdUpdater(threshold, netEmbedder);
            fnUpdater.addPropertyChangeListener(this);
            setRunning(true);
            fnUpdater.execute();
        }
    }//GEN-LAST:event_jApplyThresholdButtonActionPerformed

    private void jApplyCoordinateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyCoordinateButtonActionPerformed
        if (csMapper != null) {
            CoordinateSpace xyzSpace = csMapper.getPCATransformer().getXYZSpace();
            int xAxis = jXComboBox.getSelectedIndex();
            int yAxis = jYComboBox.getSelectedIndex();

            NetworkCoordinateUpdater updater = new NetworkCoordinateUpdater(xyzSpace, xAxis, yAxis, xyzSpace.getzAxis());
            updater.addPropertyChangeListener(this);
            setRunning(true);
            updater.execute();
        }
    }//GEN-LAST:event_jApplyCoordinateButtonActionPerformed

    private void jXComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXComboBoxActionPerformed
        jApplyCoordinateButton.setEnabled(true);
    }//GEN-LAST:event_jXComboBoxActionPerformed

    private void jYComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYComboBoxActionPerformed
        jApplyCoordinateButton.setEnabled(true);
    }//GEN-LAST:event_jYComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JSlider cutoffSlider;
    private javax.swing.JPanel densityPanel;
    private javax.swing.JButton jApplyCoordinateButton;
    private javax.swing.JButton jApplyThresholdButton;
    private javax.swing.JLabel jCutoffCurrentLabel;
    private javax.swing.JLabel jCutoffCurrentValue;
    private javax.swing.JLabel jCutoffNewLabel;
    private javax.swing.JLabel jCutoffNewValue;
    private javax.swing.JToolBar jCutoffToolBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton jLessCutoffButton;
    private javax.swing.JButton jMoreCutoffButton;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> jXComboBox;
    private javax.swing.JComboBox<String> jYComboBox;
    private javax.swing.JPanel leftTopPanel;
    private javax.swing.JScrollPane pcaScrollPane;
    private javax.swing.JPanel rightTopPanel;
    private javax.swing.JPanel tab1;
    private javax.swing.JPanel tab2;
    private javax.swing.JPanel twoDPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(NetworkThresholdUpdater.CHANGED_THRESHOLD)) {
            setRunning(false);
            thresholdChanged((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(NetworkCoordinateUpdater.UPDATED_POSITIONS)) {
            setRunning(false);
        }
    }
}
