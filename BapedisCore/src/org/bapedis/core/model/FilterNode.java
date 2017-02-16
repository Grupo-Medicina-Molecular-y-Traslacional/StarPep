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

    protected Action[] actions;

    public FilterNode(Filter filter) {
        super(Children.LEAF, Lookups.singleton(filter));
        List<? extends Action> nodeActions
                = Utilities.actionsForPath("Actions/EditFilter");

        actions = nodeActions.toArray(new Action[0]);
    }

    @Override
    public String getDisplayName() {
        Filter filter = getLookup().lookup(Filter.class);
        return filter.getDisplayName();
    }

    public Filter getFilter() {
        return getLookup().lookup(Filter.class);
    }

    public void refresh() {
        Filter filter = getLookup().lookup(Filter.class);
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
