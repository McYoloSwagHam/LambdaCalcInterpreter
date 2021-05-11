import java.util.*;

/**
 * Write a description of class main here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class main {
  // instance variables - replace the example below with your own
  private int x;

  /**
   * Constructor for objects of class main
   */
  public static void main(String[] args) {
    // initialise instance variables

    Scanner scan = new Scanner(System.in);
    String userInput = scan.nextLine();
    //String userInput = "(a (\\f.\\x.(f x))(\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x))))";

    try {

      LexicalParser lexer = new LexicalParser(userInput);
      Evaluator eval = new Evaluator();
      ASTNode reduced = lexer.rootNode;
      
      System.out.println(ASTFormatter.FormatAST(lexer.rootNode));

      ArrayList<Integer> ASTHashes = new ArrayList<Integer>();
      ArrayList<ASTNode> ASTs = new ArrayList<ASTNode>();

      int lastHash = 0;
      int currentHash = ASTNode.HashAST(lexer.rootNode);

      while (currentHash != lastHash) {
        ASTHashes.add(currentHash);
        ASTs.add(reduced);
        lastHash = currentHash;
        reduced = eval.Evaluate(reduced);
        currentHash = ASTNode.HashAST(reduced);
        System.out.println(ASTFormatter.FormatAST(reduced));
      }

      System.out.println("Final Reduction!");

    } catch (Exception err) {
      err.printStackTrace();
    }

  }

}
