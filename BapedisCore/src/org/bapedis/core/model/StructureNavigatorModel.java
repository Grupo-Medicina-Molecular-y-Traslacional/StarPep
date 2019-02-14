/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.ArrayList;

/**
 *
 * @author loge
 */
public class StructureNavigatorModel {

    private ArrayList<String> codes;
    private int selectedIndex;

    public StructureNavigatorModel() {
        codes = new ArrayList<>();
        selectedIndex = 0;
    }

    public void add(String code) {
        codes.add(code);
    }

    public void clear() {
        codes.clear();
        selectedIndex = 0;
    }

    public String getCurrentCode() {
        return (selectedIndex >= 0 && selectedIndex < codes.size()) ? codes.get(selectedIndex) : null;
    }
    
    public boolean hasNext(){
        return selectedIndex < codes.size() -1;
    }

    public void next() {
        if (selectedIndex < codes.size() - 1) {
            selectedIndex++;
        } else {
            throw new IllegalStateException("Invalid next structure");
        }
    }
    
    public boolean hasPrevious(){
        return selectedIndex > 0;
    }    

    public void prev() {
        if (selectedIndex > 0) {
            selectedIndex--;
        } else {
            throw new IllegalStateException("Invalid previous structure");
        }
    }   
    
    public boolean isEmpty(){
        return codes.isEmpty();
    }
    
    public int getSize(){
        return codes.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }        
}
