/*
 Copyright 2008-2010 Gephi
 Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.plugin.random;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class RandomLayout extends AbstractLayout {

    private Random random;
    private double size;
    private List<AlgorithmProperty> properties;

    public RandomLayout(AlgorithmFactory layoutBuilder, double size) {
        super(layoutBuilder);
        this.size = size;
        createProperties();
    }

    private void createProperties() {
        properties = new LinkedList<>();
        try {
            properties.add(AlgorithmProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "Random.spaceSize.name"),
                    null,
                    "Random.spaceSize.name",
                    NbBundle.getMessage(getClass(), "Random.spaceSize.desc"),
                    "getSize", "setSize"));
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    @Override
    public void initLayout() {
        random = new Random();
    }

    @Override
    public void runLayout() {
        for (Node n : graph.getNodes()) {
            if (!canLayout()) {
                return;
            }              
            n.setX((float) (-size / 2 + size * random.nextDouble()));
            n.setY((float) (-size / 2 + size * random.nextDouble()));
        }
        setConverged(true);
    }

    @Override
    public void endLayout() {
        random = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getSize() {
        return size;
    }
}
