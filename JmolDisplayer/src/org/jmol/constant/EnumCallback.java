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

public enum EnumCallback {

  ANIMFRAME("animFrameCallback"),
  APPLETREADY("appletReadyCallback"),
  ATOMMOVED("atomMovedCallback"),
  CLICK("clickCallback"),
  ECHO("echoCallback"),
  ERROR("errorCallback"),
  EVAL("evalCallback"),
  HOVER("hoverCallback"),
  LOADSTRUCT("loadStructCallback"),
  MEASURE("measureCallback"),
  MESSAGE("messageCallback"),
  MINIMIZATION("minimizationCallback"),
  PICK("pickCallback"),
  RESIZE("resizeCallback"),
  SCRIPT("scriptCallback"),
  SYNC("syncCallback");

  private final String name;

  private EnumCallback(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static EnumCallback getCallback(String name) {
    for (EnumCallback item : values())
      if (item.getName().equalsIgnoreCase(name))
        return item;
    return null;
  }

  private final static String nameList;

  static {
    StringBuffer names = new StringBuffer();
    for (EnumCallback item : values())
      names.append(item.getName()).append(';');
    nameList = names.toString();
  }
  
  public static synchronized String getNameList() {
    return nameList;
  }
}
