package jordenhill.project1.implementation;

public class CompilerException extends Exception {

    /**
     * Instantiates a new CompilerException.
     *
     * @param errorMessage the error message to be printed
     */
    public CompilerException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return super.getMessage();
    }
}