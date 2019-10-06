package org.bapedis.modamp;

public class LocalTool
{
    public static String getAcronymAccordingToLocal( EAminoacidLocal local )
    {
        if ( local != null ) 
        {
            switch ( local ) 
            {
                case Total:
                    return "Total";
                case Apolar:
                    return "AP";
                case PositivelyChargedPolar:
                    return "PCP";
                case NegativelyChargedPolar:
                    return "NCP";
                case UnchargedPolar:
                    return "UNP";
                case Aromatic:
                    return "ARO";
                case Aliphatic:
                    return "ALP";
                case Unfolding:
                    return "UNF";
                case AlphaHelixFavoring:
                    return "FAH";
                case BetaSheetFavoring:
                    return "FBS";
                case BetaTurnFavoring:
                    return "FBT";
                default:
                    break;
            }
        }
        
        return null;
    }
    
    public static int belong2Local( String aa, EAminoacidLocal local )
    {
        if ( local != null ) 
        {
            switch ( local ) 
            {
                case Total:
                    return 1;
                case Apolar:
                    return isApolar( aa ) ? 1 : 0;
                case PositivelyChargedPolar:
                    return isPolarPositivelyCharged( aa ) ? 1 : 0;
                case NegativelyChargedPolar:
                    return isPolarNegativelyCharged( aa ) ? 1 : 0;
                case UnchargedPolar:
                    return isPolarUncharged( aa ) ? 1 : 0;
                case Aromatic:
                    return isAromatic( aa ) ? 1 : 0;
                case Aliphatic:
                    return isAliphatic( aa ) ? 1 : 0;
                case Unfolding:
                    return isUnfolding( aa ) ? 1 : 0;
                case AlphaHelixFavoring:
                    return isAlphaHelixFavoring( aa ) ? 1 : 0;
                case BetaSheetFavoring:
                    return isBetaSheetFavoring( aa ) ? 1 : 0;
                case BetaTurnFavoring:
                    return isBetaTurnFavoring( aa ) ? 1 : 0;
                default:
                    break;
            }
        }
        
        return 0;
    }
    
    private static boolean isApolar( String aa ) 
    {        
        return  aa.equalsIgnoreCase("P") || aa.equalsIgnoreCase("I") ||
                aa.equalsIgnoreCase("A") || aa.equalsIgnoreCase("V") ||
                aa.equalsIgnoreCase("L") || aa.equalsIgnoreCase("F") ||
                aa.equalsIgnoreCase("W") || aa.equalsIgnoreCase("M");
    }
    
    private static boolean isPolarPositivelyCharged( String aa ) 
    {        
        return  aa.equalsIgnoreCase("K") || aa.equalsIgnoreCase("H") ||
                aa.equalsIgnoreCase("R");
    }
    
    private static boolean isPolarNegativelyCharged( String aa ) 
    {        
        return aa.equalsIgnoreCase("D") || aa.equalsIgnoreCase("E");
    }
    
    private static boolean isPolarUncharged( String aa ) 
    {        
        return  aa.equalsIgnoreCase("N") || aa.equalsIgnoreCase("C") ||
                aa.equalsIgnoreCase("G") || aa.equalsIgnoreCase("S") ||
                aa.equalsIgnoreCase("T") || aa.equalsIgnoreCase("Y") ||
                aa.equalsIgnoreCase("Q");
    }
    
    private static boolean isAromatic( String aa ) 
    {
        return  aa.equalsIgnoreCase("F") || aa.equalsIgnoreCase("Y") ||
                aa.equalsIgnoreCase("W");
    } 
    
    private static boolean isAliphatic( String aa ) 
    {        
        return  aa.equalsIgnoreCase("G") || aa.equalsIgnoreCase("A") ||
                aa.equalsIgnoreCase("P") || aa.equalsIgnoreCase("V") ||
                aa.equalsIgnoreCase("L") || aa.equalsIgnoreCase("I") ||
                aa.equalsIgnoreCase("M");
    }
    
    private static boolean isUnfolding( String aa ) 
    {
        return  aa.equalsIgnoreCase("G") || aa.equalsIgnoreCase("P");
    } 
    
    private static boolean isAlphaHelixFavoring( String aa ) 
    {
        return  aa.equalsIgnoreCase("A") || aa.equalsIgnoreCase("C") ||
                aa.equalsIgnoreCase("L") || aa.equalsIgnoreCase("M") ||
                aa.equalsIgnoreCase("E") || aa.equalsIgnoreCase("Q") ||
                aa.equalsIgnoreCase("H") || aa.equalsIgnoreCase("K");
    }   
    
    private static boolean isBetaSheetFavoring( String aa ) 
    {
        return  aa.equalsIgnoreCase("V") || aa.equalsIgnoreCase("I") ||
                aa.equalsIgnoreCase("F") || aa.equalsIgnoreCase("Y") ||
                aa.equalsIgnoreCase("W") || aa.equalsIgnoreCase("T"); 
    }  
    
    private static boolean isBetaTurnFavoring( String aa ) 
    {        
        return  aa.equalsIgnoreCase("G") || aa.equalsIgnoreCase("S") ||
                aa.equalsIgnoreCase("D") || aa.equalsIgnoreCase("N") ||
                aa.equalsIgnoreCase("P");
    }  
}
