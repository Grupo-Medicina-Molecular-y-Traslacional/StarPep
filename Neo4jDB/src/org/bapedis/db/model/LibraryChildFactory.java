/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.Label;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class LibraryChildFactory extends ChildFactory<MyLabels>{
            
    @Override
    protected boolean createKeys(List<MyLabels> list) {        
        list.addAll(Arrays.asList(MyLabels.values()));
        return true;
    }

    @Override
    protected Node createNodeForKey(MyLabels key) {
        switch (key){
            case All:
            case Aded:
            case Favorites:
                return new MyLibraryNode(key);
            case Tags:
                return new MyTagNode(key);
        }
        return null;
    }
    
    
    
}

