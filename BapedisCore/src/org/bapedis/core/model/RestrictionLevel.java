/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public enum RestrictionLevel {

    MATCH_ALL {

        @Override
        public String toString() {
            return NbBundle.getMessage(RestrictionLevel.class, "RestrictionLevel.matchAll");
        }

    },
    MATCH_ANY {

        @Override
        public String toString() {
            return NbBundle.getMessage(RestrictionLevel.class, "RestrictionLevel.matchAny");
        }

    };

}
