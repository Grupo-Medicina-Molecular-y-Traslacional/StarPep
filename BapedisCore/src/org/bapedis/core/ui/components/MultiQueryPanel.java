/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Cursor;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bapedis.core.spi.alg.MultiQuery;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class MultiQueryPanel extends javax.swing.JPanel {

    private final JFileChooser chooser;
    private File inputFile;
    private final MultiQuery multiQueryObj;

    /**
     * Creates new form MultiQuerySearchPanel
     *
     * @param multiQueryObj
     */
    public MultiQueryPanel(MultiQuery multiQueryObj) {
        initComponents();

        this.multiQueryObj = multiQueryObj;

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Open FASTA file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("FASTA format (*.fasta)", "fasta");
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);

        jSeqTextArea.setText(multiQueryObj.getFasta());
        jSeqTextArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFasta();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFasta();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        jBtAdd.setEnabled(enabled);
        jBtClear.setEnabled(enabled);
        jLabel1.setEnabled(enabled);
        jLabel2.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
        jSeqTextArea.setEnabled(enabled);
    }        

    private void updateFasta() {
        String seq = jSeqTextArea.getText();
        if (multiQueryObj != null) {
            multiQueryObj.setFasta(seq);
        }
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

        jBtAdd = new javax.swing.JButton();
        jBtClear = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        jSeqTextArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jBtAdd, org.openide.util.NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.jBtAdd.text")); // NOI18N
        jBtAdd.setToolTipText(org.openide.util.NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.jBtAdd.toolTipText")); // NOI18N
        jBtAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jBtAdd, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jBtClear, org.openide.util.NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.jBtClear.text")); // NOI18N
        jBtClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jBtClear, gridBagConstraints);

        jSeqTextArea.setColumns(20);
        jSeqTextArea.setRows(5);
        scrollPane.setViewportView(jSeqTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrollPane, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jBtAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtAddActionPerformed
        if (inputFile != null) {
            chooser.setCurrentDirectory(inputFile.getParentFile());
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            inputFile = chooser.getSelectedFile();
            jLabel1.setText(inputFile.getAbsolutePath());
            //Load FASTA file
            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                if (!inputFile.exists()) {
                    throw new Exception(NbBundle.getMessage(MultiQueryPanel.class, "MultiQueryPanel.fileNotExist"));
                }

                List<ProteinSequence> queries = FASTASEQ.load(inputFile);
                multiQueryObj.setFasta(FASTASEQ.asFASTA(queries));
                jSeqTextArea.setText(multiQueryObj.getFasta());
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } finally {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }//GEN-LAST:event_jBtAddActionPerformed

    private void jBtClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtClearActionPerformed
        jSeqTextArea.setText("");
        jLabel1.setText("");
    }//GEN-LAST:event_jBtClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtAdd;
    private javax.swing.JButton jBtClear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextArea jSeqTextArea;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
