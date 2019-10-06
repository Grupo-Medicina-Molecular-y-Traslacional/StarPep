/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import java.util.Comparator;
import org.bapedis.core.model.MolecularDescriptor;

/**
 *
 * @author Loge
 */
public class FeatureComparator implements Comparator<MolecularDescriptor> {

    @Override
    public int compare(MolecularDescriptor o1, MolecularDescriptor o2) {
        double entropy1 = o1.getScore();
        double entropy2 = o2.getScore();
        if (entropy1 > entropy2) {
            return -1;
        }
        if (entropy1 < entropy2) {
            return 1;
        }
        return 0;
    }
}