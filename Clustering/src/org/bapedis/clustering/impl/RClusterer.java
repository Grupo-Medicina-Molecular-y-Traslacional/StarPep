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
import java.util.logging.Logger;
import org.bapedis.core.io.OUTPUT_OPTION;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.util.ExternalTool;
import org.bapedis.core.util.OSUtil;
import org.bapedis.core.util.RScriptUtil;
import org.bapedis.core.util.RUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public abstract class RClusterer extends BaseClusterer {

    public RClusterer(AlgorithmFactory factory) {
        super(factory);
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

//            String errorOut = run(
//                    getShortName(),
//                    new String[]{BinHandler.RSCRIPT_BINARY.getLocation(), FileUtil.getAbsolutePathEscaped(rScript),
//                        FileUtil.getAbsolutePathEscaped(new File(featureTableFile)),
//                        FileUtil.getAbsolutePathEscaped(tmp)});

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static String run(String processName, String cmd[], File stdOutFile, String env[]) {
//        TaskProvider.debug("Run " + processName);
        ExternalTool ext = new ExternalTool(Logger.getLogger(RClusterer.class.getName())) {
            protected void stdout(String s) {
//                TaskProvider.verbose(s);
//                Settings.LOGGER.info(s);
            }

            protected void stderr(String s) {
//                TaskProvider.verbose(s);
//                Settings.LOGGER.warn(s);
            }
        };
        Process p = ext.run(processName, cmd, stdOutFile, stdOutFile != null, env);
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
//                Settings.LOGGER.error(e);
            }
            // check if this process should be aborted (via abort dialog)
//            if (!TaskProvider.isRunning()) {
//                p.destroy();
//                break;
//            }
            // hack to determine if process has finished
            try {
//                Settings.LOGGER.debug("Exit value: " + p.exitValue());
                break;
            } catch (IllegalThreadStateException e) {
                // this exception is thrown if the process has not finished
            }
        }
        return ext.getErrorOut();
    }

}
