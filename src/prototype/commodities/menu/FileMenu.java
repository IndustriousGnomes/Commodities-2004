/*  _ Properly Documented
 */
/**
 *  The FileMenu class controls the actions listed in the File menu.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import prototype.commodities.*; // debug only

public class FileMenu extends JMenu implements ActionListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String NEW_ACCOUNT = "New Account...";
    private static final String QUIT = "Quit";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    public FileMenu() {
        super("File");
Debug.println(this, "FileMenu start");
        setupMenu();
Debug.println(this, "FileMenu end");
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    private void setupMenu() {
Debug.println(this, "setupMenu start");
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(NEW_ACCOUNT);
        menuItem.addActionListener(this);
        add(menuItem);

        addSeparator();

        menuItem = new JMenuItem(QUIT);
        menuItem.addActionListener(this);
        add(menuItem);

Debug.println(this, "setupMenu end");
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (NEW_ACCOUNT.equals(source.getText())) {
            System.out.println("New Account Menu Selection Worked");
        } else if (QUIT.equals(source.getText())) {
            System.exit(0);
        } else {
            String s = "Action event detected."
                       + "\n"
                       + "    Event source: " + source.getText()
                       + " (an instance of " + source.getClass().getName() + ")";
            System.out.println(s + "\n");
        }
    }

}

