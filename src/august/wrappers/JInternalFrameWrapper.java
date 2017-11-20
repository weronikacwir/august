package august.wrappers;

import august.TestableComponent;
import august.TestRobot;
import august.ObjectFinder;
import java.awt.Point;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class JInternalFrameWrapper extends ComponentWrapper {

private JInternalFrame internalFrame;

protected static final String KEYWORD_SELECT = "select";
protected static final String KEYWORD_CLOSE = "close";
protected static final String KEYWORD_ICONIFY = "iconify";
protected static final String KEYWORD_DEICONIFY = "deiconify";
protected static final String KEYWORD_RESTORE = "restore";
protected static final String KEYWORD_MAXIMIZE = "maximize";
protected static final String KEYWORD_MOVE = "move";
protected static final String KEYWORD_RESIZE = "resize";

protected static final String ARG_DIRECTION = "direction";
protected static final String ARG_X_DISTANCE = "xDistance";
protected static final String ARG_Y_DISTANCE = "yDistance";

protected static final String PROPERTY_IS_CLOSABLE = "isClosable";
protected static final String PROPERTY_IS_CLOSED = "isClosed";
protected static final String PROPERTY_IS_ICONIFIABLE = "isIconifiable";
protected static final String PROPERTY_IS_ICON = "isIcon";
protected static final String PROPERTY_IS_MAXIMIZABLE = "isMaximizable";
protected static final String PROPERTY_IS_MAXIMUM = "isMaximum";
protected static final String PROPERTY_IS_RESIZABLE = "isResizable";

public JInternalFrameWrapper(JInternalFrame internalFrame) {
    super(internalFrame);
    this.internalFrame = internalFrame; 
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
    if (keyword.equals(KEYWORD_SELECT)) {
        performSelect();
    }
    else if (keyword.equals(KEYWORD_CLOSE)) {
        performClose();
    }
    else if (keyword.equals(KEYWORD_ICONIFY)) {
        performIconify();
    }
    else if (keyword.equals(KEYWORD_DEICONIFY)) {
        performDeiconify();
    }
    else if (keyword.equals(KEYWORD_RESTORE)) {
        performRestore();
    }
    else if (keyword.equals(KEYWORD_MAXIMIZE)) {
        performMaximize();
    }
    else if (keyword.equals(KEYWORD_MOVE)) {
        performMove(args);
    }
    else if (keyword.equals(KEYWORD_RESIZE)) {
        performResize(args);
    }
    else {
        super.performAction(keyword, args);
    }
}

protected void performSelect() throws TestableComponent.LogicalException {
    if (internalFrame.isShowing()) {
        Point topCorner = internalFrame.getLocationOnScreen();
        TestRobot.click();
    }
    else {
        throw new TestableComponent.LogicalException("Cannot select: " +
            internalFrame.getName() + "; it is not showing on screen");
    }
}

protected void performClose() throws TestableComponent.LogicalException {
    if (!internalFrame.isClosable()) {
        throw new TestableComponent.LogicalException("Cannot close the frame "
                + internalFrame.getName() + "; it is not closable.");
    }
    if (!internalFrame.isShowing()) {
        throw new TestableComponent.LogicalException("Cannot close the frame "
                + internalFrame.getName() + "; it is not showing on screen.");
    }
    if (internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot close the frame "
                + internalFrame.getName() + "; it is an icon.");
    }
    try {
        TestableComponent closeButton =
            TestableComponent.Wrapper.wrap(getCloseButton());
        closeButton.performAction(AbstractButtonWrapper.KEYWORD_CLICK, null);
    }
    catch(TestableComponent.BadKeywordException tcbke) {
    }
    catch(TestableComponent.BadArgumentsException tcbae) {
    }
    catch(java.lang.reflect.InvocationTargetException ite) {
        throw new TestableComponent.LogicalException(ite.getMessage());
    }
    catch(IllegalAccessException iae) {
        throw new TestableComponent.LogicalException(iae.getMessage());
    }
    catch(InstantiationException ie) {
        throw new TestableComponent.LogicalException(ie.getMessage());
    }
    catch(NoSuchMethodException nsme) {
        throw new TestableComponent.LogicalException(nsme.getMessage());
    }
}


protected void performIconify() throws TestableComponent.LogicalException {
    if (!internalFrame.isIconifiable()) {
        throw new TestableComponent.LogicalException("Cannot iconify the "
                + "frame " + internalFrame.getName()
                + "; it is not iconifiable.");
    }
    if (internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot iconify the "
                + "frame " + internalFrame.getName()
                + "; is is already iconified.");
    }
    try {
        TestableComponent iconButton =
            TestableComponent.Wrapper.wrap(getIconButton());
        iconButton.performAction(AbstractButtonWrapper.KEYWORD_CLICK, null);
        ObjectFinder.addIconifiedWindow(internalFrame);
    }
    catch(TestableComponent.BadKeywordException tcbke) {
    }
    catch(TestableComponent.BadArgumentsException tcbae) {
    }
    catch(java.lang.reflect.InvocationTargetException ite) {
        throw new TestableComponent.LogicalException(ite.getMessage());
    }
    catch(IllegalAccessException iae) {
        throw new TestableComponent.LogicalException(iae.getMessage());
    }
    catch(InstantiationException ie) {
        throw new TestableComponent.LogicalException(ie.getMessage());
    }
    catch(NoSuchMethodException nsme) {
        throw new TestableComponent.LogicalException(nsme.getMessage());
    }
}

protected void performDeiconify() throws TestableComponent.LogicalException {
    if (!internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot deiconify the "
                + " frame " + internalFrame.getName()
                + "; is is not iconified.");
    }
    // Click on the middle of the icon to deiconify.
    JInternalFrame.JDesktopIcon icon = internalFrame.getDesktopIcon();
    Point topLeftCorner = icon.getLocationOnScreen();
    int x = (int) (topLeftCorner.getX() + icon.getWidth() / 2);
    int y = (int) (topLeftCorner.getY() + icon.getHeight() / 2);
    TestRobot.click(x, y);

    // Remove from list of iconified windows.
    ObjectFinder.removeIconifiedWindow(internalFrame);
}

protected void performRestore() throws TestableComponent.LogicalException {
    if (!internalFrame.isMaximizable()) {
        throw new TestableComponent.LogicalException("Cannot restore the "
                + "frame " + internalFrame.getName()
                + "; it is not maximizable.");
    }
    if (!internalFrame.isMaximum()) {
        throw new TestableComponent.LogicalException("Cannot restore the "
                + "frame " + internalFrame.getName()
                + "; is is not maximized.");
    }
    if (internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot restore the "
                + "frame " + internalFrame.getName() + "; it is an icon.");
    }
    try {
        TestableComponent maxButton =
            TestableComponent.Wrapper.wrap(getMaximizeButton());
        maxButton.performAction(AbstractButtonWrapper.KEYWORD_CLICK, null);
    }
    catch(TestableComponent.BadKeywordException tcbke) {
    }
    catch(TestableComponent.BadArgumentsException tcbae) {
    }
    catch(java.lang.reflect.InvocationTargetException ite) {
        throw new TestableComponent.LogicalException(ite.getMessage());
    }
    catch(IllegalAccessException iae) {
        throw new TestableComponent.LogicalException(iae.getMessage());
    }
    catch(InstantiationException ie) {
        throw new TestableComponent.LogicalException(ie.getMessage());
    }
    catch(NoSuchMethodException nsme) {
        throw new TestableComponent.LogicalException(nsme.getMessage());
    }
}

protected void performMaximize() throws TestableComponent.LogicalException {
    if (!internalFrame.isMaximizable()) {
        throw new TestableComponent.LogicalException("Cannot maximize the "
                + "frame " + internalFrame.getName()
                + "; it is not maximizable.");
    }
    if (internalFrame.isMaximum()) {
        throw new TestableComponent.LogicalException("Cannot maximize the "
                + "frame " + internalFrame.getName()
                + "; is is already maximized.");
    }
    if (internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot maximize the "
                + "frame " + internalFrame.getName() + "; it is an icon.");
    }
    try {
        TestableComponent maxButton =
            TestableComponent.Wrapper.wrap(getMaximizeButton());
        maxButton.performAction(AbstractButtonWrapper.KEYWORD_CLICK, null);
    }
    catch(TestableComponent.BadKeywordException tcbke) {
    }
    catch(TestableComponent.BadArgumentsException tcbae) {
    }
    catch(java.lang.reflect.InvocationTargetException ite) {
        throw new TestableComponent.LogicalException(ite.getMessage());
    }
    catch(IllegalAccessException iae) {
        throw new TestableComponent.LogicalException(iae.getMessage());
    }
    catch(InstantiationException ie) {
        throw new TestableComponent.LogicalException(ie.getMessage());
    }
    catch(NoSuchMethodException nsme) {
        throw new TestableComponent.LogicalException(nsme.getMessage());
    }
}


protected void performMove(Map args) throws TestableComponent.LogicalException,
TestableComponent.BadArgumentsException {
    if (!internalFrame.isShowing() && !internalFrame.isIcon()) {
        throw new TestableComponent.LogicalException("Cannot move the frame "
                + internalFrame.getName() + "; it is not showing on screen.");
    }
    if (!args.containsKey(ARG_X_DISTANCE)
        || !args.containsKey(ARG_Y_DISTANCE)) {
        throw new TestableComponent.BadArgumentsException("Cannot move the "
                + "frame " + internalFrame.getName() + "; the x and y distance"
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
            + "frame " + internalFrame.getName() + "; the distance is not "
            + "correctly specified");
    }
    // Get the starting and end positions, so that we can check if the move was
    // successful at the end.
    Point startingPosition, endPosition;

    // The move is performed differently for iconified
    // and non-iconified window.
    // Move non-iconified window.
    if(!internalFrame.isIcon()) {
        // Get the current position, so that we can check if the move was
        // successful at the end.
        startingPosition = internalFrame.getLocationOnScreen();

        // Get the middle of the title bar.
        BasicInternalFrameTitlePane titleBar = getTitleBar();
        Point topLeftCorner = titleBar.getLocationOnScreen();
        int x = (int) (topLeftCorner.getX() + titleBar.getWidth() / 2);
        int y = (int) (topLeftCorner.getY() + titleBar.getHeight() / 2);

        // Perform the move.
        TestRobot.dragAndDrop(x, y, xDist, yDist);

        endPosition = internalFrame.getLocationOnScreen();
    }
    // Move the icon.
    else {
        JInternalFrame.JDesktopIcon icon = internalFrame.getDesktopIcon();
        startingPosition = icon.getLocationOnScreen();

        // Perform the move.
        TestRobot.dragAndDrop((int)(startingPosition.getX()),
                              (int)(startingPosition.getY()),
                              xDist, yDist);

        endPosition = icon.getLocationOnScreen();
    }

    // Check if the move was successful; throw exception if it was not.
    if (!(endPosition.getX() == startingPosition.getX() + xDist)
        || !(endPosition.getY() == startingPosition.getY() + yDist)) {
        throw new TestableComponent.LogicalException("The move of the "
                + "frame " + internalFrame.getName() + " did not succeed;"
                + " the current position is not as requested.");
    }
}

protected void performResize(Map args) throws 
TestableComponent.LogicalException, TestableComponent.BadArgumentsException {
    if (!internalFrame.isShowing()) {
        throw new TestableComponent.LogicalException("Cannor resize the frame "
                + internalFrame.getName() + "; it is not showing on screen.");
    }
    if (!internalFrame.isResizable()) {
        throw new TestableComponent.LogicalException("Cannor resize the frame "
                + internalFrame.getName() + "; it is not resizable.");
    }
    if (!args.containsKey(ARG_X_DISTANCE)
        || !args.containsKey(ARG_Y_DISTANCE)
        || !args.containsKey(ARG_DIRECTION)) {
        throw new TestableComponent.BadArgumentsException("Cannot resize the "
                + "frame " + internalFrame.getName() + "; the x and y distance"
                + " and the direction must be specified.");
    }

    // Extract the direction, and the x and y distance of the resize
    // from the args.
    String direction = (String)(args.get(ARG_DIRECTION));
    int xDiff, yDiff;
    try {
        xDiff=Integer.parseInt((String)(args.get(ARG_X_DISTANCE)));
        yDiff=Integer.parseInt((String)(args.get(ARG_Y_DISTANCE)));
    }
    catch(NumberFormatException nfe) {
        throw new TestableComponent.BadArgumentsException("Cannot resize the "
            + "frame " + internalFrame.getName() + "; the distance is not "
            + "correctly specified");
    }
    // Figure out the starting point.
    int startingPoint_X = 0;
    int startingPoint_Y = 0;
    int topLeftCorner_X = (int)(internalFrame.getLocationOnScreen().getX());
    int topLeftCorner_Y = (int)(internalFrame.getLocationOnScreen().getY());
    int startingHeight = internalFrame.getHeight();
    int startingWidth = internalFrame.getWidth();
    int xDist = 0;
    int yDist = 0;
    if (direction.equals("NW")) {
        startingPoint_X = topLeftCorner_X + 1;
        startingPoint_Y = topLeftCorner_Y + 1;
        xDist = - xDiff;
        yDist = - yDiff;
    }
    else if (direction.equals("NE")) {
        startingPoint_X = topLeftCorner_X + startingWidth -1;
        startingPoint_Y = topLeftCorner_Y + 1;
        xDist = xDiff;
        yDist = - yDiff;
    }
    else if (direction.equals("SE")) {
        startingPoint_X = topLeftCorner_X + startingWidth - 1;
        startingPoint_Y = topLeftCorner_Y + startingHeight - 1;
        xDist = xDiff;
        xDist = - yDiff;
    }
    else if (direction.equals("SW")) {
        startingPoint_X = topLeftCorner_X + 1;
        startingPoint_Y = topLeftCorner_Y + startingHeight - 1;
        xDist = - xDiff;
        yDist = yDiff;
    }
    else {
        throw new TestableComponent.BadArgumentsException("Direction must be "
                + "one of: NW, NE, SE, or SW.");
    }

    // Perform the resize.
    TestRobot.dragAndDrop(startingPoint_X, startingPoint_Y, xDist, yDist);

    // Make sure that the resize was successful; if not, throw exception
    if (! (internalFrame.getHeight() == startingHeight + yDiff)
        || !(internalFrame.getWidth() == startingWidth + xDiff)) {
        throw new TestableComponent.LogicalException("The resize of the "
                + "frame " + internalFrame.getName() + " did not succeed;"
                + " the current size is not as requested.");
    }
}

public String checkProperty(String prop, String expectedVal) throws 
TestableComponent.BadPropertyException {
    if (prop.equals(PROPERTY_IS_CLOSABLE)) {
        return checkIsClosable(expectedVal);
    }
    else if (prop.equals(PROPERTY_IS_CLOSED)) { 
        return checkIsClosed(expectedVal);        
    }
    else if (prop.equals(PROPERTY_IS_ICONIFIABLE)) { 
        return checkIsIconifiable(expectedVal);        
    }
    else if (prop.equals(PROPERTY_IS_ICON)) {
        return checkIsIcon(expectedVal);        
    }
    else if (prop.equals(PROPERTY_IS_MAXIMIZABLE)) {
        return checkIsMaximizable(expectedVal);        
    }
    else if (prop.equals(PROPERTY_IS_MAXIMUM)) {
        return checkIsMaximum(expectedVal);        
    }
    else if (prop.equals(PROPERTY_IS_RESIZABLE)) {
        return checkIsResizable(expectedVal);        
    }
    else {
        return super.checkProperty(prop, expectedVal);
    }
}

protected String checkIsClosable(String expectedVal) {
    boolean actual = internalFrame.isClosable();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsClosed(String expectedVal) {
    boolean actual = internalFrame.isClosed();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsIconifiable(String expectedVal) {
    boolean actual = internalFrame.isIconifiable();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsIcon(String expectedVal) {
    boolean actual = internalFrame.isIcon();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsMaximizable(String expectedVal) {
    boolean actual = internalFrame.isMaximizable();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsMaximum(String expectedVal) {
    boolean actual = internalFrame.isMaximum();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsResizable(String expectedVal) {
    boolean actual = internalFrame.isResizable();
    return checkBooleanProperty(actual, expectedVal);
}

// helper methods

private BasicInternalFrameTitlePane getTitleBar() {
    BasicInternalFrameTitlePane titleBar = null;

    ArrayList biftps = ObjectFinder.findGUIComponents(internalFrame,
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

    if (internalFrame.isClosable()) {
        closeButton = getRightmostTitleBarButton();
    }
    return closeButton;
}

private JButton getIconButton() {
    JButton iconButton = null;

    if (internalFrame.isIconifiable()) {
        // If the frame is both closable and maximizable,
        // then the icon button is the third rightmost
        // title bar button.
        if (internalFrame.isClosable() && internalFrame.isMaximizable()) {
            iconButton = getThirdRightmostTitleBarButton();
        }
        // If there is only one other button (either for
        // closing or for maximizing/restoring) then the icon
        // button is the second rightmost button.
        else if (internalFrame.isClosable() || internalFrame.isMaximizable()) {
            iconButton = getSecondRightmostTitleBarButton();
        }
        // Othewise, the icon button is the rightmost
        // title bar button.
        else {
            iconButton = getRightmostTitleBarButton();
        }
    }
    return iconButton;
}

private JButton getMaximizeButton() {
    JButton maximizeButton = null;

    if (internalFrame.isMaximizable()) {
        // If the frame is closable, then the maximize button
        // is the third rightmost title bar button.
        if (internalFrame.isClosable()) {
            maximizeButton = getSecondRightmostTitleBarButton();
        }
        // Otherwise the maximize button is the rightmost
        // button.
        else {
            maximizeButton = getRightmostTitleBarButton();
        }
    }
    return maximizeButton;
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

private JButton getSecondRightmostTitleBarButton() {
    JButton secondRightmost = null;

    ArrayList buttons = getTitleBarButtons();
    int xCoordinate;
    int largestXcoordinate =
            (int)(getRightmostTitleBarButton().getLocation().getX());
    int secondLargestXcoordinate = 0;
    JButton button;
    for (int i=0; i < buttons.size(); i++) {
        button = (JButton)(buttons.get(i));
        xCoordinate = (int)(button.getLocation().getX());
        if (xCoordinate > secondLargestXcoordinate
                && xCoordinate < largestXcoordinate) {
            secondLargestXcoordinate = xCoordinate;
            secondRightmost = button;
        }
    }
    return secondRightmost;
}

private JButton getThirdRightmostTitleBarButton() {
    JButton thirdRightmost = null;

    ArrayList buttons = getTitleBarButtons();
    int xCoordinate;
    int secondLargestXcoordinate =
            (int)(getSecondRightmostTitleBarButton().getLocation().getX());
    int thirdLargestXcoordinate = 0;
    JButton button;
    for (int i=0; i < buttons.size(); i++) {
        button = (JButton)(buttons.get(i));
        xCoordinate = (int)(button.getLocation().getX());
        if (xCoordinate > thirdLargestXcoordinate
            && xCoordinate < secondLargestXcoordinate) {
        thirdLargestXcoordinate = xCoordinate;
        thirdRightmost = button;
        }
     }
     return thirdRightmost;
}
}
