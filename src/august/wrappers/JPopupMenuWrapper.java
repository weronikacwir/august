/*
 * Created by IntelliJ IDEA.
 * User: hongfei
 * Date: Jul 25, 2002
 * Time: 3:59:56 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package august.wrappers;

import august.TestableComponent;
import august.TestRobot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.*;

public class JPopupMenuWrapper extends ComponentWrapper{
protected static final String KEYWORD_SELECT = "select";
protected static final String KEYWORD_MENUITEM = "selectMenuItem";
protected static final String ARG_MENUITEM_LABEL = "itemLabel";
protected static final String ARG_MENUITEM_INDEX = "itemIndex";
protected javax.swing.JPopupMenu popupMenu;

public JPopupMenuWrapper(JPopupMenu menu)  {
	super(menu);
	this.popupMenu = menu;
}

public void performAction(String keyword, Map args) throws
TestableComponent.BadKeywordException, TestableComponent.BadArgumentsException,
TestableComponent.LogicalException {
	if (keyword.equals(KEYWORD_SELECT)) {
		performSelect();
	}
	else if(keyword.equals(KEYWORD_MENUITEM)) {
		performSelectMenuItem(args);
	}
	else {
		super.performAction(keyword, args);
	}
}
protected void performSelect() throws TestableComponent.LogicalException {
	if (popupMenu.isShowing()) {
		Point coordinates = popupMenu.getLocationOnScreen();
		TestRobot.click(coordinates.x, coordinates.y);
	}
	else {
		throw new TestableComponent.LogicalException( "Cannot select "
		+ popupMenu.getName() + ".");
	}
}

protected void performSelectMenuItem(Map args) throws TestableComponent.LogicalException,TestableComponent.BadArgumentsException {
    if ((!args.containsKey(ARG_MENUITEM_LABEL))
        && (!args.containsKey(ARG_MENUITEM_INDEX))) {
        throw new TestableComponent.BadArgumentsException("Cannot select the "
                + "menuItem  from the menu" + popupMenu.getName() + "; the menu item's label or index must be specified.");
    }
	String label = (String)(args.get(ARG_MENUITEM_LABEL));
	JMenuItem menuItem = null;
	if(label != null) {
		//args itemLabel available
		for(int i = 0; i <  popupMenu.getComponentCount(); i ++){
			if(label.equals(popupMenu.getComponent(i).getName()))	{
	            menuItem = (JMenuItem)popupMenu.getComponent(i);
			}
		}
	}
	else {
		//args itemIndex available
		int index = -1;
		try {
			index = Integer.parseInt((String)(args.get(ARG_MENUITEM_INDEX)));
		}
		catch (Exception e) {
			throw new TestableComponent.BadArgumentsException("Cannot select the "
                + "menuItem  from the menu" + popupMenu.getName() + "; the menu item'index must be exist.");
		}
		menuItem = (JMenuItem)popupMenu.getComponent(index);

	}
	if(menuItem != null) {
		Point coordinates = menuItem.getLocationOnScreen();
		TestRobot.click(coordinates.x, coordinates.y);
	}
	else {
		throw new TestableComponent.BadArgumentsException("Cannot select the "
                + "menuItem  from the menu" + popupMenu.getName() + "; the menu item  must be exist.");
		}
}
}


