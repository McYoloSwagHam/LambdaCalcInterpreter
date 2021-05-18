import java.util.*;

/**
 * this class is just there for debugging and formatting
 * it's only purpose it to turn an AST tree into a string form
 * for printing
 */
public class ASTFormatter {

	/**
	 * returns a string based on the function type
	 * @param funcType the function type of the node
	 * @return the string that reprents the function type
	 */
  public static String FormatFunctionType(FunctionType funcType) {

    switch (funcType) {
      case NONE:
        return "None";
      case NESTED_FUNCTION:
        return "Nested Function";
      case FUNCTION_CALL:
        return "Function Call";
      default:
        return "Unreachable - this is a bug";
    }
  }

	/**
	 * depending on the function type it returns the relevant information
	 * for that node... (locals, function calls);
	 * @param node the node whose information we need to format
	 * @return the string that reprents the node info
	 */
  public static String FormatNodeInformation(ASTNode node) {

    switch (node.functionType) {
      case NONE:
        return "No Information";
      case NESTED_FUNCTION:
        return node.locals.toString();
      case FUNCTION_CALL:
        return node.functionCalls.toString();
      default:
        return "UNREACHABLE - this is a bug";
    }

  }

	/**
	 * returns the string version of the node, could be toString for ASTNode
	 * @param node - the node to format
	 * @return the human understandable notation of the node as a string
	 */
  public static String FormatNode(ASTNode node) {

    String finalString = "";
    String functionType = FormatFunctionType(node.functionType);
    String nodeInfo = FormatNodeInformation(node);

    finalString = String.format("Depth : %d [%s] - parent : %b - children : %d - information %s", node.lexicalDepth,
        functionType, node.parent, node.child.size(), FormatNodeInformation(node));

    return finalString;

  }

	/**
	 * takes a rootNode representing an AST and returns a human understandable string representing it
	 * @param rootNode - the AST
	 * @return the string representing the AST
	 */
  public static String FormatAST(ASTNode rootNode) {

    String finalString = new String();

    // Java doesn't have Tail Call optimization
    // which means we're gonna have to do this iteratively
    // if we don't want to stack overflow. :(

    ASTNode currentNode = rootNode;

    // when we go back up a node
    // we need to know where we are to find the next child node in order
    Stack<Integer> indexTracker = new Stack<Integer>();
    int currentChildIndex = 0;
    int currentChildSize;

    finalString += FormatNode(currentNode) + "\n";

    while (true) {

      currentChildSize = currentNode.child.size();

      if (currentChildSize == 0 || currentChildIndex == currentChildSize) {

        // exit
        if (indexTracker.size() == 0) {
          break;
        }

				assert currentNode.parent != null : "iterating up but no parent";

        currentNode = currentNode.parent;

        // Oh god why doesn't java have a pop method
        // what is this, every language since 1999
        // has had a way for arrays to represent stacks
        currentChildIndex = indexTracker.pop();

        continue;

      } else {

        currentNode = currentNode.child.get(currentChildIndex);
        currentChildIndex += 1;

				assert currentNode != null : "currentNode is null";

        indexTracker.push(currentChildIndex);
        currentChildIndex = 0;

        String currentLine = new String(new char[indexTracker.size()]).replace("\0", "\t");

        currentLine += FormatNode(currentNode);

        finalString += currentLine + "\n";

      }
    }

    return finalString;

  }
}
