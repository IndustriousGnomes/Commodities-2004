/*  _ Properly Documented
 */
/**
 *  The TestMenu class controls which tests are displayed for a commodity.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.tests.*;
import prototype.commodities.price.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.*; // debug only

public class TestMenu extends JMenu implements ActionListener, ItemListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Menu entry name to select all recommended tests for the currently selected commodity */
    public static final String SELECT_RECOMMENDED_TESTS = "Select Recommended Tests";
    /** Menu entry name to deselect all tests */
    public static final String DESELECT_ALL_TESTS       = "Deselect All Tests";
    /** Reference to the TestMenu instance */
    private static TestMenu testMenu = null;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** List of JCheckBoxMenuItem in menu */
    private LinkedList testList = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates the Tests menu.
     */
    public TestMenu() {
        super("Tests");
        setupMenu();
        testMenu = this;
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    /**
     *  Set up the Test menu.
     */
    private void setupMenu() {
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(SELECT_RECOMMENDED_TESTS);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(DESELECT_ALL_TESTS);
        menuItem.addActionListener(this);
        add(menuItem);

        addSeparator();

        addTestMenuItems();
    }

    /**
     *  Retrieve the names of all the tests located in the ... file and display
     *  their names in this menu.
     */
    private void addTestMenuItems() {
        JCheckBoxMenuItem menuItem = null;

        String testNames[] = TestManager.instance().getTestNames();
        for (int i = 0; i < testNames.length; i++) {
            menuItem = new JCheckBoxMenuItem(testNames[i]);
            menuItem.addItemListener(this);
            add(menuItem);
            testList.add(menuItem);
        }
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)e.getSource();
        String menuEntry = source.getText();

        if (SELECT_RECOMMENDED_TESTS.equals(menuEntry)) {
            selectRecommendedMenuCheckboxes();
        } else if (DESELECT_ALL_TESTS.equals(menuEntry)) {
            resetMenuCheckboxes();
        }
    }

/* *************************************** */
/* *** ItemListener INTERFACE          *** */
/* *************************************** */

    public void itemStateChanged(ItemEvent e) {
        JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getItem();
        String cbEntry = cb.getText();

        if (e.getStateChange() == ItemEvent.SELECTED) {
            TestManager.instance().addGraph(cbEntry, PricingTabbedPane.getInstance().getSelectedContract());
        } else {
            TestManager.instance().removeGraph(cbEntry);
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    public static void resetMenuCheckboxes() {
        Iterator it = testMenu.testList.iterator();
        while (it.hasNext()) {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem)it.next();
            if (cb.isSelected()) {
                cb.doClick();
            }
        }
    }

    public static void selectRecommendedMenuCheckboxes() {
        resetMenuCheckboxes();
        Iterator it = testMenu.testList.iterator();
        while (it.hasNext()) {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem)it.next();
            if (!cb.isSelected()) {
                cb.doClick();
            }
        }
    }
}
