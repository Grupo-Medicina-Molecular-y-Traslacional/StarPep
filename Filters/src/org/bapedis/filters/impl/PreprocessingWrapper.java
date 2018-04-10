/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class PreprocessingWrapper implements Algorithm {

    private Algorithm algorithm;
    private ActionListener actionListener;
    private final PreprocessingWrapperFactory factory;

    public PreprocessingWrapper(PreprocessingWrapperFactory factory) {
        this.factory = factory;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        this.factory.setName(algorithm.getFactory().getName());
        this.factory.setDescription(algorithm.getFactory().getDescription());
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        algorithm.initAlgo(workspace, progressTicket);
    }

    @Override
    public void endAlgo() {
        algorithm.endAlgo();
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "preprocessing"));
        }
    }

    @Override
    public boolean cancel() {
        return algorithm.cancel();
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return algorithm.getProperties();
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        algorithm.run();
    }

}
