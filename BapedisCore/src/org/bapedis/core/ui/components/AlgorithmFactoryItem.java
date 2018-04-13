/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author loge
 */
public class AlgorithmFactoryItem {
            private final AlgorithmFactory factory;

        public AlgorithmFactoryItem(AlgorithmFactory factory) {
            this.factory = factory;
        }

        public AlgorithmFactory getFactory() {
            return factory;
        }

        @Override
        public String toString() {
            return factory.getName();
        }
}
