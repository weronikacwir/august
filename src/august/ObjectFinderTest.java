package august;

import junit.framework.*;
import java.awt.*;

public class ObjectFinderTest extends TestCase {
    
public ObjectFinderTest(String name) {
    super(name);
}

public static Test suite() {
    return new TestSuite(ObjectFinderTest.class);
}

public void testFindComponent() {
    
    // Set up the following (where a, b, ..., g  stand for component names):
    //      a              e    
    //   /  |        /          \  
    //  b   c   [name not set]   g    
    //      |
    //      d
    //
    Container a = new Frame(); 
    a.setName("a");
    Component b = new Button();
    b.setName("b");
    Container c = new Panel();
    c.setName("c");
    Component d = new Label();
    d.setName("d");    
    Container e = new Frame();
    e.setName("e");
    Component f = new Checkbox();
    //f.setName("f");
    Component g = new Scrollbar();
    g.setName("g");
    
    a.add(b);
    a.add(c);
    c.add(d);
    e.add(f);
    e.add(g);
    
    Component[] components = {a, e};
    
    Component result;
    
    result = ObjectFinder.find("a", components);
    assertSame(result, a);
    
    result = ObjectFinder.find("b", components);
    assertSame(result, b);
    
    result = ObjectFinder.find("c", components);
    assertSame(result, c);
    
    result = ObjectFinder.find("d", components);
    assertSame(result, d);
    
    result = ObjectFinder.find("e", components);
    assertSame(result, e);
    
    result = ObjectFinder.find("g", components);
    assertSame(result, g);
    
    result = ObjectFinder.find("z", components);
    assertNull(result);
}
    
}
