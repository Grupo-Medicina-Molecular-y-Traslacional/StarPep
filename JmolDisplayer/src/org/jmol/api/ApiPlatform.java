package org.jmol.api;

import org.jmol.g3d.Font3D;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.Viewer;

public interface ApiPlatform {

  /////// Display

  boolean hasFocus(Object display);

  void repaint(Object display);

  void requestFocusInWindow(Object display);

  void setCursor(int i, Object display);

  void setTransparentCursor(Object display);


  ////  Mouse 

  void clearMouse();

  void disposeMouse();

  void getMouseManager(Viewer viewer, ActionManager actionManager);

  boolean handleOldJvm10Event(int id, int x, int y, int modifiers, long time);

  ///// Font
  
  int fontStringWidth(Object fontMetrics, String text);

  int getFontAscent(Object fontMetrics);

  int getFontDescent(Object fontMetrics);

  Object getFontMetrics(Object graphics, Object font);

  Object newFont(String fontFace, boolean isBold, boolean isItalic, float fontSize);

  ///// core Image handling
  
  Object allocateRgbImage(int windowWidth, int windowHeight, int[] pBuffer,
                          int windowSize, boolean backgroundTransparent);

  void disposeGraphics(Object gOffscreen);

  void drawImage(Object g, Object img, int x, int y);

  int[] drawImageToBuffer(Object gObj, Object imageOffscreen,
                          Object image, int width, int height, int bgcolor);

  void flushImage(Object imagePixelBuffer);

  Object getStaticGraphics(Object image, boolean backgroundTransparent);

  Object getGraphics(Object image1);

  int getImageWidth(Object image);

  int getImageHeight(Object image);

  int[] grabPixels(Object image, int x, int y, int width, int height);

  Object newBufferedImage(Object image, int i, int height);

  Object newBufferedRgbImage(int w, int h);
  
  void renderOffScreen(String text, Font3D font3d, Object gObj,
                       int width, int height, int ascent);

  void renderScreenImage(JmolViewer jmolViewer, Object g, Object currentSize);

  ///// Image creation for export (optional for any platform)

  /**
   * can be ignored (return null) if platform cannot save images
   * 
   * @param ret
   * @return     null only if this platform cannot save images
   */
  Object createImage(Object ret);

  /**
   * used for JPG writing only; can be ignored
   * 
   * @param viewer
   * @param quality
   * @param comment
   * @return    null only if this platform cannot save images
   */
  Object getJpgImage(Viewer viewer, int quality, String comment);

  /**
   * used for JPG writing only; can be ignored
   *  
   * @param image
   * @param width
   * @param height
   * @param values
   */
  void grabPixels(Object image, int width, int height, int[] values);

  /**
   * can be ignored (return false) if platform cannot save images
   * 
   * @param display
   * @param image
   * @return        false only if this platform cannot save images
   * @throws InterruptedException
   */
  boolean waitForDisplay(Object display, Object image) throws InterruptedException;

}
