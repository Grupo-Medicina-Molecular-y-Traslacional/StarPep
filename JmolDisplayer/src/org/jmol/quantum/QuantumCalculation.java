/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-05-13 19:17:06 -0500 (Sat, 13 May 2006) $
 * $Revision: 5114 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
package org.jmol.quantum;

import javax.vecmath.Point3f;

import org.jmol.modelset.Atom;
import org.jmol.util.Logger;

import java.util.BitSet;
import org.netbeans.jmol.displayer.Utilities;

abstract class QuantumCalculation {

  protected boolean doDebug = false;
  protected BitSet bsExcluded;

  protected final static float bohr_per_angstrom = 1 / 0.52918f;

  protected float[][][] voxelData;
  protected float[] vd;
  protected int[] countsXYZ;
  
  protected Point3f[] points;
  protected int xMin, xMax, yMin, yMax, zMin, zMax;

  protected QMAtom[] qmAtoms;
  protected int atomIndex;
  protected QMAtom thisAtom;
  protected int firstAtomOffset;

  // absolute grid coordinates in Bohr
  // these values may change if the reader
  // is switching between reading surface points 
  // and getting values for them during a 
  // progressive calculation.
  
  protected float[] xBohr, yBohr, zBohr;
  protected float[] originBohr = new float[3];
  protected float[] stepBohr = new float[3];
  protected int nX, nY, nZ;
  
  // grid coordinates relative to orbital center in Bohr 
  protected float[] X, Y, Z;

  // grid coordinate squares relative to orbital center in Bohr
  protected float[] X2, Y2, Z2;

  // range in bohr to consider affected by an atomic orbital
  // this is a cube centered on an atom of side rangeBohr*2
  protected float rangeBohrOrAngstroms = 10; //bohr; about 5 Angstroms
  
  protected float unitFactor = bohr_per_angstrom;

  protected void initialize(int nX, int nY, int nZ, Point3f[] points) {
    if (points != null) {
      this.points = points;
      nX = nY = nZ = points.length;
    }
    
    this.nX = xMax = nX;
    this.nY = yMax = nY;
    this.nZ = zMax = nZ;
    
    if (xBohr != null && xBohr.length >= nX)
      return;
    
    // absolute grid coordinates in Bohr
    xBohr = new float[nX];
    yBohr = new float[nY];
    zBohr = new float[nZ];

    // grid coordinates relative to orbital center in Bohr 
    X = new float[nX];
    Y = new float[nY];
    Z = new float[nZ];

    // grid coordinate squares relative to orbital center in Bohr
    X2 = new float[nX];
    Y2 = new float[nY];
    Z2 = new float[nZ];
  }

  protected float volume = 1;

  protected void setupCoordinates(float[] originXYZ, float[] stepsXYZ,
                                  BitSet bsSelected,
                                  Point3f[] atomCoordAngstroms,
                                  Point3f[] points, boolean renumber) {

    // all coordinates come in as angstroms, not bohr, and are converted here into bohr

    if (points == null) {
      volume = 1;
      for (int i = 3; --i >= 0;) {
        originBohr[i] = originXYZ[i] * unitFactor;
        stepBohr[i] = stepsXYZ[i] * unitFactor;
        volume *= stepBohr[i];
      }
      Utilities.getIO().getOut().println("QuantumCalculation:\n origin(Bohr)= " + originBohr[0] + " "
          + originBohr[1] + " " + originBohr[2] + "\n steps(Bohr)= "
          + stepBohr[0] + " " + stepBohr[1] + " " + stepBohr[2] + "\n counts= "
          + nX + " " + nY + " " + nZ);
    }

    /* 
     * allowing null atoms allows for selectively removing
     * atoms from the rendering. Maybe a first time this has ever been done?
     * 
     */

    if (atomCoordAngstroms != null) {
      qmAtoms = new QMAtom[renumber ? bsSelected.cardinality()
          : atomCoordAngstroms.length];
      boolean isAll = (bsSelected == null);
      int i0 = (isAll ? qmAtoms.length - 1 : bsSelected.nextSetBit(0));
      for (int i = i0, j = 0; i >= 0; i = (isAll ? i - 1 : bsSelected
          .nextSetBit(i + 1)))
        qmAtoms[renumber ? j++ : i] = new QMAtom(i, (Atom) atomCoordAngstroms[i],
            X, Y, Z, X2, Y2, Z2);
    }
  }

  public float process(Point3f pt) {
    doDebug = false;
    if (points == null || nX != 1)
      initializeOnePoint();
    points[0].set(pt);
    voxelData[0][0][0] = 0;
    setXYZBohr(points);
    processPoints();
    //System.out.println("qc pt=" + pt + " " + voxelData[0][0][0]);
    return voxelData[0][0][0];
  }

  protected void processPoints() {
    process();
  }

  protected void initializeOnePoint() {
    points = new Point3f[1];
    points[0] = new Point3f();
    voxelData = new float[1][1][1];
    xMin = yMin = zMin = 0;
    initialize(1, 1, 1, points);
  }

  protected abstract void process();
  
  protected void setXYZBohr(Point3f[] points) {
    setXYZBohr(xBohr, 0, nX, points);
    setXYZBohr(yBohr, 1, nY, points);
    setXYZBohr(zBohr, 2, nZ, points);
  }

  private void setXYZBohr(float[] bohr, int i, int n, Point3f[] points) {
    if (points != null) {
      float x = 0;
      for (int j = 0; j < n; j++) {
        switch (i) {
        case 0:
          x = points[j].x;
          break;
        case 1:
          x = points[j].y;
          break;
        case 2:
          x = points[j].z;
          break;
        }
        bohr[j] = x * unitFactor;
      }
      return;
    }
    bohr[0] = originBohr[i];
    float inc = stepBohr[i];
    for (int j = 0; ++j < n;)
      bohr[j] = bohr[j - 1] + inc;
  }

  protected void setMinMax(int ix) {
    yMax = zMax = (ix < 0 ? xMax : ix + 1);
    yMin = zMin = (ix < 0 ? 0 : ix);    
  }

  class QMAtom extends Point3f {

    // grid coordinates relative to orbital center in Bohr 
    private float[] myX, myY, myZ;

    // grid coordinate squares relative to orbital center in Bohr
    private float[] myX2, myY2, myZ2;

    Atom atom;
    int index;
    int znuc;
    int iMolecule;
    boolean isExcluded;

    QMAtom(int i, Atom atom, float[] X, float[] Y, float[] Z, 
        float[] X2, float[] Y2, float[] Z2) {
      index = i;
      myX = X;
      myY = Y;
      myZ = Z;
      myX2 = X2;
      myY2 = Y2;
      myZ2 = Z2;
      this.atom = atom;
      
      isExcluded = (bsExcluded != null && bsExcluded.get(i));
      set(atom);
      scale(unitFactor);
      znuc = atom.getElementNumber();
    }

    protected void setXYZ(boolean setMinMax) {
      int i;
      try {
        if (setMinMax) {
          if (points != null) {
            xMin = yMin = zMin = 0;
            xMax = yMax = zMax = points.length;
          } else {
            i = (int) Math.floor((x - xBohr[0] - rangeBohrOrAngstroms)
                / stepBohr[0]);
            xMin = (i < 0 ? 0 : i);
            i = (int) Math.floor(1 + (x - xBohr[0] + rangeBohrOrAngstroms)
                / stepBohr[0]);
            xMax = (i >= nX ? nX : i + 1);
            i = (int) Math.floor((y - yBohr[0] - rangeBohrOrAngstroms)
                / stepBohr[1]);
            yMin = (i < 0 ? 0 : i);
            i = (int) Math.floor(1 + (y - yBohr[0] + rangeBohrOrAngstroms)
                / stepBohr[1]);
            yMax = (i >= nY ? nY : i + 1);
            i = (int) Math.floor((z - zBohr[0] - rangeBohrOrAngstroms)
                / stepBohr[2]);
            zMin = (i < 0 ? 0 : i);
            i = (int) Math.floor(1 + (z - zBohr[0] + rangeBohrOrAngstroms)
                / stepBohr[2]);
            zMax = (i >= nZ ? nZ : i + 1);
          }
        }
        for (i = xMax; --i >= xMin;) {
          myX2[i] = myX[i] = xBohr[i] - x;
          myX2[i] *= myX[i];
        }
        for (i = yMax; --i >= yMin;) {
          myY2[i] = myY[i] = yBohr[i] - y;
          myY2[i] *= myY[i];
        }
        for (i = zMax; --i >= zMin;) {
          myZ2[i] = myZ[i] = zBohr[i] - z;
          myZ2[i] *= myZ[i];
        }
        if (points != null) {
          yMax = zMax = 1;
        }

      } catch (Exception e) {
        Logger.error("Error in QuantumCalculation setting bounds");
      }
    }
  }
  
}
