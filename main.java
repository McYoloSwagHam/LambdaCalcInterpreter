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
     * @param args - "-v" for AST printing
   */
  public static void main(String[] args) {
    // initialise instance variables

        boolean isVerbose = false;

        for (String arg : args) {
            if (arg.equals("-v")) {
                isVerbose = true;
            }
        }

        System.out.println("Enter your lambda expression: ");
    Scanner scan = new Scanner(System.in);
    String userInput = scan.nextLine();

    try {

      LexicalParser lexer = new LexicalParser(userInput);
      Evaluator eval = new Evaluator(isVerbose);
      ASTNode reduced = lexer.rootNode;
      
            if (isVerbose == true) {
                System.out.println("lexer : " + ASTFormatter.FormatAST(lexer.rootNode));
            }


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

                if (isVerbose) {
                    String hashCheck = String.format("currentHash AST (%d)", currentHash);
                    System.out.println(hashCheck);
                    System.out.println(ASTFormatter.FormatAST(reduced));
                }

      }

      System.out.println("Final Reduction!");
      System.out.println(ASTFormatter.FormatASTAsLambda(reduced));

    } catch (Exception err) {
      err.printStackTrace();
    }

  }

}
