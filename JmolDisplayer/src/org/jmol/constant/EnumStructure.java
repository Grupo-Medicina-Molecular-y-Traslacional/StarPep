/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2011  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.constant;



public enum EnumStructure {
  
  // Note: These id numbers are non-negotiable. They are documented and 
  // accessible via {atom}.structure and {atom}.substructure
  // DO NOT CHANGE THEM!
  
  NOT(-1,"",0xFF808080),
  NONE(0,"none",0xFFFFFFFF),
  TURN(1,"turn",0xFF6080FF),
  SHEET(2,"sheet",0xFFFFC800),
  HELIX(3,"helix",0xFFFF0080),
  DNA(4,"dna",0xFFAE00FE),
  RNA(5,"rna",0xFFFD0162),
  CARBOHYDRATE(6,"carbohydrate",0xFFA6A6FA),
  HELIX_310(7,"helix310",0xFFA00080),
  HELIX_ALPHA(8,"helixalpha",0xFFFF0080),
  HELIX_PI(9,"helixpi",0xFF600080);
  
  private int id;
  private String name;
  private int color;
  
  private EnumStructure(int id, String name, int color) {
    this.id = id;
    this.name = name;
    this.color = color;
  }
  
  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
 
  public int getColor() {
    return color;
  }


  /****************************************************************
   * In DRuMS, RasMol, and Chime, quoting from
   * http://www.umass.edu/microbio/rasmol/rascolor.htm
   *
   *The RasMol structure color scheme colors the molecule by
   *protein secondary structure.
   *
   *Structure                   Decimal RGB    Hex RGB
   *Alpha helices  red-magenta  [255,0,128]    FF 00 80  *
   *Beta strands   yellow       [255,200,0]    FF C8 00  *
   *
   *Turns          pale blue    [96,128,255]   60 80 FF
   *Other          white        [255,255,255]  FF FF FF
   *
   **Values given in the 1994 RasMol 2.5 Quick Reference Card ([240,0,128]
   *and [255,255,0]) are not correct for RasMol 2.6-beta-2a.
   *This correction was made above on Dec 5, 1998.
   * @param name 
   * @return     0-3 or 7-9, but not dna, rna, carbohydrate
   ****************************************************************/
  public final static EnumStructure getProteinStructureType(String name) {
    for (EnumStructure item : values())
      if (name.equalsIgnoreCase(item.name))
        return (item.isProtein() ? item : NOT);
    return NOT;
  }

  public String getBioStructureTypeName(boolean isGeneric) {
    return (isGeneric && isProtein() ? "protein" : name);
  }

  private boolean isProtein() {
    return id >= 0 && id <= 3 || id >= 7;
  }
}
