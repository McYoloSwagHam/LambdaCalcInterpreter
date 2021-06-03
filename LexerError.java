
/**
 * LexerError represents in an invalid parser error
 */
public class LexerError extends Exception {

        /**
         * constructor for the lexer error
         * @param errorMessage - the lexer error string
         */
    public LexerError(String errorMessage) {
        super(errorMessage);
    }
}


