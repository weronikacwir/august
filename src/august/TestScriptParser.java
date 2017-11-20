package august;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;

/** Parses test scripts for the August system.
 *
 * @author weronika
 */
class TestScriptParser {
    
/** The xml tag specifying the start and finish of the section containing test 
 * step nodes.
 */    
protected static final String TEST_STEPS = "testSteps"; 

/** The xml tag specifying that a utility script should be called at the point 
 * in a test run where this tag is placed.
 */    
protected static final String CALL_UTILITY_SCRIPT = "callScript";

/** The xml tag specifying that a utility script requiring data should be 
 * called at the point in a test run where this tag is placed.
 */    
protected static final String CALL_SCRIPT_WITH_DATA = "callScriptWithData";

/** The attribute specifying the name of a utility script to be called (used 
 * with the "callScript" and "callScriptWithData" tags).
 */ 
protected static final String UTILITY_SCRIPT_NAME = "script";

/** The attribute specifying the name of a data file (used with the "callScript"
 * and "callScriptWithData" tags).
 */ 
protected static final String DATA_FOR_SCRIPT = "data";

/** When data files and utility scripts are combined to produce test scripts,
 * the test script is written to this location.
 */
protected static final String TEMP_FILE = "TestScriptParser.temp";
    
/** Used internally for validating scripts and turning them into Document 
 * instances.
 */    
protected DOMParser parser;

/** Handles parsing errors.
 */
protected Errors parsingErrors;

/** Used internally for generating transformers that generate test scripts from 
 * utility stylesheets, and data in xml format.
 */  
protected TransformerFactory tFactory;

/** Handles errors generated during processing of transformation instructions.
 */
protected Errors tFactoryErrors;

/** Handles transformation errors.
 */
protected Errors transformationErrors;

/** Creates an instance of the TestScriptParser.
 * <P>
 * This constructor is called by the TestEngine during initialization.
 * @throws SAXException if there is an error setting the validation feature of
 * the parser
 */
protected TestScriptParser() throws SAXException {
    parser = new DOMParser();
    parsingErrors = new Errors();
    parser.setErrorHandler(parsingErrors);
    //parser.setFeature( "http://xml.org/sax/features/validation", true);
    
    tFactory = TransformerFactory.newInstance();
    tFactoryErrors = new Errors();
    tFactory.setErrorListener(tFactoryErrors);
    transformationErrors = new Errors();
}
    
/** Parses a test script file, and creates TestStep instances for each test step
 * declared in the test script.
 * <P>
 * Called by the TestEngine.runTest method.
 *
 * @param testScript a relative or absolute path for a test script file
 * @throws ParsingException if there were any errors parsing the test script or
 * instantiating any of the test steps contained in the test script. 
 * @return an Iterator containing objects of type TestStep
 */
Iterator parseTestSteps(String testScript) throws ParsingException {
    // The test steps that will be returned are collected here.
    List testSteps = new ArrayList();
       
    Document script = parseDocument(testScript);
    
    // Extract the node containing test steps.  There is only one of those.
    NodeList temp = script.getElementsByTagName(TEST_STEPS);
    Node testStepsSection = temp.item(0);
    // The NodeList temp and the wholeDocument are not needed anymore -   
    // mark them for garbage collection.
    temp = null;
    script = null;
    
    // Go through all of the nodes that are in the section containing
    // test step nodes.  Turn each of those nodes that are not comment 
    // nodes into (one or more) TestStep instances, and add those instances 
    // to the testSteps list.  While doing this, keep track of the step 
    // number in the stepNum variable.  (The comment nodes are ignored.)
    int stepNum = 0;
    TestStep step;
    for(Node node = testStepsSection.getFirstChild();
        node != null;
        node = node.getNextSibling()) {
        
        int type = node.getNodeType();
        if (type == Node.ELEMENT_NODE) {
            // Case One: A utility script is called at this point in the test;
            // turn the utility script into a series of test steps.
            if (node.getNodeName().equals(CALL_UTILITY_SCRIPT)) {
                String utilScriptName = 
                    ((Element)node).getAttribute(UTILITY_SCRIPT_NAME);
                Iterator utilScriptSteps = parseTestSteps(utilScriptName);
                while(utilScriptSteps.hasNext()) {
                    step = (TestStep)(utilScriptSteps.next());
                    testSteps.add(step);
                    utilScriptSteps.remove();
                }
            }
            else if (node.getNodeName().equals(CALL_SCRIPT_WITH_DATA)) {
                String utilScriptName =
                    ((Element)node).getAttribute(UTILITY_SCRIPT_NAME);
                String dataFile = 
                    ((Element)node).getAttribute(DATA_FOR_SCRIPT);
                generateTestScript(dataFile, utilScriptName);
                Iterator utilScriptSteps = parseTestSteps(TEMP_FILE);
                while(utilScriptSteps.hasNext()) {
                    step = (TestStep)(utilScriptSteps.next());
                    testSteps.add(step);
                    utilScriptSteps.remove();
                }
            }
            // Case Three: This is a single test step.  This case is the basis 
            // for the recursion.
            else {
                stepNum++;
                try {
                    step = TestStep.Creator.create(testScript, stepNum, 
                        (Element)node);
                }
                catch (InstantiationException e) {
                    throw new ParsingException(e.getMessage());
                }
                catch (IllegalAccessException e) {
                    throw new ParsingException(e.getMessage());                
                }
                catch (java.lang.reflect.InvocationTargetException e) {
					//java.lang.reflect.InvocationTargetException is a wrapper of the target exception
                    throw new ParsingException(e.getTargetException().getMessage());
                }
                testSteps.add(step);
            }
        }
    }
    return testSteps.iterator();
}

/** Used internally - turns an xml file into an instance of Document.
 *
 * @param fileName a relative or absulte path for an xml file
 * @throws ParsingException if any fatal errors, errors, or warnings were
 * encountered during parsing
 * @return a Document representation of an xml file
 */
private Document parseDocument(String fileName) throws ParsingException {
    Document d = null;
    parsingErrors.clearErrors();
    
    try {
        parser.parse(fileName);
        d = parser.getDocument();
        parser.reset();
    }
    catch (SAXException e) {
        throw new ParsingException(e.getMessage());
    }
    catch (java.io.IOException e) {
        throw new ParsingException(e.getMessage());
    }
    catch (Exception e) {
        throw new ParsingException(e.getMessage());
    }
    
    if (!parsingErrors.isEmpty()) {
        throw new ParsingException("Errors parsing " + fileName + ":\n" 
            + parsingErrors.getErrorMessages());
    }
    return d;
}

/** Generates a test script given a utility script (an xsl file) and a data file
 * (an xml file).  The test script is written to the location specified by
 * <CODE>TEMP_FILE<CODE>.
 *
 * @param dataFile a file path for the xml file containing data
 * @param utilScript a filepath for the xsl file containing the utility script
 * @throws ParsingException if there are any problems processing the file 
 * specified by utilityScript, or combining dataFile and utilityScript
 */
private void generateTestScript(String dataFile, String utilScript) throws 
ParsingException {
    tFactoryErrors.clearErrors();
    transformationErrors.clearErrors();
    
    Transformer t;
    try {
        t = tFactory.newTransformer(new StreamSource(utilScript));
    }
    catch (TransformerConfigurationException e) {
        throw new ParsingException(e.getMessage());
    }
    if(!tFactoryErrors.isEmpty()) {
        throw new ParsingException("Errors processing " + utilScript + ":\n"
            + tFactoryErrors.getErrorMessages());
    }
    try {
        t.transform(new StreamSource(dataFile), 
                    new StreamResult(new FileOutputStream(TEMP_FILE)));
    }
    catch(TransformerException e) {
        throw new ParsingException(e.getMessage());
    }
    catch(java.io.FileNotFoundException e) {
        throw new ParsingException(e.getMessage());
    }
    if(!transformationErrors.isEmpty()) {
        throw new ParsingException("Error transforming " + dataFile + ":\n"
            + transformationErrors.getErrorMessages());
    }
}

/** The ErrorHandler implementation for TestScriptParser.
 * <P>
 * For each encountered fatal error, error, or warning, composes a message, and
 * stores all these messages in one StringBuffer.
 */
static class Errors implements ErrorHandler, ErrorListener {
    
    /** Starts a "warning" type message.
    */    
    private static final String WARNING = "[Warning]";

    /** Starts an "error" type message.
    */    
    private static final String ERROR = "[Error]";

    /** Starts a "fatal error" type message.
     */    
    private static final String FATAL_ERROR = "[Fatal Error]";

    /** Always a part of the message when this is used as a ErrorHandler.
     */    
    private static final String AT_LINE_NUMBER = " at line number ";

    /** Always a part of the message when this is used as a ErrorHandler.
     */    
    private static final String COLON = ": ";

    /** This is always a part of the message.
     */    
    private static final String NEWLINE = "\n";

    /** The lenght of an empty <CODE>errorMessages<CODE> buffer.
     */    
    private static final int EMPTY = 0;

    /** All error messages are stored here (one per line).
     */    
    private StringBuffer errorMessages = new StringBuffer();

    /** Called by the TestScriptParser.parseDocument() method.
     *
     * @return if there are any messages stored, then a string containing
     * those messages, one per line, otherwise an empty String
     */    
    private String getErrorMessages() {
        return errorMessages.toString();
    }
    
    /** Checks whether there are any stored messages.
     *
     * @return 'true' if there are no messages stored, 'false' if
     * there is at least one message
     */    
    private boolean isEmpty() {
        return (errorMessages.length() == EMPTY);
    } 
    
    /** Resets this instance by clearing all the stored messages - should be
     * called after parsing each document.  This method is called by the
     * TestScriptParser.parseDocument() method.  
     */    
    private void clearErrors() {
        errorMessages.setLength(EMPTY);
    }
    
    ////////// ErrorHandler implementation //////////
    
    /** Receive a warning.
     *
     * @see ErrorHandler.warning
     * @param e warning information encapsulated in an exception
     */    
    public void warning(SAXParseException e) {
        add(e, WARNING);
    }

    /** Receive a notification of an error.
     *
     * @see ErrorHandler.error
     * @param e error information encapsulated in an exception
     */ 
    public void error(SAXParseException e) {
        add(e, ERROR);
    }

    /** Receive a notification of an fatal error.
     *
     * @see ErrorHandler.fatalError# 
     * @param e error information encapsulated in an exception
     * @throws SAXException actually, does not throw this exception 
     */ 
    public void fatalError(SAXParseException e) throws SAXException {
        add(e, FATAL_ERROR);
    }
    
    /** Composes and adds a message to the internal buffer.
     * <P>
     * Called by the warning(), error(), and fatalError() methods.
     *
     * @param e error information encapsulated in an exception
     * @param type the type of the error (warning, error, or fatal error)
     */    
    private void add(SAXParseException e, String type) {
        errorMessages.append(type);
        errorMessages.append(AT_LINE_NUMBER);
        errorMessages.append(e.getLineNumber());
        errorMessages.append(COLON); 
        errorMessages.append(e.getMessage());
        errorMessages.append(NEWLINE);
    }

    ////////// ErrorListener implementation //////////

    /** Receive a warning.
     *
     * @see ErrorListener.warning
     * @param e warning information encapsulated in an exception
     * @throws TransformerException actually, does not throw this exception 
     */      
    public void warning(TransformerException e) throws TransformerException {
        add(e, WARNING);
    }

    /** Receive a notification of an error.
     *
     * @see ErrorListener.warning
     * @param e warning information encapsulated in an exception
     * @throws TransformerException actually, does not throw this exception 
     */ 
    public void error(TransformerException e) throws TransformerException {
        add(e, ERROR);
    }

    /** Receive a notification of a fatal error.
     *
     * @see ErrorListener.warning
     * @param e warning information encapsulated in an exception
     * @throws TransformerException actually, does not throw this exception 
     */     
    public void fatalError(TransformerException e) throws TransformerException {
        add(e, FATAL_ERROR);        
    }

    /** Composes and adds a message to the internal buffer.
     * <P>
     * Called by the warning(), error(), and fatalError() methods.
     *
     * @param e error information encapsulated in an exception
     * @param type the type of the error (warning, error, or fatal error)
     */    
    private void add(TransformerException e, String type) {
        errorMessages.append(type);
        errorMessages.append(e.getMessageAndLocation());
        errorMessages.append(NEWLINE);
    }    

} // Errors

}
