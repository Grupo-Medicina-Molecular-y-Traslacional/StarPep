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
public enum AnnotationType { 
    /*
    NAME("named") {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.name");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.name.desc");
        }
    },
    */
    ORIGIN("produced_by") {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.origin");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.origin.desc");
        }
    },
    TARGET("assessed_against") {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.target");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.target.desc");
        }
    },
    FUNCTION("related_to") {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.function");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.function.desc");
        }
    },
    DATABASE("compiled_in") {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.database");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.database.desc");
        }
    },
    /*
    LITERATURE("referenced_by") {

        @Override
        public String getDisplayName() {
             return NbBundle.getMessage(AnnotationType.class, "AnnotationType.literature");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.literature.desc");
        }
    }
    */
    CROSSREF("linked_to") {

        @Override
        public String getDisplayName() {
             return NbBundle.getMessage(AnnotationType.class, "AnnotationType.crossref");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.crossref.desc");
        }
    };
    
    private final String relType;

    private AnnotationType(String relType) {
        this.relType = relType;
    }
    
    public abstract String getDisplayName();
    public abstract String getDescription();
    
    public String getRelationType(){
        return relType;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
    
    
}
