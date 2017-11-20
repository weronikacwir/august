/*
 * JTreeWrapper.java
 *
 * Created on May 10, 2002, 2:05 PM
 */

package august.wrappers;


import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Map;
import java.awt.*;

import august.TestableComponent;
import august.ObjectFinder;
import august.TestRobot;


/**
 * $Revision: 1.2 $
 * $Date: 2002/08/01 20:48:15 $
 * $Author: hongfei $
 * Original Author : adamm
 */
public class JTreeWrapper extends august.wrappers.ComponentWrapper {

public static final String KEYWORD_EXPAND = "expand";
public static final String KEYWORD_COLLAPSE = "collapse";
public static final String KEYWORD_EXECUTE = "excute";
public static final String ARG_NODE_STRING = "nodeString";
public static final String ARG_ROOT_NODE = "rootNode";

private JTree tree;
/** Creates a new instance of JTreeWrapper */
public JTreeWrapper(JTree tree) {
    super(tree);
	this.tree = tree;
}

public void performAction(String keyword, Map args) throws
    TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
    TestableComponent.LogicalException {
        if (KEYWORD_EXPAND.equals(keyword)){
            expandNode(args);
        }
        else if (KEYWORD_COLLAPSE.equals(keyword)){
            collapseNode(args);
        }
        else if (KEYWORD_EXECUTE.equals(keyword)){
            executeNode(args);
        }
        else{
            super.performAction(keyword, args);
        }
}

protected void expandNode(Map args) throws TestableComponent.BadArgumentsException{
	String nodeString = (String)args.get(ARG_NODE_STRING);
	if(nodeString == null) {
		throw new TestableComponent.BadArgumentsException("Cannot expand the "
                + "tree node, nodeString must be specified.");
	}
	TreePath targetNode = tree.getNextMatch(nodeString, 0, Position.Bias.Forward );
	if(!tree.isExpanded(targetNode)){
		doubleClick(targetNode);
	}
}

protected void collapseNode(Map args) throws TestableComponent.BadArgumentsException	{
	String nodeString = (String)args.get(ARG_NODE_STRING);
	if(nodeString == null) {
		throw new TestableComponent.BadArgumentsException("Cannot expand the "
                + "tree node, nodeString must be specified.");
	}
	TreePath targetNode = tree.getNextMatch(nodeString, 0, Position.Bias.Forward );
	if(!tree.isCollapsed(targetNode)){
		doubleClick(targetNode);
	}

}

protected void executeNode(Map args) throws TestableComponent.BadArgumentsException {
	String nodeString = (String)args.get(ARG_NODE_STRING);
	if(nodeString == null) {
		throw new TestableComponent.BadArgumentsException("Cannot expand the "
                + "tree node, nodeString must be specified.");
	}
	TreePath targetNode = tree.getNextMatch(nodeString, 0, Position.Bias.Forward );
	System.out.println("get treepath for " + nodeString + " = " + targetNode);
	doubleClick(targetNode);

}

/**
 * returns the JTree whose root node user object toString method returns a value
 * matching the given parameter
 */
protected JTree locateTree(String rootNode){
    return ObjectFinder.findTree(rootNode);

}

protected DefaultMutableTreeNode locateNode(Map args){
    String rootNodeString = args.get(ARG_ROOT_NODE).toString();
    JTree tree = locateTree(rootNodeString);
    return null;
}

protected void doubleClick(TreePath targetNode) {
	if(!tree.isVisible(targetNode)) {
		tree.makeVisible(targetNode);
	}
	Rectangle reg = tree.getPathBounds(targetNode);
	Point nodePoint = reg.getLocation();
	Point treePoint = tree.getLocationOnScreen();
	TestRobot.doubleClick(5, (int) (treePoint.x + reg.getX() + reg.getWidth() / 2), (int) (treePoint.y + reg.getY() + reg.getHeight() /2 ));
}

}
