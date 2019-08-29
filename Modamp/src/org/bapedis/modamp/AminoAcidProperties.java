package org.bapedis.modamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Cesar
 */
public class AminoAcidProperties 
{
    private final Map<String, double[]> aaProperties;
    
    private final List<String> header;    
    private final String configFile = "org/bapedis/modamp/aminoacid_weights.txt";
    
    public AminoAcidProperties() throws IOException 
    {
        aaProperties = new HashMap<>();        
        header = new ArrayList<>();
        
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( configFile ) ) );
        
        String currentLine = bufferedReader.readLine(); // headings
        
        String[] components = currentLine.split( "\t" );
        
        header.addAll( Arrays.asList( components ).subList( 2, components.length ) );
        
        currentLine = bufferedReader.readLine();
        
        while ( currentLine != null ) 
        {
            components = currentLine.split( "\t" );
            
            int len = components.length - 2;
            
            double[] currentAAProperties = new double[len];
            
            for ( int i = 0; i < len; i++ ) 
            {
                currentAAProperties[i] = Double.parseDouble(components[i + 2]);
            }
            
            String aminoAcidCode = components[1]; //aminoacid code of one letter
            
            aaProperties.put( aminoAcidCode, currentAAProperties );
            
            currentLine = bufferedReader.readLine();
        }
        
        bufferedReader.close();
    }
    
    public double[] getAminoacidPropertyValues( String seq, AminoAcidProperty property ) 
    {
        double[] lovis = new double[ seq.length() ];
        
        int pos = header.indexOf( property.toString() );
        for ( int i = 0; i < seq.length(); i++ )
        {
            String aa = Character.toString( seq.charAt( i ) );
            if ( aaProperties.containsKey( aa ) )
            {
                lovis[ i ] = aaProperties.get( aa )[pos];
            }
        }
        
        return lovis;
    }
}
