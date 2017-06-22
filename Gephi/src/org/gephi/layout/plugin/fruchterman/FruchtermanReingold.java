/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Jacomy
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
package org.gephi.layout.plugin.fruchterman;

import java.util.ArrayList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.plugin.ForceVectorNodeLayoutData;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Jacomy
 */
public class FruchtermanReingold extends AbstractLayout {

    private static final float SPEED_DIVISOR = 800;
    private static final float AREA_MULTIPLICATOR = 10000;
    //Properties
    private float area;
    private double gravity;
    private double speed;
    private Node[] nodes;
    private Edge[] edges;
    private List<AlgorithmProperty> properties;

    public FruchtermanReingold(AlgorithmFactory layoutBuilder) {
        super(layoutBuilder);
        resetPropertiesValues();
        createProperties();
    }

    private void resetPropertiesValues() {
        speed = 1;
        area = 10000;
        gravity = 10;
    }
    
    private void createProperties(){
        properties = new ArrayList<>();
        final String FRUCHTERMAN_REINGOLD = "Fruchterman Reingold";

        try {
            properties.add(AlgorithmProperty.createProperty(
                    this, Float.class,
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.area.name"),
                    FRUCHTERMAN_REINGOLD,
                    "fruchtermanReingold.area.name",
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.area.desc"),
                    "getArea", "setArea"));
            properties.add(AlgorithmProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.gravity.name"),
                    FRUCHTERMAN_REINGOLD,
                    "fruchtermanReingold.gravity.name",
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.gravity.desc"),
                    "getGravity", "setGravity"));
            properties.add(AlgorithmProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.speed.name"),
                    FRUCHTERMAN_REINGOLD,
                    "fruchtermanReingold.speed.name",
                    NbBundle.getMessage(FruchtermanReingold.class, "fruchtermanReingold.speed.desc"),
                    "getSpeed", "setSpeed"));
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    @Override
    public void initLayout() {
        nodes = graph.getNodes().toArray();
        edges = graph.getEdges().toArray();

        for (Node n : nodes) {
            n.setLayoutData(new ForceVectorNodeLayoutData());
            ForceVectorNodeLayoutData layoutData = n.getLayoutData();
            layoutData.dx = 0;
            layoutData.dy = 0;
        }
    }

    @Override
    public void runLayout() {
        for (Node n : nodes) {
            if (!canLayout()){
                return;
            }             
            if (n.getLayoutData() == null || !(n.getLayoutData() instanceof ForceVectorNodeLayoutData)) {
                n.setLayoutData(new ForceVectorNodeLayoutData());
            }
            ForceVectorNodeLayoutData layoutData = n.getLayoutData();
            layoutData.dx = 0;
            layoutData.dy = 0;
        }

        float maxDisplace = (float) (Math.sqrt(AREA_MULTIPLICATOR * area) / 10f);					// Déplacement limite : on peut le calibrer...
        float k = (float) Math.sqrt((AREA_MULTIPLICATOR * area) / (1f + nodes.length));		// La variable k, l'idée principale du layout.

        for (Node N1 : nodes) {
            for (Node N2 : nodes) {	// On fait toutes les paires de noeuds
            if (!canLayout()){
                return;
            }                 
                if (N1 != N2) {
                    float xDist = N1.x() - N2.x();	// distance en x entre les deux noeuds
                    float yDist = N1.y() - N2.y();
                    float dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

                    if (dist > 0) {
                        float repulsiveF = k * k / dist;			// Force de répulsion
                        ForceVectorNodeLayoutData layoutData = N1.getLayoutData();
                        layoutData.dx += xDist / dist * repulsiveF;		// on l'applique...
                        layoutData.dy += yDist / dist * repulsiveF;
                    }
                }
            }
        }
        for (Edge E : edges) {
            if (!canLayout()){
                return;
            }             
            // Idem, pour tous les noeuds on applique la force d'attraction
            Node Nf = E.getSource();
            Node Nt = E.getTarget();

            float xDist = Nf.x() - Nt.x();
            float yDist = Nf.y() - Nt.y();
            float dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);

            float attractiveF = dist * dist / k;

            if (dist > 0) {
                ForceVectorNodeLayoutData sourceLayoutData = Nf.getLayoutData();
                ForceVectorNodeLayoutData targetLayoutData = Nt.getLayoutData();
                sourceLayoutData.dx -= xDist / dist * attractiveF;
                sourceLayoutData.dy -= yDist / dist * attractiveF;
                targetLayoutData.dx += xDist / dist * attractiveF;
                targetLayoutData.dy += yDist / dist * attractiveF;
            }
        }
        // gravity
        for (Node n : nodes) {
            if (!canLayout()){
                return;
            }             
            ForceVectorNodeLayoutData layoutData = n.getLayoutData();
            float d = (float) Math.sqrt(n.x() * n.x() + n.y() * n.y());
            float gf = 0.01f * k * (float) gravity * d;
            layoutData.dx -= gf * n.x() / d;
            layoutData.dy -= gf * n.y() / d;
        }
        // speed
        for (Node n : nodes) {
            if (!canLayout()){
                return;
            }             
            ForceVectorNodeLayoutData layoutData = n.getLayoutData();
            layoutData.dx *= speed / SPEED_DIVISOR;
            layoutData.dy *= speed / SPEED_DIVISOR;
        }
        for (Node n : nodes) {
            if (!canLayout()){
                return;
            }             
            // Maintenant on applique le déplacement calculé sur les noeuds.
            // nb : le déplacement à chaque passe "instantanné" correspond à la force : c'est une sorte d'accélération.
            ForceVectorNodeLayoutData layoutData = n.getLayoutData();
            float xDist = layoutData.dx;
            float yDist = layoutData.dy;
            float dist = (float) Math.sqrt(layoutData.dx * layoutData.dx + layoutData.dy * layoutData.dy);
            if (dist > 0 && !n.isFixed()) {
                float limitedDist = Math.min(maxDisplace * ((float) speed / SPEED_DIVISOR), dist);
                n.setX(n.x() + xDist / dist * limitedDist);
                n.setY(n.y() + yDist / dist * limitedDist);
            }
        }
    }

    @Override
    public void endLayout() {
        nodes = null;
        edges = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    public Float getArea() {
        return area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    /**
     * @return the gravity
     */
    public Double getGravity() {
        return gravity;
    }

    /**
     * @param gravity the gravity to set
     */
    public void setGravity(Double gravity) {
        this.gravity = gravity;
    }

    /**
     * @return the speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
