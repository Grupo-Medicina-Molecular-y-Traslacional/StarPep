package org.jmol.api;

import java.util.BitSet;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.modelset.Group;
import org.jmol.modelset.ModelLoader;
import org.jmol.modelset.ModelSet;
import org.jmol.modelset.Polymer;

public interface JmolBioResolver {

  public Group distinguishAndPropagateGroup(Chain chain, String group3, int seqcode,
                                                  int firstAtomIndex, int maxAtomIndex, 
                                                  int modelIndex, int[] specialAtomIndexes,
                                                  Atom[] atoms);
  
  public Polymer buildBioPolymer(Group group, Group[] groups, int i, boolean checkPolymerConnections);
  
  public void clearBioPolymers(Group[] groups, int groupCount, BitSet bsModelsExcluded);

  public void initializeHydrogenAddition(ModelLoader modelSet, int bondCount);

  public void finalizeHydrogens();

  public void setHaveHsAlready(boolean b);

  public void addImplicitHydrogenAtoms(JmolAdapter adapter, int i);

  public void initialize(ModelSet modelSet);

}

