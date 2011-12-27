package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.text.MessageFormat;


/**
 * submenu for the simple node itself
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: SimpleNodeSubMenu.java,v 1.3 2004/04/15 18:21:58 tomcopeland Exp $
 */
public class SimpleNodeSubMenu
  extends JMenu
{
  private ViewerModel model;
  private SimpleNode  node;

  /**
   * constructs the submenu
   *
   * @param model model to which the actions will be forwarded
   * @param node menu's owner
   */
  public SimpleNodeSubMenu( ViewerModel model, SimpleNode node )
  {
    super( 
      MessageFormat.format( 
        NLS.nls( "AST.MENU.NODE.TITLE" ), new Object[] { node.toString(  ) } ) );

    this.model   = model;
    this.node    = node;

    init(  );
  }

  private void init(  )
  {
    StringBuffer buf = new StringBuffer( 200 );

    for ( Node temp = node; temp != null; temp = temp.jjtGetParent(  ) )
    {
      buf.insert( 0, "/" + temp.toString(  ) );
    }

    add( 
      new XPathFragmentAddingItem( 
        NLS.nls( "AST.MENU.NODE.ADD_ABSOLUTE_PATH" ), model, buf.toString(  ) ) );

    add( 
      new XPathFragmentAddingItem( 
        NLS.nls( "AST.MENU.NODE.ADD_ALLDESCENDANTS" ), model,
        "//" + node.toString(  ) ) );
  }
}


/*
 * $Log: SimpleNodeSubMenu.java,v $
 * Revision 1.3  2004/04/15 18:21:58  tomcopeland
 * Cleaned up imports with new version of IDEA; fixed some deprecated Ant junx
 *
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/23 07:52:16  bgr
 * menus added
 *
 */
