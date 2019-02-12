package org.jmol.api;

import org.jmol.viewer.Viewer;

public interface JmolModelKitInterface {

  public abstract JmolModelKitInterface getModelKit(Viewer viewer, Object parentFrame);

  public abstract void getMenus(boolean doTranslate);

  public abstract void show(int x, int y, char type);

}
