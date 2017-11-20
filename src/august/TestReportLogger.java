package august;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;

/** An instance of TestReportLogger class is used by the August system to log
 * reports for each test ran.  Each test is logged in the "log" and each failed
 * test is also logged, with additional information, in the "fail log."
 * <P>
 * The TestEngine object places TestReport objects on TestReportLogger's queue,
 * and returns immediatelly.  On a separate thread, the TestReport objects are
 * printed to the logs one by one.  
 * <P>
 * If an IO error is encountered during logging, then the run of August is
 * aborted abnormally.
 * <P>
 * When the TestReportLogger finishes logging all the results, it exits August.
 * If at least one of the tests failed, then August exits with a non-zero value.
 * If all of the tests passed, then the exit value is zero.
 *
 * @author weronika
 */
class TestReportLogger extends Thread {
    
/** TestReport objects to be printed to the logs are stored here.
 */    
private LinkedList reportQueue;

/** Path name of the log file.
 */
private String log;

/** Path name of the fail log file.
 */
private String failLog;

/** Flag indicating whether more TestReport objects are expected to be enqueued
 * on reportQueue.
 */
private boolean moreTestReportsComing;

/** This is set to non-zero value if there is at least one failed test.
 */
private int EXIT_STATUS = 0;

/** Constructs a TestReportLogger object with an the log and fail log path names
 * and an initially empty test report queue.
 * <P>
 * Starts a non-daemon thread on which TestReport objects are dequeued and
 * printed to the logs.
 * <P>
 * This constructor is called by the TestEngine object during intialization.
 *
 * @param logDir path name of the log dir
 */
protected TestReportLogger(String logDir) {
    reportQueue = new LinkedList();
    this.log = (new File(logDir, "log")).getPath();
    this.failLog = (new File(logDir, "failLog")).getPath();
    moreTestReportsComing = true;
    this.setName("TestReportLogger");
    this.setDaemon(false);
    this.start();
}

/** Prints test reports to log and fail log.  When it completes, it shuts down
 * the whole application.
 *  <P> 
 * This method will complete normally if the following conditions are met:
 * <BR> 1. No more TestReport objects are expected to be enqueued (i.e. the 
 * setMoreTestReportsComing method of this object has been called with 'false' 
 * as a parameter).
 * <BR> 2. All of the TestReport objects that had been enqueued by calling this
 * objects's logTestReport method have been printed to the logs.
 * <P>
 * If this method encounters i/o problems while logging reports it will force 
 * the current JVM to shut down - the rationale for this is that if the test
 * reports cannot be logged, then it does not make sense to run the tests.  In 
 * this case the following message will be printed to stdout: "August aborting; 
 * encountered IOException while logging." 
 */
public void run() {
    TestReport report;
    
    // Execute the loop body only if more test reports are expected to be 
    // enqueued, or if there are some test reports on the queue already.
    // If there are no more test reports coming and the queue is empty, then
    // this thread's work is done.
    while(moreTestReportsComing || !reportQueue.isEmpty()) {
        try {
            report = dequeueReport();
            printToLog(report);
            if(!report.isTestPassed()) {
                EXIT_STATUS = 1;
                printToFailLog(report);
            }
        }
        catch (InterruptedException e) {
            // Interrupted exception is thrown if this thread is waiting to 
            // dequeue a TestReport object, and the TestEngine object sends a 
            // message that there will be no more tests coming.  This means that
            // that there are no more test reports to print, so break out of 
            // this loop.
            break;
        }
        catch (IOException e) {
            // If we cannot write to logs then it does not make sense
            // to keep running August.  Therefore, abort this run.
            System.out.println("August aborting; " + 
                "encountered IOException while logging results.");
            System.exit(1);
        }
    }
    // This run method always completes after the TestEngine.main method, and 
    // when it completes the August run is over.
    // For some reason before this line was added August would not exit even 
    // though logger thread and the main thread were stopped...
    System.exit(EXIT_STATUS);
}

/** Queue a TestReport object to be printed to logs, and return immediatelly.
 * <P>
 * This method is called from the main method of the TestEngine class.
 *
 * @param report a TestReport object to be logged
 */
protected synchronized void logTestReport (TestReport report) {
    reportQueue.addLast(report);

    // A thread might be waiting to dequeue a TestReport from the reportQueue - 
    // if it is, it will be woken up.  (No other threads are expected to be 
    // trying to obtain a lock on this object.)
    notify();
}

/** Returns the first TestReport waiting to be printed to the logs, and removes
 * it from the queue.  If the queue is empty, then this method will block until
 * the logTestReport of this object is called, at which point it will return
 * normally, or until the setMoreTestReportsComing method is called with 'false'
 * as the parameter, at which point it will throw an InterruptedException.
 * <P>
 * This method is called internally, from this object's run method.
 *
 * @return the first object in the queue of TestReport objects waiting to be 
 * printed to the logs
 * @throws InterruptedException if a thread is interrupted while waiting for a 
 * TestReport object ot be enqueued
 */
private synchronized TestReport dequeueReport () throws InterruptedException {
    if (reportQueue.isEmpty()) {
        wait();
    }
    return (TestReport)(reportQueue.removeFirst());
}

/** Print a TestReport object to the log.  Called internally from the run 
 * method.
 *
 * @param report he TestReport object to be printed to the log
 * @throws IOException if there is an i/o error while prinitng to the log
 */
private void printToLog(TestReport report) throws IOException {
    FileUtilities.appendLineToFile(log, report.toString());
}

/** Prints the report to the fail log.  Called internally from the run method.
 *
 * @param report a TestReport object to be printed to the fail log; the
 * TestReport object's isTestPassed method must return 'false.'
 * @throws IOException if there is an i/o error while prinitng to the fail log
 */
private void printToFailLog(TestReport report) throws IOException {
    FileUtilities.appendLineToFile(failLog, report.toString());
    // The failures object should not be null, since report is for a test that 
    // has failed.
    Iterator failures = report.getFailures();
    Exception failure;
    while(failures.hasNext()) {
        failure = (Exception) (failures.next());
        FileUtilities.appendLineToFile(failLog, failure.getMessage());
    }
}

/** Sets the moreTestReportsComing flag.
 * <P>
 * This method is called (with 'false' as argument) by TestEngine from the main
 * method after it has run all the tests and placed all of the test reports on
 * the queue.
 *
 * @param moreTestReports 
 * 'false' if no more TestReport objects will be placed on the queue for logging
 * <BR>'true' otherwise
 */
protected void setMoreTestReportsComing(boolean moreTestReports) {
    moreTestReportsComing = moreTestReports;

    // If there are no more tests coming, then try to interrupt the logger 
    // thread.  The Logger thread will only be interrupted if it is currently
    // waiting to dequeue a TestReport object from an empty reportQueue.  It is 
    // necessary to interrupt it in this case, because otherwise it will 
    // continue to wait forever to dequeue, and since this is a non-deamon
    // thread, August will not be able to exit normally.  If the logger thread
    // is doing something other than waiting to dequeue, then it will not be 
    // interrupted - that's fine, because the next times it goes through the 
    // loop in the run method it will see that there are no more new test  
    // reports coming, and it will only execute the loop body until it has  
    // dequeued the last TestReport object.
    if(!moreTestReports) {
        this.interrupt();
    }
}
}