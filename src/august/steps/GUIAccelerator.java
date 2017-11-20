/*
 * Created by IntelliJ IDEA.
 * User: hongfei
 * Date: Jul 17, 2002
 * Time: 1:34:39 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package august.steps;

import august.TestStep;
import org.w3c.dom.Element;
import august.ParsingException;
import august.TestRobot;
import august.TestableComponent;

public class GUIAccelerator extends TestStep {

private static final String KEY_ATTRIBUTE = "key";
private static final String CONTROLS_ATTRIBUTE = "controls";
private String key, controls;
public GUIAccelerator(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    super(scriptID, stepNumber, node);
	//key can be null, but the controls can not.
    key = node.getAttribute(KEY_ATTRIBUTE);
	controls = node.getAttribute(CONTROLS_ATTRIBUTE);
	if (controls.equals("")) {
        throw new ParsingException("controls attribute must be specified.");
    }

}

public void execute() throws TestStep.FailureException {
	try {
		TestRobot.accelerator(key, controls);
	}
	catch (TestableComponent.BadArgumentsException e) {
		e.printStackTrace();
	}
}
}
