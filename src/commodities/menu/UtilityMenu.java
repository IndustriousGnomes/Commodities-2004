/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.price.*;
import commodities.window.*;

/**
 *  The UtilityMenu class controls the actions listed in the Utility menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class UtilityMenu extends JMenu implements ItemListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the Quit menu option. */
    private static final String PRICES = "Prices";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** List of JCheckBoxMenuItem in menu */
    private LinkedList menuList = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the Utility menu.
     */
    public UtilityMenu() {
        super("Utility");
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

        menuItem = new JCheckBoxMenuItem(PRICES, true);
        menuItem.addItemListener(this);
        add(menuItem);
        menuList.add(menuItem);

    }


/* *************************************** */
/* *** ItemListener INTERFACE          *** */
/* *************************************** */
    /**
     *  Handle the changing of a checkbox check in the menu.
     *
     *  @param  e   the ItemEvent
     */
    public void itemStateChanged(ItemEvent e) {
        JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getItem();
        String cbEntry = cb.getText();
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (PRICES.equals(cbEntry)) {
                CommodityAnalyzerJFrame.instance().setUtilityPanel(PricingTabbedPane.instance());
            }

            Iterator it = menuList.iterator();
            while (it.hasNext()) {
                JCheckBoxMenuItem otherCb = (JCheckBoxMenuItem)it.next();
                if (otherCb.isSelected() && !otherCb.equals(cb)) {
                    otherCb.doClick();
                }
            }
        } else {
            // Item not selected
        }
    }
}

