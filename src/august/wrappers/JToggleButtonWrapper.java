package august.wrappers;

import august.TestableComponent;
import java.util.Map;
import javax.swing.JToggleButton;

public class JToggleButtonWrapper extends AbstractButtonWrapper {
    
public static final String KEYWORD_SELECT = "select";

public JToggleButtonWrapper(JToggleButton button) {
    super(button);
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
    if (keyword.endsWith(KEYWORD_SELECT)) {
        performSelect();
    }
    else {
        super.performAction(keyword, args);
    }
}

public void performSelect() throws TestableComponent.LogicalException {
    if (component.isShowing()) {
        if (!((JToggleButton)component).isSelected()) {
            performClick();
        }
    }
    else {
        throw new TestableComponent.LogicalException("Cannot select "
            + component.getName() + " it is not showing on screen.");
    }
}

}
