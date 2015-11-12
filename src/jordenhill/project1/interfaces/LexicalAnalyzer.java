package jordenhill.project1.interfaces;

import jordenhill.project1.implementation.CompilerException;

public interface LexicalAnalyzer {

    /** The next character. */
    String nextCharacter = "";

    /** The current position. */
    int currentPosition = 0;

    /**
     * This is the public method to be called when the Syntax Analyzer needs a new
     * token to be parsed.
     */
    public void getNextToken() throws CompilerException;

    /**
     * This is method gets the next character from the input and places it in
     * the nextCharacter class variable.
     *
     * @return the character
     */
    void getCharacter();

    /**
     * This method adds the current character the nextToken.
     */
    void addCharacter();

    /**
     * This is method gets the next character from the input and places it in
     * the nextCharacter class variable.
     *
     * @param c the current character
     * @return true, if is space; otherwise false
     */
    boolean isSpace(String c);

    /**
     * This method checks to see if the current, possible token is legal in the
     * defined grammar.
     *
     * @return true, if it is a legal token, otherwise false
     */
    boolean lookupToken();
}
