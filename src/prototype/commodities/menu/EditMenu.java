/*  _ Properly Documented
 */
/**
 *  The EditMenu class controls the actions listed in the Edit menu.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import prototype.commodities.*; // debug only

public class EditMenu extends JMenu implements ActionListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String NEW_ACCOUNT = "New Account...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    public EditMenu() {
        super("Edit");
Debug.println(this, "EditMenu start");
        setupMenu();
Debug.println(this, "EditMenu end");
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    private void setupMenu() {
Debug.println(this, "setupMenu start");
        JMenuItem menuItem = null;

        menuItem = new JMenuItem("something");
        menuItem.addActionListener(this);
        add(menuItem);

Debug.println(this, "setupMenu end");
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + "\n"
                   + "    Event source: " + source.getText()
                   + " (an instance of " + source.getClass().getName() + ")";
        System.out.println(s + "\n");
    }

}

