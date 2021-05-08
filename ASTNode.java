import java.util.ArrayList;
import java.util.ArrayList;

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

    //current locals of the node.
    public ArrayList<Character> locals;
    public ArrayList<Character> functionCalls;

    public ASTNode parent;
    public ArrayList<ASTNode> child;
    public NodeType type;
		public int lexicalDepth;
    public FunctionType functionType;

		public boolean hasChildren() {
			return child.size() != 0;
		}

		public ASTNode(ASTNode nodeParent) {

            locals = new ArrayList<Character>();
            functionCalls = new ArrayList<Character>();
            //NodeType = FunctionType.NONE;
						functionType = FunctionType.NONE;
            parent = nodeParent;
						parent.child.add(this);
            child = new ArrayList<ASTNode>();

		}


    public ASTNode() {

            locals = new ArrayList<Character>();
            functionCalls = new ArrayList<Character>();
            //NodeType = FunctionType.NONE;
						functionType = FunctionType.NONE;
            parent = null;
            child = new ArrayList<ASTNode>();

    }
}
