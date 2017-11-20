package august;

/** Classes implementing the TestableApplication interface act as an iterface
 * between the August framework and the application which it tests.
 * <P>
 * All classes that implement this interface must implement a public constructor
 * with an empty parameter list.
 *
 * @author weronika
 */
public interface TestableApplication {
    
/** Starts the tested application.
 */    
void startApplication();

/** Exits (closes) the tested application without shutting down the JVM.
 */
void exitApplication();

}

