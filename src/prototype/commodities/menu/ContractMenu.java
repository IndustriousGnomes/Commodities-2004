/*  _ Properly Documented
 */
/**
 *  The ContractMenu class controls the actions listed in the Contract menu.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import prototype.commodities.*; // debug only
import prototype.commodities.dataaccess.*;

public class ContractMenu extends JMenu implements ActionListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String NEW_COMMODITY   = "New Commodity...";
    private static final String EDIT_COMMODITY  = "Edit Commodity...";
    private static final String EDIT_CONTRACT   = "Edit Contract...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    public ContractMenu() {
        super("Contract");
Debug.println(this, "ContractMenu start");
        setupMenu();
Debug.println(this, "ContractMenu end");
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    private void setupMenu() {
Debug.println(this, "setupMenu start");
        JMenuItem menuItem = null;

        menuItem = new JMenuItem(NEW_COMMODITY);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(EDIT_COMMODITY);
        menuItem.addActionListener(this);
        add(menuItem);

        addSeparator();

        menuItem = new JMenuItem(EDIT_CONTRACT);
        menuItem.addActionListener(this);
        add(menuItem);

Debug.println(this, "setupMenu end");
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String menuEntry = source.getText();

        if (NEW_COMMODITY.equals(menuEntry)) {
            newCommodity();
        } else if (EDIT_COMMODITY.equals(menuEntry)) {
            editCommodity();
        } else if (EDIT_CONTRACT.equals(menuEntry)) {
            editContract();
        }
    }

    private void newCommodity() {
        new CommodityDialog();
    }

    private void editCommodity() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();

        if (choices.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityJFrame.frame, "Edit Commodities", choices, "Edit", ListSelectionModel.SINGLE_SELECTION);
            String selected = null;
            if (sd.getSelected() != null) {
                selected = (String)sd.getSelected()[0];
            }

            if (selected != null) {
                new CommodityDialog(Commodity.byNameExchange(selected));
            }
        } else {
            JOptionPane.showMessageDialog(CommodityJFrame.frame, "No commodities are available to Edit");
        }
    }

    private void editContract() {
        String commoditySelected = null;
        String contractSelected = null;
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();

        if (choices.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityJFrame.frame, "Edit Contract", choices, "Edit", ListSelectionModel.SINGLE_SELECTION);
            if (sd.getSelected() != null) {
                commoditySelected = (String)sd.getSelected()[0];
            }

            if (commoditySelected != null) {
                Iterator it = Commodity.byNameExchange(commoditySelected).getContracts();
                LinkedHashSet monthChoices = new LinkedHashSet();
                while (it.hasNext()) {
                    Contract contract = (Contract)it.next();
                    monthChoices.add(contract.getMonthFormatted());
                }
                if (monthChoices.size() > 0) {
                    sd = new SelectionDialog(CommodityJFrame.frame, "Edit Contract for " + commoditySelected, monthChoices.toArray(), "Edit", ListSelectionModel.SINGLE_SELECTION);
                    if (sd.getSelected() != null) {
                        contractSelected = (String)sd.getSelected()[0];
                    }
                }

                if (contractSelected != null) {
                    new ContractDialog(Commodity.byNameExchange(commoditySelected).getContract("20" + contractSelected.substring(4) + Month.byAbbrev(contractSelected.substring(0,3)).getSymbol()));
                }
            }
        } else {
            JOptionPane.showMessageDialog(CommodityJFrame.frame, "No contracts are available to Edit");
        }
    }
}

