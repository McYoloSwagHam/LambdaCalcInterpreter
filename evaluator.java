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
		public ArrayList<ASTNode> ASTs;
    /**
     * Constructor for objects of class evaluator
     */

		//Basically copy over bottom branches of the tree with the 
		public applyToNewTree(ASTNode replacePoint, ASTNode currentNode, 
				HashMap<Character, ASTNode> values) {
		
					// This will be moving around following the
			ASTNode newNode = new ASTNode();

			//ASTNode newRoot = newNode;

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

					//newNode = newNode.parent;

					//assert newNode != null : "newNode parent null how?";

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					currentNode =  currentNode.get(currentIndex);

					//callback here 

					indexTracker.push(++currentIndex);

					if (currentNode == null) {
						//TODO: Handle.
					}

					switch (currentNode.functionType) {
						case NONE:
							continue;
						case FUNCTION_CALL:

							ArrayList<Character> toRemove = new ArrayList<Character>();

							for (char call : currentNode.functionCalls.toArray()) {

								newNode = new ASTNode(newNode);
								newNode.functionType = FunctionType.FUNCTION_CALL;
								ASTNode function = values.get(call)
								if (function != null) {
									newNode = CloneSubTree(function, newNode);
								} else {
									newNode.functionCalls.add(call);
								}

							}

							break;
						case NESTED_FUNCTION:
							//Currently we don't handle register renaming

							for (char local : currentNode.locals.toArray())
								if (values.get(local) != null) {
									throw new Exception("Cannot handle mulitple same named variables in nested functions");
								}
							}

							//Since obviously we don't allow register renaming
							// The nested functions that remain don't share any local allocation

							//Create a new child node
							newNode = new ASTNode(newNode);
							newNode.functionType = FunctionType.NESTED_FUNCTION;
							newNode.locals = new ArrayList<Character>(currentNode.locals);
							newNode.lexicalDepth = currentNode.lexicalDepth;
							// hopefully it makes a new heap allocation.
							// I hope this doesn't just point to the other locals
							// Since I have no idea how the lifetimes collid

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

			//This is basically the same as 
			ArrayList<ASTNode> replacePoints = new ArrayList<ASTNode>();
			int currentIndex;

			//Empty tree already reduced
			if (!node.hasChildren()) {
				return newTree;
			}


			//Fuck my life why do I have to write a language that has no tail call optimization
			// my fucking life would be 1000x if I could do this with tailcalls, but I feel bad
			// because I have no idea if some poor fucker inputs a 200000 line long lambda calculus
			// it's going to fucking explode
			
			// Fuck my life, a tree is only reducable when it's on a layer of its own
			// and there are multiple siblings and the oldest sibling (child[0]) is an abstraction
			// e.g.
			// (a (\y.(y)) b) - NOT reducable 
			// (a ((\y.(y)) b)) = a b - reducable

			// We need to find the deepest reducable abstraction and work from there,
			// could be done recursively by we have no TCO in Java, so iteratively it is

			// We're copying the whole tree to the new node
			// then we go over each part that needs to be reduced 
			// reduce it and the detach links in the new tree

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

					if (currentNode == null) {
						//TODO: Handle.
					}

					//is abstraction and is on the very left side.
					if (currentNode.functionType == FunctionType.NESTED_FUNCTION && currentIndex == 0) {
						reducableBranches.add(currentNode);
						replacePoints.add(newNode);
					}


					indexTracker.push(++currentIndex);


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
					reductionLeaves.add(i);

					// We're gonna use this list as a checklist
					// to see what we have to finish
				}

				lastLexicalDepth = node.lexicalDepth;
			}

			for (int leafIndex : reductionLeaves.toArray()) {
				
				//leaf with no parent what lol?
				ASTNode leaf = reducableBranches.get(leafIndex);
				ASTNode replacePoint = replacePoints.get(leafIndex);

				//should never happen, literally
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

				//Copy over remaining branches 
				//because we didn't need to simplify them
				//since we filled all our local function variables

				if (argsLeft > numParams) {
					for (int i = numParams; i < argsLeft; i++) {
						AST tmpNode = new ASTNode();
						ASTNode unusedFunction = leaf.parent.child.get(i + 1);
						CloneSubTree(unusedFunction, tmpNode);
					}
				}

				// There must necessarily be functionCall(s) after this
				// or atleast functionCall followed by nestedFunction

				HashMap<Character, ASTNode> functionMapper = new HashMap<Character, ASTNode>();

				//Map functions to name
				for (int i = 0; i < numArgs; i++) {
					char funcName = leaf.locals.get(0);
					ASTNode appliedNode = leaf.parent.child.get(i + 1);
					Map.put(funcName, appliedNode);
				}

				applyToNewTree(replacePoint,  leaf, functionMapper);

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
