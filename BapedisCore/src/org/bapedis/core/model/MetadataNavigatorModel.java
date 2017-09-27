/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author loge
 */
public class MetadataNavigatorModel {

    private int selectedIndex;
    private final boolean[] showAll;

    public MetadataNavigatorModel() {
        selectedIndex = -1;
        showAll = new boolean[AnnotationType.values().length];
        for (int i = 0; i < showAll.length; i++) {
            showAll[i] = true;
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public boolean isShowAll(int index){
        return showAll[index];
    }

    public void setShowAll(int index, boolean value){
        showAll[index] = value;
    }
}
