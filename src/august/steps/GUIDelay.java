package august.steps;

import august.TestStep;
import org.w3c.dom.Element;
import august.ParsingException;

public class GUIDelay extends TestStep {

private int delay_milliseconds = -1;

private static final String DELAY_TIME_ATTRIBUTE = "delayTime";

public GUIDelay(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    super(scriptID, stepNumber, node);

    String delay = node.getAttribute(DELAY_TIME_ATTRIBUTE);
    if (delay==null) {
        throw new ParsingException("delayTime attribute must be specified.");
    }

    try {
        delay_milliseconds=Integer.parseInt(delay);
    }
    catch (NumberFormatException nfe) {
        throw new ParsingException("delayTime must be a number; "
                + delay + " is not a number");
    }

    if (delay_milliseconds < 0) {
        throw new ParsingException("delayTime must be a positive number.");
    }
}

public void execute() throws TestStep.FailureException {
    try {
        Thread.currentThread().sleep(delay_milliseconds);
    }
    catch(InterruptedException ie) {
        new TestStep.FailureException("The wait was interrupted.");
    }
}

}
