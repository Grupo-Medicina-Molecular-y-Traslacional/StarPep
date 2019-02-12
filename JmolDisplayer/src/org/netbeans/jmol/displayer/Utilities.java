/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.displayer;

import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Geertjan
 */
public class Utilities {
    
    static InputOutput io = IOProvider.getDefault().getIO("JMol Output", false);
    
    public static InputOutput getIO (){
//        io.select();
        return io;
    }
    
}
