package org.bapedis.network.impl;

import java.text.MessageFormat;
import org.bapedis.network.wizard.CSNWizardIterator;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class CSNWizardSetupUI {

    private final WizardDescriptor wiz;

    public CSNWizardSetupUI(CSNAlgorithm csnAlgo) {
        // Wizard iterator
        WizardDescriptor.Iterator<WizardDescriptor> iterator = new CSNWizardIterator(csnAlgo);

        // Open wizard
        wiz = new WizardDescriptor(iterator);
        
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(CSNWizardSetupUI.class, "CSNWizard.title"));
        
    }

    public WizardDescriptor getWizardDescriptor() {
        return wiz;
    }
}
