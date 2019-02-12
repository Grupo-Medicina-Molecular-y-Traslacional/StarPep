/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.ui;

import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public interface PDBWindowController {

    TopComponent getPDBWindow(String code);

    void openGraphWindow(String code);
}
