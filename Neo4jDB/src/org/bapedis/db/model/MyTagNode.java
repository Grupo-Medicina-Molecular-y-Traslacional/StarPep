/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.neo4j.graphdb.DynamicLabel;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class MyTagNode extends MyLibraryNode {
    
    private final String tagName;
    
    public MyTagNode() {
        super(Children.create(new MyTagChildFactory(), true));
        tagName = MyLabels.Tags.getDisplayName();
    }
    
    public MyTagNode(String tagName){
        super(Children.LEAF, Lookups.singleton(DynamicLabel.label(tagName)));
        this.tagName = tagName;
    }

    @Override
    public String getDisplayName() {
        return tagName;
    }
        
}
