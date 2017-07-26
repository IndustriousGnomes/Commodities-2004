/*  x Properly Documented
 */
package commodities.order;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;

import commodities.account.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.event.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The ContractPanel contains the table of contracts that are
 *  outstanding.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class ContractPanel extends JPanel {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The singleton instance of the DataManagerFactory. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The panel holding the JTable the orders are displayed in. */
    private ContractOrderTablePanel tablePanel;
    /** The preferred size of this JPanel */
    private Dimension preferredSize = new Dimension(0,0);

    /** The account that owns the orders. */
    Account account;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the panel and setup the table to hold the contracts.
     */
    public ContractPanel() {
        this(null);
    }

    /**
     *  Create the panel and setup the table to hold the contracts.
     *
     *  @param  account The account currently being tracked.
     */
    public ContractPanel(Account account) {
        this.account = account;
        setupGUI();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the display of the commodity order table.
     */
    private void setupGUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

        tablePanel = new ContractOrderTablePanel(account);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.YELLOW);

        JLabel title = null;
        if (account == null) {
            title = new JLabel("Contract Management");
        } else {
            title = new JLabel("Contract Management - " + account.getFirstName() + " " + account.getLastName());
        }
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.BLACK);



        AccountValuePanel accountPanel = new AccountValuePanel(account);


        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(accountPanel, BorderLayout.CENTER);

        add(BorderLayout.NORTH, titlePanel);
        add(BorderLayout.CENTER, tablePanel);
    }

    /**
     *  Gets the preferred size of the ContractPanel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    /**
     *  Sets the preferred size of the ContractPanel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }
}
