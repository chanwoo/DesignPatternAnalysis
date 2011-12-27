package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.util.viewer.gui.MainFrame;


/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: Viewer.java,v 1.1 2003/09/23 20:32:42 tomcopeland Exp $
 */
public class Viewer
{
  /**
   * starts the viewer
   *
   * @param args arguments
   */
  public static void main( String[] args )
  {
    new MainFrame(  );
  }
}


/*
 * $Log: Viewer.java,v $
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
