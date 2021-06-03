package com.mcyoloswagham;

import java.util.*;

/**
 * Iteratively reduce the expression tree, substituting the relevant functions
 * and reducing and cleaning the AST, removing empty brackets (Null Nodes)
 */
public class Evaluator {

  /**
   * debug printing/ verbosity
   */
  boolean isVerbose;

  /**
   * ApplyToNewTree is a function that reduces and applies substituion to a
   * subtree
   *
   * @param replaceNode - this is the copy of the original tree that we will edit
   *                    while iterating
   * @param currentNode - this is the original tree that we won't edit, we pass it
   *                    because it's easier to iterate it, becase we don't edit it
   * @param values      - values is a hashmap of function calls, to substitute
   *                    function calls with their relevant nodes
   */
  public void ApplyToNewTree(ASTNode replaceNode, ASTNode currentNode, HashMap<String, ASTNode> values) {

    // The currentNode of the new tree
    ASTNode newNode = replaceNode;

    // If we encounter a nested abstraction that has the same local definitions
    // we have to ignore it to be inline with lexical scoping.
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
        //
        // exit
        if (indexTracker.empty()) {
          break;
        }

        // Shouldn't happen because by the time
        // we make it up to the rootNode indexTracker should be empty
        assert currentNode.parent != null : "parent is null while going up tree";
        assert newNode.parent != null : "parent is null while going up tree";

        newNode = newNode.parent;
        currentNode = currentNode.parent;
        currentIndex = indexTracker.pop();
        copyIndex = copyTracker.pop();

        // if we've gone up the tree to the point where we're no longer in a nested
        // abstraction with local redefiniton we can remove these elements from the hash
        // map
        // this is how you iterate and remove elements from a hashmap as you iterate
        Iterator<Map.Entry<String, Integer>> iter = ignoreMap.entrySet().iterator();

        while (iter.hasNext()) {

          Map.Entry<String, Integer> entry = iter.next();

          if (entry.getValue() == indexTracker.size()) {
            iter.remove();
          }

        }

        continue;

      } else {

        // if we haven't found are first abstraction, it's definitely gonna be
        // part of the tree.

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

                // copyIndex += newNode.child.size();
                // newNode.child.addAll(savedChild);
              }
            }

            break;
          case NESTED_FUNCTION:
            // Currently we don't handle register renaming

            // rename register
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
    int currentIndex = 0;

    // Empty tree already reduced
    if (!node.hasChildren()) {
      return newTree;
    }

   
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
        if (currentNode.functionType == FunctionType.NESTED_FUNCTION && currentIndex == 0
            && currentNode.parent.child.size() != 1) {
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

    // System.out.println("Leaf : " + ASTFormatter.FormatNode(replaceNode));

    replaceNode.locals.removeAll(toRemove);

    // Since we're reducing the abstraction
    if (replaceNode.locals.size() == 0) {
      replaceNode.functionType = FunctionType.NONE;
    }

    ApplyToNewTree(replaceNode, reduceNode, functionMapper);

    ArrayList<ASTNode> toUnlink = new ArrayList<ASTNode>();

    // Since the nested abstraction is at the 0th index
    // we just want to unlink the arguments which are obviously not the first
    for (int i = 0; i < numArgs; i++) {
      toUnlink.add(replaceNode.parent.child.get(i + 1));
    }

    replaceNode.parent.child.removeAll(toUnlink);

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

        if (currentNode == null) {
          System.out.println("fudege " + currentNode);
          // TODO: Handle.
        }

        if (currentNode.functionType == FunctionType.NONE && currentNode.parent != null
            && currentNode.child.size() == 1) {

          // System.out.println("lmao");

          for (ASTNode node : currentNode.child) {
            node.parent = currentNode.parent;
          }

          if (currentNode.parent != null) {

            int childAge = indexTracker.pop();

            currentNode.parent.child.addAll(childAge, currentNode.child);
            currentNode.parent.child.remove(currentNode);
            // indexTracker.push(indexTracker.pop() + 1);

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

  /**
   * reduces a given AST and cleans it
   * 
   * @param rootNode - the AST to evaluate
   * @return a new reduced and cleaned tree, it is a copy of the input tree
   */
  public ASTNode Evaluate(ASTNode rootNode) {
    ASTNode reduced = Reduce(rootNode);
    CleanAST(reduced);
    return reduced;
  }

  /**
   * just adds
   * 
   * @param isVerbose - should print extra information?
   */
  public Evaluator(boolean isVerbose) {
    this.isVerbose = isVerbose;
  }

}
