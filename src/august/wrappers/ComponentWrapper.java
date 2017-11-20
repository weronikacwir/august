package august.wrappers;

import august.TestableComponent;
import august.TestRobot;

import javax.swing.*;
import java.awt.Component;
import java.awt.Point;
import java.util.Map;

public class ComponentWrapper implements TestableComponent {

public static final String KEYWORD_FOCUS_ON = "focusOn";
public static final String KEYWORD_RIGHT_CLICK = "rightClick";


public static final String PROPERTY_IS_SHOWING = "isShowing";
public static final String PROPERTY_IS_ENABLED = "isEnabled";

protected Component component;   

public ComponentWrapper(Component component) {    
    this.component = component;
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
    if (keyword.equals(KEYWORD_FOCUS_ON)) {
        performFocusOn();
    }
	else if (keyword.equals(KEYWORD_RIGHT_CLICK)) {
        performRightClick(args);
    }
    else {
            throw new TestableComponent.BadKeywordException
            (keyword, component.getClass().getName());
    }
}

protected void performFocusOn() throws TestableComponent.LogicalException {
    if (component.isShowing()) {
        Point coordinates = component.getLocationOnScreen();
        TestRobot.mouseMove(coordinates.x, coordinates.y);
    }
    else {
        throw new TestableComponent.LogicalException( "Cannot focus on " 
            + component.getName() + "; is is not showing on screen.");
    }
}

protected void performRightClick(Map args) throws TestableComponent.LogicalException {
    if (component.isShowing()) {
        Point coordinates = component.getLocationOnScreen();
        TestRobot.rightClick(coordinates.x, coordinates.y);
    }
    else {
        throw new TestableComponent.LogicalException( "Cannot select the popup menu from  "
            + component.getName() + "; is is not showing on screen.");
    }
}

public String checkProperty(String prop, String expectedVal) throws
BadPropertyException {
    if (prop.equals(PROPERTY_IS_ENABLED)) {
        return checkIsEnabled(expectedVal);
    }
    else if (prop.equals(PROPERTY_IS_SHOWING)) {
        return checkIsShowing(expectedVal);
    }
    else {
        throw new TestableComponent.BadPropertyException
            (prop, component.getClass().getName());
    }
}

protected String checkIsEnabled(String expectedVal) {
    boolean actual = component.isEnabled();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkIsShowing(String expectedVal) {
    boolean actual = component.isShowing();
    return checkBooleanProperty(actual, expectedVal);
}

protected String checkBooleanProperty(boolean actual, String expectedVal) {
    String returnVal = null;

    boolean expected = Boolean.valueOf(expectedVal).booleanValue();
    if(actual != expected) {
        returnVal = actual? "true" : "false";
    }
    return returnVal;
}
}