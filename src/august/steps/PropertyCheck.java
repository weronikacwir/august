/*
 * PropertyCheck.java
 *
 * Created on May 9, 2002, 11:57 AM
 */

package august.steps;

import august.ObjectFinder;
import august.TestStep;
import august.ParsingException;
import org.w3c.dom.Element;
import java.awt.Component;
import java.awt.Frame;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author  adamm
 */
public class PropertyCheck extends TestStep {

public static final String COMPONENT_NAME_ATTRIBUTE = "componentName";
public static final String VARIABLE_NAME_ATTRIBUTE = "variableName";
public static final String VARIABLE_TYPE_ATTRIBUTE = "variableType";
public static final String VARIABLE_TYPE_BOOLEAN = "boolean";
public static final String VARIABLE_TYPE_CHAR = "char";
public static final String VARIABLE_TYPE_BYTE = "byte";
public static final String VARIABLE_TYPE_SHORT = "short";
public static final String VARIABLE_TYPE_INT = "int";
public static final String VARIABLE_TYPE_LONG = "long";
public static final String VARIABLE_TYPE_FLOAT = "float";
public static final String VARIABLE_TYPE_DOUBLE = "double";
public static final String VARIABLE_TYPE_OBJECT = "object";

/**
 * When <code>variableType="object"</code>, this attribute contains the name
 * of a variable or method which will be (called and the result) compared for
 * 1) both null, and 2) for equality based on the return value of a call to the
 * <code>equals()</code> of the variable identified by <code>variableName</code>.
 * For the primitive types, the value of this attribute should be the actual
 * value expected.
 */
public static final String EXPECTED_VALUE_ATTRIBUTE = "expectedValue";

public static final String EXPECTED_VALUE_TYPE_ATTRIBUTE = "expectedValueType";
public static final String EXPECTED_VALUE_TYPE_VARIABLE = "variable";
/**
 * @TODO For now, only zero-parameter methods may be called. It would be nice to
 * be able to also call parameterized methods.
 */
public static final String EXPECTED_VALUE_TYPE_METHOD = "method";

private String componentName;
private String variableName;
private String variableType;
private String expectedValue;
private String expectedValueType;

/** Creates a new instance of PropertyCheck */
public PropertyCheck(String scriptID, Integer stepNumber, Element node)
throws ParsingException {
    super(scriptID, stepNumber, node);
    componentName = node.getAttribute(COMPONENT_NAME_ATTRIBUTE);
    // get the name of the variable to test
    variableName = node.getAttribute(VARIABLE_NAME_ATTRIBUTE);
    // get the type of the variable to test
    variableType = node.getAttribute(VARIABLE_TYPE_ATTRIBUTE);
    // get the value of the expected result,
    //or the name of the variable or method containing the expected result
    expectedValue = node.getAttribute(EXPECTED_VALUE_ATTRIBUTE);
    // get the type of the expected result
    expectedValueType = node.getAttribute(EXPECTED_VALUE_TYPE_ATTRIBUTE);
}

/** This method must be implemented by all subclasses.
 * <P>
 * It executes the instructions contained in this TestStep instance.  This may
 * consist of finding a GUI component instance and calling a method on that
 * instance, or of forwarding this TestStep instance to another object for
 * processing.
 *
 * @throws TestStep.FailureException if something that happend while this test
 * step was being executed should be reported in the FailLog
 */
public void execute() throws TestStep.FailureException {
    Component namedComponent = ObjectFinder.findGUIComponent(componentName);
    Object inQuestion = getValue(namedComponent, variableName);
    Object expected = getExpectedValue(namedComponent);
    if (!inQuestion.equals(expected)){
        throw new TestStep.FailureException("Component " + namedComponent +
          "failed check of property " + variableName + "; expected \"" + expected +
          "\", but actual value was \"" + inQuestion + "\"");
    }
}


private Object getExpectedValue(Object namedComponent) throws TestStep.FailureException{
    Object result = null;
    if (expectedValueType.equals(EXPECTED_VALUE_TYPE_VARIABLE)){
        result = getValue(namedComponent, expectedValue); // find the value of the variable named in expectedValue
    }
    else if(expectedValueType.equals(EXPECTED_VALUE_TYPE_METHOD)){
        try{
            result = invokeMethod(namedComponent, expectedValue, null);
        }catch (Exception e){
            throw new TestStep.FailureException("Error while attempting to invoke method \"" + expectedValue + "\": " + e.getMessage());
        }
    }
    else{
        result = decodeExpectedValue(); // use expectedValue directly
    }
    return result;
}

private Object decodeExpectedValue(){
    Object expected = null;
    if (variableType.equals(VARIABLE_TYPE_BOOLEAN)){
        expected = new Boolean(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_CHAR)){
        expected = new Character(expectedValue.charAt(0));
    }
    else if (variableType.equals(VARIABLE_TYPE_BYTE)){
        expected = Byte.valueOf(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_SHORT)){
        expected = Short.valueOf(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_INT)){
        expected = Integer.valueOf(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_LONG)){
        expected = Long.valueOf(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_FLOAT)){
        expected = Float.valueOf(expectedValue);
    }
    else if (variableType.equals(VARIABLE_TYPE_DOUBLE)){
        expected = Double.valueOf(expectedValue);
    }
    return expected;
}

/**
 * Gets the value of the named field and returns it as an object.
 *
 * @param instance the object instance
 * @param fieldName the name of the field
 * @return an object representing the value of the field
 */
public Object getValue( Object instance, String fieldName )
    throws TestStep.FailureException {
    Object value = null;
    try{
        Field field = getField(instance.getClass(), fieldName);
        field.setAccessible(true);
        value= field.get(instance);
    }catch(NoSuchFieldException e){
        throw new TestStep.FailureException("Could not find field \"" + fieldName +"\"");
    }catch(IllegalAccessException e){
        throw new TestStep.FailureException("Access to field \"" + fieldName +"\" dissallowed");
    }
    return value;
}

/**
 * Calls a method on the given object instance with the given argument.
 *
 * @param instance the object instance
 * @param methodName the name of the method to invoke
 * @param arg the argument to pass to the method
 * @see PrivilegedAccessor#invokeMethod(Object,String,Object[])
 */
public static Object invokeMethod( Object instance, String methodName, Object arg ) throws NoSuchMethodException,
                                                     IllegalAccessException, InvocationTargetException  {
    Object[] args = new Object[1];
    args[0] = arg;
    return invokeMethod(instance, methodName, args);
}

/**
 * Calls a method on the given object instance with the given arguments.
 *
 * @param instance the object instance
 * @param methodName the name of the method to invoke
 * @param args an array of objects to pass as arguments
 * @see PrivilegedAccessor#invokeMethod(Object,String,Object)
 */
public static Object invokeMethod( Object instance, String methodName, Object[] args ) throws NoSuchMethodException,
                                                         IllegalAccessException, InvocationTargetException  {
    Class[] classTypes = null;
    if( args != null) {
        classTypes = new Class[args.length];
        for( int i = 0; i < args.length; i++ ) {
            if( args[i] != null )
                classTypes[i] = args[i].getClass();
        }
    }
    return getMethod(instance,methodName,classTypes).invoke(instance,args);
}

/**
 *
 * @param instance the object instance
 * @param methodName the
 */
public static Method getMethod( Object instance, String methodName, Class[] classTypes ) throws NoSuchMethodException {
    Method accessMethod = getMethod(instance.getClass(), methodName, classTypes);
    accessMethod.setAccessible(true);
    return accessMethod;
}

/**
 * Return the named field from the given class.
 */
private static Field getField(Class thisClass, String fieldName) throws NoSuchFieldException {
    if (thisClass == null)
        throw new NoSuchFieldException("Invalid field : " + fieldName);
    try {
        return thisClass.getDeclaredField( fieldName );
    }
    catch(NoSuchFieldException e) {
        return getField(thisClass.getSuperclass(), fieldName);
    }
}

/**
 * Return the named method with a method signature matching classTypes
 * from the given class.
 */
private static Method getMethod(Class thisClass, String methodName, Class[] classTypes) throws NoSuchMethodException {
    if (thisClass == null)
        throw new NoSuchMethodException("Invalid method : " + methodName);
    try {
        return thisClass.getDeclaredMethod( methodName, classTypes );
    }
    catch(NoSuchMethodException e) {
        return getMethod(thisClass.getSuperclass(), methodName, classTypes);
    }
}

}
