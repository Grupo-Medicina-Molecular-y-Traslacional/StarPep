/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.visualization.ui;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.gephi.tools.api.ToolController;
import org.gephi.ui.utils.UIUtils;
import org.gephi.visualization.VizController;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.opengl.AbstractEngine;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ConvertAsProperties(dtd = "-//org.gephi.visualization.component//Graph//EN",
        autostore = false)
@TopComponent.Description(preferredID = "GraphTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true, roles = {"overview"})
@ActionID(category = "Window", id = "org.gephi.visualization.component.GraphTopComponent")
@ActionReference(path = "Menu/Window", position = 500)
@TopComponent.OpenActionRegistration(displayName = "#CTL_GraphTopComponent",
        preferredID = "GraphTopComponent")
public class GraphTopComponent extends TopComponent  {


    private transient AbstractEngine engine;
    private transient VizBarController vizBarController;
    private transient GraphDrawable drawable;
    private CollapsePanel collapsePanel;

    public GraphTopComponent() {
        initComponents();

        setName(NbBundle.getMessage(GraphTopComponent.class, "CTL_GraphTopComponent"));
//        setToolTipText(NbBundle.getMessage(GraphTopComponent.class, "HINT_GraphTopComponent"));

        //Request component activation and therefore initialize JOGL2 component
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                open();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //Init
                        initCollapsePanel();
                        add(collapsePanel, java.awt.BorderLayout.PAGE_END);
                        initToolPanels();
                        drawable = VizController.getInstance().getDrawable();
                        engine = VizController.getInstance().getEngine();

                        requestActive();
//                        jScrollPane1.setViewportView(drawable.getGraphComponent());
                        add(drawable.getGraphComponent(), BorderLayout.CENTER);
                        remove(waitingLabel);
                    }
                });
            }
        });
    }

    private void initCollapsePanel() {
        vizBarController = new VizBarController();
        collapsePanel = new CollapsePanel();
        if (VizController.getInstance().getVizConfig().isShowVizVar()) {
            collapsePanel.init(vizBarController.getToolbar(), vizBarController.getExtendedBar(), false);
        } else {
            collapsePanel.setVisible(false);
        }
    }
    private SelectionToolbar selectionToolbar;
    private ActionsToolbar actionsToolbar;
    private JComponent toolbar;
    private JComponent propertiesBar;

    private void initToolPanels() {
        final ToolController tc = Lookup.getDefault().lookup(ToolController.class);
        if (tc != null) {
            if (VizController.getInstance().getVizConfig().isToolbar()) {
                JPanel westPanel = new JPanel(new BorderLayout(0, 0));
                if (UIUtils.isAquaLookAndFeel()) {
                    westPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
                }

                toolbar = tc.getToolbar();
                if (toolbar != null) {
                    westPanel.add(toolbar, BorderLayout.CENTER);
                }
                selectionToolbar = new SelectionToolbar();
                actionsToolbar = new ActionsToolbar();

                westPanel.add(selectionToolbar, BorderLayout.NORTH);
                westPanel.add(actionsToolbar, BorderLayout.SOUTH);
                add(westPanel, BorderLayout.WEST);
            }

            if (VizController.getInstance().getVizConfig().isPropertiesbar()) {
                propertiesBar = tc.getPropertiesBar();
                if (propertiesBar != null) {
                    add(propertiesBar, BorderLayout.NORTH);
                }
            }
        }

        //Workspace events
//        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
//        projectController.addWorkspaceListener(new WorkspaceListener() {
//            @Override
//            public void initialize(Workspace workspace) {
//            }
//
//            @Override
//            public void select(Workspace workspace) {
//                if (toolbar != null) {
//                    toolbar.setEnabled(true);
//                }
//                if (propertiesBar != null) {
//                    propertiesBar.setEnabled(true);
//                }
//                if (actionsToolbar != null) {
//                    actionsToolbar.setEnabled(true);
//                }
//                if (selectionToolbar != null) {
//                    selectionToolbar.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void unselect(Workspace workspace) {
//            }
//
//            @Override
//            public void close(Workspace workspace) {
//            }
//
//            @Override
//            public void disable() {
//                if (toolbar != null) {
//                    toolbar.setEnabled(false);
//                }
//                if (tc != null) {
//                    tc.select(null);//Unselect any selected tool
//                }
//                if (propertiesBar != null) {
//                    propertiesBar.setEnabled(false);
//                }
//                if (actionsToolbar != null) {
//                    actionsToolbar.setEnabled(false);
//                }
//                if (selectionToolbar != null) {
//                    selectionToolbar.setEnabled(false);
//                }
//            }
//        });
        boolean hasWorkspace = true;
        if (toolbar != null) {
            toolbar.setEnabled(hasWorkspace);
        }
        if (propertiesBar != null) {
            propertiesBar.setEnabled(hasWorkspace);
        }
        if (actionsToolbar != null) {
            actionsToolbar.setEnabled(hasWorkspace);
        }
        if (selectionToolbar != null) {
            selectionToolbar.setEnabled(hasWorkspace);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        waitingLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        waitingLabel.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(waitingLabel, org.openide.util.NbBundle.getMessage(GraphTopComponent.class, "GraphTopComponent.waitingLabel.text")); // NOI18N
        waitingLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(waitingLabel, java.awt.BorderLayout.CENTER);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel waitingLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {

    }



    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

}
