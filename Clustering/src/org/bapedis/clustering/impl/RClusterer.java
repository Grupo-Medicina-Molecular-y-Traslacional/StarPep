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
import java.util.Map;
import java.util.TreeMap;
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public abstract class RClusterer extends BaseClusterer {

    static protected final String PRO_CATEGORY = "Properties";
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

            // Configure a cluster instance
            try {
                validateClusterer();
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                cancel();
            }

            if (!stopRun) {
                File tmp = OSUtil.createTempFile("rscript-", "-cluster");
                File rScript = OSUtil.createTempFile("rscript", "-R");
                OSUtil.writeStringToFile(rScript.getAbsolutePath(), getRScriptCode());
                File dataFile = RUtil.writeToCSV(peptides, features, OUTPUT_OPTION.MIN_MAX);

                if (RUtil.RSCRIPT_BINARY.getLocation() == null) {
                    BinaryLocator.locate(RUtil.RSCRIPT_BINARY);
                }

                if (RUtil.RSCRIPT_BINARY.isFound()) {
                    if (!stopRun) {
                        String errorOut = run(
                                factory.getName(),
                                new String[]{RUtil.RSCRIPT_BINARY.getLocation(),
                                    OSUtil.getAbsolutePathEscaped(rScript),
                                    OSUtil.getAbsolutePathEscaped(dataFile),
                                    OSUtil.getAbsolutePathEscaped(tmp)});

                        if (tmp.exists()) {
                            List<Integer[]> assignments = RUtil.readCluster(tmp.getAbsolutePath());
                            if (assignments.size() != peptides.length) {
                                if (errorOut.contains(TOO_FEW_UNIQUE_DATA_POINTS)) {
                                    throw new MyClusterException(TOO_FEW_UNIQUE_DATA_POINTS);
                                }
                                // else: unknown exception
                                throw new MyClusterException(errorOut);
                            }

                            TreeMap<Integer, Cluster> clusterMap = new TreeMap<>();
                            Integer clusterID;
                            Cluster c;
                            int index = 0;
                            for (Integer[] cluster : assignments) {
                                clusterID = cluster[0];
                                if (clusterMap.containsKey(clusterID)) {
                                    c = clusterMap.get(clusterID);
                                } else {
                                    c = new Cluster(clusterID);
                                    clusterMap.put(clusterID, c);
                                }
                                c.addMember(peptides[index++]);
                            }

                            for (Map.Entry<Integer, Cluster> entry : clusterMap.entrySet()) {
                                clusterList.add(entry.getValue());
                            }
                            return clusterList;
                        }
                    }
                } else {
                    throw new MyClusterException(NbBundle.getMessage(RClusterer.class, "RClusterer.r.norFound"));
                }
            }
        } catch (MyClusterException ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getErrorMsg(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            cancel();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    protected abstract void validateClusterer() throws Exception;

    protected String run(String processName, String cmd[]) {
        pc.reportMsg("Runing " + processName, workspace);
        ExternalTool ext = new ExternalTool(workspace);
        p = ext.run(processName, cmd, null, true);
        return ext.getErrorOut();
    }

    class MyClusterException extends RuntimeException {

        private final String msg;

        public MyClusterException(String msg) {
            this.msg = msg;
        }

        public String getErrorMsg() {
            return msg;
        }
    }

}
