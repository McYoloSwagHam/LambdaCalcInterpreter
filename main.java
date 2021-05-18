import java.util.*;

/**
 * This is the main class, where program execution starts.
 * it gets an input from the user and then passes it to LexicalParser
 * it then reduces until the hash is equivalent to the last
 * TODO: make an ArrayList of hashes, and check since recursive processes
 *  might have multiple steps instead of being 1 step reductions.
 *
 * @author Ayaz Mammadov
 * @version 1.0.0
 */
public class main {

  /**
   * Main...
   */
  public static void main(String[] args) {
    // initialise instance variables

    Scanner scan = new Scanner(System.in);
    //String userInput = scan.nextLine();
    //String userInput = "(\\f.\\x.(f (f (f (f (f x))))))(\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x)))";
    //String userInput = "(a ((\\b.\\c.(b (\\f.\\x.(f (f x))) b c))))";
    //String userInput = "(a ((\\b.\\c.(b (b c)(b c)))))";
    //String userInput = "(\\a.\\b.\\f.(a (b f)))(\\f.\\x.(f (f x)))(\\f.\\x.(f (f (f x))))";
    //String userInput = "(\\x.\\y.(x (y (\\a.\\b.\\c.(b (a b c))) (\\d.\\p.(d p)))))(\\f.\\x.(e (e l)))(\\f.\\x.(f x))";
    //String userInput = "(\\b.\\e.(e b))(\\f.\\x.(f (f (f x))))(\\f.\\d.(f (f d)))";

    String userInput = "(\\n.\\f.\\x.(n (\\g.\\h.(h (g f))) (\\u.(x)) (\\u.(u)) ))(\\e.\\l.(e (e (e l))))";

    try {

      LexicalParser lexer = new LexicalParser(userInput);
      Evaluator eval = new Evaluator();
      ASTNode reduced = lexer.rootNode;
      
      System.out.println("lexer : " + ASTFormatter.FormatAST(lexer.rootNode));

      ArrayList<Integer> ASTHashes = new ArrayList<Integer>();
      ArrayList<ASTNode> ASTs = new ArrayList<ASTNode>();

      int lastHash = 0;
      int currentHash = ASTNode.HashAST(lexer.rootNode);

      while (true) {
        ASTs.add(reduced);
        lastHash = currentHash;
        reduced = eval.Evaluate(reduced);
        currentHash = ASTNode.HashAST(reduced);

				// If we've seen the hash before
				// then we know that it's recursive since the has will always result in this hash again
				// making it cyclically infinite.
				if (ASTHashes.contains(currentHash)) {
					System.out.println("ASTHashes : " + ASTHashes);
					System.out.println("CurrentHash (" + currentHash + ") previously seen before!");
					break;
				}


        ASTHashes.add(currentHash);
				String hashCheck = String.format("currentHash AST (%d)", currentHash);
				System.out.println(hashCheck);
        System.out.println(ASTFormatter.FormatAST(reduced));
      }

      System.out.println("Final Reduction!");

    } catch (Exception err) {
      err.printStackTrace();
    }

  }

}
