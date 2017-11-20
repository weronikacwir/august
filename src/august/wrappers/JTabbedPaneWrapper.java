package august.wrappers;

import august.TestableComponent;
import august.TestRobot;
import javax.swing.JTabbedPane;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

public class JTabbedPaneWrapper extends ComponentWrapper {

protected JTabbedPane tabbedPane;

public static final String KEYWORD_SELECT_TAB = "selectTab";

public static final String PROPERTY_SELECTED_TAB = "selectedTab";

public static final String ARG_TAB_TITLE = "tabTitle";

public JTabbedPaneWrapper(JTabbedPane tabbedPane) {
    super(tabbedPane);
    this.tabbedPane = tabbedPane;
}
    
public void performAction(String keyword, Map args) throws BadKeywordException, 
BadArgumentsException, LogicalException {
    if (keyword.equals(KEYWORD_SELECT_TAB)) {
        performSelectTab(args);
    }
    else {
        super.performAction(keyword, args);
    }
}
    
protected void performSelectTab(Map args) throws 
TestableComponent.BadArgumentsException, TestableComponent.LogicalException {
    if (!args.containsKey(ARG_TAB_TITLE)) {
        throw new TestableComponent.BadArgumentsException(ARG_TAB_TITLE 
            + " must be specified to perform selectTab.");
    }
    else if (tabbedPane.isShowing()) {
        String tabTitle = (String)(args.get(ARG_TAB_TITLE));
        int tabIndex = tabbedPane.indexOfTab(tabTitle);

        // Calculate on screen location of tab using the position of the tab 
        // relative to the left corner of tab manager
        Point tabbedPaneCoordinates = tabbedPane.getLocationOnScreen();  
        Rectangle relativeTabCoordinates = tabbedPane.getBoundsAt(tabIndex);
        int x = (int) (tabbedPaneCoordinates.getX() + 
                       relativeTabCoordinates.getX() + 
                       relativeTabCoordinates.getWidth() / 2);
        int y = (int) (tabbedPaneCoordinates.getY() + 
                       relativeTabCoordinates.getY() + 
                       relativeTabCoordinates.getHeight() / 2);
        
        // Select the tab.
        TestRobot.click(x,y);
    }
    else {
        throw new TestableComponent.LogicalException(tabbedPane.getName() +
            " must be showing on screen before a tab can be selected.");
    }
}

public String checkProperty(String prop, String expectedVal) throws 
BadPropertyException {
    if (prop.equals(PROPERTY_SELECTED_TAB)) {
        return checkSelectedTab(expectedVal);
    }
    else {  
        return super.checkProperty(prop, expectedVal);
    }
}

protected String checkSelectedTab(String expectedVal) {
    String returnVal = null;
    
    String actual = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    if (! actual.equals(expectedVal)) {
        returnVal = actual;
    }
    return returnVal;
}
}

