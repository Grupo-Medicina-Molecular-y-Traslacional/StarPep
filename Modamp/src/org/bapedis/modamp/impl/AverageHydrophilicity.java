/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.HydrophilicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class AverageHydrophilicity extends AbstractMD 
{
    protected boolean GRAVY;
    protected String  GRAVY_NAME;
    protected boolean HOPT810101;
    protected String  HOPT810101_NAME;
    protected boolean KUHL950101;    
    protected String  KUHL950101_NAME;
    
    protected final List<AlgorithmProperty> properties;
    
    public AverageHydrophilicity( AlgorithmFactory factory ) 
    {
        super( factory );
        
        properties = new LinkedList<>();
        
        populateProperties();
    }
    
    protected void populateProperties()
    {
        GRAVY = true;
        GRAVY_NAME = "AvgGRAVY";        
        HOPT810101 = false;
        HOPT810101_NAME = "AvgHydrophilicity(HOPT810101)";
        KUHL950101 = true;
        KUHL950101_NAME = "AvgHydrophilicity(KUHL950101)";
        
        try
        {
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AverageHydrophilicity.class, "AverageHydrophilicity.HOPT810101.name" ), PRO_CATEGORY, NbBundle.getMessage( AverageHydrophilicity.class, "AverageHydrophilicity.HOPT810101.desc" ), "isHOPT810101", "setHOPT810101" ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AverageHydrophilicity.class, "AverageHydrophilicity.KUHL950101.name" ), PRO_CATEGORY, NbBundle.getMessage( AverageHydrophilicity.class, "AverageHydrophilicity.KUHL950101.desc" ), "isKUHL950101", "setKUHL950101" ) );
        }
        catch ( NoSuchMethodException ex )
        {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public boolean isHOPT810101()
    {
        return HOPT810101;
    }
    
    public void setHOPT810101( Boolean HOPT810101 ) 
    {
        this.HOPT810101 = HOPT810101;
    }
    
    public boolean isKUHL950101()
    {
        return KUHL950101;
    }
    
    public void setKUHL950101( Boolean KUHL950101 )
    {
        this.KUHL950101 = KUHL950101;
    }
    
    @Override
    public void initAlgo( Workspace workspace, ProgressTicket progressTicket ) 
    {
        super.initAlgo( workspace, progressTicket );
        
        if ( HOPT810101 )
        {
            addAttribute( HOPT810101_NAME, HOPT810101_NAME, Double.class );
        }
        
        if ( KUHL950101 ) 
        {
            addAttribute( KUHL950101_NAME, KUHL950101_NAME, Double.class );
        }
        
        if ( GRAVY )
        {
            addAttribute( GRAVY_NAME, GRAVY_NAME, Double.class );
        }
    }
    
    @Override
    protected void compute( Peptide peptide ) 
    {
        double val;
        
        if ( HOPT810101 ) 
        {
            val = MD.gravy(peptide.getSequence(), HydrophilicityScale.hopp_Woods_hydrov_hash());
            peptide.setAttributeValue(getAttribute(HOPT810101_NAME), val);
        }
        
        if ( KUHL950101 )
        {
            val = MD.gravy(peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash());
            peptide.setAttributeValue(getAttribute(KUHL950101_NAME), val);
        }
        
        val = MD.gravy(peptide.getSequence());
        peptide.setAttributeValue(getAttribute( GRAVY_NAME ), val);
    }
    
    @Override
    public AlgorithmProperty[] getProperties()
    {
        return properties.toArray(new AlgorithmProperty[0]);
    }
}
