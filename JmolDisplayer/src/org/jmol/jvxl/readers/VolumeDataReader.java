/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-30 11:40:16 -0500 (Fri, 30 Mar 2007) $
 * $Revision: 7273 $
 *
 * Copyright (C) 2007 Miguel, Bob, Jmol Development
 *
 * Contact: hansonr@stolaf.edu
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;

import org.jmol.atomdata.AtomDataServer;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.Logger;
import org.netbeans.jmol.displayer.Utilities;

class VolumeDataReader extends SurfaceReader {

  /*        (requires AtomDataServer)
   *                |-- IsoSolventReader
   *                |-- IsoIntersectReader
   *                |-- IsoMOReader, IsoMepReader
   *                |-- IsoPlaneReader
   *                |
   *            AtomDataReader (abstract)
   *                |
   *                |         |-- IsoFxyReader (not precalculated)
   *                |         |-- IsoShapeReader (not precalculated)  
   *                |         |         
   *            VolumeDataReader (precalculated data)       
   *                   |
   *                SurfaceReader
   * 
   * 
   */
  
  protected int dataType;
  protected boolean precalculateVoxelData;
  protected boolean allowMapData;
  protected Point3f point;
  protected float ptsPerAngstrom;
  protected int maxGrid;
  protected AtomDataServer atomDataServer;
  protected boolean useOriginStepsPoints;

  VolumeDataReader(SurfaceGenerator sg) {
    super(sg);
    useOriginStepsPoints = (params.origin != null && params.points != null && params.steps != null);
    dataType = params.dataType;
    precalculateVoxelData = true;
    allowMapData = true;    
  }
  
  /**
   * @param isMapData  
   */
  void setup(boolean isMapData) {
    //as is, just the volumeData as we have it.
    //but subclasses can modify this behavior.
    jvxlFileHeaderBuffer = new StringBuffer("volume data read from file\n\n");
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
  }
  
  @Override
  protected boolean readVolumeParameters(boolean isMapData) {
    setup(isMapData);
    initializeVolumetricData();
    return true;
  }

  @Override
  protected boolean readVolumeData(boolean isMapData) {
    try {
      readSurfaceData(isMapData);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  protected void readVoxelDataIndividually(boolean isMapData) throws Exception {
    if (isMapData && !allowMapData)
      return; //not applicable
    if (!isMapData || volumeData.sr != null) {
      volumeData.setVoxelData(voxelData = null);
      return;      
    }
    newVoxelDataCube();
    for (int x = 0; x < nPointsX; ++x) {
      float[][] plane = new float[nPointsY][];
      voxelData[x] = plane;
      int ptyz = 0;
      for (int y = 0; y < nPointsY; ++y) {
        float[] strip = plane[y] = new float[nPointsZ];
        for (int z = 0; z < nPointsZ; ++z, ++ptyz) {
          strip[z] = getValue(x, y, z, ptyz);
        }
      }
    }
  }
  
  protected void setVolumeData() {
    // required; just that this is not an abstract class;
  }
  
  protected boolean setVolumeDataParams() {
    if (params.volumeData != null) {
      setVolumeData(params.volumeData);
      return true;
    }
    if (!useOriginStepsPoints) {
      return false;
    }
    volumetricOrigin.set(params.origin);
    volumetricVectors[0].set(params.steps.x, 0, 0);
    volumetricVectors[1].set(0, params.steps.y, 0);
    volumetricVectors[2].set(0, 0, params.steps.z);
    voxelCounts[0] = (int) params.points.x;
    voxelCounts[1] = (int) params.points.y;
    voxelCounts[2] = (int) params.points.z;
    if (voxelCounts[0] < 1 || voxelCounts[1] < 1 || voxelCounts[2] < 1)
      return false;
    showGridInfo();
    return true;
  }  

  protected void showGridInfo() {
    Utilities.getIO().getOut().println("grid origin  = " + params.origin);
    Utilities.getIO().getOut().println("grid steps   = " + params.steps);
    Utilities.getIO().getOut().println("grid points  = " + params.points);
    ptTemp.x = params.steps.x * params.points.x;
    ptTemp.y = params.steps.y * params.points.y;
    ptTemp.z = params.steps.z * params.points.z;
    Utilities.getIO().getOut().println("grid lengths = " + ptTemp);
    ptTemp.add(params.origin);
    Utilities.getIO().getOut().println("grid max xyz = " + ptTemp);
  }
  protected int setVoxelRange(int index, float min, float max,
                              float ptsPerAngstrom, int gridMax) {
    int nGrid;
    float d;
    if (min >= max) {
      min = -10;
      max = 10;
    }
    float range = max - min;
    float resolution = params.resolution;
    if (resolution != Float.MAX_VALUE) {
      ptsPerAngstrom = resolution;
    }
    nGrid = (int) (range * ptsPerAngstrom) + 1;
    if (nGrid > gridMax) {
      if ((dataType & Parameters.HAS_MAXGRID) > 0) {
        if (resolution == Float.MAX_VALUE) {
          if (!isQuiet)
            Utilities.getIO().getOut().println("Maximum number of voxels for index=" + index
                + " exceeded (" + nGrid + ") -- set to " + gridMax);
          nGrid = gridMax;
        } else {
          if (!isQuiet)
            Utilities.getIO().getOut().println("Warning -- high number of grid points: " + nGrid);
        }
      } else if (resolution == Float.MAX_VALUE) {
        nGrid = gridMax;
      }
    }
    ptsPerAngstrom = (nGrid - 1) / range;
    d = volumeData.volumetricVectorLengths[index] = 1f / ptsPerAngstrom;
    voxelCounts[index] = nGrid;// + ((dataType & Parameters.IS_SOLVENTTYPE) != 0 ? 3 : 0);
    if (!isQuiet)
      Utilities.getIO().getOut().println("isosurface resolution for axis " + (index + 1) + " set to "
          + ptsPerAngstrom + " points/Angstrom; " + voxelCounts[index]
          + " voxels");
    switch (index) {
    case 0:
      volumetricVectors[0].set(d, 0, 0);
      volumetricOrigin.x = min;
      break;
    case 1:
      volumetricVectors[1].set(0, d, 0);
      volumetricOrigin.y = min;
      break;
    case 2:
      volumetricVectors[2].set(0, 0, d);
      volumetricOrigin.z = min;
      if (isEccentric)
        eccentricityMatrix.transform(volumetricOrigin);
      if (center.x != Float.MAX_VALUE)
        volumetricOrigin.add(center);
      
      //System.out.println("/*volumeDatareader*/draw vector @{point" + volumetricOrigin + "} {" 
        // +volumetricVectors[0].x * voxelCounts[0] 
        //  + " " + volumetricVectors[1].y *voxelCounts[1] 
        //  + " " + volumetricVectors[2].z *voxelCounts[2] + "}");
      
    }
    if (isEccentric)
      eccentricityMatrix.transform(volumetricVectors[index]);
    return voxelCounts[index];
  }

  @Override
  protected void readSurfaceData(boolean isMapData) throws Exception {
    //precalculated -- just creating the JVXL equivalent
    Logger.startTimer();
    if (isProgressive && !isMapData) {
      nDataPoints = volumeData.setVoxelCounts(nPointsX, nPointsY, nPointsZ);
      voxelData = null;
      return;
    }
    if (precalculateVoxelData) 
      generateCube();
    else
      readVoxelDataIndividually(isMapData);
  }
  
  protected void generateCube() {
    Utilities.getIO().getOut().println("data type: user volumeData");
    Utilities.getIO().getOut().println("voxel grid origin:" + volumetricOrigin);
    for (int i = 0; i < 3; ++i)
      Utilities.getIO().getOut().println("voxel grid vector:" + volumetricVectors[i]);
    Utilities.getIO().getOut().println("Read " + nPointsX + " x " + nPointsY + " x " + nPointsZ
        + " data points");
  }

  @Override
  protected void closeReader() {
    // unnecessary -- no file opened
  }

 }
