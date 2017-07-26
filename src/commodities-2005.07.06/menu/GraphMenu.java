/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.graph.*;
import commodities.window.*;

/**
 *  The GraphMenu class controls the actions listed in the Graph menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class GraphMenu extends JMenu implements ItemListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the Normal Size menu option. */
    private static final String NORMAL = "Normal Size";
    /** Text for the Quit menu option. */
    private static final String ENLARGED = "Enlarged Size";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** List of JCheckBoxMenuItem in menu */
    private LinkedList menuList = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the Graph menu.
     */
    public GraphMenu() {
        super("Graph");
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

        menuItem = new JCheckBoxMenuItem(NORMAL, true);
        menuItem.addItemListener(this);
        add(menuItem);
        menuList.add(menuItem);

        menuItem = new JCheckBoxMenuItem(ENLARGED, false);
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
            if (NORMAL.equals(cbEntry)) {
                ((Graph)CommodityAnalyzerJFrame.instance().getGraphPanel()).setXScale(Graph.DEFAULT_X_SCALE);
                ((TestGraphManager)CommodityAnalyzerJFrame.instance().getTestGraphPanel()).setXScale(Graph.DEFAULT_X_SCALE);
            } else if (ENLARGED.equals(cbEntry)) {
                ((Graph)CommodityAnalyzerJFrame.instance().getGraphPanel()).setXScale(Graph.DEFAULT_X_SCALE + 2);
                ((TestGraphManager)CommodityAnalyzerJFrame.instance().getTestGraphPanel()).setXScale(Graph.DEFAULT_X_SCALE + 2);
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

