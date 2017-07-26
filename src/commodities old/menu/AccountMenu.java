/*  x Properly Documented
 */
package commodities.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import commodities.account.*;
import commodities.order.*;
import commodities.window.*;

/**
 *  The AccountMenu class controls the actions listed in the Account menu.
 *
*  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class AccountMenu extends JMenu implements ActionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Text for the New Account menu option. */
    private static final String NEW_ACCOUNT         = "New Account...";
    /** Text for the Open Account menu option. */
    private static final String OPEN_ACCOUNT        = "Open Account...";
    /** Text for the Adjust Account menu option. */
    private static final String ADJUST_ACCOUNT      = "Adjust Account...";
    /** Text for the Inactivate Account menu option. */
    private static final String INACTIVATE_ACCOUNT  = "Inactivate Account...";
    /** Text for the Reactivate Account menu option. */
    private static final String REACTIVATE_ACCOUNT  = "Reactivate Account...";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the Account menu.
     */
    public AccountMenu() {
        super("Account");
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

        menuItem = new JMenuItem(NEW_ACCOUNT);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(OPEN_ACCOUNT);
        menuItem.addActionListener(this);
        add(menuItem);

        menuItem = new JMenuItem(ADJUST_ACCOUNT);
        menuItem.addActionListener(this);
        add(menuItem);

        addSeparator();

        menuItem = new JMenuItem(INACTIVATE_ACCOUNT);
        menuItem.addActionListener(this);
        menuItem.setEnabled(false);
        add(menuItem);

        menuItem = new JMenuItem(REACTIVATE_ACCOUNT);
        menuItem.addActionListener(this);
        menuItem.setEnabled(false);
        add(menuItem);
    }


/* *************************************** */
/* *** ActionListener INTERFACE        *** */
/* *************************************** */
    /**
     *  Handle the selection of menu options.
     */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());

        if (NEW_ACCOUNT.equals(source.getText())) {
            new AccountDialog();
            Account account = selectAccount();
            if (account != null) {
                CommodityAnalyzerJFrame.instance().setOrderPanel(new ContractPanel(account));
            } else {
                JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Account NOT selected.");
            }
        } else if (OPEN_ACCOUNT.equals(source.getText())) {
            Account account = selectAccount();
            if (account != null) {
                CommodityAnalyzerJFrame.instance().setOrderPanel(new ContractPanel(account));
            } else {
                JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Account NOT selected.");
            }
        } else if (ADJUST_ACCOUNT.equals(source.getText())) {
            Account account = selectAccount();
            if (account != null) {
                new AccountDialog(account);
            } else {
                JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No account selected.");
            }
        } else if (INACTIVATE_ACCOUNT.equals(source.getText())) {
            System.out.println(INACTIVATE_ACCOUNT);
        } else if (REACTIVATE_ACCOUNT.equals(source.getText())) {
            System.out.println(REACTIVATE_ACCOUNT);
        } else {
            String s = "Action event detected."
                       + "\n"
                       + "    Event source: " + source.getText()
                       + " (an instance of " + source.getClass().getName() + ")";
            System.out.println(s + "\n");
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Present a dialog box to select from the available accounts.
     *
     *  @return The selected account.
     */
    public static Account selectAccount() {
        Map acctMap = Account.getAccountMap();
        Object[] choices = acctMap.keySet().toArray();
        Object[] choiceLabels = new Object[choices.length];
        for (int i = 0; i < choiceLabels.length; i++) {
            Account acct = (Account)acctMap.get(choices[i]);
            choiceLabels[i] = acct.getLastName() + ", " + acct.getFirstName();
        }

        if (choices.length > 0) {
            if (choices.length == 1) {  // No need for a selection screen.
                return (Account)acctMap.get(choices[0]);
            }

            SelectionDialog sd = new SelectionDialog(CommodityAnalyzerJFrame.instance(),
                                                     "Edit Accounts",
                                                     choiceLabels,
                                                     "Edit",
                                                     ListSelectionModel.SINGLE_SELECTION);
            String selected = null;
            if (sd.getSelected() != null) {
                selected = (String)sd.getSelected()[0];
            }

            if (selected != null) {
                for (int i = 0; i < choiceLabels.length; i++) {
                    if (selected.equals(choiceLabels[i])) {
                        return (Account)acctMap.get(choices[i]);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "No accounts are available to Edit");
        }
        return null;
    }
}

