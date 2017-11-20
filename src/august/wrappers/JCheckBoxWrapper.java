package august.wrappers;

import august.TestableComponent;
import javax.swing.JCheckBox;
import java.util.Map;

public class JCheckBoxWrapper extends JToggleButtonWrapper {

public static final String KEYWORD_DESELECT = "deselect";

public JCheckBoxWrapper(JCheckBox checkbox) {
    super(checkbox);
}

public void performAction(String keyword, Map args) throws 
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
    if (keyword.equals(KEYWORD_DESELECT)) {
        performDeselect();
    }
    else {
        super.performAction(keyword, args);
    }
}

protected void performDeselect() throws TestableComponent.LogicalException {
    if(component.isShowing()) {
        if(((JCheckBox)component).isSelected()) {
            performClick();
        }
    }
    else {
        throw new TestableComponent.LogicalException("Cannot deselect "
            + component.getName() + "; it is not showing on screen.");
    }
}
}
