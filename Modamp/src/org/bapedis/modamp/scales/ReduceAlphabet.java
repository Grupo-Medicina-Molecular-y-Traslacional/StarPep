/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.scales;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author beltran
 */
public class ReduceAlphabet {
    private final String name;
    private Map<String, Double> count;
    private Map<String, String> rAlphabet;

    
    public ReduceAlphabet(String name,Map<String,Double>count){
        this(name, count, null);
    }
    
    public ReduceAlphabet(String name, Map<String, Double> count, Map<String, String> rAlphabet) {
        this.name = name;
        this.count = count;
        this.rAlphabet = rAlphabet;
    }

    public Map<String, Double> getCount() {
        return count;
    }

    public Map<String, String> getrAlphabet() {
        return rAlphabet;
    }
    
    public void init(){
        Iterator<String> it = count.keySet().iterator();
    
        while (it.hasNext()) {
            String key = it.next();
            count.replace(key, 0.0);
        }
    }

    public String getName() {
        return name;
    }        
    
}
