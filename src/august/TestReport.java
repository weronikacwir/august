package august;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/** Instances of the TestReport class contain information about a single test
 * run.  TestReport instances are created and filled with information in the
 * TestEngine.runTest() method, and are queued in an Instance of the 
 * TestReportLogger class, which then prints the contained information to the 
 * August system logs.
 *
 * @author weronika
 */
class TestReport {

/** The ID of the test for which this TestReport instance was generated.
 */    
protected String testID;

/** Indicates whether the test for which this report was generated passed or
 * failed. ('true' unless the test failed.)
 */
protected boolean testPassed;

/** A list of failures that happened during the run of the test specified by
 * testID variable.  This is a List rather than a single object because it is
 * possible that a failure of a test step does not abort the test run; it is
 * possible that more than one failure occurs.  The failures List holds 
 * objects of TestStep.Failure and TestStepParse.Exception types.
 */
protected List failures;

/** This character is printed between various pieces of information in the
 * TestReport.toString method.
 */
// This is declared as a constant to prevent creation of many String objects
// during calls to TestReport.toString method.
protected static final String SEPARATOR = " "; 

/** This string is printed to indicate that a test for which this TestReport
 * object was generated has passed.
 */
protected static String PASS = "pass";

/** This string is printed to indicate that a test for which this TestReport
 * object was generated has failed.
 */
protected static String FAIL = "FAIL";

/** This constructor is called by the TestEngine.runTest() mehtod to construct
 * a TestReport object for each test that is run.  The TestReport initially has
 * an empty list of failures.
 *
 * @param testID the ID of the test for which this TestReport object is
 * constructed
 */
protected TestReport(String testID) {
    this.testID = testID;
    testPassed = true;
    failures = null;
}

/** Called by the Logger.
 *
 * @return the ID of the test for which TestReport was generated
 */
protected String getTestID() {
    return testID;
}

/** Called by the Logger.
 *
 * @return 'true' if none of the test steps in the test for
 * which this TestReport instance was generated have
 * failed <BR>
 * 'false' if the test for which this TestReport was
 * generated had at least one step which failed
 *
 */
protected boolean isTestPassed() {
    return testPassed;
}

/** Called by the TestEngine.runTest() method if parsing of a test script fails, 
 * or if a single test step execution fails. Adds the Exception object passed as
 * argument to the list of failures.  After this method is called, 
 * the isTestPassed() method of this TestReport instance will always return 
 * 'false.'
 *
 * @param e a ParsingException or a TestStep.FailureException object 
 */
protected void addFailure(Exception e) {
    if (failures == null) {
        failures = new LinkedList();
        testPassed = false;
    }
    failures.add(e);
}

/** Called by TestReportLogger, only if the isTestPassed() method returns false.
 *
 * @return If this TestReport intance's isTestPassed() method returns 'false,' 
 * then this method returns an Iterator containing TestStep.Failure instances 
 * or ParsingException instances in the the order in which they were added.  
 * Otherwise, this method throws a NullPointerException.
 */
protected Iterator getFailures() {
    return failures.iterator();
}

/** Called from the TestReportLogger while printing a test report to the log.
 *
 * @return a string containing an id of a test, followed by a single space, 
 * followed by "pass" if the test has passed, or "FAIL" if the test has failed.
 */
public String toString() {
    return testID + SEPARATOR + ((testPassed)? PASS : FAIL);
}

}
