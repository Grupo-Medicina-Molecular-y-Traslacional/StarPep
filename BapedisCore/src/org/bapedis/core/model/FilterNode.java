/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.ui.actions.EditFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class FilterNode extends AbstractNode {

    protected final Filter filter;
    protected Action[] actions;

    public FilterNode(Filter filter) {
        super(Children.LEAF, Lookups.singleton(filter));
        this.filter = filter;
        List<? extends Action> nodeActions
                = Utilities.actionsForPath("Actions/EditFilter");

        actions = nodeActions.toArray(new Action[0]);
    }

    @Override
    public String getHtmlDisplayName() {
        String name = filter.getHTMLDisplayName();
        if (name != null && name.startsWith("<html>") && name.endsWith("</html>")){
            return name;
        }
        return null;
    }        

    @Override
    public String getDisplayName() {        
        return filter.getDisplayName();
    }

    public Filter getFilter() {
        return filter;
    }

    public void refresh() {
        fireDisplayNameChange("", filter.getDisplayName());
    }

    @Override
    public Action getPreferredAction() {
        for (Action action : actions) {
            if (action instanceof EditFilter) {
                return action;
            }
        }
        return (actions.length > 0) ? actions[0] : null;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }
    
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/bapedis/core/resources/filter.png", true);
    }

}
