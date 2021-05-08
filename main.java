
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
				String additionTest = "(a ((\\b.\\c.(b (b c)(b c)))))";
				//String additionTest = "(a ((\\b.\\c.(b (\\f.\\x.(f x)) b c))))";
				//String additionTest = "(\\x.(x (x (\\y.(y)) (c b))))";
				//String additionTest = "((\\x.(\\x.(x x))))";
				//String additionTest = "(\\x.(\\x.(x x)))";

				try {
						LexicalParser lexer = new LexicalParser(additionTest);
						System.out.println(ASTFormatter.FormatAST(lexer.rootNode));
				} catch (Exception err) {
						System.out.println("line num : " + err.getStackTrace()[0].getLineNumber());
						System.out.println(err);
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
