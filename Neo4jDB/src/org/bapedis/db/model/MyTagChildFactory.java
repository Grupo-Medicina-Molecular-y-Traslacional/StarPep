/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.List;
import org.neo4j.graphdb.Label;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author loge
 */
public class MyTagChildFactory extends ChildFactory<Label> {

    @Override
    protected boolean createKeys(List<Label> list) {
        return false;
    }
    
}
