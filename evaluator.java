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
		public void ApplyToNewTree(ASTNode replacePoint, ASTNode currentNode, 
				HashMap<Character, ASTNode> values) {
		

			// This will be moving around following the
			ASTNode treeRoot = new ASTNode();
			ASTNode newNode = treeRoot;
			//ASTNode lmaoRoot = new ASTNode();
			

			//ASTNode newRoot = newNode;

				// Java no TCO so I hate recursion for handling this sorta stuff
				// each new stack frame is way bigger than you need for tracking this stuff

			Stack<Integer> indexTracker = new Stack<Integer>();
			int currentIndex = 0;

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

					//newNode = newNode.parent;
					currentNode = currentNode.parent;
					currentIndex = indexTracker.pop();
					newNode = newNode.parent;

					//assert newNode != null : "newNode parent null how?";

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					currentNode =  currentNode.child.get(currentIndex);

					//callback here 

					indexTracker.push(++currentIndex);
					currentIndex = 0;

					if (currentNode == null) {
						//TODO: Handle.
					}

					switch (currentNode.functionType) {
						case NONE:
							break;
						case FUNCTION_CALL:

							//ArrayList<Character> toRemove = new ArrayList<Character>();
							ASTNode curNode = newNode;
							ASTNode tmpNode;

							for (char call : currentNode.functionCalls) {

								tmpNode = new ASTNode(curNode);
								tmpNode.functionType = FunctionType.FUNCTION_CALL;
								ASTNode function = values.get(call);

								if (function != null) {
									tmpNode = tmpNode.CloneSubTree(function);
									newNode = tmpNode;
								} else {
									tmpNode.functionCalls.add(call);
									newNode = tmpNode;
								}

							}


							break;
						case NESTED_FUNCTION:
							//Currently we don't handle register renaming

							for (char local : currentNode.locals) {
								if (values.get(local) != null) {
									System.out.println("fuck this failed");
									//throw new Exception("Cannot handle mulitple same named variables in nested functions");
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
							}
						}
				}

				//replace the replacePoint Node
				replacePoint.parent.child = new ArrayList<ASTNode>();
				replacePoint.parent.child.add(treeRoot);
				treeRoot.parent = replacePoint.parent;
				//replacePoint.parent.child.remove(replacePoint);


		}

		public ASTNode Reduce(ASTNode node) {


			ASTNode newTree = new ASTNode();
			ASTNode newNode = newTree;
			ASTNode currentNode = node;
			// iterative instead of recursive
			// see comment on TCO in LexicalParser.java
			Stack<Integer> indexTracker = new Stack<Integer>();
			ArrayList<ASTNode> reducableBranches = new ArrayList<ASTNode>();

			//This is basically the same as 
			ArrayList<ASTNode> replacePoints = new ArrayList<ASTNode>();
			int currentIndex = 0;

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
					newNode = newNode.parent;

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					newNode = new ASTNode(newNode);
					currentNode =  currentNode.child.get(currentIndex);
					ASTNode.CloneNode(currentNode, newNode);

					if (currentNode == null) {
						//TODO: Handle.
					}

					//is abstraction and is on the very left side.
					if (currentNode.functionType == FunctionType.NESTED_FUNCTION && currentIndex == 0) {
						reducableBranches.add(currentNode);
						replacePoints.add(newNode);
					}


					indexTracker.push(++currentIndex);
					currentIndex = 0;

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

			if (reducableBranches.size() == 0) {
				return newTree;
			}

			ArrayList<Integer> reductionLeaves = new ArrayList<Integer>();
			ASTNode lastNode = null;
			int lastLexicalDepth = 0;

			//First nodes might be null from previous passes
			for (ASTNode reducibleNode : reducableBranches) {
				if (reducibleNode != null) {
					lastLexicalDepth = node.lexicalDepth;
				}
			}

			for (int i = 0; i < reducableBranches.size(); i++) {

				ASTNode reducibleNode = reducableBranches.get(i);


				//First cmp will always pass since the first nodes are equal
				if (reducibleNode.lexicalDepth > lastLexicalDepth) {
					reductionLeaves.add(i);
					// We're gonna use this list as a checklist
					// to see what we have to finish
				}

				lastLexicalDepth = reducibleNode.lexicalDepth;
			}

			if (reductionLeaves.size() == 0 && reducableBranches.size() != 0) {
				reductionLeaves.add(reducableBranches.size() -1);
			}

			for (int leafIndex : reductionLeaves) {
				
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
				int argsLeft = leaf.parent.child.size() - 1;
				int numArgs = Math.min(argsLeft, numParams);

				// There must necessarily be functionCall(s) after this
				// or atleast functionCall followed by nestedFunction

				HashMap<Character, ASTNode> functionMapper = new HashMap<Character, ASTNode>();

				//Map functions to name
				for (int i = 0; i < numArgs; i++) {
					char funcName = leaf.locals.get(i);
					ASTNode appliedNode = leaf.parent.child.get(i + 1);
					functionMapper.put(funcName, appliedNode);
				}


				ApplyToNewTree(replacePoint,  leaf, functionMapper);

			}

			return newTree;

		}

		public void CleanAST(ASTNode rootNode) {

			Stack<Integer> indexTracker = new Stack<Integer>();
			int currentIndex = 0;
			int depth = 0;
			ASTNode currentNode = rootNode;

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

					if (currentNode == null) {
							
					}

					currentIndex = indexTracker.pop();
					depth -= 1;

					//newNode = newNode.parent;

					//assert newNode != null : "newNode parent null how?";

					continue;

				} else {

					// if we haven't found are first abstraction, it's definitely gonna be 
					// part of the tree.
					currentNode =  currentNode.child.get(currentIndex);
					//callback here 

					indexTracker.push(++currentIndex);
					currentIndex = 0;

					if (currentNode == null) {
						System.out.println("fudege " + currentNode);
						//TODO: Handle.
					}
					
					if (currentNode.functionType == FunctionType.NONE) {
						
						for (ASTNode node : currentNode.child) {
							node.parent = currentNode.parent;
						}

						if (currentNode.parent != null) {
							currentNode.parent.child = currentNode.child;
						}

						indexTracker.pop();

					} else {
						depth++;
						currentNode.lexicalDepth = depth;
					}
				}
			}
		}

	public ASTNode Evaluate(ASTNode rootNode) {
		ASTNode reduced = Reduce(rootNode);
		CleanAST(reduced);
		ASTs.add(reduced);
		return reduced;
	}
		
    public Evaluator(ASTNode rootNode)
    {

			ASTs = new ArrayList<ASTNode>();
			ASTs.add(rootNode);

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
