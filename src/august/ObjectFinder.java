package august;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.List;

/** This is a utility used for finding objects in the application tested by the
 * August system.
 * <P>
 * Right now there is only two methods; they both look for  GUI component
 * objects.
 * <P>
 * All of the methods are class methods, so that they can be used without
 * instantiating the ObjectFinder.
 *
 * @author weronika
 */
public class ObjectFinder {

/** Maintains reference to windows that are currently iconified
 */
private static List iconifiedWindows = new ArrayList();

/** Should be called (probably from a window wrapper)
 * whenever a window is iconified.
 *
 *  @param window a window that just became iconified
 */
public static void addIconifiedWindow(Component window) {
    iconifiedWindows.add(window);
}

/** Should be called (probably form a window wrapper)
 * whever a window is deiconified.
 *
 *  @param window a window that just became deiconified
 */
public static void removeIconifiedWindow(Component window) {
    iconifiedWindows.remove(window);
}

/** Looks for all components of a given class contained in a given container.
 * This search is non-recursive; it will return only those components that are
 * directs children of the given container in the component tree.
 *
 * @param cont the container in which to search
 * @param compClass the class of the components to be found
 */
public static ArrayList findGUIComponents(Container cont, Class compClass) {
    ArrayList compClassComponents = new ArrayList();

    Component[] allComponents = cont.getComponents();
    for(int i=0; i < allComponents.length; i++) {
        if(compClass.isInstance(allComponents[i])) {
            compClassComponents.add(allComponents[i]);
        }
    }
    return compClassComponents;
}

 /** Looks for a component whose name matches the parameter componentName in
  * all existing windows (Frame objects) which were instantiated by the same
  * JVM. Stops looking if it finds a match.
  *
  * @param componentName name of the GUI component to look for
  * @return the first Component object whose name matches the parameter
  * componentName encountered during depth-first traversal of all of the
  * existing containers of type Frame, or null if such component is not found
  */
public static Component findGUIComponent(String componentName) {
    Component theOne = null;

    // First, search the component trees under all the active frames.
    theOne = find(componentName, Frame.getFrames());

    // If the component was not found, look in dialogs that are not owned by
    // any of the frames.
    if (theOne == null) {
        Component[] defaultDialogOwner = {(new JDialog()).getOwner()};
        theOne = find(componentName, defaultDialogOwner);
    }

    // If the component still was not found, look in iconified windows.
    if (theOne == null) {
        Component[] iconified = new Component[iconifiedWindows.size()];
        for (int i = 0; i < iconified.length; i++) {
            iconified[i] = (Component)(iconifiedWindows.get(i));
        }
        theOne = find(componentName, iconified);
    }

    return theOne;
}

/** Looks for a component whose name matches the parameter componentName in the
 * list of components specified by the parameter containers, and inside each
 * container in that list.  Stops looking if it finds a match.
 *
 * @param componentName name of the GUI component to look for
 * @param containers an which may contain objects of type Container or Component
 * @return the first Component object whose name matches the parameter
 * componentName encountered during depth-first traversal of all of the
 * components on specified by the containers parameter, or null if such
 * component is not found
 */
protected static Component find(String componentName, Component[] components) {
    // Traverse the "component tree" in depth-first order until it finds the
    // the component with a name specified by componentName.

    // A placeholder for the component I am looking for.
    Component theOne = null;

    Component c;
    for(int i = (components.length - 1); i >= 0; i--) {
        c = components[i];
        // If c is the component I am looking for, I am done.
        if (componentName.equals(c.getName())) {
            theOne = c;
            break;
        }
        // If c is a container, then I will check if it contains the
        // component I am looking for.
        if (c instanceof Container) {
            theOne = find(componentName, ((Container)c).getComponents());
            // If I found the component I am looking for inside c,
            // then I am done.
            if (theOne != null) {
                break;
            }
        }
        // If c is a window, then I will check if the component that I am
        // looking for is in one of the windows that c owns.
        if (c instanceof Window) {
            theOne = find(componentName, ((Window)c).getOwnedWindows());
            // If I found the component I am looking for inside on of the
            // windows owned by c, then I am done.
            if (theOne != null) {
                break;
            }
        }
        // The component I am looking for is not c, it is not inside c, and it
        // not inside on of the windows owned by c, so I must look some more.
        else {
            continue;
        }
    }
    return theOne;
}

/**
 * First checks each Frame for matching titles, since the title is highly likely
 * to be set.
 */
public static Component findInFrames(String componentTitle, Frame[] frames){
    Component result = null;
    for (int i=0;i<frames.length;i++){
        if (frames[i].getTitle().equals(componentTitle)){
            return frames[i];
        }
    }
    return find(componentTitle,frames);
}


public static JTree findTree(String userObjectString){
    Frame[] frames = Frame.getFrames();
    return findTree(userObjectString, frames);
}

protected static JTree findTree(String userObjectString, Component[] components){
    JTree result = null;
    for (int i=0;i<components.length;i++){
        if (components[i] instanceof JTree){
            TreeModel model = ((JTree)components[i]).getModel();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getRoot();
            Object userObject = node.getUserObject();
            if (userObjectString.equals(userObject.toString())){
                result = (JTree)components[i];
            }
        }
        if (components[i] instanceof Container){
            Component[] subComponents = ((Container)components[i]).getComponents();
            result = findTree(userObjectString, subComponents);
        }
    }
    return result;
}

}