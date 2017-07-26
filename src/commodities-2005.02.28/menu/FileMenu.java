/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import javax.swing.*;

/**
 *  The FileMenu class controls the actions listed in the File menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class FileMenu extends JMenu implements ActionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the Quit menu option. */
    private static final String QUIT = "Quit";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the File menu.
     */
    public FileMenu() {
        super("File");
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

        menuItem = new JMenuItem(QUIT);
        menuItem.addActionListener(this);
        add(menuItem);

    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */
    /**
     *  Handle the selection of menu options.
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (QUIT.equals(source.getText())) {
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

