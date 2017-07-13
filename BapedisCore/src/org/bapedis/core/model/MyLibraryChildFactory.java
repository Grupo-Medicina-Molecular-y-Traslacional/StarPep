/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.bapedis.core.spi.data.BioCategoryDAO;

/**
 *
 * @author loge
 */
public class MyLibraryChildFactory extends ChildFactory<String>{
    private final String ALL= "All";
    private final String FAVORITES= "Favorites";
    private final String ADDED= "Added";
    private final String TAGS= "Tags";
    private final String CLASS= "Class";
    
    @Override
    protected boolean createKeys(List<String> list) {        
        list.add(ALL);
        list.add(FAVORITES);
        list.add(ADDED);
        list.add(TAGS);
        list.add(CLASS);
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        switch (key){
            case ALL:
            case FAVORITES:
            case ADDED:
                return new LabelNode(MyLabels.valueOf(key));
            case TAGS:
                return new MyTagNode();
            case CLASS:
                BioCategoryDAO metadataDAO = Lookup.getDefault().lookup(BioCategoryDAO.class);
                return new MetadataNode(metadataDAO.getBioCategory());
        }
        return null;
    }
    
    
    
}



