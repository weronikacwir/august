package august;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

/** FileUtilities is a collection of utilities for file operations.
 * <P>
 * These utilities are used by many classes in the August system, but so far 
 * nothing in this class is specific to the August system, so the class is 
 * public.
 * <P>
 * All of the methods are class methods, so they can be called without
 * instantiating the FileUtilities class.
 *
 * @author weronika
 */
public class FileUtilities {
    
/** Checks if a file or a directory exists given its path.
 *
 * @param pathname absolute or relative path for a file or a directory
 * @return 'true' if the file or directory given by pathname exists,
 * 'false' otherwise
 */    
public static boolean exists(String pathname) {
    File file = new File(pathname);
    return file.exists();
}

/** Deletes the file or directory denoted by the pathname parameter. If the
 * pathname parameter denotes a directory, then the directory must be empty in
 * order to be deleted.
 *
 * @param pathname relative or absolute path for the file or directory to be 
 * deleted
 * @return 'true' if the file or directory was succesfully deleted,
 * 'false' otherwise
 */
public static boolean delete(String pathname) {
    File file = new File(pathname);
    return file.delete();
}

/** Creates a file with pathname given by the pathname parameter if it doesn't
 * already exist.
 *
 * @param pathname relative or absolute path of the file to be created
 * @return 'true' if the file given by filename did not exist and was
 * successfully created, 'false' if the file already existed
 * @throws IOException if an i/o error is encountered
 */
public static boolean createFile(String pathname) throws IOException {
    File file = new File(pathname);
    return file.createNewFile();
}

/** Creates the directory named by the pathname parameter.
 *
 * @param pathname relative or absolute path for the directory to be created
 * @return 'true' if the directory was created, 'false' otherwise
 */
public static boolean mkdir(String pathname) {
    File file = new File(pathname);
    return file.mkdir();
}

/** Creates the directory named by this abstract pathname, including any 
 * necessary but nonexistent parent directories. Note that if this operation 
 * fails it may have succeeded in creating some of the necessary parent 
 * directories.
 *
 * @param pathname relative or absolute path for the directory to be created
 * @return 'true' if the directory was created, along with all necessary
 * parent directories, 'false' otherwise
 */
public static boolean mkdirs(String pathname) {
    File file = new File(pathname);
    return file.mkdirs();
}

/** Creates and returns a new Proproperties object given a path of a properties
 * file, and a set of default properties.
 * <P>
 * The format of the properties file is the same as described in documentation
 * for java.util.Properties class.
 *
 * @param propertiesFilePath relative or absolute path for the file from which 
 * the properties are to be read
 * @param defaults properties to be used as defaults in case that they are not
 * specified in the file denoted by propertiesFileName
 * @return a Properties object containing the properties specified
 * in the file denoted by the propertiesFileName
 * @throws IOException if an i/o error is encountered while trying to read the
 * properties file
 * @throws FileNotFoundException if the file denoted by propertiesFileName 
 * cannot be found
 */
public static Properties loadProperties(String propertiesFilePath,
                                        Properties defaults) throws 
IOException, FileNotFoundException {
    Properties properties = new Properties(defaults);
    loadProperties(properties, propertiesFilePath);
    return properties;
}

/** Creates and returns a new Proproperties object given a path of a properties
 * file.
 * <P>
 * The format of the properties file is the same as described in documentation
 * for java.util.Properties class.
 *
 * @param propertiesFilePath relative or absolute path for the file from which 
 * the properties are to be read
 * @return a Properties object containing the properties specified
 * in the file denoted by the propertiesFileName
 * @throws IOException if an i/o error is encountered while trying to read the
 * properties file
 * @throws FileNotFoundException if the file denoted by propertiesFileName 
 * cannot be found
 */
public static Properties loadProperties(String propertiesFilePath) throws 
IOException, FileNotFoundException {
    Properties properties = new Properties();
    loadProperties(properties, propertiesFilePath);
    return properties;
}

/** Reads properties from a property file into an existing Properties object.
 *
 * @param properties a Properties object into which the properties from the
 * file denoted by propertiesFileName are to be loaded
 * @param propertiesFilePath relative or absolute path for the file from which 
 * the properties are to be read
 * @throws IOException if an i/o error is encountered while trying to read the
 * properties file
 * @throws FileNotFoundException if the file denoted by propertiesFileName 
 * cannot be found
 */
public static void loadProperties(Properties properties, 
                                  String propertiesFilePath) throws IOException,
FileNotFoundException {
    FileInputStream instream = null;
    try {
        instream = new FileInputStream(propertiesFilePath);
        properties.load(instream);
    }
    finally {
        // Make sure that the file input stream is closed.
        try {
            instream.close();
        }
        catch (NullPointerException npe) {
            // if npe was thrown, then instream was never opened, so just bury
            // this exception.
        }
        catch (IOException ioe) {
            // if ioe was thrown, then instream was probably already closed, so 
            // just bury this exception.
        }
    }                                            
}

/** Searches for files with an extension denoted by extension in the directory
 * tree whose root is denoted by the directory parameter.
 *
 * @param dir the root of the directory tree which is to be searched for files
 * @param extension the extension of the files for which this method searches
 * @return a list containing pathnames of files as Strings; the path names are 
 * absolute iff dir is an absolute path, otherwise the names are relative to the  
 * same directory as dir; the paths are not guaranteed to appear in any 
 * particular order
 */
public static List getFilesWithExtension(String dir, String extension) {
    ArrayList files = new ArrayList();
    getFiles(files,  new File(dir) , extension);
    return files;
}

/** Helper method called by the getFilesWithExtension method.  Recursively 
 * searches a directory tree for files with a specified extension.
 *
 * @param files an ArrayList containing pathnames of files found so far
 * @param path the root of the directory subtree to be searched next
 * @param extension the extension for which to search
 */
private static void getFiles(List files, File path, String extension) {
    // Base case: the path represents a file.
    if (path.isFile()) {
        if (hasExtension(path, extension)) {
            files.add(path.getPath());
        }
    }
    // If path is a directory, recurse.
    else if (path.isDirectory()) { 
        File[] pathList = path.listFiles();
        for (int i = 0; i < pathList.length; i++) {
                  getFiles(files, pathList[i], extension);
        }
    }
}

/** Checks whether a file has a specified extension.  Case sensitive.
 *
 * @param file the file
 * @param extension the extension
 * @return 'true' if the file has the specified extension,
 * 'false' otherwise
 */
public static boolean hasExtension(File file, String extension) {
    return file.getName().endsWith(extension);
}

/** Writes a given string, followed by a newline, to the end of the file whose
 * path is denoted by pathname.
 * @param pathname relative or absolute path of the file
 * @param string a string to be written to the file
 * @throws IOException if an i/o error is encountered
 */
public static void appendLineToFile(String pathname, String string) throws 
IOException{
    if(string ==  null) {
        string = "";
    }

    BufferedWriter writer = null;
    try
    {
        writer = new BufferedWriter(new FileWriter(pathname, true));
        writer.write(string);
        writer.newLine();
    }
    finally {
        try {
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            // Bury this exception - the stream was probably not open in the
            // first place.
        }
        catch (NullPointerException e) {
            // Bury this exception - the stream was probably not open in the
            // first place.
        }
    } 
}
}