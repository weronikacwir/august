package august.steps;

import august.ObjectFinder;
import august.TestStep;
import august.TestableComponent;
import august.ParsingException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.awt.Component;
import java.util.Map;
import java.util.HashMap;

public class GUIAction extends TestStep {
    
private static final String COMPONENT_NAME_ATTRIBUTE = "componentName";
private static final String KEYWORD_ATTRIBUTE = "keyword";
private static final String ARG_TAG_NAME = "arg";
private static final String ARG_TYPE_ATTRIBUTE = "type";
private static final String ARG_VALUE_ATTRIBUTE = "value";

protected String componentName;
protected String keyword;
protected Map args;

public GUIAction(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    super(scriptID, stepNumber, node);
    
    componentName = node.getAttribute(COMPONENT_NAME_ATTRIBUTE);
    keyword = node.getAttribute(KEYWORD_ATTRIBUTE);

    // Extract the arguments from the node (if any)
    NodeList argList = node.getElementsByTagName(ARG_TAG_NAME);
    Element arg;
    args = new HashMap();
    String type, value;
    for (int i = 0; i < argList.getLength(); i++ ) {
        arg = (Element)(argList.item(i));
        type = arg.getAttribute(ARG_TYPE_ATTRIBUTE);
        value = arg.getAttribute(ARG_VALUE_ATTRIBUTE);
        args.put(type, value);
    }
}

public void execute() throws TestStep.FailureException {
    // First, find the component.
    Component component = ObjectFinder.findGUIComponent(componentName);

    // If the component cannot be found, then this test step is a failure.
    if (component == null) {
        throw new TestStep.FailureException("Cannot find " + componentName);
    }
    // If the component was found, then, try to find and invoke the right 
    // method on this component.
    else {
        try {
            TestableComponent tc = TestableComponent.Wrapper.wrap(component);
            tc.performAction(keyword, args);
        }
        catch (Exception e) {
            throw new TestStep.FailureException(e);
        }
    }
}
    
}
