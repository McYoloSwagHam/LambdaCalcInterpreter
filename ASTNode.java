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
 *
 *  Function1 -                         Function2 -                 Function3
 *    |                                                    |
 *    |                                                ....                                ....
 *  locals - function-body
 *                 |
 *           expression/Nested function
 *                                                   |
 *                                       locals  - function-body 
 *
 *
 *                                       etc.....
*/

public class ASTNode {

  // current locals of the node.
  public ArrayList<String> locals;
  public ArrayList<String> functionCalls;
  public static final int PRIME_BASE = 65537;
  public ASTNode parent;
  public ArrayList<ASTNode> child;
  public int lexicalDepth;
  public FunctionType functionType;

  // This is a dumb rolling hash for just making checksumming
  // AST trees to realize that they're no longer reduceable
  public static int RollingHash(int hash, int info) {
    hash *= PRIME_BASE;
    hash ^= info;
    hash += info % 2;
    return hash;
  }

  public void Upgrade() {

    //Remove this from parent
    this.parent.child.remove(this);
    this.parent = this.parent.parent;
    this.parent.child.add(this);
    this.lexicalDepth -= 1;
  }

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

  public boolean hasChildren() {
    return child.size() != 0;
  }

  public ASTNode CloneSubTree(ASTNode sourceNode) {

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

    return this;

  }

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

  public ASTNode(ASTNode nodeParent) {

    locals = new ArrayList<String>();
    functionCalls = new ArrayList<String>();
    // NodeType = FunctionType.NONE;
    functionType = FunctionType.NONE;
    parent = nodeParent;
    parent.child.add(this);
    child = new ArrayList<ASTNode>();

  }

  public ASTNode() {

    locals = new ArrayList<String>();
    functionCalls = new ArrayList<String>();
    // NodeType = FunctionType.NONE;
    functionType = FunctionType.NONE;
    parent = null;
    child = new ArrayList<ASTNode>();

  }
}
