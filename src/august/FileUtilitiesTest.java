package august; 

import junit.framework.*;
import java.io.File;
import java.util.Properties;
import java.util.List;

public class FileUtilitiesTest extends TestCase {

private String ext = ".a";
private String other_ext = ".b";
private File top_dir;
private File sub_dir_one;
private File sub_dir_two;
private File sub_dir_three;
private File[] files_with_ext;
private File[] files_with_other_ext;
    
public FileUtilitiesTest(String name) {
    super(name);
}

public static Test suite() {
    return new TestSuite(FileUtilitiesTest.class);
}

public void testGetFilesWithExtension() {
    
    // Set up this directory structure:
    //
    //                    top_dir
    //           /    /       |          \          
    //         f.a   f.b  sub_dir_one   sub_dir_two
    //                     |     |           |
    //                    f.a   f.b      sub_dir_three
    //                                     |     |
    //                                    f.a   f.b
    //
    File top_dir = new File("top_dir");
    File sub_dir_one = new File(top_dir, "sub_dir_one");
    File sub_dir_two = new File(top_dir, "sub_dir_two");
    File sub_dir_three = new File(sub_dir_two, "sub_dir_three");
    File[] _with_ext = { new File(top_dir, "f" + ext),
                         new File(sub_dir_one, "f" + ext),
                         new File(sub_dir_three, "f" + ext)
                       };
    files_with_ext = _with_ext;
    File[] _with_other_ext = { new File(top_dir, "f" + other_ext),
                               new File(sub_dir_one, "f" + other_ext),
                               new File(sub_dir_three, "f" + other_ext)
                             };
    files_with_other_ext = _with_other_ext;
    try {
        top_dir.mkdirs();
        sub_dir_one.mkdirs();
        sub_dir_two.mkdirs();
        sub_dir_three.mkdirs();
        for (int i = 0; 
             i < Math.min(files_with_ext.length, files_with_other_ext.length); 
             i++) {
             files_with_ext[i].createNewFile();
             files_with_other_ext[i].createNewFile();
        }
    }
    catch (Exception e) {
        fail (e.getMessage());
    }
    
    // Set up complete - now test!
    
    List result=FileUtilities.getFilesWithExtension(top_dir.getPath(), ext);
    
    // The next two tests assert that files_with_ext, and result are the 
    // same sets.
    
    // The sizes of files_with_ext and result should be the same:
    assertEquals(files_with_ext.length, result.size());
    
    // Every element in files_with_ext should also be in found_elements.
    for (int i = 0; i < files_with_ext.length; i++) {
        File f = files_with_ext[i];
        String fileName = f.getPath();
        assertTrue(result.contains(fileName));
    }
    
    // Delete the files that were created
    try {
        for (int i = 0; 
             i < Math.min(files_with_ext.length, files_with_other_ext.length);
             i++) {
             files_with_ext[i].delete();
             files_with_other_ext[i].delete();
        }
        sub_dir_three.delete();
        sub_dir_two.delete();
        sub_dir_one.delete();
        top_dir.delete();
        files_with_ext = null;
        files_with_other_ext = null;
    }
    catch (Exception e) {
    }    
}

public void testLoadProperties() {
    try {
        String propertyFile = "D:\\temp\\props_delete.properties";
        FileUtilities.appendLineToFile(propertyFile,"key=value");
        assert(FileUtilities.exists(propertyFile));
        Properties p = null;
        p = FileUtilities.loadProperties(propertyFile);
        assertNotNull(p);
    }
    catch (Exception e) {
        fail(e.getMessage());
    }
}

}


