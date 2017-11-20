package august.steps;

import august.ObjectFinder;
import august.TestStep;
import august.TestableComponent;
import august.ParsingException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.awt.Component;

public class GUIResultCheck extends TestStep {
    
private static final String COMPONENT_NAME_ATTRIBUTE = "componentName";
private static final String PROPERTY_NAME_ATTRIBUTE = "property";
private static final String PROPERTY_VALUE_ATTRIBUTE = "expectedValue";

private String componentName;
private String property;
private String expectedValue;

public GUIResultCheck(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    super(scriptID, stepNumber, node);
    
    componentName = node.getAttribute(COMPONENT_NAME_ATTRIBUTE);
    property = node.getAttribute(PROPERTY_NAME_ATTRIBUTE);
    expectedValue = node.getAttribute(PROPERTY_VALUE_ATTRIBUTE);
}

public void execute() throws TestStep.FailureException {
    // First, find the component.
    Component component = ObjectFinder.findGUIComponent(componentName);

    // If the component cannot be found, then this test step results in 
    // a failure.
    if (component == null) {
        throw new TestStep.FailureException("Cannot find " + componentName);
    }
    
    // If the component was found, then check the property.
    else {
        try {
            TestableComponent tc = TestableComponent.Wrapper.wrap(component);
            String actualValue = tc.checkProperty(property, expectedValue);
            
            // If the actual value under the property was different than 
            // expected, then the actualValue variable will not be null.
            // That means that the result check failed, and that this step 
            // results in a failure.
            // (If it is null, then the result check passed.)
            if (actualValue != null) {
                throw new TestStep.FailureException( 
                    "Component " + componentName 
                    + " failed check on property " + property + ";"
                    + " expected " + expectedValue + " was " + actualValue);
            }
        }
        catch (Exception e) {
            throw new TestStep.FailureException(e);
        }
    }    
}
}