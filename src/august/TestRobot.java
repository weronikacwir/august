package august;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.AWTException;
import java.util.StringTokenizer;

/** TestRobot is a utility used to generate native system input events for
 * the purposes of test automation.
 * <P>
 * The TestRobot class behaviour is similar to that of the java.awt.Robot class,
 * except TestRobot has some convenience methods that perform sequences of input
 * events that often appear together, such as "double click" implemented by the
 * doubleClick method.  The TestRobot is synchronized, so one logical event
 * (such as "double click") must complete before another sequence can start.
 * All of the public methods methods are class methods, so it is not necessary
 * to create a TestRobot instance to call them.  However, the TestRobot must be
 * initialized before it is used; otherwise every method call will result in a
 * NullPointerException.  The intialization is performed by the framework with
 * which the TestRobot is used.
 * <P>
 * Using the TestRobot class to generate input events differs from posting
 * events to the AWT event queue or AWT components in that the events are
 * generated in the platform's native input queue. For example,
 * TestRobot.mouseMove will actually move the mouse cursor instead of just
 * generating mouse move events.
 * <P>
 * Within the August framework, the TestRobot's public methods are called only  
 * by components of the GUI of the tested application. 
 *
 * @author weronika
 * @see java.awt.Robot
 */
public class TestRobot {

/** This Robot instance generates all of the events.
 */    
protected static Robot robot = null;

/** The maximum amount of time this TestRobot pauses between generating key 
 * prees and key release events when typing
 */
protected static final int TYPING_DELAY = 50;

/** This method must complete before any other TestRobot method is called.
 * <P>
 * It initializes the TestRobot class for use in the coordinate system of the
 * primary screen, and sets the amount of time it will pause between generating
 * most events. (Note, the autoDelay property can be temporarily overriden for
 * some types of events, such as typing or double clicking.)
 * <P>
 * SecurityException may be thrown if createRobot permission is not granted.
 *
 * @see SecurityManager.checkPermission(java.security.Permission)
 * @param autoDelay The number of milliseconds this TestRobot will pause
 * between generating events
 * @throws AWTException if the platform configuration does not allow low-level
 * input control
 */
protected static synchronized void initialize(int autoDelay) throws AWTException {
    robot = new Robot();
    robot.setAutoDelay(autoDelay);
}

/** Presses a given key.
 * <P>
 * Key codes that have more than one physical key associated with them
 * (e.g. KeyEvent.VK_SHIFT could mean either the left or right shift key)
 * will map to the left key.
 *
 * @see java.awt.KeyEvent
 * @see java.awt.Robot.keyPress(int)
 * @param keyCode Key to press (e.g. KeyEvent.VK_A)
 */
public static synchronized void keyPress(int keyCode) {
    robot.keyPress(keyCode);
}

/** Releases a given key.
 * <P>
 * Key codes that have more than one physical key associated with them
 * (e.g. KeyEvent.VK_SHIFT could mean either the left or right shift key) will
 * map to the left key.
 *
 * @see java.awt.KeyEvent
 * @see java.awt.Robot.keyRelease(int)
 * @param keyCode Key to release (e.g. KeyEvent.VK_A)
 */
public static synchronized void keyRelease(int keyCode) {
    robot.keyRelease(keyCode);
}

/** Simulates a key stroke: key press and release of the same key.
 *
 * @param keyCode code for the key to be stroked
 * @see java.awt.KeyEvent
 */
public static synchronized void keyStroke(int keyCode) {
    strikeKey(keyCode);
}

/** Moves mouse pointer to given screen coordinates.
 *
 * @param x X position on screen
 * @param y Y position on screen
 * @see java.awt.Robot.mouseMove(int, int)
 */
public static synchronized void mouseMove(int x, int y) {
    robot.mouseMove(x, y);
}

/** Simulates pressing of a mouse button.
 *
 * @param button The mouse button to be pressed:
 * MouseEvent.BUTTON1_MASK for left mouse key
 * MouseEvent.BUTTON2_MASK for middle mouse key
 * MouseEvent.BUTTON3_MASK for right mouse key
 * @see java.awt.InputEvent
 * @see java.awt.MouseEvent
 * @see java.awt.Robot.mousePress(int)
 */
public static synchronized void mousePress(int button) {
    robot.mousePress(button);
}

/** Simulates release of a mouse button.
 *
 * @param button The mouse button to be pressed:
 * MouseEvent.BUTTON1_MASK for left mouse key
 * MouseEvent.BUTTON2_MASK for middle mouse key
 * MouseEvent.BUTTON3_MASK for right mouse key
 * @see java.awt.InputEvent
 * @see java.awt.MouseEvent
 * @see java.awt.Robot.mouseRelease(int)
 */
public static synchronized void mouseRelease(int button) {
    robot.mouseRelease(button);
}

/** Simulates a button click: pressing and releasing of the left mouse button.
 */
public static synchronized void click() {
    robot.mousePress(MouseEvent.BUTTON1_MASK);
    robot.mouseRelease(MouseEvent.BUTTON1_MASK);
}

/** Moves the mouse pointer to a specified position on screen, and simulates a
 * click (press and release of the left mouse button) at that position.
 *
 * @param x X position on screen
 * @param y Y position on screen
 */
public static synchronized void click(int x, int y) {
    robot.mouseMove(x, y);        
    click();
}

/** Simulates a double click: press and release of the left mouse button
 * followed by another press and release.
 * <P>
 * The TestRobot's normal delay is overriden by the delay parameter for this
 * method, but after the method completes the old value is restored.
 *
 * @param delay number of milliseconds the TestRobot will pause between
 * generating the press and release events
 */
public static synchronized void doubleClick(int delay) {
    int oldDelay = robot.getAutoDelay();
    robot.setAutoDelay(delay);
    
    try {
    robot.mousePress(MouseEvent.BUTTON1_MASK);
    robot.mouseRelease(MouseEvent.BUTTON1_MASK);
    robot.mousePress(MouseEvent.BUTTON1_MASK);
    robot.mouseRelease(MouseEvent.BUTTON1_MASK);
    }
    finally {
    robot.setAutoDelay(oldDelay);
    }
}

/** Moves the mouse pointer to a specified position on screen, and simulates a
 * double click (press and release of the left mouse button followed by another
 * press and release) at that location.
 * <P>
 * The TestRobot's normal delay is overriden by the delay parameter for this
 * method, but after the method completes the old value is restored.
 *
 * @param delay number of milliseconds the TestRobot will pause between
 * generating the press and release events
 * @param x X position on screen
 * @param y Y position on screen
 */
public static synchronized void doubleClick(int delay, int x, int y) {
    robot.mouseMove(x, y);
    doubleClick(delay);
}

/** Simulates a right button click: pressing and releasing of the right mouse
 * button.
 */
public static synchronized void rightClick() {
    robot.mousePress(MouseEvent.BUTTON3_MASK);
    robot.mouseRelease(MouseEvent.BUTTON3_MASK);
}

/** Moves the mouse pointer to a specified position on screen, and simulates a
 * right click (press and release of the right mouse button) at that location.
 *
 * @param x X position on screen
 * @param y Y position on screen
 */
public static synchronized void rightClick(int x, int y) {
    robot.mouseMove(x, y);
    rightClick();	
}

/** Simulates a drag action: a left mouse button press (at mouse pointer's
 * current screen location), followed by moving of the mouse pointer to the
 * specified location.
 *
 * @param to_x X position on screen at which to end dragging
 * @param to_y Y position on screen at which to end dragging
 */
public static synchronized void drag(int to_x, int to_y) {
    robot.mousePress(MouseEvent.BUTTON1_MASK);
    robot.mouseMove(to_x, to_y);
}

/** Simulates a drag action from a specified position by a specified distance:
 * moves the mouse pointer to the (from_x, from_y) position on screen, simulates
 * a left mouse button press at that position, and moves the mouse pointer to
 * position (from_x + distance_x, from_y + distance_y), and releases the left
 * mouse button.
 *
 * @param from_x X position on screen at which to start dragging
 * @param from_y Y position on screen at which to start dragging
 * @param distance_x X on-screen distance from from_x at which to end dragging
 * @param distance_y Y on-screen distance from from_x at which to end dragging
 */
public static synchronized void drag(int from_x, int from_y, 
                                     int distance_x, int distance_y) {
    int to_x = from_x + distance_x;
    int to_y = from_y + distance_y;
	
    robot.mouseMove(from_x, from_y);
    drag(to_x, to_y);
}

/** Simulates a "drag and drop" action: a left mouse button press (at mouse
 * pointer's current screen location), followed by moving of the mouse pointer
 * to the specified location, followed by a release of the the left mouse
 * button.
 *
 * @param to_x X position on screen at which to drop
 * @param to_y Y position on screen at which to drop
 */
public static synchronized void dragAndDrop(int to_x, int to_y) {
    drag(to_x, to_y);
    robot.mouseRelease(MouseEvent.BUTTON1_MASK);    
}

/** Simulates a "drag and drop" action from a specified position by a specified
 * distance: moves the mouse pointer to the (from_x, from_y) position on screen,
 * simulates a left mouse button press at that position, and moves the mouse
 * pointer to position (from_x + distance_x, from_y + distance_y), and releases
 * the left mouse button.
 *
 * @param from_x X position on screen at which to start dragging
 * @param from_y Y position on screen at which to start dragging
 * @param distance_x X on-screen distance from from_x at which to drop
 * @param distance_y Y on-screen distance from from_x at which to drop
 */
public static synchronized void dragAndDrop(int from_x, int from_y, 
                                            int distance_x, int distance_y) {
    drag(from_x, from_y,distance_x, distance_y);
    robot.mouseRelease(MouseEvent.BUTTON1_MASK);
}

/** Convenience method, simulates pressing and releasing the 'Delete' key.
 */
public static synchronized void hitDelete() {
    strikeKey(KeyEvent.VK_DELETE);
}

/** Convenience method, simulates pressing and releasing the 'Enter' key.
 */
public static synchronized void hitEnter() {
    strikeKey(KeyEvent.VK_ENTER);
}

/** Convenience method, simulates pressing and releasing the 'Esc' key.
 */
public static synchronized void hitEscape() {
    strikeKey(KeyEvent.VK_ESCAPE);
}

/** Convenience method, simulates pressing and releasing the 'Insert' key.
 */
public static synchronized void hitInsert() {
    strikeKey(KeyEvent.VK_INSERT);
}

/** Convenience method, simulates pressing and releasing the 'Tab' key.
 */
public static synchronized void tabForward() {
    strikeKey(KeyEvent.VK_TAB);
}

/** Convenience method, simulates pressing and releasing the 'Tab' key with the
 * 'Shift' key down.
 */
public static synchronized void tabBackward() {
    robot.keyPress(KeyEvent.VK_SHIFT);
    strikeKey(KeyEvent.VK_TAB);
    robot.keyRelease(KeyEvent.VK_SHIFT);
}

/** Simulates key strokes to type a string.
 * <P>
 * The string to be typed may consist of zero or more:
 * <BR>- lowercase letters (a-z),
 * <BR>- uppercase letters (A_Z),
 * <BR>- punctuation signs (, . : ; ? !),
 * <BR>- quotation marks (" '),
 * <BR>- whitespace characters (space, tab, newline, carriage return),
 * <BR>- digits (0-9),
 * <BR>- brackets( ( ) { } [ ] < > ),
 * <BR>- and these other characters (~ ` @ # $ % ^ & * _ - + = \ | /).
 * <P>
 * If the string contains any other characters, an IllegalArgumentException will
 * be thrown when the first unrecognized character is encountered.
 * <P>
 * The implementation of this method is specific to the standard U.S. Windows
 * keyboard.  For example, this implementation assumes that typing of the
 * character '?' is achieved by pressing the 'Shift' key and the '/'
 * simultaneously.
 * <P>
 * During this method the TestRobot delay may be temporarily changed to a
 * smaller value.
 * <P>
 * Note that for the typed string to appear on screen there might be some
 * conditions that have to be met; For example, the mouse pointer may have to be
 * in a special state, a certain component might have to be in focus, etc.
 *
 * @param string the string to be typed
 */
public static synchronized void type(String string) {
    // Save the delay that was set during initialisation; It will be
    // reset to that value at the end of this method.
    int oldDelay = robot.getAutoDelay();
    // If the normal delay for this TestRobot is more than the TYPING_DELAY
    // value, then set it to the TYPING_DELAY value temporarily.
    robot.setAutoDelay(Math.min(oldDelay,TYPING_DELAY));
    
    try {
    int length = string.length();
    for (int i = 0; i < length; i++) {
        switch (string.charAt(i)) {
  
            // lowercase letters
	    case 'a':  typeChar(KeyEvent.VK_A, false); 
	    break;
	    case 'b':  typeChar(KeyEvent.VK_B, false); 
	    break;
	    case 'c':  typeChar(KeyEvent.VK_C, false); 
	    break;
	    case 'd':  typeChar(KeyEvent.VK_D, false); 
	    break;
	    case 'e':  typeChar(KeyEvent.VK_E, false); 
	    break;
	    case 'f':  typeChar(KeyEvent.VK_F, false); 
	    break;
	    case 'g':  typeChar(KeyEvent.VK_G, false); 
	    break;
	    case 'h':  typeChar(KeyEvent.VK_H, false); 
	    break;
	    case 'i':  typeChar(KeyEvent.VK_I, false); 
	    break;
	    case 'j':  typeChar(KeyEvent.VK_J, false); 
	    break;
	    case 'k':  typeChar(KeyEvent.VK_K, false); 
	    break;
	    case 'l':  typeChar(KeyEvent.VK_L, false); 
	    break;
	    case 'm':  typeChar(KeyEvent.VK_M, false); 
	    break;
	    case 'n':  typeChar(KeyEvent.VK_N, false); 
	    break;
	    case 'o':  typeChar(KeyEvent.VK_O, false); 
	    break;
	    case 'p':  typeChar(KeyEvent.VK_P, false); 
	    break;
	    case 'q':  typeChar(KeyEvent.VK_Q, false); 
	    break;
	    case 'r':  typeChar(KeyEvent.VK_R, false); 
	    break;
	    case 's':  typeChar(KeyEvent.VK_S, false); 
	    break;
	    case 't':  typeChar(KeyEvent.VK_T, false); 
	    break;
	    case 'u':  typeChar(KeyEvent.VK_U, false); 
	    break;
	    case 'v':  typeChar(KeyEvent.VK_V, false); 
	    break;
	    case 'w':  typeChar(KeyEvent.VK_W, false); 
	    break;
	    case 'x':  typeChar(KeyEvent.VK_X, false); 
	    break;
	    case 'y':  typeChar(KeyEvent.VK_Y, false); 
	    break;
	    case 'z':  typeChar(KeyEvent.VK_Z, false); 
	    break;

            // uppercase letters
	    case 'A':  typeChar(KeyEvent.VK_A, true); 
	    break;
	    case 'B':  typeChar(KeyEvent.VK_B, true); 
	    break;
	    case 'C':  typeChar(KeyEvent.VK_C, true); 
	    break;
	    case 'D':  typeChar(KeyEvent.VK_D, true); 
	    break;
	    case 'E':  typeChar(KeyEvent.VK_E, true); 
	    break;
	    case 'F':  typeChar(KeyEvent.VK_F, true); 
	    break;
	    case 'G':  typeChar(KeyEvent.VK_G, true); 
	    break;
	    case 'H':  typeChar(KeyEvent.VK_H, true); 
	    break;
	    case 'I':  typeChar(KeyEvent.VK_I, true); 
	    break;
	    case 'J':  typeChar(KeyEvent.VK_J, true); 
	    break;
	    case 'K':  typeChar(KeyEvent.VK_K, true); 
	    break;
	    case 'L':  typeChar(KeyEvent.VK_L, true); 
	    break;
	    case 'M':  typeChar(KeyEvent.VK_M, true); 
	    break;
	    case 'N':  typeChar(KeyEvent.VK_N, true); 
	    break;
	    case 'O':  typeChar(KeyEvent.VK_O, true); 
	    break;
	    case 'P':  typeChar(KeyEvent.VK_P, true); 
	    break;
	    case 'Q':  typeChar(KeyEvent.VK_Q, true); 
	    break;
	    case 'R':  typeChar(KeyEvent.VK_R, true); 
	    break;
	    case 'S':  typeChar(KeyEvent.VK_S, true); 
	    break;
	    case 'T':  typeChar(KeyEvent.VK_T, true); 
	    break;
	    case 'U':  typeChar(KeyEvent.VK_U, true); 
	    break;
	    case 'V':  typeChar(KeyEvent.VK_V, true); 
	    break;
	    case 'W':  typeChar(KeyEvent.VK_W, true); 
	    break;
	    case 'X':  typeChar(KeyEvent.VK_X, true); 
	    break;
	    case 'Y':  typeChar(KeyEvent.VK_Y, true); 
	    break;
	    case 'Z':  typeChar(KeyEvent.VK_Z, true); 
	    break;
            
            // punctuation signs
            case ',':  typeChar(KeyEvent.VK_COMMA, false);
            break;
            case '.':  typeChar(KeyEvent.VK_PERIOD, false); 
	    break;
            case ':':  typeChar(KeyEvent.VK_SEMICOLON, true); 
	    break;
            case ';':  typeChar(KeyEvent.VK_SEMICOLON, false); 
	    break;
            case '?':  typeChar(KeyEvent.VK_SLASH, true);
            break;
            case '!':  typeChar(KeyEvent.VK_1, true);
            break;

            // quotation marks
            case '"':  typeChar(KeyEvent.VK_QUOTE, true);
            break;
            case '\'': typeChar(KeyEvent.VK_QUOTE, false);
            break;
            
            // whitespace
            case ' ':  typeChar(KeyEvent.VK_SPACE, false);
            break;
            case '\b': typeChar(KeyEvent.VK_SPACE, false);
            break;
            case '\t': typeChar(KeyEvent.VK_TAB, false);
            break;
            case '\n': typeChar(KeyEvent.VK_ENTER, false);
            break;
            case '\r': typeChar(KeyEvent.VK_ENTER, false);
            break;

            // digits
            case '0':  typeChar(KeyEvent.VK_0, false); 
	    break;
	    case '1':  typeChar(KeyEvent.VK_1, false); 
	    break;
	    case '2':  typeChar(KeyEvent.VK_2, false); 
	    break;
	    case '3':  typeChar(KeyEvent.VK_3, false); 
	    break;
	    case '4':  typeChar(KeyEvent.VK_4, false); 
	    break;
	    case '5':  typeChar(KeyEvent.VK_5, false); 
	    break;
	    case '6':  typeChar(KeyEvent.VK_6, false); 
	    break;
	    case '7':  typeChar(KeyEvent.VK_7, false); 
	    break;
	    case '8':  typeChar(KeyEvent.VK_8, false); 
	    break;
	    case '9':  typeChar(KeyEvent.VK_9, false); 
	    break;
            
            // brackets
            case '(':  typeChar(KeyEvent.VK_9, true);
            break;
            case ')':  typeChar(KeyEvent.VK_0, true);
            break;
            case '{':  typeChar(KeyEvent.VK_OPEN_BRACKET, true);
            break;
            case '}':  typeChar(KeyEvent.VK_CLOSE_BRACKET, true);
            break;
            case '[':  typeChar(KeyEvent.VK_OPEN_BRACKET, false);
            break;
            case ']':  typeChar(KeyEvent.VK_CLOSE_BRACKET, false);
            break;           
            case '<':  typeChar(KeyEvent.VK_COMMA, true);
            break;
            case '>':  typeChar(KeyEvent.VK_PERIOD, true);
            break;
            
            // other
            case '~':  typeChar(KeyEvent.VK_DEAD_TILDE, true);// does not work
            break;
            case '`':  typeChar(KeyEvent.VK_DEAD_TILDE, false);// does not work
            break;
            case '@':  typeChar(KeyEvent.VK_2, true);
            break;
            case '#':  typeChar(KeyEvent.VK_3, true);
            break;
            case '$':  typeChar(KeyEvent.VK_4, true);
            break;
            case '%':  typeChar(KeyEvent.VK_5, true); 
	    break;
            case '^':  typeChar(KeyEvent.VK_6, true);
            break;
            case '&':  typeChar(KeyEvent.VK_7, true);
            break;
            case '*':  typeChar(KeyEvent.VK_8, true);
            break;
            case '-':  typeChar(KeyEvent.VK_UNDERSCORE, true);// does not work
            break;
            case '_':  typeChar(KeyEvent.VK_UNDERSCORE, false);// does not work
            break;
            case '+':  typeChar(KeyEvent.VK_EQUALS, true);
            break;
            case '=':  typeChar(KeyEvent.VK_EQUALS, false);
            break;
            case '\\': typeChar(KeyEvent.VK_BACK_SLASH, false); 
	    break;
            case '|':  typeChar(KeyEvent.VK_BACK_SLASH, true);
            break;
            case '/':  typeChar(KeyEvent.VK_SLASH, false); 
	    break;            
            
            default:  throw new IllegalArgumentException
            ("Unrecognized character in TestRobot.type(): " + string.charAt(i));
        }
    }
    }
    finally {
    // Restore the normal delay value.
    robot.setAutoDelay(oldDelay);
    }
}

/** Simulates key strokes to type a single character.
 * <P>
 * For example, to type 'a' the 'A' key is pressed and released, and to type '!'
 * the 'Shift' key is pressed, then the '1' key is pressed and released, and
 * then the 'Shift' key is released.
 * <P>
 * This method is called by the TestRobot.type(String) method.
 *
 * @param keyCode code for the key to be stroked
 * @param shift true if the 'Shift' key should be down before the key
 * specified by keyCode is pressed, false otherwise
 */
protected static void typeChar(int keyCode, boolean shift) {
    if (shift) {
        robot.keyPress(KeyEvent.VK_SHIFT);
    }
    strikeKey(keyCode);
    if (shift) {
    	robot.keyRelease(KeyEvent.VK_SHIFT);
    }
}

/** Simulates a key stroke: press amd release of the same key.
 * <P>
 * This method is called by TestRobot method instead of calling the public
 * keyStroke(int) method, which is synchronized.
 *
 * @param keyCode code for the key to be stroked
 * @see java.awt.KeyEvent
 */
protected static void strikeKey(int keyCode) {
    robot.keyPress(keyCode);
    robot.keyRelease(keyCode);    
}

/** Simulates an accelerator: press and release of a combination of keys.
 * @param the character of the key to be stroked, like "a"
 * @prarm button list like Alt, Ctrl, Shift, F1---F12, devided by "+" for example "Ctrl+Shift"
 * @see java.awt.KeyEvent
 */
public static synchronized void accelerator(String key, String controls) throws TestableComponent.BadArgumentsException{
	StringTokenizer control_tokenizer = new StringTokenizer(controls, "+");
	String controlName = null;
	boolean isCtrlPressed = false, isShiftPressed = false, isAltPressed = false;
	boolean isF1Pressed = false, isF2Pressed = false, isF3Pressed = false, isF4Pressed = false, isF5Pressed = false;
	boolean isF6Pressed = false, isF7Pressed = false, isF8Pressed = false, isF9Pressed = false, isF10Pressed = false;
	boolean isF11Pressed = false, isF12Pressed = false;

	while( control_tokenizer.hasMoreTokens()) {
		controlName = control_tokenizer.nextToken();
		System.out.println("----controlName=" + controlName);
		if(controlName.equals("Ctrl")) {
			keyPress(KeyEvent.VK_CONTROL);
			System.out.println("control button is pressed");
			isCtrlPressed = true;
		}
		else if(controlName.equals("Alt")) {
			keyPress(KeyEvent.VK_ALT);
			System.out.println("alt button is pressed");
			isAltPressed = true;
		}
		else if(controlName.equals("Shift")) {
			keyPress(KeyEvent.VK_SHIFT);
			System.out.println("Shift button is pressed");
			isShiftPressed = true;
		}
		else if(controlName.equals("F1")){
			keyPress(KeyEvent.VK_F1);
			System.out.println("F1 button is pressed");
			isF1Pressed = true;
		}
		else if(controlName.equals("F2")){
			keyPress(KeyEvent.VK_F2);
			System.out.println("F2 button is pressed");
			isF2Pressed = true;
		}
		else if(controlName.equals("F3")){
			keyPress(KeyEvent.VK_F3);
			System.out.println("F3 button is pressed");
			isF3Pressed = true;
		}
		else if(controlName.equals("F4")){
			keyPress(KeyEvent.VK_F4);
			System.out.println("F4 button is pressed");
			isF4Pressed = true;
		}
		else if(controlName.equals("F5")){
			keyPress(KeyEvent.VK_F5);
			System.out.println("F5 button is pressed");
			isF5Pressed = true;
		}
		else if(controlName.equals("F6")){
			keyPress(KeyEvent.VK_F6);
			System.out.println("F6 button is pressed");
			isF6Pressed = true;
		}
		else if(controlName.equals("F7")){
			keyPress(KeyEvent.VK_F7);
			System.out.println("F7 button is pressed");
			isF7Pressed = true;
		}
		else if(controlName.equals("F8")){
			keyPress(KeyEvent.VK_F8);
			System.out.println("F8 button is pressed");
			isF8Pressed = true;
		}
		else if(controlName.equals("F9")){
			keyPress(KeyEvent.VK_F9);
			System.out.println("F9 button is pressed");
			isF9Pressed = true;
		}
		else if(controlName.equals("F10")){
			keyPress(KeyEvent.VK_F10);
			System.out.println("F10 button is pressed");
			isF10Pressed = true;
		}
		else if(controlName.equals("F11")){
			keyPress(KeyEvent.VK_F11);
			System.out.println("F11 button is pressed");
			isF11Pressed = true;
		}
		else if(controlName.equals("F12")){
			keyPress(KeyEvent.VK_F12);
			System.out.println("F12 button is pressed");
			isF12Pressed = true;
		}
		else {
			throw new TestableComponent.BadArgumentsException("control key must be Ctrl, Alt, Shift, F1--F12");
		}
	}
	if(!key.equals("")) {
		type(key);
		System.out.println("key:" + key + " is pressed and release");

	}
	if(isAltPressed) {
		keyRelease(KeyEvent.VK_ALT);
		System.out.println("Alt button is released");
	}
	if(isCtrlPressed) {
		keyRelease(KeyEvent.VK_CONTROL);
		System.out.println("Control button is released");
	}
	if(isShiftPressed) {
		keyRelease(KeyEvent.VK_SHIFT);
		System.out.println("Shift button is released");
	}
	if(isF1Pressed) {
		keyRelease(KeyEvent.VK_F1);
		System.out.println("F1 button is released");
	}
	if(isF2Pressed) {
		keyRelease(KeyEvent.VK_F2);
		System.out.println("F2 button is released");
	}
	if(isF3Pressed) {
		keyRelease(KeyEvent.VK_F3);
		System.out.println("F3 button is released");
	}
	if(isF3Pressed) {
		keyRelease(KeyEvent.VK_F4);
		System.out.println("F4 button is released");
	}
	if(isF5Pressed) {
		keyRelease(KeyEvent.VK_F5);
		System.out.println("F5 button is released");
	}
	if(isF6Pressed) {
		keyRelease(KeyEvent.VK_F6);
		System.out.println("F6 button is released");
	}
	if(isF7Pressed) {
		keyRelease(KeyEvent.VK_F7);
		System.out.println("F7 button is released");
	}
	if(isF8Pressed) {
		keyRelease(KeyEvent.VK_F8);
		System.out.println("F8 button is released");
	}
	if(isF9Pressed) {
		keyRelease(KeyEvent.VK_F9);
		System.out.println("F9 button is released");
	}
	if(isF10Pressed) {
		keyRelease(KeyEvent.VK_F10);
		System.out.println("F10 button is released");
	}
	if(isF11Pressed) {
		keyRelease(KeyEvent.VK_F11);
		System.out.println("F11 button is released");
	}
	if(isF12Pressed) {
		keyRelease(KeyEvent.VK_F12);
		System.out.println("F12 button is released");
	}

}

}