/*
 Copyright 2008-2013 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Gephi Consortium. All rights reserved.

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

 Portions Copyrighted 2013 Gephi Consortium.
 */
package org.gephi.desktop.appearance;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.spi.TransformerCategory;
import org.gephi.appearance.spi.TransformerUI;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author mbastian
 */
public class AppearanceToolbar implements AppearanceUIModelListener {

    protected final AppearanceUIController controller;
    protected AppearanceUIModel model;
    //Toolbars
    private final CategoryToolbar categoryToolbar;
    private final TransformerToolbar transformerToolbar;
    private final ControlToolbar controlToolbar;

    public AppearanceToolbar(AppearanceUIController controller) {
        this.controller = controller;
        categoryToolbar = new CategoryToolbar();
        transformerToolbar = new TransformerToolbar();
        controlToolbar = new ControlToolbar();

        controller.addPropertyChangeListener(this);

        AppearanceUIModel uimodel = controller.getModel();
        if (uimodel != null) {
            setup(uimodel);
        }
    }

    public CategoryToolbar getCategoryToolbar() {
        return categoryToolbar;
    }

    public TransformerToolbar getTransformerToolbar() {
        return transformerToolbar;
    }

    public ControlToolbar getControlToolbar() {
        return controlToolbar;
    }

    public void addRankingControl(AbstractButton btn) {
        controlToolbar.addRankingButton(btn);
    }

    public void addPartitionControl(AbstractButton btn) {
        controlToolbar.addPartitionButton(btn);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(AppearanceUIModelEvent.MODEL)) {
            setup((AppearanceUIModel) pce.getNewValue());
        } else if (pce.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_ELEMENT_CLASS)) {
            refreshSelectedElementClass((String) pce.getNewValue());
        } else if (pce.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_CATEGORY)) {
            refreshSelectedCategory((TransformerCategory) pce.getNewValue());
        } else if (pce.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_FUNCTION)) {
            refreshSelectedFunction((Function) pce.getNewValue());
        } else if (pce.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_TRANSFORMER_UI)) {
            refreshSelectedTransformerUI();
        }
//        if (pce.getPropertyName().equals(AppearanceUIModelEvent.CURRENT_ELEMENT_TYPE)) {
//            refreshSelectedElmntGroup((String) pce.getNewValue());
//        }
//        if (pce.getPropertyName().equals(RankingUIModel.CURRENT_TRANSFORMER)
//                || pce.getPropertyName().equals(RankingUIModel.CURRENT_ELEMENT_TYPE)) {
//            refreshTransformers();
//        }
//        if (pce.getPropertyName().equalsIgnoreCase(RankingUIModel.START_AUTO_TRANSFORMER)
//                || pce.getPropertyName().equalsIgnoreCase(RankingUIModel.STOP_AUTO_TRANSFORMER)) {
//            refreshDecoratedIcons();
//        }
    }

    private void setup(final AppearanceUIModel model) {
        this.model = model;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                categoryToolbar.setEnabled(model != null);
                categoryToolbar.setup();
                categoryToolbar.refreshSelectedElmntGroup();
                categoryToolbar.refreshTransformers();

                transformerToolbar.setEnabled(model != null);
                transformerToolbar.setup();
                transformerToolbar.refreshTransformers();

                controlToolbar.setEnabled(model != null);
                controlToolbar.setup();
                controlToolbar.refreshControls();
            }
        });
    }

    private void refreshSelectedElementClass(final String elementClass) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                categoryToolbar.refreshSelectedElmntGroup();
                categoryToolbar.refreshTransformers();

                transformerToolbar.refreshTransformers();
                controlToolbar.refreshControls();
            }
        });
    }

    private void refreshSelectedCategory(final TransformerCategory category) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                categoryToolbar.refreshTransformers();

                transformerToolbar.refreshTransformers();

                controlToolbar.refreshControls();
            }
        });
    }

    private void refreshSelectedFunction(final Function ui) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                transformerToolbar.refreshTransformers();
                controlToolbar.refreshControls();
            }
        });
    }

    private void refreshSelectedTransformerUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                controlToolbar.refreshControls();
            }
        });
    }

    private class AbstractToolbar extends JToolBar {

        public AbstractToolbar() {
            setFloatable(false);
            setRollover(true);
            Border b = (Border) UIManager.get("Nb.Editor.Toolbar.border"); //NOI18N
            setBorder(b);
        }

        @Override
        public void setEnabled(final boolean enabled) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (Component c : getComponents()) {
                        c.setEnabled(enabled);
                    }
                }
            });
        }
    }

    public class CategoryToolbar extends AbstractToolbar {

        private final List<ButtonGroup> buttonGroups = new ArrayList<>();
        private final List<ActionListener> actionListener = new LinkedList<>();
        private final ButtonGroup popupGroup;
        
        public CategoryToolbar() {
            //Init components
            final JLabel elementClassLabel = new JLabel(NbBundle.getMessage(AppearanceToolbar.class, "AppearanceToolbar." + AppearanceUIController.ELEMENT_CLASSES[0] + ".label") + ": ");
            popupGroup = new ButtonGroup();
            final JPopupMenu popup = new JPopupMenu();
            JCheckBoxMenuItem item;
            for (final String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                item = new JCheckBoxMenuItem();
                final String btnLabel = NbBundle.getMessage(AppearanceToolbar.class, "AppearanceToolbar." + elmtType + ".label");
                
                item.setText(btnLabel);
                item.setSelected(false);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        controller.setSelectedElementClass(elmtType);
                        elementClassLabel.setText(btnLabel + ": ");
                    }
                });
                popup.add(item);
                popupGroup.add(item);
            }
            final JButton btn = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("/org/gephi/desktop/appearance/resources/chain.png", false), popup);
            btn.setToolTipText(NbBundle.getMessage(AppearanceToolbar.class, "AppearanceToolbar.chain.toolTipText"));
            btn.setFocusPainted(false);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    popup.show(btn, 0, btn.getHeight());
                }
            });

            popup.addPopupMenuListener(new PopupMenuListener() {

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                    btn.setSelected(false);
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    btn.setSelected(false);
                }

                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
            });
            add(btn);
            addSeparator();
            add(elementClassLabel);
        }

        private void clear() {
            //Clear precent buttons
            for (ButtonGroup bg : buttonGroups) {
                for (Enumeration<AbstractButton> btns = bg.getElements(); btns.hasMoreElements();) {
                    AbstractButton btn = btns.nextElement();
                    remove(btn);
                }
            }
            buttonGroups.clear();
        }

        protected void setup() {
            clear();
            if (model != null) {
                //Add transformers buttons, separate them by element group
                for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                    ButtonGroup buttonGroup = new ButtonGroup();
                    for (final TransformerCategory c : controller.getCategories(elmtType)) {
                        //Build button
                        Icon icon = c.getIcon();
//                        DecoratedIcon decoratedIcon = getDecoratedIcon(icon, t);
//                        JToggleButton btn = new JToggleButton(decoratedIcon);
                        JButton btn = new JButton(icon);

                        btn.setToolTipText(c.getDisplayName());
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                controller.setSelectedCategory(c);
                                for(ActionListener action: actionListener){
                                    action.actionPerformed(e);
                                }
                            }
                        });
                        btn.setName(c.getDisplayName());
                        btn.setFocusPainted(false);
                        buttonGroup.add(btn);
                        add(btn);
                    }

                    buttonGroups.add(buttonGroup);
                }
            } else {
                popupGroup.clearSelection();
            }
        }

        protected void refreshTransformers() {
            if (model != null) {
                //Select the right transformer
                int index = 0;
                for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                    ButtonGroup g = buttonGroups.get(index);
                    boolean active = model.getSelectedElementClass().equals(elmtType);
                    g.clearSelection();
                    for (Enumeration<AbstractButton> btns = g.getElements(); btns.hasMoreElements();) {
                        AbstractButton btn = btns.nextElement();
                        btn.setVisible(active);
                    }
                    index++;
                }
            }
        }

        protected void refreshSelectedElmntGroup() {
            String selected = model == null ? null : model.getSelectedElementClass();
            JCheckBoxMenuItem item = null;
            Enumeration<AbstractButton> items = popupGroup.getElements();
            for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                if (selected == null || elmtType.equals(selected)) {
                    item = (JCheckBoxMenuItem) items.nextElement();
                    item.setSelected(true);
                    break;
                }
                items.nextElement();
            }
        }
        
        public void addActionListener(ActionListener listener){
            actionListener.add(listener);
        }
        
        public void removeActionListener(ActionListener listener){
            actionListener.remove(listener);
        }

    }

    public class TransformerToolbar extends AbstractToolbar {

        private final List<ButtonGroup> buttonGroups = new ArrayList<>();

        public TransformerToolbar() {
        }

        private void clear() {
            //Clear precent buttons
            for (ButtonGroup bg : buttonGroups) {
                for (Enumeration<AbstractButton> btns = bg.getElements(); btns.hasMoreElements();) {
                    AbstractButton btn = btns.nextElement();
                    remove(btn);
                }
            }
            buttonGroups.clear();
        }

        protected void setup() {
            clear();
            if (model != null) {

                for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                    for (TransformerCategory c : controller.getCategories(elmtType)) {

                        ButtonGroup buttonGroup = new ButtonGroup();
                        Map<String, TransformerUI> titles = new LinkedHashMap<>();
                        for (TransformerUI t : controller.getTransformerUIs(elmtType, c)) {
                            titles.put(t.getDisplayName(), t);
                        }

                        for (Map.Entry<String, TransformerUI> entry : titles.entrySet()) {
                            //Build button
                            final TransformerUI value = entry.getValue();
                            Icon icon = entry.getValue().getIcon();
//                        DecoratedIcon decoratedIcon = getDecoratedIcon(icon, t);
//                        JToggleButton btn = new JToggleButton(decoratedIcon);
                            JToggleButton btn = new JToggleButton(icon);
                            btn.setToolTipText(entry.getValue().getDescription());
                            btn.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    controller.setSelectedTransformerUI(value);
                                }
                            });
                            btn.setName(entry.getKey());
                            btn.setText(entry.getKey());
                            btn.setFocusPainted(false);
                            buttonGroup.add(btn);
                            add(btn);
                        }
                        buttonGroups.add(buttonGroup);
                    }
                }
            }
        }

        protected void refreshTransformers() {
            if (model != null) {
                //Select the right transformer
                int index = 0;
                for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
                    for (TransformerCategory c : controller.getCategories(elmtType)) {
                        ButtonGroup g = buttonGroups.get(index);

                        boolean active = model.getSelectedElementClass().equals(elmtType) && model.getSelectedCategory().equals(c);
                        g.clearSelection();
                        TransformerUI t = model.getSelectedTransformerUI();

                        for (Enumeration<AbstractButton> btns = g.getElements(); btns.hasMoreElements();) {
                            AbstractButton btn = btns.nextElement();
                            btn.setVisible(active);
                            if (t != null && active && btn.getName().equals(t.getDisplayName())) {
                                g.setSelected(btn.getModel(), true);
                            }
                        }
                        index++;
                    }
                }
            }
        }
    }

    public class ControlToolbar extends AbstractToolbar {

        private transient final Set<AbstractButton> rankingSouthControls;
        private transient final Set<AbstractButton> partitionSouthControls;
        private transient final Set<AbstractButton> controlButtons;

        public ControlToolbar() {
            rankingSouthControls = new HashSet<>();
            partitionSouthControls = new HashSet<>();
            controlButtons = new HashSet<>();
        }

        public void addRankingButton(AbstractButton btn) {
            removeAll();
            rankingSouthControls.add(btn);
        }

        public void addPartitionButton(AbstractButton btn) {
            removeAll();
            partitionSouthControls.add(btn);
        }

        private void clear() {
            //Clear precent buttons
            for (AbstractButton btn : rankingSouthControls) {
                remove(btn);
            }
            for (AbstractButton btn : partitionSouthControls) {
                remove(btn);
            }
        }

        private void clearControlButtons() {
            for (AbstractButton btn : controlButtons) {
                remove(btn);
            }
            controlButtons.clear();
        }

        protected void setup() {
            clear();
            if (model != null) {
                removeAll();
                for (AbstractButton btn : partitionSouthControls) {
                    add(btn);
                }
                for (AbstractButton btn : rankingSouthControls) {
                    if (!partitionSouthControls.contains(btn)) {
                        add(btn);
                    }
                }
                JLabel box = new javax.swing.JLabel();
                box.setMaximumSize(new java.awt.Dimension(32767, 32767));
                addSeparator();
                add(box);
            }
        }

        protected void refreshControls() {
            if (model != null) {
                for (AbstractButton btn : partitionSouthControls) {
                    btn.setVisible(false);
                }
                for (AbstractButton btn : rankingSouthControls) {
                    btn.setVisible(false);
                }
                TransformerUI u = model.getSelectedTransformerUI();
                if (u != null && model.isAttributeTransformerUI(u)) {
                    //Ranking
                    Function selectedColumn = model.getSelectedFunction();
                    if (selectedColumn != null) {
                        if (selectedColumn.isRanking()) {
                            for (AbstractButton btn : rankingSouthControls) {
                                btn.setVisible(true);
                            }
                        } else if (selectedColumn.isPartition()) {
                            for (AbstractButton btn : partitionSouthControls) {
                                btn.setVisible(true);
                            }
                        }
                    }
                }
                clearControlButtons();
                if (u != null) {
                    Function selectedColumn = model.getSelectedFunction();
                    if (selectedColumn != null) {
                        AbstractButton[] bb = selectedColumn.getUI().getControlButton();
                        if (bb != null) {
                            for (AbstractButton b : bb) {
                                add(b);
                                controlButtons.add(b);
                            }
                        }
                    }
                }
            }
        }
//    private void refreshDecoratedIcons() {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                int index = 0;
//                for (String elmtType : AppearanceUIController.ELEMENT_CLASSES) {
//                    ButtonGroup g = buttonGroups.get(index++);
//                    boolean active = model == null ? false : model.getCurrentElementType().equals(elmtType);
//                    if (active) {
//                        for (Enumeration<AbstractButton> btns = g.getElements(); btns.hasMoreElements();) {
//                            btns.nextElement().repaint();
//                        }
//                    }
//                }
//            }
//        });
//    }
//    private DecoratedIcon getDecoratedIcon(Icon icon, final Transformer transformer) {
//        Icon decoration = ImageUtilities.image2Icon(ImageUtilities.loadImage("org/gephi/desktop/ranking/resources/chain.png", false));
//        return new DecoratedIcon(icon, decoration, new DecoratedIcon.DecorationController() {
//            @Override
//            public boolean isDecorated() {
//                return model != null && model.isAutoTransformer(transformer);
//            }
//        });
//    }
    }
}
