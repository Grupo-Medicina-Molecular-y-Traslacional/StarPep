
/*
 Copyright 2008-2010 Gephi
 Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.visualization.spi;

import javax.swing.Icon;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 * <b>Please note that the methods offered in this service are the same as Data
 * Laboratory nodes manipulators. It is possible to reuse actions
 * implementations by adding both <code>ServiceProvider</code> annotations.</b>
 * <p>
 * Interface from providing graph context menu items as services.
 * <p>
 * All context menu items are able to:
 * <ul>
 * <li>Execute an action</li>
 * <li>Provide a name, type and order of appearance (position in group of its
 * type)</li>
 * <li>Indicate whether they have to be available (appear in the context menu)
 * or not</li>
 * <li>Indicate whether they have to be executable (enabled in the context menu)
 * or not</li>
 * <li>Provide and icon or not</li>
 * </ul>
 * <p>
 * Used for different manipulators such as NodesManipulator, EdgesManipulator
 * and GeneralActionsManipulator.
 * <p>
 * The only methods that are called before setting up an item with the data are
 * <b>getSubItems, getType and getPosition.</b>
 * This way, the other methods behaviour can depend on the data that has been
 * setup before
 * <p>
 * <b>getSubItems will be called before and after setup. Take care when the
 * nodes are null!</b>
 *
 * To provide a context menu item, a class has to implement this interface and
 * have a <code>@ServiceProvider</code> annotation
 *
 * @author Eduardo Ramos
 */
public interface GraphContextMenuItem {

    /**
     * Prepare nodes for this item. Note that nodes could contain 0 nodes.
     *
     * @param graph graph
     * @param nodes All selected nodes
     */
    public void setup(Graph graph, Node[] nodes);
    
    /**
     * <p>Return name to show for this Manipulator on the ui.</p>
     * <p>Implementations can provide different names depending on the data this
     * Manipulator has (for example depending on the number of nodes in a NodesManipulator).</p>
     * @return Name to show at current time and conditions
     */
    String getName();

    /**
     * Description of the Manipulator.
     * @return Description
     */
    String getDescription();

    /**
     * Indicates if this Manipulator has to be executable.
     * Implementations should evaluate the current data and conditions.
     * @return True if it has to be executable, false otherwise
     */
    boolean canExecute();
    
    /**
     * Execute this Manipulator.
     * It will operate with data like nodes and edges previously setup for the type of manipulator.
     */
    void execute();    
    
    /**
     * Type of manipulator. This is used for separating the manipulators
     * in groups when shown, using popup separators. First types to show will be the lesser.
     * @return Type of this manipulator
     */
    int getType(); 

    /**
     * Indicates if this item has to appear in the context menu at all
     * @return True to show, false otherwise
     */
    boolean isAvailable();

        /**
     * <p>This is optional. Return sub items for this menu item if desired.</p>
     * <p>If this item should contain more items, return a new instance of each sub item.
     * If not return null and implement execute for this item.</p>
     * <p>In order to declare mnemonic keys for subitem(s), the implementation of this item
     * must return the subitem(s) with the mnemonic even when it has not been setup.
     * If you don't need a mnemonic, return null if the item is not setup.</p>
     * <p>Returned items have to be of the same type as the subinterface (NodesManipulator for example)</p>
     * @return sub items
     */
    GraphContextMenuItem[] getSubItems();
    
    /**
     * Returns an icon for this manipulator if necessary.
     * @return Icon for the manipulator or null
     */
    Icon getIcon();

        /**
     * Optional. Allows to declare a mnemonic key for this item in the menu.
     * There should not be 2 items with the same mnemonic at the same time.
     * @return Integer from <code>KeyEvent</code> values or null
     */
    Integer getMnemonicKey();
    
    /**
     * Returns a position value that indicates the position
     * of this Manipulator in its type group. Less means upper.
     * @return This Manipulator position
     */
    int getPosition();    
}
