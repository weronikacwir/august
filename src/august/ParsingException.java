package august;

/** Wrapper for all exceptions that occur during parsing of test scripts in the
 * August system.
 *
 * @author  weronika
 */
public class ParsingException extends java.lang.Exception {
    
/** Creates new <code>ParsingException</code> without detail message.
 */
public ParsingException() {
}

/** Constructs an <code>ParsingException</code> with the specified detail 
 * message.
 *
 * @param msg the detail message.
 */
public ParsingException(String msg) {
    super(msg);
}
}


