/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.Transferable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Home
 */
public class QueryNode extends AbstractNode {
    
    public QueryNode(QueryModel model) {
        super(Children.create(new QueryModelChildFactory(model), true), Lookups.singleton(model));
        setDisplayName(NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name"));
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        return super.getDropType(t, action, index); //To change body of generated methods, choose Tools | Templates.
    }        
    
}
