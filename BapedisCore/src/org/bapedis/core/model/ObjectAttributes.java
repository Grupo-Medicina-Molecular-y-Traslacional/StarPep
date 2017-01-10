/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Class that store all attributes and its values for an entity
 * @author loge
 */
public class ObjectAttributes {

    protected HashMap<Attribute, Object> attrsValue;

    public ObjectAttributes() {
        attrsValue = new LinkedHashMap<>();
    }

    public void setAttributeValue(Attribute attr, Object value) {
        attrsValue.put(attr, value);
    }

    public Object getAttributeValue(Attribute attr) {
        return attrsValue.get(attr);
    }

    public Set<Attribute> getAttributes() {
        return attrsValue.keySet();
    }
}
