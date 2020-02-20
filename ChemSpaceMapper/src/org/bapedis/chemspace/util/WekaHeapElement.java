/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

  /**
   * A class for storing data about a neighboring instance.
   * 
   * @author Ashraf M. Kibriya (amk14[at-the-rate]cs[dot]waikato[dot]ac[dot]nz)
   * @version $Revision: 10203 $
   */
public class WekaHeapElement {
    /** the index of this element. */
    public int index;

    /** the distance of this element. */
    public double distance;

    /**
     * constructor.
     * 
     * @param i the index
     * @param d the distance
     */
    public WekaHeapElement(int i, double d) {
      distance = d;
      index = i;
    }

  }