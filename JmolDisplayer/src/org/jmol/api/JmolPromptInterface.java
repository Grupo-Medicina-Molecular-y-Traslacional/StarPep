package org.jmol.api;

public interface JmolPromptInterface {
  
  public abstract String prompt(String label, String data, String[] list,
                              boolean asButtons);

}
