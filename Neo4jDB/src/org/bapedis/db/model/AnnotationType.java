/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public enum AnnotationType { 
    NAME {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.name");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.name.desc");
        }
    },
    ORIGIN {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.origin");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.origin.desc");
        }
    },
    TARGET {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.target");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.target.desc");
        }
    },
    BIOCATEGORY {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.biocategory");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.biocategory.desc");
        }
    },
    DATABASE {

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.database");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.database.desc");
        }
    },
    LITERATURE {

        @Override
        public String getDisplayName() {
             return NbBundle.getMessage(AnnotationType.class, "AnnotationType.literature");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.literature.desc");
        }
    },
    CROSSREF {

        @Override
        public String getDisplayName() {
             return NbBundle.getMessage(AnnotationType.class, "AnnotationType.crossref");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(AnnotationType.class, "AnnotationType.crossref.desc");
        }
    };
    
    public abstract String getDisplayName();
    public abstract String getDescription();
}
