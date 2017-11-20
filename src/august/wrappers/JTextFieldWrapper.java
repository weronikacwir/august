package august.wrappers;

import august.TestRobot;
import august.TestableComponent;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Map;

public class JTextFieldWrapper extends JTextComponentWrapper {

public static final String KEYWORD_TYPE = "type";

public static final String ARG_TEXT = "text";
    
public JTextFieldWrapper (JTextField jtextfield) {
    super(jtextfield);
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException { 
    if (keyword.equals(KEYWORD_TYPE)) {
        performType(args);
    }
    else {
        super.performAction(keyword, args);
    }
}

protected void performType(Map args) throws TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
     if (!args.containsKey(ARG_TEXT)) {
        throw new TestableComponent.BadArgumentsException(ARG_TEXT 
            + " must be specified to perform type.");
    }
     
    if (!component.isShowing()) {
        throw new TestableComponent.LogicalException("Cannot type into field " 
            + component.getName() + "; is is not showing on screen.");
    }
    else if (!((JTextField)component).isEditable()) {
        throw new TestableComponent.LogicalException("Cannot type into field " 
            + component.getName() + "; is is not editable.");
    }
    else {
        // Find the mid-point of the component.
        Point topLeftCorner = component.getLocationOnScreen();        
        int x = (int) (topLeftCorner.getX() + component.getWidth() / 2);
        int y = (int) (topLeftCorner.getY() + component.getHeight() / 2);
        
        // Select the field and go to home position (beginning of text).
        TestRobot.click(x, y);
        TestRobot.keyStroke(KeyEvent.VK_HOME);
        
        // Delete text, if any.
        TestRobot.keyPress(KeyEvent.VK_SHIFT);
        TestRobot.keyStroke(KeyEvent.VK_END);
        TestRobot.keyRelease(KeyEvent.VK_SHIFT);
        TestRobot.hitDelete();
        
        // Now, finally, type.
        String text = (String)(args.get(ARG_TEXT));
        TestRobot.type(text);
    }
}

}
