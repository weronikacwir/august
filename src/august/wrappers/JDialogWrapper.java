/*
 * Created by IntelliJ IDEA.
 * User: hongfei
 * Date: Aug 28, 2002
 * Time: 4:43:10 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package august.wrappers;

import august.TestableComponent;
import august.TestRobot;
import august.ObjectFinder;
import java.awt.Point;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class JDialogWrapper extends ComponentWrapper {

private JDialog dialog;

protected static final String KEYWORD_CLOSE = "close";
//not implement yet
protected static final String KEYWORD_MOVE = "move";

protected static final String ARG_DIRECTION = "direction";
protected static final String ARG_X_DISTANCE = "xDistance";
protected static final String ARG_Y_DISTANCE = "yDistance";

public JDialogWrapper(JDialog dialog) {
    super(dialog);
    this.dialog = dialog;
}

public void performAction(String keyword, Map args) throws
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
	if (keyword.equals(KEYWORD_CLOSE)) {
        performClose();
    }
    else if (keyword.equals(KEYWORD_MOVE)) {
        performMove(args);
    }
    else {
        super.performAction(keyword, args);
    }
}

protected void performClose() throws TestableComponent.LogicalException {
    if (!dialog.isShowing()) {
        throw new TestableComponent.LogicalException("Cannot close the frame "
                + dialog.getName() + "; it is not showing on screen.");
    }
	dialog.dispose();
}
protected void performMove(Map args) throws TestableComponent.LogicalException,
TestableComponent.BadArgumentsException {
    if (!dialog.isShowing())  {
        throw new TestableComponent.LogicalException("Cannot move the frame "
                + dialog.getName() + "; it is not showing on screen.");
    }
    if (!args.containsKey(ARG_X_DISTANCE)
        || !args.containsKey(ARG_Y_DISTANCE)) {
        throw new TestableComponent.BadArgumentsException("Cannot move the "
                + "frame " + dialog.getName() + "; the x and y distance"
                + " must be specified.");
    }

    // Extract the x and y distance of the move from the args.
    int xDist, yDist;
    try {
        xDist=Integer.parseInt((String)(args.get(ARG_X_DISTANCE)));
        yDist=Integer.parseInt((String)(args.get(ARG_Y_DISTANCE)));
    }
    catch(NumberFormatException nfe) {
        throw new TestableComponent.BadArgumentsException("Cannot move the "
            + "dialog " + dialog.getName() + "; the distance is not "
            + "correctly specified");
    }
    // Get the starting and end positions, so that we can check if the move was
    // successful at the end.
    Point startingPosition, endPosition;

    // The move is performed differently for iconified
    // and non-iconified window.
    // Move non-iconified window.
    // Get the current position, so that we can check if the move was
    // successful at the end.
    startingPosition = dialog.getLocationOnScreen();

    // Get the middle of the title bar.
    BasicInternalFrameTitlePane titleBar = getTitleBar();
    Point topLeftCorner = titleBar.getLocationOnScreen();
    int x = (int) (topLeftCorner.getX() + titleBar.getWidth() / 2);
    int y = (int) (topLeftCorner.getY() + titleBar.getHeight() / 2);

        // Perform the move.
    TestRobot.dragAndDrop(x, y, xDist, yDist);
    endPosition = dialog.getLocationOnScreen();
    // Check if the move was successful; throw exception if it was not.
    if (!(endPosition.getX() == startingPosition.getX() + xDist)
        || !(endPosition.getY() == startingPosition.getY() + yDist)) {
        throw new TestableComponent.LogicalException("The move of the "
                + "frame " + dialog.getName() + " did not succeed;"
                + " the current position is not as requested.");
    }
}

private BasicInternalFrameTitlePane getTitleBar() {
    BasicInternalFrameTitlePane titleBar = null;

    ArrayList biftps = ObjectFinder.findGUIComponents(dialog,
            BasicInternalFrameTitlePane.class);
    titleBar = (BasicInternalFrameTitlePane)(biftps.get(0));

    return titleBar;
}

private ArrayList getTitleBarButtons() {
    ArrayList buttons = null;

    BasicInternalFrameTitlePane titleBar = getTitleBar();
    buttons = ObjectFinder.findGUIComponents(titleBar, JButton.class);

    return buttons;
}

private JButton getCloseButton() {
    JButton closeButton = null;
    closeButton = getRightmostTitleBarButton();
    return closeButton;
}



private JButton getRightmostTitleBarButton() {
    JButton rightmost = null;

    ArrayList buttons = getTitleBarButtons();
    int xCoordinate;
    int largestXcoordinate = 0;
    JButton button;
    for (int i=0; i < buttons.size(); i++) {
        button = (JButton)(buttons.get(i));
        xCoordinate = (int)(button.getLocation().getX());
        if (xCoordinate > largestXcoordinate) {
            largestXcoordinate = xCoordinate;
            rightmost = button;
        }
    }
    return rightmost;
}
}
