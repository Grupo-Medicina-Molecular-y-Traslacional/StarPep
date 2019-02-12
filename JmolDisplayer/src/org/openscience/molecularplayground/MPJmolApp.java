/*
 * Copyright 2011 University of Massachusetts
 *
 * File: MPJmolApp.java
 * Description: Molecular Playground Jmol interface component/application
 * Author: Adam Williams
 *
 * See http://molecularplayground.org/
 * 
 * A Java application that listens over a port on the local host for 
 * instructions on what to display. Instructions come in over the port as JSON strings.
 * 
 * This class uses the Naga asynchronous socket IO package, the JSON.org JSON package and Jmol.
 * 
 * Adapted by Bob Hanson for Jmol 12.2
 *  
 * see JsonNioService for details.
 *   
 */
package org.openscience.molecularplayground;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

//import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.openscience.jmol.app.jmolpanel.BannerFrame;
import org.openscience.jmol.app.jmolpanel.JsonNioClient;
import org.openscience.jmol.app.jmolpanel.JsonNioService;
import org.openscience.jmol.app.jmolpanel.KioskFrame;

public class MPJmolApp implements JsonNioClient {

  protected JmolViewer jmolViewer;

  public static void main(String args[]) {
    new MPJmolApp(args.length > 1 ? Integer.parseInt(args[1]) : 31416);
  }

  public MPJmolApp(int port) {
    startJsonNioKiosk(port);
  }

  private JsonNioService service;
  private BannerFrame bannerFrame;
  private KioskFrame kioskFrame;

  private void startJsonNioKiosk(int port) {
    KioskPanel kioskPanel = new KioskPanel();
    bannerFrame = new BannerFrame(1024, 75);
    kioskFrame = new KioskFrame(0, 75, 1024, 768 - 75, kioskPanel);
    try {
      setBannerLabel("click below and type exitJmol[enter] to quit");
      jmolViewer
          .script("set allowKeyStrokes;set zoomLarge false;set frank off;set antialiasdisplay off");
      String path = System.getProperty("user.dir").replace('\\', '/')
          + "/Content-Cache/%ID%/%ID%.json";
      jmolViewer.script("NIOcontentPath=\"" + path + "\";NIOterminatorMessage='MP_DONE'");

      service = new JsonNioService();
      service.startService(port, this, jmolViewer, "-MP");

      // Bob's demo model
      jmolViewer
          .script("load http://chemapps.stolaf.edu/jmol/docs/examples-12/data/caffeine.xyz");

    } catch (Throwable e) {
      e.printStackTrace();
      if (service == null)
        nioClosed(null);
      else
        service.close();
    }
  }

  /// JsonNiosClient ///

  public void setBannerLabel(String label) {
    bannerFrame.setLabel(label);
  }

  public void nioClosed(JsonNioService jns) {
    try {
      jmolViewer.setModeMouse(-1);
      bannerFrame.dispose();
      kioskFrame.dispose();
    } catch (Throwable e) {
      //
    }
    System.exit(0);
  }


  ////////////////////////

  class KioskPanel extends JPanel {

    private final Dimension currentSize = new Dimension();

    KioskPanel() {
//      jmolViewer = JmolViewer.allocateViewer(this, new SmarterJmolAdapter(),
//          null, null, null, ""/*-multitouch-mp"*/, null);
    }

    @Override
    public void paint(Graphics g) {
      getSize(currentSize);
      jmolViewer.renderScreenImage(g, currentSize.width, currentSize.height);
    }

  }

}
