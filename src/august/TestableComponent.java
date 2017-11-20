package august;

import java.util.Map;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;

/** Implemented by objects which are called by the August framework during 
 * testing.
 * <P>
 * Defines exceptions thrown by the interface methods.
 * <P>
 * Also defines the TestableComponent.Wrapper class: a utility for wrapping 
 * instance of TestableComponent around instances of classes that do not 
 * implement the TestableComponent interface.
 *
 * @author weronika
 */
public interface TestableComponent {

/** Performs an action specified by the kayword parameter.
 * <P>
 * Called by TestStep instances.
 *
 * @param keyword the action to perform
 * @param args parameters needed to perform an action (may be null)
 * @throws BadKeywordException if the keyword does not specify a recognized 
 * action
 * @throws BadArgumentsException if args does not contain the parameters needed 
 * to perform the action specified by keyword, or if the parameters have bad 
 * format or bad values
 * @throws LogicalException if the action specified by keyword cannot be 
 * performed at this moment because of the system's state
 */    
void performAction(String keyword, Map args) throws BadKeywordException, 
BadArgumentsException, LogicalException;

/** Checks a property.
 * <P>
 * Called by TestStep instances.
 * @param prop the name of the property to be checked
 * @param expectedVal the expected value of the property specified by prop
 * @throws BadPropertyException if prop is not a recognized property
 * @return null if the expectedVal matches the actual value of the property 
 * prop, otherwise a String representation of the actual value
 */
String checkProperty(String prop, String expectedVal) throws 
BadPropertyException;

/** Thrown if an unrecognized keyword is encountered.
 */
class BadKeywordException extends Exception {

    /** This string is always a part of the message.
     */    
    private static final String IS_NOT_RECOGNIZED_FOR = 
        " is not a recognized keyword for components of type ";
    
    /** Creates an instance of the TestableComponent.BadKeywordException with
     * the following message:
     * <P>
     * "<keyword> is not a recognized keyword for components of type 
     * <className>"
     *
     * @param keyword specifies an action
     * @param className the name of a class (one that either implements the
     * TestableComponent interface, or that can be wrapped in an instance of a
     * class that does)
     */    
    public BadKeywordException(String keyword, String className) {
        super(keyword + IS_NOT_RECOGNIZED_FOR + className);
    }
} // BadKeywordException

/** Thrown when an argument map is missing a necessary argument, or if an 
 * argument has a bad format or value.
 */
class BadArgumentsException extends Exception {

    /** Standard exception constructor.
     *
     * @param message explaining why this exception was thrown
     */    
    public BadArgumentsException(String message) {
        super(message);
    }
} // BadArgumentsException

/** Thrown when an unrecgnized property is encountered.
 */
class BadPropertyException extends Exception {

    /** This string is always a part of the message.
     */    
    private static final String IS_NOT_RECOGNIZED_FOR = 
        " is not a recognized property for components of type ";

    /** Creates an instance of the TestableComponent.BadPropertyException with
     * the following message:
     * <P>
     * "<prop> is not a recognized property for components of type 
     * <className>"
     *
     * @param prop specifies a property
     * @param className the name of a class (one that either implements the
     * TestableComponent interface, or that can be wrapped in an instance of a
     * class that does)
     */    
    public BadPropertyException(String prop, String className) { 
        super(prop + IS_NOT_RECOGNIZED_FOR + className);
    }
} // BadPropertyException

/** Thrown when an action cannot be performed due to the current state of the 
 * system.
 */
class LogicalException extends Exception {

    /** Standard exception constructor.
     *
     * @param message explaining why this exception was thrown
     */  
    public LogicalException(String message) {
        super(message);
    }
} // LogicalException

/** A utility used by TestStep instances for wrapping objects (usually of type
 * Component) in objects that implement the TestableComponent interface.
 * <P>
 * Usage:
 * <BR>
 * <CODE>
 * TestableComponent tc = TestableComponent.Wrapper.wrap(someComponent);
 * <CODE>
 * <P>
 */
static class Wrapper {
    
    /** The name of the package where the classes implementing TestableComponent
     * interface should be placed.
     */    
    private static final String WRAPPER_PCKG_NAME = 
        "august.wrappers.";
    
    /** The names of the classes implementing the TestableComponent interface 
     * and places in the pacckage specigied by <CODE>WRAPPER_PRCKG_NAME<CODE> 
     * should always end with this string.
     */    
    private static final String WRAPPER_SUFFIX = "Wrapper";
    
    /** Given an objects, returns an istance of a corresponding 
     * TestableComponent class wrapped around that object.
     * <P>
     * The appropriate TestableComponent class is found in the following way:
     * <BR>- if o is an instance of TestableComponent, then o itself is returned
     * unchanged.
     * <BR>- if there is a class in the package specified by
     * <CODE>WRAPPER_PCKG_NAME<CODE> whose name is the same as the name of the
     * (runtime) class of o (without the package name) followed by the suffix
     * specified by <CODE>WRAPPER_SUFFIX<CODE> then an instance of that class is
     * returned
     * <BR>- if there is a class in the package specified by
     * <CODE>WRAPPER_PCKG_NAME<CODE> whose name is the same as the name of some
     * superclassclass of o (without the package name) followed by the suffix
     * specified by <CODE>WRAPPER_SUFFIX<CODE> then an instance of that class is
     * returned (if there are more than superclasses of o that have a 
     * corresponding wrapper class, than the wrapper class that corresponds to 
     * that superclass of o which is closest in the inheritance hierarchy to the 
     * class of o is used.)
     *
     * @param o the object to be wrapped
     * @throws InvocationTargetException if an exception is thrown by 
     * constructor of the TestableComponent class corresponding to o
     * @throws IllegalAccessException if the constructor of the 
     * TestableComponent class corresponding to o is not public
     * @throws InstantiationException if the TestableComponent class 
     * corresponding to o is an abstact class or an iterface. 
     * @throws NoSuchMethodException if the TestableComponent class 
     * corresponding to o does not have a constructor that takes an istance of 
     * the (runtime) class of o as a parameter
     * @return an instance of TestableComponent type wrapped around o, or null 
     * if an appropriate TestableComponent class for o cannot be found.
     */    
    public static TestableComponent wrap(Object o) throws 
    java.lang.reflect.InvocationTargetException, IllegalAccessException, 
    InstantiationException, NoSuchMethodException {
        TestableComponent tc = null;
        if (o instanceof TestableComponent) {
            tc = (TestableComponent)o;
        }
        else {
            Class wrapper;
            for(Class c = o.getClass(); c != null; c = c.getSuperclass()) {
                try {
                    wrapper = Class.forName(WRAPPER_PCKG_NAME
                                            + extractClassName(c.getName())
                                            + WRAPPER_SUFFIX);
                    // The contstructor for this class should take an instance
                    // of the class currently stored in c as an argument.
                    Class[] paramType = {c};
                    Object[] initArg = {o};
                    Constructor con = wrapper.getDeclaredConstructor(paramType);
                    tc = (TestableComponent)(con.newInstance(initArg));
                    // If we got to this point, then we have found what we were 
                    // looking for.
                    break;
                }
                catch (ClassNotFoundException e) {
                    // There is no wrapper class for this class, but maybe 
                    // there is one for its superclass.
                    continue;
                }
            }
        }
        return tc;
    }
    
    /** Given a fully qualified class name (ie. name including package) returns 
     * just the name without the package name.
     * <P>
     * For example, if "java.lang.String" is passed in, then "String" is 
     * returned.
     *
     * @param packageAndClass a fully qualified class name
     * @return the name of a class without the package name
     */    
    private static String extractClassName(String packageAndClass) {
        StringTokenizer st = new StringTokenizer(packageAndClass, ".");
        String className = null;
        while(st.hasMoreTokens()) {
            className = st.nextToken();
        }
        return className;
    }
} // Wrapper

}

