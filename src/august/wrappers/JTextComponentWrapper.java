package august.wrappers;

import august.TestableComponent;
import javax.swing.text.JTextComponent;

public class JTextComponentWrapper extends ComponentWrapper {

public static final String PROPERTY_TEXT = "text";
public static final String PROPERTY_IS_EDITABLE = "isEditable";

public JTextComponentWrapper(JTextComponent jtextcomponent) {
    super(jtextcomponent);
}

public String checkProperty(String prop, String expectedVal) throws 
TestableComponent.BadPropertyException {
    if (prop.equals(PROPERTY_TEXT)) {
        return checkText(expectedVal);
    }
    if (prop.equals(PROPERTY_IS_EDITABLE)) {
        return checkIsEditable(expectedVal);
    }
    else {
        return super.checkProperty(prop, expectedVal);
    }
}

protected String checkText(String expectedVal) {
    String returnVal = null;
    
    String actual = ((JTextComponent)component).getText();
    if (!(expectedVal.equals(actual))) {
        if (actual == null) {
            returnVal = "[null]";
        }
        else {
            returnVal = actual;
        }
    }
    return returnVal;
}

protected String checkIsEditable(String expectedVal) {
    boolean actual = ((JTextComponent)component).isEditable();
    return checkBooleanProperty(actual, expectedVal);
}

}
    
