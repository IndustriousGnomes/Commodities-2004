/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.price.*;
import commodities.tests.*;
import commodities.window.*;

/**
 *  The TestOptimizationMenu class determines what tests to run the optimizer for.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.15
 */

public class TestOptimizationMenu extends JMenu implements ActionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Menu entry name to optimize the tests for the selected contract. */
    public static final String OPTIMIZE_SELECTED_CONTRACT   = "Selected Contract";
    /** Menu entry name to optimize the tests for all contracts. */
    public static final String OPTIMIZE_SELECTED_COMMODITY  = "Selected Commodity";
    /** Menu entry name to optimize the tests for all contracts. */
    public static final String OPTIMIZE_ALL_COMMODITIES     = "All Commodities";
    /** Reference to the TestOptimizationMenu instance */
    private static TestOptimizationMenu menu = null;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates the Tests menu.
     */
    public TestOptimizationMenu() {
       super("Optimize Tests");
        setupMenu();
        menu = this;
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */
    /**
     *  Set up the Test menu.
     */
    private void setupMenu() {
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(OPTIMIZE_SELECTED_CONTRACT);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(OPTIMIZE_SELECTED_COMMODITY);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(OPTIMIZE_ALL_COMMODITIES);
        menuItem.addActionListener(this);
        add(menuItem);
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

        if (OPTIMIZE_SELECTED_CONTRACT.equals(menuEntry)) {
            try {
                new TestOptimizer(Contract.getSelectedContract());
            } catch (ContractNotSelectedException ex) {
                JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No contract is currently selected.  Please select a contract first.");
            }
        } else if (OPTIMIZE_SELECTED_COMMODITY.equals(menuEntry)) {
            try {
                new TestOptimizer(Contract.getSelectedContract().getCommodity());
            } catch (ContractNotSelectedException ex) {
                JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No contract in a commodity is currently selected.  Please select a contract first.");
            }
        } else if (OPTIMIZE_ALL_COMMODITIES.equals(menuEntry)) {
            new TestOptimizer();
        }
    }
}
