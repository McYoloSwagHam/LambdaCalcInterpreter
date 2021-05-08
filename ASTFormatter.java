import java.util.*;

// This class just makes it easy for us to debug our ASTs
public class ASTFormatter {


	public static String FormatFunctionType(FunctionType funcType) {

		switch (funcType) 
		{
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

	// for the love of god why doesn't arraylist implement toString
	// what sort of absurdity is this
	public static String CharArrayToString(ArrayList<Character> vec) {

		String finalString = new String("[");

		for (char letter : vec) {
			finalString += letter + ",";
		}

		finalString += "]";

		return finalString;


	}


	public static String FormatNodeInformation(ASTNode node) {

		switch (node.functionType) {
			case NONE:
				return "No Information";
			case NESTED_FUNCTION:
				return CharArrayToString(node.locals);
			case FUNCTION_CALL:
				return CharArrayToString(node.functionCalls);
			default:
				return "UNREACHABLE - this is a bug";
		}


	}



	private static String FormatNode(ASTNode node) {

		String finalString = "";
		String functionType = FormatFunctionType(node.functionType);
		String nodeInfo = FormatNodeInformation(node);

		finalString = String.format("Depth : %d [%s] - parent : %b - children : %d - information %s",
				node.lexicalDepth,
				functionType,
				node.parent,
				node.child.size(),
				FormatNodeInformation(node));

		return finalString;

	}


	public static String FormatAST(ASTNode rootNode) throws Exception {

		String finalString = new String();

		// Java doesn't have Tail Call optimization
		// which means we're gonna have to do this iteratively
		// if we don't want to stack overflow. :(

		ASTNode currentNode = rootNode;


		// when we go back up a node
		// we need to know where we are to find the next child node in order
		ArrayList<Integer> indexTracker = new ArrayList<Integer>();
		int currentChildIndex = 0;
		int currentChildSize;

		finalString += FormatNode(currentNode) + "\n";


		while (true) {

			currentChildSize = currentNode.child.size();
			
			if (currentChildSize == 0 || currentChildIndex == currentChildSize) {


				//exit
				if (indexTracker.size() == 0) {
					break;
				}

				// What?? throw error not possible
				if (currentNode.parent == null) {
					throw new Exception("Need to go up the the AST, but no parent");
				}

				currentNode = currentNode.parent;


				// Oh god why doesn't java have a pop method
				// what is this, every language since 1999
				// has had a way for arrays to represent stacks
				int lastIndex = indexTracker.size() - 1;
				currentChildIndex = indexTracker.get(lastIndex);
				indexTracker.remove(lastIndex);
				
				continue;

			} else {


				currentNode = currentNode.child.get(currentChildIndex);
				currentChildIndex += 1;

				if (currentNode == null) {
					throw new Exception("Child node in array but not real value");
				}

				indexTracker.add(currentChildIndex);
				currentChildIndex = 0;

				String currentLine = new String(new char[indexTracker.size()]).replace("\0", "\t");

				currentLine += FormatNode(currentNode);

				finalString += currentLine + "\n";

			}
		}

		return finalString;

	}
}


