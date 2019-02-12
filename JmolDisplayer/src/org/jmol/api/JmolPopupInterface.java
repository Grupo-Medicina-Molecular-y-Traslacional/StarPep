package org.jmol.api;

public interface JmolPopupInterface {

  public String getMenu(String string);

  public Object getJMenu();

  public void show(int x, int y);

  public void initialize(JmolViewer viewer, boolean doTranslate,
                                       String menu, boolean asPopup);

  public void updateComputedMenus();
 
}
