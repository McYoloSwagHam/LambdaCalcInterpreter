package com.mcyoloswagham;

import java.util.*;

/**
 * LexicalParser is responsible for turning user input into an initial AST
 */
public class LexicalParser {

  /**
   * This is the parsed AST of the user input
   */
  public ASTNode rootNode;

  /**
   * these are special characters in lambda expressions described by a Set
   */
  public Set<Character> specialCharacters;

  /**
   * this is a set of characters that determine valid closing charcters. used in
   * IsInvalidClosing
   */
  public ArrayList<Character> validClosingCharacters;

  /**
   * Check is a character is a special character
   * 
   * @param symbol - the character to be checked
   */
  public boolean IsSpecialCharacter(char symbol) {
    return specialCharacters.contains(symbol);
  }

  /**
   * Checks if a character is an Invalid closing
   * 
   * @param lastChar - the character to be checked
   */
  public boolean IsInvalidClosing(char lastChar) {
    return validClosingCharacters.contains(lastChar);
  }

  /**
   * This is the method that turns the lambda expression into an AST.
   * 
   * @param lambdaExpression the user input string
   * @throws LexerError If the input is not a valid lambda calculus expression
   */
  public void Tokenize(String lambdaExpression) throws LexerError {

    int ExprRefCount = 0;
    boolean isLocalDefinitionAllowed = false;
    // Everytime we upgrade a node
    // we have to skip a ')', because that takes us up the AST
    int upgradeCount = 0;

    // While it may be smart to use an iterator and move that along
    // The most information we need is
    char lastCharacter = ' ';
    char lastLetter = ' ';

    // TODO: We have
    // lastLetter = currentLetter;
    // continue;
    //
    // if we were programming in a language with lambda capture
    // I could make the switch case a lambda, capture all the variables in my
    // current function
    // frame and then just return from that and set the last letter
    // but oh well Java in PF2 :) life is life.
    // shoulda been in Rust!!!!!

    int counter = 0;
    ASTNode currentNode = rootNode;

    for (char currentLetter : lambdaExpression.toCharArray()) {

      // Solution for the problem above
      lastCharacter = lastLetter;
      lastLetter = currentLetter;

      switch (currentLetter) {
        case ' ': // Skip spaces
          continue;
        case '(': //

          if (!IsSpecialCharacter(lastCharacter)) {
            throw new LexerError(
                "Syntax Error : '(' preceded by invalid character, " + "needs to be ' ' or '.' or '(' ')' " + counter);
          }

          // Constructor does child parent linking
          currentNode = new ASTNode(currentNode);
          ExprRefCount += 1;
          currentNode.lexicalDepth = ExprRefCount;

          continue;

        case ')':

          // Check if our last character is a valid char
          if (IsInvalidClosing(lastCharacter)) {
            throw new LexerError(
                "Syntax Error : ')' preceded by invalid character, " + "needs to be letter or ')' or ' '");
          }

          // we're at the root node, break
          if (currentNode.parent == null) {
            return;
          }

          if (upgradeCount == 0) {
            currentNode = currentNode.parent;
            ExprRefCount -= 1;
          } else {
            upgradeCount -= 1;
          }

          continue;
        case '\\':
          // This is invalid,
          // lambda definitions are only valid at the start of an expression
          if (ExprRefCount == 0) {
            throw new LexerError(
                "Definition Error: lambda definitions are only valid " + "in open expressions " + counter);
          }

          // Check syntax validity of lambda def
          if (lastCharacter != '(' && lastCharacter != '.') {
            throw new LexerError("Syntax Error: Lambda definition preceded by invalid "
                + "character, needs to be '.' or '(', last character " + lastCharacter);
          }

          isLocalDefinitionAllowed = true;

          continue;
        case '.':

          if (IsSpecialCharacter(lastCharacter)) {
            throw new LexerError(
                "Syntax Error: . preceded by invalid character, " + "should be preceded by letter (Definition)");
          }

          continue;
        default: // Not a special character, used for names

          // Either this is a function call or a nested lambda
          if (!isLocalDefinitionAllowed && ExprRefCount == 0) {
            throw new LexerError(
                "Definition Error: invalid function call or local definition" + " , open the expression");
          }

          // This is a function call not a function definition
          currentNode.functionType = isLocalDefinitionAllowed ? FunctionType.NESTED_FUNCTION
              : FunctionType.FUNCTION_CALL;

          if (currentNode.functionType == FunctionType.FUNCTION_CALL) {

            if (lastCharacter != '(' && lastCharacter != ' ') {
              throw new LexerError("Syntax Error: invalid function call syntax, Function name"
                  + "must be preceded by '(' or ' ' " + counter);
            }

            // we just went down a level
            if (currentNode.functionCalls.size() != 0) {
              currentNode = new ASTNode(currentNode.parent);
              currentNode.functionType = FunctionType.FUNCTION_CALL;
              currentNode.lexicalDepth = ExprRefCount;
            } else {

              boolean hasNestedChild = false;

              for (ASTNode node : currentNode.child) {
                if (node.functionType == FunctionType.NESTED_FUNCTION) {
                  hasNestedChild = true;

                  node.parent = currentNode.parent;
                  currentNode.parent.child.add(node);

                }
              }

              if (hasNestedChild) {
                ASTNode newNode = new ASTNode(currentNode.parent);
                newNode.functionType = FunctionType.FUNCTION_CALL;
                newNode.lexicalDepth = ExprRefCount;
                currentNode.parent.child.remove(currentNode);
                currentNode = newNode;
              }

            }

            currentNode.functionCalls.add(Character.toString(currentLetter));

          } else {

            // We need to upgrade because we just figured out
            // That this is a abstraction

            if (currentNode.locals.size() == 0 && currentNode.parent != null && currentNode.parent.parent != null) {
              currentNode.Upgrade();
              upgradeCount++;
              ExprRefCount--;
            }

            currentNode.locals.add(Character.toString(currentLetter));

            if (lastCharacter != '\\') {
              throw new LexerError("Syntax Error: local definition must be preceded by '.' " + counter);
            }

            isLocalDefinitionAllowed = false;

          }

          continue;

      }

    }

  }

  /**
   * Construct for lexicalParser, it initializes the special characters and calls
   * Tokenize.
   * 
   * @param lambdaExpression - user input
   */
  public LexicalParser(String lambdaExpression) throws LexerError {
    // initialise instance variables
    rootNode = new ASTNode();
    specialCharacters = new HashSet<>(Arrays.asList('\\', '.', '(', ')', ' '));
    validClosingCharacters = new ArrayList<Character>(Arrays.asList('.', '\\'));
    Tokenize(lambdaExpression);

  }
}
