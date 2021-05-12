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
    //String userInput = scan.nextLine();
    String userInput = "(\\f.\\x.(f (f (f (f (f x))))))(\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x)))";
    //String userInput = "(a ((\\b.\\c.(b (\\f.\\x.(f (f x))) b c))))";
    //String userInput = "(a ((\\b.\\c.(b (b c)(b c)))))";
    //String userInput = "((\\a.\\b.\\c.(a (b c)))(\\f.\\x.(f (f (f x))))(\\f.\\x.(f (f (f (f x))))))";
    //String userInput = "(\\x.\\y.(x (y (\\a.\\b.\\c.(b (a b c))) (\\d.\\p.(d p)))))(\\f.\\x.(e (e l)))(\\f.\\x.(f x))";
    //String userInput = "(\\b.\\e.(e b))(\\f.\\x.(f (f (f x))))(\\f.\\d.(f (f d)))";

    //String userInput = "(\\n.\\f.\\x.(n (\\g.\\h.(h (g f))) (\\u.(x)) (\\u.(u)) ))(\\e.\\l.(e (e l)))";

    try {

      LexicalParser lexer = new LexicalParser(userInput);
      Evaluator eval = new Evaluator();
      ASTNode reduced = lexer.rootNode;
      
      System.out.println("lexer : " + ASTFormatter.FormatAST(lexer.rootNode));

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
        System.out.println("\n");
        System.out.println(ASTFormatter.FormatAST(reduced));
      }

      System.out.println("Final Reduction!");

    } catch (Exception err) {
      err.printStackTrace();
    }

  }

}
