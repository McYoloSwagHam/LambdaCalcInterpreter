
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
                //String additionTest = "(a ((\\a.\\b.\\c.(b (a b c)))(\\f.\\x.(f (f x)))))";
				//String additionTest = "(a ((\\b.\\c.(b (b c)(b c)))))";
				String additionTest = "(a ((\\b.\\c.(b (\\f.\\x.(f (f x))) b c))))";
				//String additionTest = "(\\x.(x (x (\\y.(y)) (c b))))";
				//String additionTest = "((\\x.(\\x.(x x))))";
				//String additionTest = "(\\x.(\\x.(x x)))";


				try {
						LexicalParser lexer = new LexicalParser(additionTest);
						System.out.println(ASTFormatter.FormatAST(lexer.rootNode));
                        Evaluator eval = new Evaluator(lexer.rootNode);
						System.out.println(ASTFormatter.FormatAST(eval.ASTs.get(1)));
                        eval = new Evaluator(eval.ASTs.get(1));
						System.out.println(ASTFormatter.FormatAST(eval.ASTs.get(1)));
                        eval = new Evaluator(eval.ASTs.get(1));
						System.out.println(ASTFormatter.FormatAST(eval.ASTs.get(1)));
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
