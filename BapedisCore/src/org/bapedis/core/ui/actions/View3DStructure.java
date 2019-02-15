/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.StarPepAnnotationType;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
public class View3DStructure extends AbstractAction implements Presenter.Popup {

    private final Peptide peptide;
    protected JMenu menu;

    public View3DStructure(Peptide peptide) {
        this.peptide = peptide;
        menu = new JMenu(NbBundle.getMessage(View3DStructure.class, "CTL_View3DStructure"));
        populateMenuItems();
    }
    
    private void populateMenuItems(){
        String[] crossRefs = peptide.getAnnotationValues(StarPepAnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        String db, code;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            db = tokenizer.nextToken();
            if (db.equals("PDB")) {
                code = tokenizer.nextToken();
                menu.add(new OpenJMol(code.trim()));
            }
        }  
        if (menu.getItemCount() == 0){
            menu.add(new OpenJMol(null));
        }
    }    



    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return menu;
    }
    
}
