import java.util.*;

/**
 * Iteratively reduce the expression tree, substituting the relevant functions
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Evaluator {
  // instance variables - replace the example below with your own
  private int x;

  // This holds the rootNode
  // of each step of the AST reduction
  // this way we can visualize the reduction live
  public ArrayList<ASTNode> ASTs;

  /**
   * Constructor for objects of class evaluator
   */

  // Basically copy over bottom branches of the tree with the
  public void ApplyToNewTree(ASTNode replaceNode, ASTNode currentNode, HashMap<String, ASTNode> values) {


    //Since we cannot iterate over a structure we're editing
    //(possible but hard)
    //We're just gonna iterate over the previous structure handed to us
    //following and making adjustments in our new struct
    
    ASTNode newNode = replaceNode;

    // This will be moving around following the


    HashMap<String, Integer> ignoreMap = new HashMap<String, Integer>();

    // We need 2 trackers, because we're modifying one tree
    // so when we copy subtrees we're gonna need to modify the tracker
    Stack<Integer> copyTracker = new Stack<Integer>();
    Stack<Integer> indexTracker = new Stack<Integer>();
    int currentIndex = 0;
    int copyIndex = 0;

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

        newNode = newNode.parent;
        currentNode = currentNode.parent;
        currentIndex = indexTracker.pop();
        copyIndex = copyTracker.pop();


        Iterator<Map.Entry<String, Integer>> iter = ignoreMap.entrySet().iterator();

        while (iter.hasNext()) {

          Map.Entry<String, Integer> entry = iter.next();
          
          if (entry.getValue() == indexTracker.size()) {
            iter.remove();
          }

        }

        // assert newNode != null : "newNode parent null how?";

        continue;

      } else {

        // if we haven't found are first abstraction, it's definitely gonna be
        // part of the tree.

        try {
          //System.out.println("intermediate : "  + ASTFormatter.FormatAST(replaceNode));
        } catch (Exception e) {}


        currentNode = currentNode.child.get(currentIndex);
        newNode = newNode.child.get(copyIndex);
        // callback here

        indexTracker.push(++currentIndex);
        copyTracker.push(++copyIndex);
        currentIndex = 0;
        copyIndex = 0;

        if (currentNode == null) {
          // TODO: Handle.
        }

        switch (currentNode.functionType) {
          case NONE:
            break;
          case FUNCTION_CALL:

            // ArrayList<Character> toRemove = new ArrayList<Character>();
            // So there's a small problem we want to clone the subtree that we 
            // want to substitute into this node, but we'll lose its children
            // since we'll overwrite the child field 
            // but if we keep the children we have to mark them for deletion later

            for (String call : currentNode.functionCalls) {

              ASTNode function = values.get(call);

              if (function != null && ignoreMap.get(call) == null) {
                ArrayList<ASTNode> savedChild = newNode.child;
                newNode.child = new ArrayList<ASTNode>();
                newNode.CloneSubTree(function);
                  
                if (savedChild.size() != 0) {

                  ASTNode noneNode = new ASTNode();

                  // child age is just the index into
                  // the array that holds the children
                  int childAge = indexTracker.pop();
                    
                  noneNode.parent = newNode.parent;
                  noneNode.child = savedChild;
                  
                  for (ASTNode child : noneNode.child) {
                    child.parent = noneNode;
                  }

                  newNode.parent.child.add(childAge, noneNode);

                  indexTracker.push(childAge);
                  newNode = noneNode;
                }





                //copyIndex += newNode.child.size();
                //newNode.child.addAll(savedChild);
              }
            }

            break;
          case NESTED_FUNCTION:
            // Currently we don't handle register renaming

            //rename register
            for (int i = 0; i < currentNode.locals.size(); i++) {
              String local = currentNode.locals.get(i);
              if (values.get(local) != null) {

                ignoreMap.put(local, indexTracker.size());

              }
            }

            // Since obviously we don't allow register renaming
            // The nested functions that remain don't share any local allocation

            // Create a new child node
            // hopefully it makes a new heap allocation.
            // I hope this doesn't just point to the other locals
            // Since I have no idea how the lifetimes collid

            break;
        }
      }
    }

  }

  public ASTNode Reduce(ASTNode node) {

    ASTNode newTree = new ASTNode();
    ASTNode newNode = newTree;
    ASTNode currentNode = node;
    // iterative instead of recursive
    // see comment on TCO in LexicalParser.java
    Stack<Integer> indexTracker = new Stack<Integer>();
    ArrayList<ASTNode> reducableBranches = new ArrayList<ASTNode>();

    // This is basically the same as
    ArrayList<ASTNode> replaceNodes = new ArrayList<ASTNode>();
    int currentIndex = 0;

    // Empty tree already reduced
    if (!node.hasChildren()) {
      return newTree;
    }

    // Fuck my life why do I have to write a language that has no tail call
    // optimization
    // my fucking life would be 1000x if I could do this with tailcalls, but I feel
    // bad
    // because I have no idea if some poor fucker inputs a 200000 line long lambda
    // calculus
    // it's going to fucking explode

    // Fuck my life, a tree is only reducable when it's on a layer of its own
    // and there are multiple siblings and the oldest sibling (child[0]) is an
    // abstraction
    // e.g.
    // (a (\y.(y)) b) - NOT reducable
    // (a ((\y.(y)) b)) = a b - reducable

    // We need to find the deepest reducable abstraction and work from there,
    // could be done recursively by we have no TCO in Java, so iteratively it is

    // We're copying the whole tree to the new node
    // then we go over each part that needs to be reduced
    // reduce it and the detach links in the new tree

    ASTNode reduceNode = null;
    ASTNode replaceNode = null;

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

        currentNode = currentNode.parent;
        currentIndex = indexTracker.pop();
        newNode = newNode.parent;

        continue;

      } else {

        // if we haven't found are first abstraction, it's definitely gonna be
        // part of the tree.
        newNode = new ASTNode(newNode);
        currentNode = currentNode.child.get(currentIndex);
        ASTNode.CloneNode(currentNode, newNode);

        if (currentNode == null) {
          // TODO: Handle.
        }

        // is abstraction and is on the very left side.
        if (currentNode.functionType == FunctionType.NESTED_FUNCTION && currentIndex == 0 && currentNode.parent.child.size() != 1) {
          if (reduceNode == null || replaceNode == null) {
            reduceNode = currentNode;
            replaceNode = newNode;
          }
        }

        indexTracker.push(++currentIndex);
        currentIndex = 0;

        continue;

      }

    }

  // Nothing to reduce.
  if (reduceNode == null || replaceNode == null) {
    return newTree;
  }


    // should never happen, literally
    if (reduceNode.parent == null) {
      return newTree;
    }

    int numParams = reduceNode.locals.size();
    int argsLeft = reduceNode.parent.child.size() - 1;
    int numArgs = Math.min(argsLeft, numParams);

    // There must necessarily be functionCall(s) after this
    // or atleast functionCall followed by nestedFunction

    HashMap<String, ASTNode> functionMapper = new HashMap<String, ASTNode>();
    ArrayList<String> toRemove = new ArrayList<String>();
    // Map functions to name
    for (int i = 0; i < numArgs; i++) {
      String funcName = reduceNode.locals.get(i);
      toRemove.add(funcName);
      ASTNode appliedNode = reduceNode.parent.child.get(i + 1);
      functionMapper.put(funcName, appliedNode);
    }


    System.out.println("Leaf : " + ASTFormatter.FormatNode(replaceNode));

    replaceNode.locals.removeAll(toRemove);

    //If we've removed all the variables change it into a NONE type
    if (replaceNode.locals.size() == 0) {
      replaceNode.functionType = FunctionType.NONE;
    }

    ApplyToNewTree(replaceNode, reduceNode, functionMapper);

    ArrayList<ASTNode> toUnlink = new ArrayList<ASTNode>();

    for (int i = 0; i < numArgs; i++) {
      toUnlink.add(replaceNode.parent.child.get(i+1));
    }

    replaceNode.parent.child.removeAll(toUnlink);

    try {
      //System.out.println("lol : " + ASTFormatter.FormatAST(newTree));
    } catch (Exception Err) {}

    //if (numParams > argsLeft) {
    //  
    //  for (int i = 0; i < replaceNode.parent.child.size(); i++) {
    //    if (i == 0) { continue; }
    //    replaceNode.parent.child.get(i).Unlink();
    //  }
    //  

    //} else if (argsLeft > numParams) {
    //  ASTNode changePoint = replaceNode.parent;

    //  for (int i = 0; i < (argsLeft - numParams); i++) {
    //    changePoint.child.add(reduceNode.parent.child.get(i + numParams));
    //  }
    //}

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
          // TODO: Handle this too
        }

        currentNode = currentNode.parent;

        if (currentNode == null) {

        }

        currentIndex = indexTracker.pop();
        depth -= 1;

        // newNode = newNode.parent;

        // assert newNode != null : "newNode parent null how?";

        continue;

      } else {

        // if we haven't found are first abstraction, it's definitely gonna be
        // part of the tree.


        currentNode = currentNode.child.get(currentIndex);
				depth += 1;
        // callback here

        indexTracker.push(++currentIndex);
        currentIndex = 0;

        try {
          //System.out.println("indexTracker : " + indexTracker);
          //System.out.println("Cleaning : " + ASTFormatter.FormatAST(rootNode));
        } catch (Exception err) {}

        if (currentNode == null) {
          System.out.println("fudege " + currentNode);
          // TODO: Handle.
        }

        if (currentNode.functionType == FunctionType.NONE && currentNode.parent != null &&
        currentNode.child.size() == 1) {

          //System.out.println("lmao");

          for (ASTNode node : currentNode.child) {
            node.parent = currentNode.parent;
          }

          if (currentNode.parent != null) {

            int childAge = indexTracker.pop();

            currentNode.parent.child.addAll(childAge, currentNode.child);
            currentNode.parent.child.remove(currentNode);
            //indexTracker.push(indexTracker.pop() + 1);

          } else {
            indexTracker.pop();
          }

          depth -= 1;
        }
				currentNode.lexicalDepth = depth;

      }
    }

    return;

  }

  public ASTNode Evaluate(ASTNode rootNode) {
    ASTNode reduced = Reduce(rootNode);
    
    //if (reduced.functionType != FunctionType.NONE) {
      //ASTNode topNode = new ASTNode();
      //reduced.parent = topNode;
      //topNode.child.add(reduced);
      //reduced = topNode;
    //}

    try {
      //System.out.println("NotCleaned : " + ASTFormatter.FormatAST(reduced));
    } catch (Exception E ) {}

    CleanAST(reduced);
    return reduced;
  }

  public Evaluator() {
    
  }

  /**
   * An example of a method - replace this comment with your own
   *
   * @param y a sample parameter for a method
   * @return the sum of x and y
   */
  public int sampleMethod(int y) {
    // put your code here
    return x + y;
  }

}
