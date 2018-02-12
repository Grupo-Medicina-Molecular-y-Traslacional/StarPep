/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class PropertySheetPanel extends JPanel {
        private static final String ENABLED="enabled";
        private static final String NOT_ENABLED="not_enabled";
        private final PropertySheet propertySheet;
        private final JPanel notEditablePanel;

        public PropertySheetPanel() {
            super(new CardLayout());
            propertySheet = new PropertySheet();
            notEditablePanel = new JPanel(new BorderLayout());
            notEditablePanel.setBackground(Color.WHITE);
            JLabel label = new JLabel(NbBundle.getMessage(PropertySheetPanel.class, "PropertySheetPanel.notEditable.text"));
            label.setForeground(Color.LIGHT_GRAY);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            notEditablePanel.add(label, BorderLayout.CENTER);
            initComponents();
        }
        
        private void initComponents(){
            add(propertySheet, ENABLED);
            add(notEditablePanel, NOT_ENABLED);
        }

        public PropertySheet getPropertySheet() {
            return propertySheet;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
            CardLayout cl = (CardLayout) getLayout();
            if (enabled){
                cl.show(this, ENABLED);
            }else{
                cl.show(this, NOT_ENABLED);
            }
        }                        
    
}
