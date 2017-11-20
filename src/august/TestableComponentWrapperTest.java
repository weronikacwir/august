package august;

import junit.framework.*;
import java.awt.*;
import javax.swing.*;
import august.wrappers.*;

public class TestableComponentWrapperTest extends TestCase {
    
public TestableComponentWrapperTest(String name) {
    super(name);
}

public static Test suite() {
    return new TestSuite(TestableComponentWrapperTest.class);
}

public void testWrap() {

    JToggleButton jtb = new JToggleButton();
    try {
        TestableComponent tc = TestableComponent.Wrapper.wrap(jtb);
        assertEquals(JToggleButtonWrapper.class, tc.getClass());
    } 
    catch (Exception e) {
        fail();
    }
    
    JPanel p = new JPanel();
    try {
        TestableComponent tc = TestableComponent.Wrapper.wrap(p);
        assertEquals(ComponentWrapper.class, tc.getClass());
    } 
    catch (Exception e) {
        fail();
    }
}
}
