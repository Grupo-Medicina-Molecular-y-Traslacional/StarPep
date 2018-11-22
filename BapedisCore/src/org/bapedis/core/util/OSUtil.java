package org.bapedis.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSUtil {

    public static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux");
    }

    /**
     * replace a backslash in windows with a double-backslash
     *
     * @param f
     * @return
     */
    public static String getAbsolutePathEscaped(File f) {
        if (OSUtil.isWindows()) {
            return f.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
        } else {
            return f.getAbsolutePath();
        }
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        return tempFile;
    }

    public static String getParent(String file) {
        return new File(file).getParent();
    }

    public static void writeStringToFile(String file, String content) {
        writeStringToFile(file, content, false);
    }

    public static void writeStringToFile(String file, String content, boolean append) {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(file, append));
            w.write(content);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean robustRenameTo(String source, String dest) {
        return robustRenameTo(new File(source), new File(dest));
    }

    public static String readStringFromFile(String file) {
        try {
            StringBuffer res = new StringBuffer();
            String line;
            BufferedReader r = new BufferedReader(new FileReader(file));
            boolean firstLine = true;
            while ((line = r.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    res.append("\n");
                }
                res.append(line);
            }
            r.close();
            return res.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * renameto is not reliable to windows
     *
     * @param source
     * @param dest
     * @return
     */
    public static boolean robustRenameTo(File source, File dest) {
        if (OSUtil.isWindows()) {
            try {
                String line;
                String cmd[] = new String[]{"cmd", "/c", "MOVE", "/Y", source.getAbsolutePath(),
                    dest.getAbsolutePath()};
                
                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
                input.close();
                BufferedReader input2 = new BufferedReader(
                        new InputStreamReader(p.getErrorStream()));
                while ((line = input2.readLine()) != null) {
                    System.out.println(line);
                }
                input2.close();
                p.waitFor();
                // System.err.println(p.exitValue());
                return p.exitValue() == 0;
            } catch (Exception err) {
                err.printStackTrace();
                return false;
            }
        } else {
            //file.renameTo failed on gome as well, use streams! 
            if (!source.exists()) {
                return false;
            }
            if (!copy(source, dest)) {
                return false;
            }
            if (!dest.exists()) {
                return false;
            }
            if (!source.delete()) {
                return false;
            }
            return true;
        }
    }

    public static boolean copy(String source, String dest) {
        return copy(new File(source), new File(dest));
    }

    public static boolean copy(File source, File dest) {
        return copy(source, dest, false);
    }

    public static boolean copy(File source, File dest, boolean append) {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(source);
            to = new FileOutputStream(dest, append);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead); // write
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
