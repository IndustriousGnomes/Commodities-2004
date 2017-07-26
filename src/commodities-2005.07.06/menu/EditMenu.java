/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import javax.swing.*;

/**
 *  The EditMenu class controls the actions listed in the Edit menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class EditMenu extends JMenu implements ActionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the Edit menu.
     */
    public EditMenu() {
        super("Edit");
        setupMenu();
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */
    /**
     *  Setup the menu with its options.
     */
    private void setupMenu() {
        JMenuItem menuItem = null;

    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */
    /**
     *  Handle the selection of menu options.
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + "\n"
                   + "    Event source: " + source.getText()
                   + " (an instance of " + source.getClass().getName() + ")";
        System.out.println(s + "\n");
    }

}

