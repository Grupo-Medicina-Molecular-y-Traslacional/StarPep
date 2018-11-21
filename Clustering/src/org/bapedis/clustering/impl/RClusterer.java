/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.io.OUTPUT_OPTION;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.BinaryLocator;
import org.bapedis.core.util.ExternalTool;
import org.bapedis.core.util.OSUtil;
import org.bapedis.core.util.RUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public abstract class RClusterer extends BaseClusterer {

    public static String TOO_FEW_UNIQUE_DATA_POINTS = "Too few unique data points, add features or decrease number of clusters.";
    protected Process p;

    public RClusterer(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        p = null;
    }

    @Override
    public boolean cancel() {
        super.cancel();
        if (p != null) {
            p.destroy();
        }
        return stopRun;
    }

    @Override
    public void endAlgo() {
        super.endAlgo();
        p = null;
    }

    protected abstract String getRScriptCode();

    @Override
    protected List<Cluster> cluterize(MolecularDescriptor[] features) {
        try {
            List<Cluster> clusterList = new LinkedList<>();

            File tmp = OSUtil.createTempFile("r-", "cluster");
            File rScript = OSUtil.createTempFile("rscript", "R");
            OSUtil.writeStringToFile(rScript.getAbsolutePath(), getRScriptCode());
            File dataFile = RUtil.writeToTable(peptides, features, OUTPUT_OPTION.MIN_MAX);

            if (RUtil.RSCRIPT_BINARY.getLocation() == null) {
                BinaryLocator.locate(RUtil.RSCRIPT_BINARY);
            }

            if (RUtil.RSCRIPT_BINARY.getLocation() != null) {
                String errorOut = run(
                        factory.getName(),
                        new String[]{RUtil.RSCRIPT_BINARY.getLocation(),
                            OSUtil.getAbsolutePathEscaped(rScript),
                            OSUtil.getAbsolutePathEscaped(dataFile),
                            OSUtil.getAbsolutePathEscaped(tmp)});

                if (tmp.exists()) {
                    List<Integer[]> cluster = RUtil.readCluster(tmp.getAbsolutePath());
                    if (cluster.size() != peptides.length) {
                        if (errorOut.contains(TOO_FEW_UNIQUE_DATA_POINTS)) {
                            throw new MyClusterException(TOO_FEW_UNIQUE_DATA_POINTS);
                        }
                        // else: unknown exception
                        throw new MyClusterException(errorOut);
                    }
                    
                    return clusterList;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public String run(String processName, String cmd[]) {
//        TaskProvider.debug("Run " + processName);
        System.out.println("Runing " + processName);
        ExternalTool ext = new ExternalTool();
        p = ext.run(processName, cmd, null, true);
        return ext.getErrorOut();
    }
    
    class MyClusterException extends RuntimeException{
        private final String msg;

        public MyClusterException(String msg) {
            this.msg = msg;
        }

        public String getErrorMsg() {
            return msg;
        }                
    }

}
