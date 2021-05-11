import java.util.ArrayList;

/**
 * Write a description of class main here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class main
{
    // instance variables - replace the example below with your own
    private int x;

    /**
     * Constructor for objects of class main
     */
    public static void main(String[] args)
    {
        // initialise instance variables

				//Our sample lambda calculus equivalent to 1+2
				//String additionTest = "(a ((\\f.\\x.(f x ))(\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x)))))";
				//String additionTest = "((\\x.(x x))(\\x.(x x))";
                //String additionTest = "(a ((\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x)))))";
				//String additionTest = "(a ((\\b.\\c.(b (b c)(b c)))))";
				String additionTest = "(a ((\\b.\\c.(b (\\f.\\x.(f (f x))) b c))))";
				//String additionTest = "(\\x.(x (x (\\y.(y)) (c b))))";
				//String additionTest = "((\\x.(\\x.(x x))))";
				//String additionTest = "(\\x.(\\x.(x x)))";


				try {
					LexicalParser lexer = new LexicalParser(additionTest);
					Evaluator eval = new Evaluator(lexer.rootNode);
					ASTNode reduced = lexer.rootNode;

					ArrayList<Integer> ASTHashes = new ArrayList<Integer>();

					int lastHash = 0;
					int currentHash = 1;

					while (currentHash != lastHash) {
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

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int sampleMethod(int y)
    {
        // put your code here
                return x + y;
    }
}
