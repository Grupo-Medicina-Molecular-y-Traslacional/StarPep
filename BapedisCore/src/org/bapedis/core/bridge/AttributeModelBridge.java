/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.bridge;

import java.util.List;
import org.bapedis.core.model.AttributesModel;

/**
 *
 * @author loge
 */
public interface AttributeModelBridge {
    void copyTo(AttributesModel attrModel, List<Integer> peptideIDs);
}
