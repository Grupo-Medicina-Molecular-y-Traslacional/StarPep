/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

  /**
   * A class for a heap to store the nearest k neighbours to an instance. The
   * heap also takes care of cases where multiple neighbours are the same
   * distance away. i.e. the minimum size of the heap is k.
   * 
   * @author Ashraf M. Kibriya (amk14[at-the-rate]cs[dot]waikato[dot]ac[dot]nz)
   * @version $Revision: 10203 $
   */
public class WekaHeap {
    /** the heap. */
    WekaHeapElement m_heap[] = null;

    /**
     * constructor.
     * 
     * @param maxSize the maximum size of the heap
     */
    public WekaHeap(int maxSize) {
      if ((maxSize % 2) == 0) {
        maxSize++;
      }

      m_heap = new WekaHeapElement[maxSize + 1];
      m_heap[0] = new WekaHeapElement(0, 0);
    }

    /**
     * returns the size of the heap.
     * 
     * @return the size
     */
    public int size() {
      return m_heap[0].index;
    }

    /**
     * peeks at the first element.
     * 
     * @return the first element
     */
    public WekaHeapElement peek() {
      return m_heap[1];
    }

    /**
     * returns the first element and removes it from the heap.
     * 
     * @return the first element
     * @throws Exception if no elements in heap
     */
    public WekaHeapElement get() throws Exception {
      if (m_heap[0].index == 0) {
        throw new Exception("No elements present in the heap");
      }
      WekaHeapElement r = m_heap[1];
      m_heap[1] = m_heap[m_heap[0].index];
      m_heap[0].index--;
      downheap();
      return r;
    }

    /**
     * adds the value to the heap.
     * 
     * @param i the index
     * @param d the distance
     * @throws Exception if the heap gets too large
     */
    public void put(int i, double d) throws Exception {
      if ((m_heap[0].index + 1) > (m_heap.length - 1)) {
        throw new Exception("the number of elements cannot exceed the "
          + "initially set maximum limit");
      }
      m_heap[0].index++;
      m_heap[m_heap[0].index] = new WekaHeapElement(i, d);
      upheap();
    }

    /**
     * Puts an element by substituting it in place of the top most element.
     * 
     * @param i the index
     * @param d the distance
     * @throws Exception if distance is smaller than that of the head element
     */
    public void putBySubstitute(int i, double d) throws Exception {
      WekaHeapElement head = get();
      put(i, d);
      // System.out.println("previous: "+head.distance+" current: "+m_heap[1].distance);
      if (head.distance == m_heap[1].distance) { // Utils.eq(head.distance,
                                                 // m_heap[1].distance)) {
        putKthNearest(head.index, head.distance);
      } else if (head.distance > m_heap[1].distance) { // Utils.gr(head.distance,
                                                       // m_heap[1].distance)) {
        m_KthNearest = null;
        m_KthNearestSize = 0;
        initSize = 10;
      } else if (head.distance < m_heap[1].distance) {
        throw new Exception("The substituted element is smaller than the "
          + "head element. put() should have been called "
          + "in place of putBySubstitute()");
      }
    }

    /** the kth nearest ones. */
    WekaHeapElement m_KthNearest[] = null;

    /** The number of kth nearest elements. */
    int m_KthNearestSize = 0;

    /** the initial size of the heap. */
    int initSize = 10;

    /**
     * returns the number of k nearest.
     * 
     * @return the number of k nearest
     * @see #m_KthNearestSize
     */
    public int noOfKthNearest() {
      return m_KthNearestSize;
    }

    /**
     * Stores kth nearest elements (if there are more than one).
     * 
     * @param i the index
     * @param d the distance
     */
    public void putKthNearest(int i, double d) {
      if (m_KthNearest == null) {
        m_KthNearest = new WekaHeapElement[initSize];
      }
      if (m_KthNearestSize >= m_KthNearest.length) {
        initSize += initSize;
        WekaHeapElement temp[] = new WekaHeapElement[initSize];
        System.arraycopy(m_KthNearest, 0, temp, 0, m_KthNearest.length);
        m_KthNearest = temp;
      }
      m_KthNearest[m_KthNearestSize++] = new WekaHeapElement(i, d);
    }

    /**
     * returns the kth nearest element or null if none there.
     * 
     * @return the kth nearest element
     */
    public WekaHeapElement getKthNearest() {
      if (m_KthNearestSize == 0) {
        return null;
      }
      m_KthNearestSize--;
      return m_KthNearest[m_KthNearestSize];
    }

    /**
     * performs upheap operation for the heap to maintian its properties.
     */
    protected void upheap() {
      int i = m_heap[0].index;
      WekaHeapElement temp;
      while (i > 1 && m_heap[i].distance > m_heap[i / 2].distance) {
        temp = m_heap[i];
        m_heap[i] = m_heap[i / 2];
        i = i / 2;
        m_heap[i] = temp; // this is i/2 done here to avoid another division.
      }
    }

    /**
     * performs downheap operation for the heap to maintian its properties.
     */
    protected void downheap() {
      int i = 1;
      WekaHeapElement temp;
      while (((2 * i) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i].distance)
        || ((2 * i + 1) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i + 1].distance)) {
        if ((2 * i + 1) <= m_heap[0].index) {
          if (m_heap[2 * i].distance > m_heap[2 * i + 1].distance) {
            temp = m_heap[i];
            m_heap[i] = m_heap[2 * i];
            i = 2 * i;
            m_heap[i] = temp;
          } else {
            temp = m_heap[i];
            m_heap[i] = m_heap[2 * i + 1];
            i = 2 * i + 1;
            m_heap[i] = temp;
          }
        } else {
          temp = m_heap[i];
          m_heap[i] = m_heap[2 * i];
          i = 2 * i;
          m_heap[i] = temp;
        }
      }
    }

    /**
     * returns the total size.
     * 
     * @return the total size
     */
    public int totalSize() {
      return size() + noOfKthNearest();
    }

  }
