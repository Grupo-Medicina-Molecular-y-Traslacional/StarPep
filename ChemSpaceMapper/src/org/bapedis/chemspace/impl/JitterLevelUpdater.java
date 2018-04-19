/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import javax.swing.SwingWorker;
import org.bapedis.chemspace.model.TwoDSpace;

/**
 *
 * @author loge
 */
public class JitterLevelUpdater extends SwingWorker<Void, Void> {
    static final String CHANGED_LEVEL = "changed_level";
    private final TwoDSpace twoDSpace;
    private final int level;

    public JitterLevelUpdater(TwoDSpace twoDSpace, int level) {
        this.twoDSpace = twoDSpace;
        this.level = level;
    }        
    
    @Override
    protected Void doInBackground() throws Exception {
        return null;
    }

    @Override
    protected void done() {
        super.done(); //To change body of generated methods, choose Tools | Templates.
    }        
    
}
