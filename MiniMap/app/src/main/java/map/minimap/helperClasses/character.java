package map.minimap.helperClasses;

/**
 * Created by Matthew on 2/16/2015.
 */
public class character {
    /**
     * Check if a character is alphaNumeric
     */
    public static boolean isAlphaNumeric(char c){
        return ((c >='0' && c<='9') || (c>='A' && c<='Z') || (c>='a' && c<='z'));
    }

    /**
     * Check if character is space
     */
    public static boolean isSpace(char c){
        return (c==' ' || c=='\t' || c=='\n' || c=='\r');
    }

    /**
     * Check if character is a lower case character
     */
    public static boolean isLowerCase(char c){
        return (c >= 'a' && c <= 'z');
    }
}
