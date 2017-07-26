/*  _ Properly Documented
 */
/**
 *  The PriceMenu class controls the actions listed in the Price menu.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.price.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.*; // debug only

public class PriceMenu extends JMenu implements ActionListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String ADD_COMMODITY = "Add Commodity To Tab...";
    private static final String REMOVE_COMMODITIES = "Remove Commodities From Tab...";
    private static final String ADD_COMMODITY_TAB = "Add New Commodities Tab...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    public PriceMenu() {
        super("Price");
Debug.println(DEBUG, this, "PriceMenu start");
        setupMenu();
Debug.println(DEBUG, this, "PriceMenu end");
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    private void setupMenu() {
Debug.println(DEBUG, this, "setupMenu start");
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

Debug.println(DEBUG, this, "setupMenu end");
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

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

    private void addCommodityToPricePanelTab() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();
        Set choiceSet = new LinkedHashSet();
        for (int i = 0; i < choices.length; i++) {
            choiceSet.add(choices[i]);
        }
        Set usedSet = new HashSet();
        Iterator it = CommodityPriceLocation.byPage(PricingTabbedPane.getInstance().getSelectedIndex());
        while (it.hasNext()) {
            usedSet.add(Commodity.bySymbol(((CommodityPriceLocation)it.next()).getSymbol()).getNameExchange());
        }
        choiceSet.removeAll(usedSet);
        Object[] choose = choiceSet.toArray();

        if (choose.length > 0) {
            String nameExchange = (String)JOptionPane.showInputDialog(
                                CommodityJFrame.frame,
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
                    PricingTabbedPane.getInstance().addToCurrentPricingPanel(nameExchange);
                }
            }
        } else {
            JOptionPane.showMessageDialog(CommodityJFrame.frame, "No commodities are available to add to this panel.");
        }
    }

    private void removeCommodityFromPricePanelTab() {
        Set usedSet = new LinkedHashSet();
        Iterator it = CommodityPriceLocation.byPage(PricingTabbedPane.getInstance().getSelectedIndex());
        while (it.hasNext()) {
            usedSet.add(Commodity.bySymbol(((CommodityPriceLocation)it.next()).getSymbol()).getNameExchange());
        }
        Object[] used = usedSet.toArray();

        if (used.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityJFrame.frame, "Remove Commodities From Tab", used, "Remove");
            Object s[] = sd.getSelected();
            if (s != null) {
                String selected[] = new String[s.length];
                for (int i = 0; i < s.length; i++) {
                    selected[i] = (String)s[i];
                }
                PricingTabbedPane.getInstance().removeFromCurrentPricingPanel(selected);
            }
        } else {
            JOptionPane.showMessageDialog(CommodityJFrame.frame, "No commodities are available to \n" +
                                                                 "be removed from this panel.");
        }
    }

    private void addTabToPricePanelTabs() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();
        if (choices.length > 0) {
            new PricingTabDialog(choices);
        } else {
            JOptionPane.showMessageDialog(CommodityJFrame.frame, "No commodities are available to add to tabs. \n" +
                                                                 "Create a commodity for the tab to hold first.");
        }
    }
}

