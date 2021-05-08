import java.util.*;

/**
 * Iteratively reduce the expression tree, substituting the relevant functions
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Evaluator
{
    // instance variables - replace the example below with your own
    private int x;

		// This holds the rootNode
		// of each step of the AST reduction
		// this way we can visualize the reduction live
		ArrayList<ASTNode> ASTs;
    /**
     * Constructor for objects of class evaluator
     */

		//Basically copy over bottom branches of the tree with the 
		public applyToNewTree(ASTNode newNode, ASTNode currentNode, 
				HashMap<Character, ASTNode> values) {


				// Java no TCO so I hate recursion for handling this sorta stuff
				// each new stack frame is way bigger than you need for tracking this stuff

			Stack<Integer> indexTracker = new Stack<Integer>();

			while (true) {

				// go up or exit
				if (!currentNode.hasChildren() || currentIndex == currentNode.child.size()) { 


					// exit
					if (indexTracker.empty()) {
						break;
					}

					// Shouldn't happen because by the time 
					// we make it up to the rootNode indexTracker should be empty
					if (currentNode.parent == null) {
						//TODO: Handle this too
					}

					currentNode = currentNode.parent;
					currentIndex = indexTracker.pop();

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					newNode = new ASTNode(newNode);
					currentNode =  currentNode.get(currentIndex);

					//callback here 

					indexTracker.push(++currentIndex);


					if (currentNode == null) {
						//TODO: Handle.
					}

					switch (currentNode.functionType) {
						case NONE:
							break;
						case FUNCTION_CALL:
							break;
						case NESTED_FUNCTION:
							//Currently we don't handle register renaming

							ArrayList<Character> toRemove 

							for (int i = 0; i < values.locals.size(); i++) {

								if (values.get(local) != null) {
									//TODO: throw 


								}

							}

							break;
						default:
							//TODO: throw on default
							break;

					}

					continue;

				}


			}


		}

		public ASTNode Reduce(ASTNode node) {

			ASTNode newTree = new ASTNode();
			ASTNode newNode = newTree;
			// iterative instead of recursive
			// see comment on TCO in LexicalParser.java
			Stack<Integer> indexTracker = new Stack<Integer>();
			ArrayList<ASTNode> reducableBranches = new ArrayList<ASTNode>();
			int currentIndex;

			//Empty tree already reduced
			if (!node.hasChildren()) {
				return newTree;
			}

			// Fuck my life, a tree is only reducable when it's on a layer of its own
			// and there are multiple siblings and the oldest sibling (child[0]) is an abstraction
			// e.g.
			// (a (\y.(y)) b) - NOT reducable 
			// (a ((\y.(y)) b)) = a b - reducable


			// We need to find the deepest reducable abstraction and work from there,
			// could be done recursively by we have no TCO in Java, so iteratively it is

			while (true) {

				// go up or exit
				if (!currentNode.hasChildren() || currentIndex == currentNode.child.size()) { 


					// exit
					if (indexTracker.empty()) {
						break;
					}

					// Shouldn't happen because by the time 
					// we make it up to the rootNode indexTracker should be empty
					if (currentNode.parent == null) {
						//TODO: Handle this too
					}

					currentNode = currentNode.parent;
					currentIndex = indexTracker.pop();

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					newNode = new ASTNode(newNode);
					currentNode =  currentNode.get(currentIndex);

					//is abstraction and is on the very left side.
					if (currentNode.functionType == FunctionType.NESTED_FUNCTION && currentIndex == 0) {
						reducableBranches.add(currentNode);
					}

					indexTracker.push(++currentIndex);

					if (currentNode == null) {
						//TODO: Handle.
					}

					continue;

				}


			}

			// Array of Ast nodes goes like this
			// AstNode 
			// 	AstNode 
			//   	AstNode 
			//  AstNode  
			//
			//  each indent is a new reducable branch
			//  we HAVE to reduce the inner branches before reducing the outer branches

			if (reducableBranches.size == 0) {
				return 
			}

			ArrayList<ASTNode> reductionLeaves = new ArrayList<ASTNode>();

			//First nodes might be null from previous passes
			for (ASTNode node : reducableBranches.toArray()) {
				if (node != null) {
					lastlexicalDepth = node.lexicalDepth;
				}
			}


			for (int i = 0; i < reducableBranches.size(); i++) {
				if (node.lexicalDepth < lastLexicalDepth) {
					reductionLeaves.add(reducableBranches.get(i));

					// We're gonna use this list as a checklist
					// to see what we have to finish
					reducableBranches[i] = null;
				}
			}

			for (ASTNode leaf : reductionLeaves.toArray()) {
				
				//leaf with no parent what lol?
				if (leaf.parent == null) {
					continue;
				}

				// NestedFunc
				//    FunctionCall
				// NestedFunc
				// 		FunctionCall
				// NestedFunc
				// 		FunctionCall
				//
				// into:
				//
				// NestedFunc
				// 		FunctionCall
				// NestedFunc
				// 		FunctionCall
				//
				int numParams = leaf.locals.size();
				int argsLeft = leaf.parent.child.size()
				int numArgs = Math.min(argsLeft, numParams);

				// There must necessarily be functionCall(s) after this
				// or atleast functionCall followed by nestedFunction

				HashMap<Character, ASTNode> functionMapper = new HashMap<Character, ASTNode>();

				//Map functions to name
				for (int i = 0; i < numArgs; i++) {
					char funcName = leaf.locals.get(0);
					ASTNode appliedNode = leaf.parent.child.get(i + 1);
					Map.put(funcName, appliedNode);
				}

				applyToNewTree(newNode,  leaf, functionMapper);
				

			}


			int numParams = currentNode.locals.size();

			ArrayList<ASTNode> args = new Stack<ASTNode>();

			assert currentNode.parent != null : "Abstraction parent is null";
			ASTNode parentNode = currentNode.parent;

			//Obviously we don't want to push the current node and previous nodes into the args list
			// for substitution
			
			int argsLeft = parentNode.child.size() - currentIndex;
			int numArgs = Math.min(argsLeft, numParams);

			for (int i = 0; i < numArgs; i++) {
				args.push(parentNode.child.get(i + 1));
			}



		}

    public Evaluator(ASTNode rootNode)
    {

			ASTs = new ArrayList<ASTNode>();
			ASTs.add(rootNode);

			Reduce(rootNode);

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
