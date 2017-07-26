/* _ Review Javadocs */
package commodities.order;

import java.awt.*;
import java.text.*;
import javax.swing.*;

import commodities.account.*;
import commodities.event.*;

/**
 *  The AccountValuePanel is a representation of how much funds are in the
 *  account as cash, how much is reserved for margins, and what the account is
 *  actually worth.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2004.12.21
 */

public class AccountValuePanel extends JPanel implements PriceListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The account that is being displayed. */
    private Account account;

    /** The currency number format */
    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    /** Label for account value. */
    private JLabel acctValueLabel;
    /** Label for used margin. */
    private JLabel marginLabel;
    /** Label for available funds. */
    private JLabel availableLabel;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create an AccountValuePanel.
     */
    public AccountValuePanel(Account account) {
        this.account = account;

        marginLabel = new JLabel(currency.format(0));

        if (account == null) {
            acctValueLabel = new JLabel(currency.format(0));
            availableLabel = new JLabel(currency.format(0));

        } else {
            acctValueLabel = new JLabel(currency.format(account.getAccountValue()));
            availableLabel = new JLabel(currency.format(account.getAccountValue() - 0.0));
        }

        setupGUI();
    }

/* *************************************** */
/* *** JPanel METHODS                  *** */
/* *************************************** */


/* *************************************** */
/* *** PriceListener METHODS           *** */
/* *************************************** */
    public void changeDate(PriceEvent e) {}

    public void changeOpenPrice(PriceEvent e) {}

    public void changeHighPrice(PriceEvent e) {}

    public void changeLowPrice(PriceEvent e) {}

    public void changeClosePrice(PriceEvent e) {
        acctValueLabel.setText(currency.format(account.getAccountValue()));
        availableLabel.setText(currency.format(account.getAccountValue() - 0.0));
    }

    public void changeOpenInterest(PriceEvent e) {}

    public void changeVolume(PriceEvent e) {}

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the GUI for the panel.
     */
    private void setupGUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.YELLOW);

        add(Box.createHorizontalGlue());

        add(new JLabel("Value:  "));
        add(acctValueLabel);

        add(Box.createHorizontalStrut(30));

        add(new JLabel("Margin:  "));
        add(marginLabel);

        add(Box.createHorizontalStrut(30));

        add(new JLabel("Available:  "));
        add(availableLabel);

        add(Box.createHorizontalGlue());
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */


/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}