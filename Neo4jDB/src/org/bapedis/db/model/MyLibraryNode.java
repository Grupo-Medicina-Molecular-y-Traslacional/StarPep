/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class MyLibraryNode extends AbstractNode {

    public MyLibraryNode(){
        super(Children.create(new MyLibraryChildFactory(), false));
    }
    
    public MyLibraryNode(Children children){
        super(children);
    }
    
    public MyLibraryNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> nodeActions
                = Utilities.actionsForPath("Actions/ShowDataFromLibrary/Peptides");

        return nodeActions.toArray(new Action[0]);
    }

}
