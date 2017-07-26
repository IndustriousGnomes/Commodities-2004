/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.*;
import commodities.contract.*;
import commodities.price.*;
import commodities.tests.*;
import commodities.window.*;

/**
 *  The TestMenu class controls which tests are displayed for a commodity.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class TestMenu extends JMenu implements ActionListener, ItemListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the test manager. */
//    protected static TestManager testManager = TestManager.instance();
//    protected static TestManager testManager = null;

    /** Menu entry name to select all recommended tests for the currently selected commodity */
    public static final String SELECT_RECOMMENDED_TESTS = "Select Recommended Tests";
    /** Menu entry name to deselect all tests */
    public static final String DESELECT_ALL_TESTS       = "Deselect All Tests";
    /** Menu entry name to display short term tests */
    public static final String SHORT_TERM_TESTS         = "Display Short Term Tests";
    /** Menu entry name to display medium term tests */
    public static final String MEDIUM_TERM_TESTS        = "Display Medium Term Tests";
    /** Menu entry name to display long term tests */
    public static final String LONG_TERM_TESTS          = "Display Long Term Tests";

    /** Reference to the TestMenu instance */
    private static TestMenu testMenu = null;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Short Term Tests JCheckBoxMenuItem */
    private JCheckBoxMenuItem cbShortTerm;
    /** Medium Term Tests JCheckBoxMenuItem */
    private JCheckBoxMenuItem cbMediumTerm;
    /** Long Term Tests JCheckBoxMenuItem */
    private JCheckBoxMenuItem cbLongTerm;
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

        cbShortTerm = new JCheckBoxMenuItem(SHORT_TERM_TESTS);
        cbShortTerm.setSelected(true);
        cbShortTerm.addItemListener(this);
        add(cbShortTerm);

        cbMediumTerm = new JCheckBoxMenuItem(MEDIUM_TERM_TESTS);
        cbMediumTerm.setSelected(true);
        cbMediumTerm.addItemListener(this);
        add(cbMediumTerm);

        cbLongTerm = new JCheckBoxMenuItem(LONG_TERM_TESTS);
        cbLongTerm.setSelected(true);
        cbLongTerm.addItemListener(this);
        add(cbLongTerm);

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
    /**
    *  Handle a selection in the menu.
     *
     *  @param  e   the ItemEvent
     */
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
    /**
     *  Handle the changing of a checkbox check in the menu.
     *
     *  @param  e   the ItemEvent
     */
    public void itemStateChanged(ItemEvent e) {
        JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getItem();
        String cbEntry = cb.getText();

        if (SHORT_TERM_TESTS.equals(cbEntry)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TestManager.instance().graphShortTermTests(true);
            } else {
                TestManager.instance().graphShortTermTests(false);
            }
            refreshMenuCheckboxes();
        } else if (MEDIUM_TERM_TESTS.equals(cbEntry)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TestManager.instance().graphMediumTermTests(true);
            } else {
                TestManager.instance().graphMediumTermTests(false);
            }
            refreshMenuCheckboxes();
        } else if (LONG_TERM_TESTS.equals(cbEntry)) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TestManager.instance().graphLongTermTests(true);
            } else {
                TestManager.instance().graphLongTermTests(false);
            }
            refreshMenuCheckboxes();
        } else {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    TestManager.instance().addGraph(cbEntry, Contract.getSelectedContract());
                } catch (ContractNotSelectedException ex) {
                    cb.setSelected(false);
                    JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No contract is currently selected.  Please select a contract first.");
                }
            } else {
                TestManager.instance().removeGraph(cbEntry);
            }
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Turn off each of the check boxes in the menu by
     *  automatically clicking on them if they are currently
     *  on so that they process the deselection.
     */
    public static void resetMenuCheckboxes() {
        Iterator it = testMenu.testList.iterator();
        while (it.hasNext()) {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem)it.next();
            if (cb.isSelected()) {
                cb.doClick();
            }
        }
    }

    /**
     *  Turn off and one each of the check boxes in the menu by
     *  automatically clicking on them if they are currently
     *  on so that they process the deselection and reselection.
     */
    public static void refreshMenuCheckboxes() {
        Iterator it = testMenu.testList.iterator();
        while (it.hasNext()) {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem)it.next();
            if (cb.isSelected()) {
                cb.doClick();
                cb.doClick();
            }
        }
    }

    /**
     *  Turn on each of the check boxes corresponding to a
     *  test that the test manager indicates is a recommended
     *  test for the currently selected commodity.
     */
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
