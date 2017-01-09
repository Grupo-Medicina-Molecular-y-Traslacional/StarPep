/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import org.bapedis.core.ui.components.AttributesPanel;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class AttributeModelPanel extends javax.swing.JPanel implements Lookup.Provider{
    protected final AttributesPanel attributesViewer;

    public AttributeModelPanel(AttributesPanel attributesViewer) {
        this.attributesViewer = attributesViewer;
    }

    public AttributesPanel getAttributesPanel() {
        return attributesViewer;
    }
    
    @Override
    public Lookup getLookup() {
        return attributesViewer.getLookup();
    }
    
    
    
}
