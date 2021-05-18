import java.util.*;

/*
 * The Abstract Syntax Tree will not be a binary tree of Operations
 * instead it will take the form of a linked-list relating statements on the same level
 * and allowing for tree-like branching when dropping into an expression
 * this allows us to evaluate expressions on the same level with much more easy 
 * by just iterating over the horizontal linked-list instead of having to traverse a binary
 * tree and figure out where the last expression ended
 *
 * E.G
 */

/**
 * ASTNode is the class that reprents a node in an AST, 
 * it has 1 parent and many childs,
 * it can either be a none (representing empty brackets),
 * NESTED_FUNCTION (representing an abstraction)
 * FUNCTION_CALL (represneting an application)
 */
public class ASTNode {

	/**
	 * if the function is an abstraction
	 * these are the locals
	 */
  public ArrayList<String> locals;


	/**
	 * if the function is an application
	 * these are the function calls
	 */
  public ArrayList<String> functionCalls;

	/**
	 * for rolling hash purposes
	 */
  public static final int PRIME_BASE = 65537;

	/**
	 * the parent of this node
	 */
  public ASTNode parent;

	/**
	 * the children of this node
	 */
  public ArrayList<ASTNode> child;

	/**
	 * how far down the the node is in the tree
	 * used only for decorative purposes (printing)
	 */
  public int lexicalDepth;

	/**
	 * what type of node is this
	 */
  public FunctionType functionType;

	/**
	 * this is my implementation of a dumb rolling hash that is
	 * meant to be unique, obviously doesn't have a gread period,
	 * could be its own class but it's way too small
	 * @param hash - basically a this pointer
	 * @param info - the hash info that changes this rolling hash
	 * @return the new rolling hash
	 */
  public static int RollingHash(int hash, int info) {
    hash *= PRIME_BASE;
    hash ^= info;
    hash += info % 2;
    return hash;
  }

	/**
	 * this function moves a node up a level.
	 */
  public void Upgrade() {

    //Remove this from parent
    this.parent.child.remove(this);
    this.parent = this.parent.parent;
    this.parent.child.add(this);
    this.lexicalDepth -= 1;
  }

	/**
	 * this function Hashes the AST starting from the rootNode
	 * @param rootNode - the AST
	 * @return the hash of the AST represented as an integer.
	 */
  public static int HashAST(ASTNode rootNode) {

    ASTNode currentNode = rootNode;

    Stack<Integer> indexTracker = new Stack<Integer>();
    int currentIndex = 0;
    int rollingHash = 0;

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
          // TODO: Handle this too
        }

        // newNode = newNode.parent;
        currentNode = currentNode.parent;
        currentIndex = indexTracker.pop();

        continue;

      } else {

        currentNode = currentNode.child.get(currentIndex);

        // callback here

        indexTracker.push(++currentIndex);
        currentIndex = 0;

        if (currentNode == null) {
          // TODO: Handle.
        }

        switch (currentNode.functionType) {
          case FUNCTION_CALL:
            for (String call : currentNode.functionCalls) {

              int stringValue = 0;
              
              for (char letter : call.toCharArray()) {
                stringValue += letter;
              }
              // cast character to ascii integer
              rollingHash = RollingHash(rollingHash, stringValue);
            }

            break;
          case NESTED_FUNCTION:
          

            for (String local : currentNode.locals) {

              int stringValue = 0;
              
              for (char letter : local.toCharArray()) {
                stringValue += letter;
              }

              // cast character to ascii integer
              rollingHash = RollingHash(rollingHash, stringValue);
            }

        }

        rollingHash = RollingHash(rollingHash, currentNode.child.size());

      }
    }

    return rollingHash;

  }

	/** 
	 * checks if a node has children
	 * @return true if this node has childre otherwise false
	 */
  public boolean hasChildren() {
    return child.size() != 0;
  }

	/**
	 * CloneSubTree literally recursively copies the tree of a node starting a sourceNode
	 * to the currentNode, it is a graphic substition
	 * @param sourceNode - the node to copy the tree from
	 */
  public void CloneSubTree(ASTNode sourceNode) {

    ASTNode newNode = this;
    ASTNode.CloneNode(sourceNode, newNode);

    Stack<Integer> indexTracker = new Stack<Integer>();
    int currentIndex = 0;

    // copy tree structure
    while (true) {

      // go up or exit
      if (!sourceNode.hasChildren() || currentIndex == sourceNode.child.size()) {
        // exit
        if (indexTracker.empty()) {
          break;
        }

        // Shouldn't happen because by the time
        // we make it up to the rootNode indexTracker should be empty
        if (sourceNode.parent == null) {
          // TODO: Handle this too
        }

        newNode = newNode.parent;
        sourceNode = sourceNode.parent;
        currentIndex = indexTracker.pop();

        // newNode = newNode.parent;

        // assert newNode != null : "newNode parent null how?";

      } else {

        // if we haven't found are first abstraction, it's definitely gonna be
        // part of the tree.
        newNode = new ASTNode(newNode);
        sourceNode = sourceNode.child.get(currentIndex);
        if (sourceNode == null) {
          // TODO: Handle.
        }

        indexTracker.push(++currentIndex);
        currentIndex = 0;
        ASTNode.CloneNode(sourceNode, newNode);

      }
    }

  }

	/**
	 * this function copys the properties of the node from one node to another
	 * but it does not copy the graphical portions (parents/children)
	 * @param sourceNode - the source node to copy from
	 * @param targetNode - the target node to copy to
	 */
  public static void CloneNode(ASTNode sourceNode, ASTNode targetNode) {
    targetNode.functionType = sourceNode.functionType;
    targetNode.lexicalDepth = sourceNode.lexicalDepth;

    switch (sourceNode.functionType) {
      case NONE:
        break;
      case NESTED_FUNCTION:
        // copy over locals
        // yes I know that it already had an empty initialized
        // array, but I also have no idea whether this is better
        targetNode.locals = new ArrayList<String>(sourceNode.locals);
      case FUNCTION_CALL:
        targetNode.functionCalls = new ArrayList<String>(sourceNode.functionCalls);
      default:
        // unreachable
        break;

    }

  }

	/**
	 * this constructor makes a new astnode, and links it to a parent
	 * @param nodeParent - the to be parent of this node
	 */
  public ASTNode(ASTNode nodeParent) {

    locals = new ArrayList<String>();
    functionCalls = new ArrayList<String>();
    functionType = FunctionType.NONE;
    parent = nodeParent;
    parent.child.add(this);
    child = new ArrayList<ASTNode>();

  }

	/**
	 * this constructor makes a empty new node assigned as a NONE type
	 */
  public ASTNode() {

    locals = new ArrayList<String>();
    functionCalls = new ArrayList<String>();
    functionType = FunctionType.NONE;
    parent = null;
    child = new ArrayList<ASTNode>();

  }
}
