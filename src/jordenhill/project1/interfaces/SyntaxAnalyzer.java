package jordenhill.project1.interfaces;

import jordenhill.project1.implementation.CompilerException;

public interface SyntaxAnalyzer {

    /**
     * This method implements the BNF grammar rule for the document annotation.
     * @throws CompilerException
     */
    void markdown() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the head annotation.
     * @throws CompilerException
     */
    void head() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the title annotation.
     * @throws CompilerException

     */
    void title() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the body annotation.
     * @throws CompilerException
     */
    void body() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the paragraph annotation.
     * @throws CompilerException
     */
    void paragraph() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the inner-text annotation.
     * @throws CompilerException
     */
    void innerText() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the variable-define annotation.
     * @throws CompilerException
     */
    void variableDefine() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the variable-use annotation.
     * @throws CompilerException
     */
    void variableUse() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the bold annotation.
     * @throws CompilerException
     */
    void bold() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the italics annotation.
     * @throws CompilerException
     */
    void italics() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the listitem annotation.
     * @throws CompilerException
     */
    void listItem() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the inner-item annotation.
     * @throws CompilerException
     */
    void innerItem() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the link annotation.
     * @throws CompilerException
     */
    void link() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the audio annotation.
     * @throws CompilerException
     */
    void audio() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the video annotation.
     * @throws CompilerException
     */
    void video() throws CompilerException;

    /**
     * This method implements the BNF grammar rule for the newline annotation.
     * @throws CompilerException
     */
    void newline() throws CompilerException;

}
