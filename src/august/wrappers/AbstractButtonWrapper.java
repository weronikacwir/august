package august.wrappers;

import august.TestRobot;
import august.TestableComponent;
import java.util.Map;
import javax.swing.AbstractButton;
import java.awt.Point;

public class AbstractButtonWrapper extends ComponentWrapper {

public static final String KEYWORD_CLICK = "click";
public static final String KEYWORD_SELECT = "select";

public static final String PROPERTY_IS_SELECTED = "isSelected";

public AbstractButtonWrapper(AbstractButton button) {
    super(button);
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException { 
    if (keyword.equals(KEYWORD_CLICK)) {
        performClick();
    }
    else if (keyword.equals(KEYWORD_SELECT)) {
        performSelect();
    }
    else {
        super.performAction(keyword, args);
    }
}

protected void performClick() throws TestableComponent.LogicalException {
    if (component.isShowing()) {
        Point coordinates = component.getLocationOnScreen();
        TestRobot.click(coordinates.x, coordinates.y);
    }
    else {
        throw new TestableComponent.LogicalException("Cannot click " 
            + component.getName() + "; is is not showing on screen.");
    }
}

protected void performSelect() throws TestableComponent.LogicalException {
    if (component.isShowing()) {
        Point coordinates = component.getLocationOnScreen();
        TestRobot.click(coordinates.x, coordinates.y);
    }
    else {
        throw new TestableComponent.LogicalException("Cannot select " 
            + component.getName() + "; is is not showing on screen.");
    }    
}

public String checkProperty(String prop, String expectedVal) throws 
TestableComponent.BadPropertyException {
    if (prop.equals(PROPERTY_IS_SELECTED)) {
        return checkIsSelected(expectedVal);
    }
    else {
        return super.checkProperty(prop, expectedVal);
    }
}

protected String checkIsSelected(String expectedVal) {
    boolean actual = ((AbstractButton)component).isSelected();
    return checkBooleanProperty(actual, expectedVal);
}
}