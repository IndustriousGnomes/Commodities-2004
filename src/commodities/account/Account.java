/* _ Review Javadocs */
package commodities.account;

import java.io.*;
import java.util.*;

import commodities.dataaccess.*;

/**
 *  The Account class contains the information for an account.
 *
 *  @author     J.R. Titko
 *  @since      1.00
 *  @version    1.00
 */

public class Account {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

    /** The account table sorted by account number. */
    private static Map accountsById;

    /** The accounts are loaded. */
    private static boolean loaded = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Account number. */
    private int acctNumber;
    /** First name of the account owner. */
    private String firstName;
    /** Last name of the account owner. */
    private String lastName;
    /** Initial capital in the account. */
    private double  capital;
    /** Current account value. */
    private double acctValue;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create an account with the given first and last name
     *  of the account owner and the initial capital in the account.
     *
     *  @param  firstName   The first name of the account owner.
     *  @param  lastName    The last name of the account owner.
     *  @param  capital     The initial capital in the account.
     */
    public Account(String firstName, String lastName, double capital) {
         this(0, firstName, lastName, capital);
    }

    /**
     *  Create an account with the given account number, first and last name
     *  of the account owner and the initial capital in the account.
     *
     *  @param  acctNumber  The account number.
     *  @param  firstName   The first name of the account owner.
     *  @param  lastName    The last name of the account owner.
     *  @param  capital     The initial capital in the account.
     */
    public Account(int acctNumber, String firstName, String lastName, double capital) {
        if (acctNumber == 0) {
            try {
                this.acctNumber = (int)(dataManager.getLastAccountIndex() + 1);
                accountsById.put(new Integer(this.acctNumber), this);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            this.acctNumber = acctNumber;
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.capital = capital;
        this.acctValue = capital;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the account number.
     *  @return The account number.
     */
    public int getAccountNumber() {
        return acctNumber;
    }

    /**
     *  Get the first name of the account owner.
     *  @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  Get the last name of the account owner.
     *  @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *  Get the capital in the account
     *  @return The capital.
     */
    public double getCapital() {
        return capital;
    }

    /**
     *  Get the value of the account.
     *  @return The account value.
     */
    public double getAccountValue() {
        return acctValue;
    }

    /**
     *  Set the first name of the account owner.
     *  @param  firstName   The first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *  Set the last name of the account owner.
     *  @param  lastName    The last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *  Set the capital in the account.
     *  @param  capital     The capital.
     */
    public void setCapital(double capital) {
        this.capital = capital;
    }


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  The description of a method goes here.
     *
     *  @param  <param>             <param description>
     *  @throws <exception type>    <exception description>
     *  @return                     <description of return value>
     */


/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve a map keyed on the names of the available accounts with Account
     *  objects as associated values.
     *
     *  @return     a map containing all Accounts keyed by account number.
     */
   public static Map getAccountMap() {
        loadTable();
        return accountsById;
    }

    /**
     *  Load the Account table and store it to be keyed.
     */
    private static void loadTable() {
        if (!loaded) {
            synchronized(Account.class) {
                if (!loaded) {
                    accountsById    = new TreeMap();

                    try {
                        Iterator it = dataManager.getAccounts();
                        while (it.hasNext()) {
                            Account account = (Account)it.next();
                            accountsById.put(new Integer(account.getAccountNumber()), account);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                loaded = true;
            }
        }
    }

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}