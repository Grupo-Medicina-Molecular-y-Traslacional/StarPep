/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-10-03 01:35:13 +0200 (lun., 03 oct. 2011) $
 * $Revision: 16205 $
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
package org.jmol.g3d;

import org.jmol.api.ApiPlatform;

/**
 *<p>
 * Provides font support using a byte fid
 * (<strong>F</strong>ont <strong>ID</strong>) as an index into font table.
 *</p>
 *<p>
 * Supports standard font faces, font styles, and font sizes.
 *</p>
 *
 * @author Miguel, miguel@jmol.org
 */
final public class Font3D {

  public final byte fid;
  public final String fontFace;
  public final String fontStyle;
  public final float fontSizeNominal;
  public final int idFontFace;
  public final int idFontStyle;
  public final float fontSize;
  public final Object font;
  public final Object fontMetrics;
  private ApiPlatform apiPlatform;

  private Font3D(ApiPlatform apiPlatform, byte fid,
                 int idFontFace, int idFontStyle, float fontSize,
                 float fontSizeNominal, Object graphics) {
    this.apiPlatform = apiPlatform;
    this.fid = fid;
    this.fontFace = fontFaces[idFontFace];
    this.fontStyle = fontStyles[idFontStyle];
    this.idFontFace = idFontFace;
    this.idFontStyle = idFontStyle;
    this.fontSize = fontSize;
    this.fontSizeNominal = fontSizeNominal;
    font = apiPlatform.newFont(fontFaces[idFontFace], 
        (idFontStyle & FONT_STYLE_BOLD) == FONT_STYLE_BOLD,
        (idFontStyle & FONT_STYLE_ITALIC) == FONT_STYLE_ITALIC, 
        fontSize);
    fontMetrics = apiPlatform.getFontMetrics(graphics, font);
    //System.out.println("font3d constructed for fontsizeNominal=" + fontSizeNominal + "  and fontSize=" + fontSize);
  }

  ////////////////////////////////////////////////////////////////
  
  private final static int FONT_ALLOCATION_UNIT = 8;
  private static int fontkeyCount = 1;
  private static int[] fontkeys = new int[FONT_ALLOCATION_UNIT];
  private static Font3D[] font3ds = new Font3D[FONT_ALLOCATION_UNIT];

  static synchronized Font3D getFont3D(int fontface, int fontstyle,
                                       float fontsize, float fontsizeNominal,
                                       Platform3D platform) {
    if (platform == null)
      return null;
    if (fontsize > 0xFF)
      fontsize = 0xFF;
    int fontsizeX16 = ((int) fontsize) << 4;
    int fontkey = ((fontface & 3) | ((fontstyle & 3) << 2) | (fontsizeX16 << 4));
    // watch out for race condition here!
    for (int i = fontkeyCount; --i > 0;)
      if (fontkey == fontkeys[i]
          && font3ds[i].fontSizeNominal == fontsizeNominal)
        return font3ds[i];
    /*
    return allocFont3D(fontkey, fontface, fontstyle, fontsize, 
        fontsizeNominal, platform);
    }

    private static synchronized Font3D allocFont3D(int fontkey, int fontface,
                                                int fontstyle, float fontsize, float fontsizeNominal,
                                                Platform3D platform) {
    // recheck in case another process just allocated one
    for (int i = fontkeyCount; --i > 0; )
      if (fontkey == fontkeys[i] && font3ds[i].fontSizeNominal == fontsizeNominal)
        return font3ds[i];
        */
    int fontIndexNext = fontkeyCount++;
    if (fontIndexNext == fontkeys.length) {
      int[] t0 = new int[fontIndexNext + FONT_ALLOCATION_UNIT];
      System.arraycopy(fontkeys, 0, t0, 0, fontIndexNext);
      fontkeys = t0;

      Font3D[] t1 = new Font3D[fontIndexNext + FONT_ALLOCATION_UNIT];
      System.arraycopy(font3ds, 0, t1, 0, fontIndexNext);
      font3ds = t1;
    }
    Font3D font3d = new Font3D(platform.apiPlatform, (byte) fontIndexNext, fontface, fontstyle,
        fontsize, fontsizeNominal, platform.graphicsOffscreen);
    // you must set the font3d before setting the fontkey in order
    // to prevent a race condition with getFont3D
    font3ds[fontIndexNext] = font3d;
    fontkeys[fontIndexNext] = fontkey;
    return font3d;
  }

  public final static int FONT_FACE_SANS  = 0;
  public final static int FONT_FACE_SERIF = 1;
  public final static int FONT_FACE_MONO  = 2;
  
  private final static String[] fontFaces =
  {"SansSerif", "Serif", "Monospaced", ""};

  public final static int FONT_STYLE_PLAIN      = 0;
  public final static int FONT_STYLE_BOLD       = 1;
  public final static int FONT_STYLE_ITALIC     = 2;
  public final static int FONT_STYLE_BOLDITALIC = 3;
  
  private final static String[] fontStyles =
  {"Plain", "Bold", "Italic", "BoldItalic"};
  
  public static int getFontFaceID(String fontface) {
    if ("Monospaced".equalsIgnoreCase(fontface))
      return FONT_FACE_MONO;
    if ("Serif".equalsIgnoreCase(fontface))
      return FONT_FACE_SERIF;
    return FONT_FACE_SANS;
  }

  public static int getFontStyleID(String fontstyle) {
    for (int i = 4; --i >= 0; )
      if (fontStyles[i].equalsIgnoreCase(fontstyle))
       return i;
    return -1;
  }

  public static Font3D getFont3D(byte fontID) {
    return font3ds[fontID & 0xFF];
  }

  public int getAscent() {
    return apiPlatform.getFontAscent(fontMetrics);
  }
  
  public int getDescent() {
    return apiPlatform.getFontDescent(fontMetrics);
  }
  
  public int getHeight() {
    return getAscent() + getDescent();
  }
  
  public int stringWidth(String text) {
    return apiPlatform.fontStringWidth(fontMetrics, text);
  }
}

