package jordenhill.project1.implementation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//A token class
//Contains data members for all valid markdown tokens and useful methods
public class Token {
    public static final String DOCB = "#BEGIN";
    public static final String DOCE = "#END";
    public static final String HEAD = "^";
    public static final String TITLEB = "<";
    public static final String TITLEE = ">";
    public static final String PARAB = "{";
    public static final String PARAE = "}";
    public static final String DEFB = "$DEF";
    public static final String USEB = "$USE";
    public static final String DEFUSEE = "$END";
    public static final String EQSIGN = "=";
    public static final String BOLD = "**";
    public static final String ITALICS = "*";
    public static final String LISTITEMB = "+";
    public static final String LISTITEME = ";";
    public static final String NEWLINE = "~";
    public static final String LINKB = "[";
    public static final String LINKE = "]";
    public static final String AUDIO = "@";
    public static final String VIDEO = "%";
    public static final String ADDRESSB = "(";
    public static final String ADDRESSE = ")";

    //Array of the tokens in class.
    public static final String[] tokens = 	{DOCB,DOCE,HEAD,TITLEB,TITLEE,PARAB,PARAE,DEFB,USEB,DEFUSEE,EQSIGN,BOLD,
                                             ITALICS,LISTITEMB,LISTITEME,NEWLINE,LINKB,LINKE,AUDIO,VIDEO,ADDRESSB,
                                             ADDRESSE};

    //First character in each tag
    public static final List<String> tagStartingChars = getTagStartingChars();

    public static List<String> getBeginningLexemes(){
        return Arrays.asList(DOCB,TITLEB,PARAB,LISTITEMB,ADDRESSB);
    }

    //Checks if str is a markdown tag
    public static boolean isMarkdownTag(String str){
        for (String s : tokens) {
            if (s.equalsIgnoreCase(str)) return true;
        }
        return false;
    }

    //Get the tags that will convert to HTML
    public static List<String> getRelevantHTMLTags(){ return Arrays.asList(DOCB, DOCE, HEAD, TITLEB, TITLEE, PARAB,
            PARAE, BOLD, ITALICS, LISTITEMB, LISTITEME, NEWLINE);
    }

    //Method to get first character of each markdown tag
    private static List<String> getTagStartingChars(){
        List<String> array = new ArrayList<>();
        for(String x: tokens){
            array.add(x.substring(0,1));
        }
        return array;

    }
}
