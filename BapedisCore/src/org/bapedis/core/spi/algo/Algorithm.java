/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public interface Algorithm extends Runnable {

    /**
     * initAlgo() is called to initialize the algorithm (prepare to run).
     */
    public abstract void initAlgo();

    /**
     * Called when the algorithm is finished.
     */
    public abstract void endAlgo();

    /**
     * Cancel the algorithm while it is running. Returns <code>true</code> if
     * the algorithm has been sucessfully cancelled, <code>false</code>
     * otherwise.
     *
     * @return  <code>true</code> if the algorithm has been sucessfully
     * cancelled, <code>false</code> otherwise
     */
    public boolean cancel();

    /**
     * The properties for this algorithm.
     *
     * @return the algorithm properties
     */
    public AlgorithmProperty[] getProperties();

    /**
     * The reference to the Factory that instanciated this algorithm.
     *
     * @return the reference to the factory that create this instance
     */
    public AlgorithmFactory getFactory();

    /**
     * Set the progress ticket for the algorithm. Can't be null.
     *
     * @param progressTicket the progress ticket for this algorithm
     */
    public void setProgressTicket(ProgressTicket progressTicket);

}
