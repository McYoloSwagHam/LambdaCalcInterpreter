package com.mcyoloswagham;
/**
 * LexerError represents in an invalid parser error
 * @author Ayaz Mammadov
 */
public class LexerError extends Exception {

    /**
     * constructor for the lexer error
     * 
     * @param errorMessage - the lexer error string
     */
    public LexerError(String errorMessage) {
        super(errorMessage);
    }
}
