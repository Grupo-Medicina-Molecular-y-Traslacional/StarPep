/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-09-25 02:21:24 +0200 (dim., 25 sept. 2011) $
 * $Revision: 16111 $

 *
 * Copyright (C) 2003-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.shape;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;

public class BallsRenderer extends ShapeRenderer {

  @Override
  protected void render() {

    // also crosshairs for navigation mode

    if (!viewer.getWireframeRotation() || !viewer.getInMotion()) {
      Atom[] atoms = modelSet.atoms;
      BitSet bsOK = viewer.getRenderableBitSet();
      for (int i = bsOK.nextSetBit(0); i >= 0; i = bsOK.nextSetBit(i + 1)) {
        Atom atom = atoms[i];
        if (atom.screenDiameter > 0
            && (atom.getShapeVisibilityFlags() & myVisibilityFlag) != 0
            && g3d.setColix(atom.getColix())) {
          g3d.drawAtom(atom);
        }
      }
    }

    // this is the square and crosshairs for the navigator
    int[] minMax = viewer.getCrossHairMinMax();
    if (minMax[0] != Integer.MAX_VALUE) {
      Point3f navOffset = new Point3f(viewer.getNavigationOffset());
      boolean antialiased = g3d.isAntialiased();
      float navDepth = viewer.getNavigationDepthPercent();
      g3d.setColix(navDepth < 0 ? Graphics3D.RED
          : navDepth > 100 ? Graphics3D.GREEN : Graphics3D.GOLD);
      int x = Math.max(Math.min(viewer.getScreenWidth(), (int) navOffset.x), 0);
      int y = Math
          .max(Math.min(viewer.getScreenHeight(), (int) navOffset.y), 0);
      int z = (int) navOffset.z + 1;
      // TODO: fix for antialiasDisplay
      int off = (antialiased ? 8 : 4);
      int h = (antialiased ? 20 : 10);
      int w = (antialiased ? 2 : 1);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.drawRect(x - off, y - off, z, 0, h, h);
      off = h;
      h = h >> 1;
      g3d.setColix(minMax[1] < navOffset.x ? Graphics3D.YELLOW
          : Graphics3D.GREEN);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.setColix(minMax[0] > navOffset.x ? Graphics3D.YELLOW
          : Graphics3D.GREEN);
      g3d.drawRect(x + h, y, z, 0, h, w);
      g3d.setColix(minMax[3] < navOffset.y ? Graphics3D.YELLOW
          : Graphics3D.GREEN);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.setColix(minMax[2] > navOffset.y ? Graphics3D.YELLOW
          : Graphics3D.GREEN);
      g3d.drawRect(x, y + h, z, 0, w, h);
    }
  }
}
