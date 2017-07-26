/*  _ Properly Documented
 */
/**
 *  The UtilityMenu class controls the actions listed in the Utility menu.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import prototype.commodities.*; // debug only
import prototype.commodities.tests.*;

public class UtilityMenu extends JMenu implements ActionListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String OPTIMIZE_TESTS = "Optimize Tests";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    public UtilityMenu() {
        super("Utility");
Debug.println(this, "UtilityMenu start");
        setupMenu();
Debug.println(this, "UtilityMenu end");
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    private void setupMenu() {
Debug.println(this, "setupMenu start");
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(OPTIMIZE_TESTS);
        menuItem.addActionListener(this);
        add(menuItem);

Debug.println(this, "setupMenu end");
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (OPTIMIZE_TESTS.equals(source.getText())) {
            TestManager.instance().optimizeTests();
        } else {
            String s = "Action event detected."
                       + "\n"
                       + "    Event source: " + source.getText()
                       + " (an instance of " + source.getClass().getName() + ")";
            System.out.println(s + "\n");
        }
    }
}

