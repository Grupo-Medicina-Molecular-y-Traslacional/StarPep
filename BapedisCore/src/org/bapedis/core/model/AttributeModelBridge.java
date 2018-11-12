/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.List;

/**
 *
 * @author loge
 */
public interface AttributeModelBridge {
    void copyTo(AttributesModel attrModel, List<Integer> peptideIDs);
    void copyTo(Workspace workspace, List<Integer> peptideIDs);
}
