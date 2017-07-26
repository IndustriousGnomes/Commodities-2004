/*  x Properly Documented
 */
package commodities.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.commodity.*;
import commodities.price.*;
import commodities.window.*;

/**
 *  The PriceMenu class controls the actions listed in the Price menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PriceMenu extends JMenu implements ActionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the Add Commodity menu option. */
    private static final String ADD_COMMODITY = "Add Commodity To Tab...";
    /** Text for the Remove Commodity menu option. */
    private static final String REMOVE_COMMODITIES = "Remove Commodities From Tab...";
    /** Text for the Add Commodity Tab menu option. */
    private static final String ADD_COMMODITY_TAB = "Add New Commodities Tab...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the Price menu.
     */
    public PriceMenu() {
        super("Price");
        setupMenu();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Setup the menu with its options.
     */
    private void setupMenu() {
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(ADD_COMMODITY);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(REMOVE_COMMODITIES);
        menuItem.addActionListener(this);
        add(menuItem);

        addSeparator();

        menuItem = new JMenuItem(ADD_COMMODITY_TAB);
        menuItem.addActionListener(this);
        add(menuItem);

    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */
    /**
     *  Handle the selection of menu options.
     *
     *  @param  e   the ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String menuEntry = source.getText();

        if (ADD_COMMODITY.equals(menuEntry)) {
            addCommodityToPricePanelTab();
        } else if (REMOVE_COMMODITIES.equals(menuEntry)) {
            removeCommodityFromPricePanelTab();
        } else if (ADD_COMMODITY_TAB.equals(menuEntry)) {
            addTabToPricePanelTabs();
        }
    }

    /**
     *  Present a dialog box to select the commodity to add to the active price panel tab
     *  and then add the selection.  Only commodities that do not already appear on the
     *  active price panel tab will be displayed in the selection list.
     */
    private void addCommodityToPricePanelTab() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();
        Set choiceSet = new LinkedHashSet();
        for (int i = 0; i < choices.length; i++) {
            choiceSet.add(choices[i]);
        }
        Set usedSet = new HashSet();
        Iterator it = CommodityPriceLocation.byPage(PricingTabbedPane.instance().getSelectedIndex()).iterator();
        while (it.hasNext()) {
            usedSet.add(Commodity.bySymbol(((CommodityPriceLocation)it.next()).getSymbol()).getNameExchange());
        }
        choiceSet.removeAll(usedSet);
        Object[] choose = choiceSet.toArray();

        if (choose.length > 0) {
            String nameExchange = (String)JOptionPane.showInputDialog(
                                CommodityAnalyzerJFrame.instance(),
                                "Choose the commodity to add to \n" +
                                "the current tab in the pricing panel",
                                "Add Commodity To Tab",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                choose,
                                choose[0]);
            if (nameExchange != null) {
                nameExchange = nameExchange.trim();
                if (nameExchange.length() > 0) {
                    PricingTabbedPane.instance().addToCurrentPricingPanel(nameExchange);
                }
            }
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No commodities are available to add to this panel.");
        }
    }

    /**
     *  Present a dialog box to select the commodity to remove from the active price panel tab
     *  and then remove the selection.  Only commodities that appear on the
     *  active price panel tab will be displayed in the selection list.
     */
    private void removeCommodityFromPricePanelTab() {
        Set usedSet = new LinkedHashSet();
        Iterator it = CommodityPriceLocation.byPage(PricingTabbedPane.instance().getSelectedIndex()).iterator();
        while (it.hasNext()) {
            usedSet.add(Commodity.bySymbol(((CommodityPriceLocation)it.next()).getSymbol()).getNameExchange());
        }
        Object[] used = usedSet.toArray();

        if (used.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityAnalyzerJFrame.instance(),
                                                     "Remove Commodities From Tab",
                                                     used,
                                                     "Remove");
            Object s[] = sd.getSelected();
            if (s != null) {
                String selected[] = new String[s.length];
                for (int i = 0; i < s.length; i++) {
                    selected[i] = (String)s[i];
                }
                PricingTabbedPane.instance().removeFromCurrentPricingPanel(selected);
            }
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No commodities are available to \n" +
                                                                 "be removed from this panel.");
        }
    }

    /**
     *  Create a new tab for the price panel with an initial list of commodities to add.
     */
    private void addTabToPricePanelTabs() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();
        if (choices.length > 0) {
            new PricingTabDialog(choices);
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No commodities are available to add to tabs. \n" +
                                                                 "Create a commodity for the tab to hold first.");
        }
    }
}

