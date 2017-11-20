package august;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.xml.sax.SAXException;

/** TestEngine instance is an entry point into the August system - it implements
 * the method main, and the runTest method, from which all tests are run.
 * <P>
 * The following should be specified as java options:
 * <BR>-base.dir - The base directory.  This directory must include a sub
 * directory called config which must contain a file called
 * testStepCreator.properties.  If this option is not specified, then August
 * will assume the current directory to be the base directory.
 * <BR>-config.dir - The config directory that is specific to the product
 * being tested.  This will usually be a sub directory of the config directory
 * described in the point about base.dir.  This directory must contain a file
 * called august.properties.  The path to the config file may be alternatively
 * specified with the -c or --configfile command line argument (the path must
 * include the file name).  If this command line argument is present, August
 * will use the config file specified with this argument.
 * <BR>-test.dir - The test directory.  This points to a directory tree
 * containing test scripts for a particular product. The test scripts must
 * have the file extension specified in the august.properties file.  The test
 * directory may alternatively be specified with the -t or --testdir command
 * line argument.  If this command line argument is present, August will run
 * tests from the directory specified with this argument.
 * log.dir -  The log directory.  August will place its outputs there.
 * The log directory may alternatively be specified with the -l or --logfile
 * command line argument.  If this command line argument is present, August
 * will write logs to the directory specified with this argument.
 *
 * @author weronika
 */
public class TestEngine {

/** Config file is loaded into this object and accessed from it.
 */ 
private Properties properties;

/** Logs test reports.
 */
private TestReportLogger logger;

/** Parses test scripts.
 */
private TestScriptParser parser;

/** The extensions of the test script files used by this engine.
 */
private String testScriptFileExtension;

/** Point of access to the tested application; opens and shuts down that
 * application.
 */
private TestableApplication application;
    
/** Creates an instance of the TestEngine class.
 * <P>
 * Called from the method main.
 * <P>
 * The TestEngine.loadProperties method, followed by the TestEngine methods
 * with names starting with "init" must be called to initialize instance
 * variables.
 */
public TestEngine(){
}

/** Initializes the August framework components or, if initialization fails,
 * the run of aorts the run of August.
 * <P>
 * Runs all the tests in the test direcory, and forwards the TestReport objects
 * to the TestReportLogger.
 * <P>
 * This method does not not force the application to exit when it completes.
 *
 * @param args The following may be specified as command line options:
 * <BR>-The path to the config file may be specified with the -c or
 * --configfile (the path must include the file name).
 * <BR>-The test directory may be specified with the -t or --testdir.
 * <BR>-The log directory may be specified with the -l or --logfile.
 */
public static void main(String[] args) {
    TestEngine engine = new TestEngine();

    String configfile = null;
    String testdir = null;
    String logdir = null;
    if (args.length > 0) {
    // Set up commandline options.
    Option opt[] = new Option[4];
    opt[0] = new Option(false, Option.REQUIRES_ARGUMENT, 'c', "configfile");
    opt[1] = new Option(false, Option.REQUIRES_ARGUMENT, 't', "testdir");
    opt[2] = new Option(false, Option.REQUIRES_ARGUMENT, 'l', "logdir");
    opt[3] = new Option(false, Option.NO_ARGUMENT,       'h', "help");

    OptParser optParser = new OptParser(opt);
    try {
        optParser.setArgs(args);
    }
    catch (IllegalArgumentException e) {
        // This means that there are no args.  That's fine, we don't really need any.
    }

    // Figure out what commandline options we have been passed.
    int option;
    try {
        while ((option = optParser.getOption()) != OptParser.FINISHED_PARSING) {
            switch (option) {
                case 'c': // handle the configfile option
                    configfile = optParser.getOptionArg();
                    break;

                case 't': // handle the testdir option
                    testdir = optParser.getOptionArg();
                    break;

                case 'l': // handle the logdir option
                    testdir = optParser.getOptionArg();
                    break;

                case 'h': // handle the help option
                    printHelp();
                    System.exit(1);
                    break;

                default: // catch anything else
                    System.out.println("Unrecognized option: " + option);
                    printHelp();
                    System.exit(1);
                    break;
            } // end switch
        } // end while
    }
    catch (IllegalArgumentException e) {
        System.out.println();
        System.out.println(e.getMessage());
        System.out.println();
        printHelp();
        System.exit(1);
    }
    }
    // Initialize the August framework.
    try {
        if (configfile == null) {
            File f = new File(System.getProperty("config.dir"),
                    "august.properties");
            configfile = f.getPath();
        }
        engine.loadProperties(configfile);
        engine.initLogger(logdir);
        engine.initTestScriptParser();
        engine.initTestStepCreator();
        engine.initRobot();
        engine.initApplication();
        engine.setTestScriptFileExtension();
    }
    catch (Exception e) {
        // Abort if something failed during initialization.
        System.out.println("August aborting; " +
            "encountered error during initialization" );
        e.printStackTrace();
        System.exit(1);
    }

    // Run tests
    if (testdir == null) {
        testdir = System.getProperty("test.dir");
    }
    String testScript;
    TestReport report;
    Iterator testScripts =
            FileUtilities.getFilesWithExtension(testdir,
                    engine.testScriptFileExtension).iterator();
    while(testScripts.hasNext()) {
        testScript = (String)(testScripts.next());
        report = engine.runTest(testScript);
        engine.logger.logTestReport(report);
    }
    engine.logger.setMoreTestReportsComing(false);
}

/** Prints help on using august.
 */
private static void printHelp() {
    System.out.println("java com.sitraka.aougust.TestEngine [options]");
    System.out.println("Options:");
    System.out.println("-h or --help               print this message");
    System.out.println("-c or --config <file>      use given config file");
    System.out.println("-l or --logdir <dir>       write logs to given directory");
    System.out.println("-t or --testdir <dir>      run tests from given directory");
}

/** Initializes the properties variable and loads the config file into it.
 *
 * @param configFileName a relative or absolute path for the config file for the
 * August system
 * @throws IOException if i/o error was encountered while reading from the 
 * config file
 * @throws FileNotFoundException if the config file could not be found
 */
private void loadProperties(String configFileName) throws java.io.IOException,
java.io.FileNotFoundException {
    properties = FileUtilities.loadProperties(configFileName);
    System.out.println("--in loadproperties() properties=" + properties);
}

/** Initializes the application variable.
 * <P>
 * Creates an instance of a class implamenting the TestableApplication 
 * interface, and associates application with that instance.  The class to be 
 * instantiated must be specified in the config file.
 *
 * @see TestEngine.AugustProperties.TESTABLE_APPLIACATION#
 * @throws ClassNotFoundException if the class specified in the config file as 
 * implementing the TestableApplication interface cannot be found
 * @throws InstantiationException if the class specified in the config file as 
 * implementing the TestableApplication interface is an interface or an abstract
 * class
 * @throws IllegalAccessException if the class specified in the config file as 
 * implementing the TestableApplication interface does not have a zero-argument
 * constructor accessible by this method
 */
private void initApplication() throws ClassNotFoundException,
InstantiationException, IllegalAccessException {
    String testableApplication = 
        properties.getProperty(AugustProperties.TESTABLE_APPLICATION);
    Class testableApplicationClass = Class.forName(testableApplication);
    application = (TestableApplication)(testableApplicationClass.newInstance());
}

/** Initializes the logger variable.
 * <P>
 * Creates an instance of the TestReportLogger class, and associates the logger
 * variable with this instance.
 */
private void initLogger(String logDir) {
    if (logDir == null) {
        logDir = System.getProperty("log.dir");
    }
    logger = new TestReportLogger(logDir);
}

/** Initializes the TestRobot class using the test delay specified in the config
 * file.
 *
 * @throws NumberFormatException if the test delay property in the config file 
 * is not an integer
 * @throws AWTException if the platform configuration does not allow low-level
 * input control
 * @see TestEngine.AugustProperties.TEST_DELAY
 */
private void initRobot() throws NumberFormatException, java.awt.AWTException {
    String delay = properties.getProperty(AugustProperties.ROBOT_DELAY);
    int autoDelay = Integer.parseInt(delay);
    TestRobot.initialize(autoDelay);
}

/** Initializes the TestStep.Creator class, sending it a file path for a
 * properties file.
 *
 * @see TestEngine.AugustProperties.TEST_STEP_PROPERTIES_FILE#
 * @see TestStep.Creator.initialize
 * @throws Exception if an exception occurs while TestStep.Creator is being 
 * initialized
 */
private void initTestStepCreator() throws Exception {
    TestStep.Creator.initialize();
}

/** Initlizes the parser variable.
 * <P>
 * Creates an instance of TestScriptParser class and associates the parser
 * variable with that instance.
 * @throws SAXException if the parser was not properly initialized
 */
private void initTestScriptParser() throws org.xml.sax.SAXException {
    parser = new TestScriptParser();
}

/** Sets <CODE>testScriptFileExtension<CODE> to the value of the property with 
 * the same name in the config file.
 */
private void setTestScriptFileExtension() {
    testScriptFileExtension = 
        properties.getProperty(AugustProperties.TEST_SCRIPT_FILE_EXTENSION);    
}

/** Parses and executes tests.
 * <P>
 * If parsing is successful, then the tested application is open, and the test
 * steps are excecuted one by one.  If a test step results in a failure, then 
 * that failure is addes to the test report whihc is returned by this method, 
 * and the test run may be aborted.  At the end of the test run the application 
 * is shut down.
 * <P>
 * If parsing fails, then an appropriate failure is added to the test report, 
 * and the method returns without ever opening the tested application, or 
 * executing any test steps.
 *
 * @param testScriptFileName a relative or absolute path for a test script file
 * @return a TestReport object with details of the run of the test
 */
private TestReport runTest(String testScriptFileName) {
    TestReport report = new TestReport(testScriptFileName);
    Iterator test = null;

    // First, try parsing the test
    try {
        test = parser.parseTestSteps(testScriptFileName);
    }
    catch (ParsingException e) {
		e.printStackTrace();
        report.addFailure(e);
    }
    
    // If the test object was successfully constructed, then we can run 
    // the test.
    if (test != null) {
        // Start the application to be tested.
        application.startApplication();

        // Wait for the appliction to start 
		String s_delay = properties.getProperty(AugustProperties.START_DELAY);
    	int startDelay = Integer.parseInt(s_delay);
        
        try {
            Thread.currentThread().sleep(startDelay);
        }
        catch (InterruptedException ie){
        }
        
        // Execute the instructions until the end of the test step sequence, or 
        // until an abort causing failure occurs.
        TestStep step = null;
        boolean abort = false;
        while(test.hasNext() && !abort) {
           step = (TestStep)test.next();
           try {
               step.execute();
           } // end try
           // If the step fails, then...
           catch (TestStep.FailureException e) {
               // If this step is not be retried then record the failure
               // message.
               if (step.getRetryTimes() <= 0) {
                   report.addFailure(e);
                   // Also, if this is an "abort if failed" kind of step,
                   // then abort the test.
                   if (step.abortIfFailed()) {
                       abort = true;
                   } // end if
               } // end if
               // Otherwise, retry the step.
               else {
                   int delay = step.getRetryDelay();
                   for (int i=1; i <= step.getRetryTimes(); i++) {
                      try {
                         Thread.currentThread().sleep(delay);
                         step.execute();
                         // Stop retrying if step executed without failing.
                         break;
                      }
                      catch(InterruptedException ie) {
                      }
                      // If the step failed again then...
                      catch (TestStep.FailureException fe) {
                          // If this was the last retry, then
                          // record the failure.
                          if (i == step.getRetryTimes()) {
                              report.addFailure(fe);
                              // Also, if this is an "abort if failed" kind of step,
                              // then abort the test.
                              if (step.abortIfFailed()) {
                                  abort = true;
                              } // end if
                          } // end if
                      } // end catch
                   } // end for
               } // end else
           } // end catch
           test.remove();
        } // end while

        // Close the tested application.
        application.exitApplication();

        // Wait for the application to shut down 
		String sd_delay = properties.getProperty(AugustProperties.SHUTDOWN_DELAY);
    	int shutDownDelay = Integer.parseInt(sd_delay);
        try {
            Thread.currentThread().sleep(shutDownDelay);
        }
        catch (InterruptedException ie){
        }
    }
    return report;        
}

/** This is a collection of property names that should appear in the config file
 * for the August system.
 */
private static interface AugustProperties {
    
    /** The name of the property which maps to a number of milliseconds the
     * TestRobot should sleep inbetween generating events.
     */
    String ROBOT_DELAY = "robotDelay";

    /** The name of the property which maps to the name of a class implementing
     * the TestableApplication iterface, and which will act as an iterface
     * between the August framework and the tested application.
     */
    String TESTABLE_APPLICATION = "testableApplicationClass";

    /** The name of the property which maps to an extension that specifies that
     * a file is a test script file.
     */
    String TEST_SCRIPT_FILE_EXTENSION = "testScriptFileExtension";
    String START_DELAY = "startDelay";
    String SHUTDOWN_DELAY="shutDownDelay";
}
}
