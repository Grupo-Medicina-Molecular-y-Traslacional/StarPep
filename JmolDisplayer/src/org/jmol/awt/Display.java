package org.jmol.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import org.jmol.api.JmolViewer;
import org.jmol.viewer.JmolConstants;

/**
 * methods required by Jmol that access java.awt.Component
 * 
 * private to org.jmol.awt
 * 
 */

class Display {

  static boolean hasFocus(Object display) {
    return ((Component) display).hasFocus();
  }

  static void requestFocusInWindow(Object display) {
    ((Component) display).requestFocusInWindow();
  }

  static void repaint(Object display) {
    ((Component) display).repaint();
  }

  /**
   * legacy apps will use this
   * 
   * @param viewer
   * @param g
   * @param size
   */
  static void renderScreenImage(JmolViewer viewer, Object g, Object size) {
    viewer.renderScreenImage(g, ((Dimension)size).width, ((Dimension)size).height);
  }

  static void setTransparentCursor(Object display) {
    int[] pixels = new int[1];
    java.awt.Image image = Toolkit.getDefaultToolkit().createImage(
        new MemoryImageSource(1, 1, pixels, 0, 1));
    Cursor transparentCursor = Toolkit.getDefaultToolkit()
        .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
    ((Container) display).setCursor(transparentCursor);
  }

  static void setCursor(int c, Object display) {
    Container d = (Container) display;
    switch (c) {
    case JmolConstants.CURSOR_HAND:
      c = Cursor.HAND_CURSOR;
      break;
    case JmolConstants.CURSOR_MOVE:
      c = Cursor.MOVE_CURSOR;
      break;
    case JmolConstants.CURSOR_ZOOM:
      c = Cursor.N_RESIZE_CURSOR;
      break;
    case JmolConstants.CURSOR_CROSSHAIR:
      c = Cursor.CROSSHAIR_CURSOR;
      break;
    case JmolConstants.CURSOR_WAIT:
      c = Cursor.WAIT_CURSOR;
      break;
    default:
      d.setCursor(Cursor.getDefaultCursor());
      return;
    }
    d.setCursor(Cursor.getPredefinedCursor(c));
  }


}
