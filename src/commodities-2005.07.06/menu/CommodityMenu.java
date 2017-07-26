/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.util.*;
import commodities.window.*;

/**
 *  The CommodityMenu class controls the actions listed in the Commodity menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class CommodityMenu extends JMenu implements ActionListener {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the New Commodity menu option. */
    private static final String NEW_COMMODITY   = "New Commodity...";
    /** Text for the Edit Commodity menu option. */
    private static final String EDIT_COMMODITY  = "Edit Commodity...";
    /** Text for the Edit Contract menu option. */
    private static final String EDIT_CONTRACT   = "Edit Contract...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the File menu.
     */
    public CommodityMenu() {
        super("Commodity");
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

        if (NEW_COMMODITY.equals(source.getText())) {
            new CommodityDialog();
        } else if (EDIT_COMMODITY.equals(source.getText())) {
            editCommodity();
        } else if (EDIT_CONTRACT.equals(source.getText())) {
            editContract();
        } else {
            String s = "Action event detected."
                       + "\n"
                       + "    Event source: " + source.getText()
                       + " (an instance of " + source.getClass().getName() + ")";
            System.out.println(s + "\n");
        }
    }

    /**
     *  Present a dialog box to select from the available commodities and
     *  then edit the selected commodity.
     */
    private void editCommodity() {
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();

        if (choices.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityAnalyzerJFrame.instance(),
                                                     "Edit Commodities",
                                                     choices,
                                                     "Edit",
                                                     ListSelectionModel.SINGLE_SELECTION);
            String selected = null;
            if (sd.getSelected() != null) {
                selected = (String)sd.getSelected()[0];
            }

            if (selected != null) {
                new CommodityDialog(Commodity.byNameExchange(selected));
            }
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No commodities are available to Edit");
        }
    }

    /**
     *  Present a dialog box to select from the available commodities,
     *  then select from the available contracts, and edit the selected contract.
     */
    private void editContract() {
        String commoditySelected = null;
        String contractSelected = null;
        Map nameMap = Commodity.getNameMap();
        Object[] choices = nameMap.keySet().toArray();

        if (choices.length > 0) {
            SelectionDialog sd = new SelectionDialog(CommodityAnalyzerJFrame.instance(), "Edit Contract", choices, "Edit", ListSelectionModel.SINGLE_SELECTION);
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
                    sd = new SelectionDialog(CommodityAnalyzerJFrame.instance(), "Edit Contract for " + commoditySelected, monthChoices.toArray(), "Edit", ListSelectionModel.SINGLE_SELECTION);
                    if (sd.getSelected() != null) {
                        contractSelected = (String)sd.getSelected()[0];
                    }
                }

                if (contractSelected != null) {
                    new ContractDialog(Commodity.byNameExchange(commoditySelected).getContract("20" + contractSelected.substring(4) + Month.byAbbrev(contractSelected.substring(0,3)).getSymbol()));
                }
            }
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No contracts are available to Edit");
        }
    }
}

