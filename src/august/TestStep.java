package august;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Enumeration;
import org.w3c.dom.Element;
import java.lang.reflect.Constructor;
import java.io.File;

/** Base class for all classes that contain information about a single test step
 * in a test script for the August system.
 * <P>
 * The TestStep class itself contains the id of the script where this step is
 * specified and the line number where the step starts in the script.  This
 * information is necessary for reporting in case of failure during execution
 * of this test step.  TestStep must be subclassed in order to be instantiated;
 * its subclasses must contain additional information needed to execute the
 * step.
 * <P>
 * Known subclasses: GUIActionStep, ResultCheckStep
 *
 * @author weronika
 * @see TestStepSequence
 */
public abstract class TestStep {
    
/** The xml attribute which specifies whether the test run containing this test
 * step should be aborted if this test step fails.
 */    
protected static final String ABORT_IF_FAILED = "abortIfFailed";

protected static final String RETRY_TIMES = "retryTimes";

protected static final String RETRY_DELAY = "retryDelay";

/** The id of the script where the step represented by this TestStep comes from.
 */    
protected String scriptID;

/** The number of this step in the script specified by scriptID.
 */
protected int stepNumber;

/** True if a test step sequence should be aborted if the step represented by
 * this TestStep object fails, false otherwise. 
 */
protected boolean abortIfFailed;

protected int retryTimes = 0;

protected int retryDelay = 0;

/** This constructor cannot be called directly, since TestStep is an abstract
 * class, but it should be called by constructors of subclasses of TestStep.
 * <P>
 * The subclasses of TestStep type should be called by TestStep.Creator.create
 * method.  This constructor is declared public only because it will be called
 * using reflection.
 *
 * @param scriptID id of the script where the step represented by this TestStep 
 * comes from
 * @param stepNumber number of the step in the script specified by scriptID, 
 * this is an integer and not an int bcause this constructor is called using 
 * reflection
 * @param node a Node object representing the XML element which has the 
 * information needed to execute this test step
 */
public TestStep(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    this.scriptID = scriptID;
    this.stepNumber = stepNumber.intValue();

    abortIfFailed =
        new Boolean(node.getAttribute(ABORT_IF_FAILED)).booleanValue();

    String retryTimesAttribute = node.getAttribute(RETRY_TIMES);
    if (retryTimesAttribute != null && !retryTimesAttribute.equals("")) {
        try {
            retryTimes = Integer.parseInt(retryTimesAttribute);
        }
        catch(NumberFormatException nbe) {
            throw new ParsingException("retryTimes must be a positive number");
        }
    }

    String retryDelayAttribute = node.getAttribute(RETRY_DELAY);
    if (retryDelayAttribute != null && !retryDelayAttribute.equals("")) {
        try {
            retryDelay = Integer.parseInt(retryDelayAttribute);
        }
        catch(NumberFormatException nbe) {
            throw new ParsingException("retryDelay must be a positive number");
        }
    }
}

/** Called by the TestEngine, when the TestStep represented by this TestStep
 * object fails, in order to determine if the test step sequence containing
 * this step should be aborted.
 *
 * @return 'true' if a test step sequence should be aborted if this test step 
 * fails <BR>
 * 'false' if the test step sequence should continue
 */
public boolean abortIfFailed() {
    return abortIfFailed;
}

public int getRetryTimes() {
    return retryTimes;
}

public int getRetryDelay() {
    return retryDelay;
}

/** This method must be implemented by all subclasses.
 * <P>
 * It executes the instructions contained in this TestStep instance.  This may
 * consist of finding a GUI component instance and calling a method on that
 * instance, or of forwarding this TestStep instance to another object for
 * processing.
 *
 * @throws TestStep.FailureException if something that happened while this test 
 * step was being executed should be reported in the FailLog
 */
public abstract void execute() throws TestStep.FailureException;

/** Instances of TestStep.FailureException class are generated and thrown by the
 * TestStep.execute method if any kind of failure occurs during that method
 * (for example, if the execute method performs a result check and that check
 * fails, or if the component which is supposed to be tested cannot be found).
 */
protected class FailureException extends Exception {

    /** This character is printed between various pieces of information in the
     * TestStep.Failure.toString method.
     */
    // This is declared as a constant to prevent creation of many String objects
    // during calls to TestStep.Failure.toString method.
    protected static final String SEPARATOR = " "; 
    
    /** Creates a TestStep.FailureEception object using the message contained 
     * by e.
     *
     * @param e the Exception that caused a test step failure
     */    
    public FailureException(Exception e) {
        super(e.getMessage());
    }
    
    /** Creates a TestStep.FailureException instance.
     *
     * @param message a description of why a test step failed
     */    
    public FailureException(String message) {
        super(message);
    }
    
    /** Called by the Logger to produce a failure message for the FailLog.
     *
     * @return a String containing the id of the script that the failed test 
     * step came from, the number of the test step in the script, and a 
     * message describing why the test step failed, all on one line, 
     * separated by spaces.
     */    
    public String getMessage() {
        return TestStep.this.scriptID + SEPARATOR 
               + TestStep.this.stepNumber + SEPARATOR 
               + super.getMessage();
    }
}// FailureException

/** TestStep.creator is a factory for instances of TestStep subclasses.
 * <P>
 * It is initialized by the TestEngine, and then used by the TestScriptParser.
 * <P>
 * The implementation is based on the Factory Method patern.
 *
 */
protected static class Creator {

/** Maps xml tags to constructors for subclasses of TestStep which implement the
 * test steps specified by those tags.
 */    
    private static Map tagsAndConstructors = new HashMap();
    
    /** Reads a mapping of XML tags to names of classes that implement test 
     * steps declared by those tags in test scripts.
     * <P>
     * Called by the TestEngine during initialization.
     *
     * @see java.util.Properties#
     * @param testStepProperties relative or absolute path for a property file  
     * that maps tags to names of TestStep subclasses; the format of the file 
     * with this path must be the same as that expected by the Properties.load 
     * method, with tag names on the left, and testStep subclass names on the 
     * right
     * @throws IOException if i/o error is encountered while reading the 
     * testStepProperties file
     * @throws FileNotFoundException if the file with path described by 
     * testStepProperties is not found
     * @throws ClassNotFoundException if at least one of the classes listed in 
     * the testStepProperties file cannot be found
     * @throws NoSuchMethodException if at least on class listed in the 
     * testStepProperties file does not have a public constructor with this
     * signature: <BR>
     * (java.lang.String, java.lang.Integer, org.w3c.dom.Node)
     */    
    protected static void initialize() throws
    java.io.IOException, java.io.FileNotFoundException, ClassNotFoundException,
    NoSuchMethodException {
        // Read tags and TestStep classes that implement test steps
        // declared by those tags in test scripts into a Properties object p.
        File properties = new File(System.getProperty("base.dir"),
                "config/testStepCreatorConfig.properties");
        Properties p = FileUtilities.loadProperties(properties.getPath());
        
        // For each tag in p, create a constructor object for the class that 
        // implements test steps declared with that tag in the test script,
        // and map the tag to the constructor in the tagsAndConstructors map.
        // This involves the following intermediate steps:
        //  1. Create a Class object for a class name that corresponds to the
        //     tag in p.
        //  2. Get the constructor object with the signature 
        //     (String, Integer, Node).
        // This is implemented below:
        Enumeration tags = p.propertyNames();
        String tag;
        String testStepClassName;
        Class testStepClass;
        Constructor constructor;
        Class[] parameterTypes = {String.class, Integer.class, Element.class};
        while (tags.hasMoreElements()) {
            tag = (String) (tags.nextElement());
            testStepClassName = p.getProperty(tag);
            testStepClass = Class.forName(testStepClassName);
            constructor = testStepClass.getConstructor(parameterTypes);
            tagsAndConstructors.put(tag, constructor);            
        }
    }
    
    /** Creates a new instance of TestStep subclass.  The type of the test step 
     * that is created depends on the tag name of the node. This method should 
     * always be used to create instances of TestStep.
     * <P>
     * This method is called by the TestStepParser, only after the TestEngine 
     * has called the TestStep.Creator.initialize method.
     * <P>
     * Note: The implementation is based on parameterized Factory Method pattern.
     * The corresponding method in the TestStep subclass (i.e. the method that 
     * is called by this method to actually create a TestStep instance) is the 
     * public constructor with signature (java.lang.String, java.lang.Integer,
     * org.w3c.dom.Node).  This constructor must be overridden by subclasses of
     * TestStep.
     * @param script id of the script where the step to be created is declared
     * @param stepNum number of the step in the script
     * @param node a Node object representing the XML element which has the 
     * information needed to create the new test step
     * @throws InstantiationException if the class that is supposed to implement
     * the test step declared by node is an abstract class or an interface
     * @throws IllegalAccessException if the class that is supposed to implement
     * the test step declared by node is not a public class and is not in the 
     * same package as this TestStep.Creator class
     * @throws InvocationTargetException if an exception is thrown by the 
     * constructor of the TestStep subclass
     * @return a new instance of a TestStep subclass.
     * @throws ParsingException if the node is missing information needed to create 
     * a TestStep instance.
     */   
    protected static TestStep create(String script, int stepNum, Element node) 
    throws InstantiationException, IllegalAccessException, ParsingException, 
    java.lang.reflect.InvocationTargetException {
        String tag = node.getNodeName();
        Constructor c = (Constructor)(tagsAndConstructors.get(tag));
        if (c == null) {
            throw new ParsingException("Cannot handle " + tag);
        }
        Object[] initargs = {script, new Integer(stepNum), node};
        return (TestStep)(c.newInstance(initargs));
    }
}// Creator

}