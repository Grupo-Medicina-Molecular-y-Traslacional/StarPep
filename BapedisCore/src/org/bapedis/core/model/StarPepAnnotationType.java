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
public enum StarPepAnnotationType {
         
    DATABASE("compiled_in") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.database");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.database.desc");
        }
    },
    FUNCTION("related_to") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.function");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.function.desc");
        }
    },
    ORIGIN("produced_by") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.origin");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.origin.desc");
        }
    },
    TARGET("assessed_against") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.target");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.target.desc");
        }
    },
    CROSSREF("linked_to") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.crossref");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.crossref.desc");
        }
    },
    Nterminus("modified_by") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.nterm");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.nterm.desc");
        }
    },
    Cterminus("modified_by") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.cterm");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.cterm.desc");
        }
    },
    UnusualAA("constituted_by") {

        @Override
        public String getLabelName() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.unusualAA");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(StarPepAnnotationType.class, "AnnotationType.unusualAA.desc");
        }
    }
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
    ;

    private final String relType;

    private StarPepAnnotationType(String relType) {
        this.relType = relType;
    }

    public abstract String getLabelName();

    public abstract String getDescription();

    public String getRelationType() {
        return relType;
    }

    @Override
    public String toString() {
        return getLabelName();
    }

}
