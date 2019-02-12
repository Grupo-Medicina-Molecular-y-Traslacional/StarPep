package org.jmol.awt;

import org.jmol.api.ApiPlatform;
import org.jmol.api.JmolViewer;
import org.jmol.g3d.Font3D;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.Viewer;

public class Platform implements ApiPlatform {

  private Mouse mouse;

  ///// Display 
  public boolean hasFocus(Object display) {
    return Display.hasFocus(display);
  }

  public void requestFocusInWindow(Object display) {
    Display.requestFocusInWindow(display);
  }

  public void repaint(Object display) {
    Display.repaint(display);
  }

  /**
   * legacy apps will use this
   * 
   * @param viewer
   * @param g
   * @param size
   */
  public void renderScreenImage(JmolViewer viewer, Object g, Object size) {
    Display.renderScreenImage(viewer, g, size);
  }

  public void setTransparentCursor(Object display) {
    Display.setTransparentCursor(display);
  }

  public void setCursor(int c, Object display) {
    Display.setCursor(c, display);
  }

  ////// Mouse

  public void getMouseManager(Viewer viewer, ActionManager actionManager) {
    mouse = new Mouse(viewer, actionManager);
  }

  public boolean handleOldJvm10Event(int id, int x, int y, int modifiers,
                                     long time) {
    return mouse.handleOldJvm10Event(id, x, y, modifiers, time);
  }

  public void clearMouse() {
    mouse.clear();
  }

  public void disposeMouse() {
    mouse.dispose();
    mouse = null;
  }

  ////// Image 

  public Object allocateRgbImage(int windowWidth, int windowHeight,
                                 int[] pBuffer, int windowSize,
                                 boolean backgroundTransparent) {
    return Image.allocateRgbImage(windowWidth, windowHeight, pBuffer, windowSize, backgroundTransparent);
  }

  public Object createImage(Object data) {
    return Image.createImage(data);
  }

  public void disposeGraphics(Object gOffscreen) {
    Image.disposeGraphics(gOffscreen);
  }

  public void drawImage(Object g, Object img, int x, int y) {
    Image.drawImage(g, img, x, y);
  }

  public int[] drawImageToBuffer(Object gOffscreen, Object imageOffscreen,
                                 Object imageobj, int width, int height, int bgcolor) {
    return Image.drawImageToBuffer(gOffscreen, imageOffscreen, imageobj, width, height, bgcolor);
  }

  public void flushImage(Object imagePixelBuffer) {
    Image.flush(imagePixelBuffer);
  }

  public Object getGraphics(Object image) {
    return Image.getGraphics(image);
  }

  public int getImageHeight(Object image) {
    return Image.getHeight(image);
  }

  public int getImageWidth(Object image) {
    return Image.getWidth(image);
  }

  public Object getJpgImage(Viewer viewer, int quality, String comment) {
    return Image.getJpgImage(this, viewer, quality, comment);
  }

  public Object getStaticGraphics(Object image, boolean backgroundTransparent) {
    return Image.getStaticGraphics(image, backgroundTransparent);
  }

  public void grabPixels(Object image, int imageWidth, int imageHeight,
                         int[] values) {
    Image.grabPixels(image, imageWidth, imageHeight, values);
  }

  public int[] grabPixels(Object image, int x, int y, int width,
                          int height) {
    return Image.grabPixels(image, x, y, width, height);
  }

  public Object newBufferedImage(Object image, int w, int h) {
    return Image.newBufferedImage(image, w, h);
  }

  public Object newBufferedRgbImage(int w, int h) {
    return Image.newBufferedImage(w, h);
  }

  public void renderOffScreen(String text, Font3D font3d, Object gObj,
                              int mapWidth, int height, int ascent) {
    Image.renderOffScreen(text, font3d, gObj, mapWidth, height, ascent);
  }

  public boolean waitForDisplay(Object display, Object image) throws InterruptedException {
    Image.waitForDisplay(display, image);
    return true;
  }

  
  ///// FONT
  
  public int fontStringWidth(Object fontMetrics, String text) {
    return Font.stringWidth(fontMetrics, text);
  }

  public int getFontAscent(Object fontMetrics) {
    return Font.getAscent(fontMetrics);
  }

  public int getFontDescent(Object fontMetrics) {
    return Font.getDescent(fontMetrics);
  }

  public Object getFontMetrics(Object graphics, Object font) {
    return Font.getFontMetrics(graphics, font);
  }

  public Object newFont(String fontFace, boolean isBold, boolean isItalic, float fontSize) {
    return Font.newFont(fontFace, isBold, isItalic, fontSize);
  }

}
