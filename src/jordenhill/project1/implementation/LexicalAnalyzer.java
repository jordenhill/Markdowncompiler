package jordenhill.project1.implementation;

import java.util.Arrays;
import java.util.List;

//A lexical analyzer
//Gets a source file, which is then read character-by-character
//Reports any lexical errors if an unknown or illegal lexeme is found
//Valid lexemes are assigned a token and sent to the syntax analyzer for further processing
public class LexicalAnalyzer implements jordenhill.project1.interfaces.LexicalAnalyzer {
    private String source;
    private String currentToken;
    private String nextCharacter;
    private int currentPosition;
    private List<String> Lexemes;
    private List<String> tagStartingchars;
    private List<String> formTags;

    //Constructor
    public LexicalAnalyzer(String source) {
        this.source = source;
        currentPosition = 0;
        currentToken = "";


        Lexemes = Arrays.asList(Token.tokens);
        tagStartingchars = Token.tagStartingChars;
        formTags = Arrays.asList(Token.BOLD, Token.ITALICS);

        getCharacter();
    }

    //get the next token
    public void getNextToken() throws CompilerException {
        if(currentToken != null){
            currentToken = "";
        }
        while(currentToken != null && currentToken.length() == 0) {
            if(nextCharacter == null)
                currentToken = null;
            else {
                currentToken = "";
                if(isSpace(nextCharacter))
                    getCharacter();
                else {
                    if(tagStartingchars.contains(nextCharacter))
                        makeMarkdownToken();
                    else
                        convertToText();
                }
            }

            if(currentToken != null) {
                currentToken = currentToken.replaceAll("\\s", "");
            }
        }

        Compiler.currentToken = currentToken;
    }

    //get length of lexeme
    private int getLexemeLength() {
        int result = 0;
        for(String lexeme: Token.tokens) {
            if(lexeme.length() > result) {
                result = lexeme.length();
            }
        }
        return result;
    }

    //Take characters and attempt to create a legal markdown token
    private void makeMarkdownToken() throws CompilerException{
        boolean isLegalToken = false;

        if(Lexemes.contains(nextCharacter)) {
            addCharacter();
            getCharacter();
            if(formTags.contains(nextCharacter)) {
                addCharacter();
                getCharacter();
                currentToken = currentToken.substring(0,currentToken.length());
                currentPosition--;
            }
        }
        else {
            int lexemeLength = getLexemeLength();
            int count = 0;
            while(count <= lexemeLength && nextCharacter != null && !isLegalToken) {
                addCharacter();
                getCharacter();
                if(lookupToken()){
                    isLegalToken = true;
                }
                count++;
            }
            if(!isLegalToken) {
                throw new CompilerException("Illegal token found: " + currentToken);
            }
        }
    }

    //Create a text token
    private void convertToText(){
        boolean doneCoverting = false;

        while(!doneCoverting) {
            if(!isSpace(nextCharacter) && !tagStartingchars.contains(nextCharacter)) {
                addCharacter();
                getCharacter();
            }
            else {
                doneCoverting = true;
            }
        }
    }

    //Get the next character from the source file
    public void getCharacter() {
        if (currentPosition < source.length()) {
            nextCharacter = Character.toString(source.charAt(currentPosition++));
        }
        else {
            nextCharacter = null;
        }
    }

    //Add character to current token
    public void addCharacter(){
        currentToken = currentToken + nextCharacter;
    }

    //Check if current character is a space
    public boolean isSpace(String c) {
        boolean result = true;
        if(c != null){
            result = c.equals(" ");
        }
        return result;

    }

    //Check if token is legal in language
    public boolean lookupToken() {
        return Token.isMarkdownTag(currentToken);
    }

}
