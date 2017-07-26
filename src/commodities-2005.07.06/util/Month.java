/*  x Properly Documented
 */
package commodities.util;

import java.io.*;
import java.util.*;
import commodities.dataaccess.*;

/**
 *  The Month class is a table lookup of the variety of representations that are used
 *  for a given month in the commodities market.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class Month {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

    /** The month table sorted by month name. */
    private static Map tableByName;
    /** The month table sorted by month abbreviation. */
    private static Map tableByAbbrev;
    /** The month table sorted by month number. */
    private static Map tableByNumber;
    /** The month table sorted by month symbol. */
    private static Map tableBySymbol;
    /** A static flag to indicate the table is loaded. */
    private static boolean loaded = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The name of the month. */
    private String  name;
    /** The 3 character abbreviation of the month name. */
    private String  abbrev;
    /** The number of the month (1-12). */
    private int     number = -1;
    /** The commodity symbol for the month. */
    private String  symbol;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a month with the different representations for the month.
     *
     *  @param  name    The name of the month
     *  @param  abbrev  The 3 char abbreviation of the month name
     *  @param  number  The number of the month (1-12)
     *  @param  symbol  The commodity symbol for the month
     */
    public Month(String name, String abbrev, int number, String symbol) {
        this.name   = name.trim();
        this.abbrev = abbrev.trim();
        this.number = number;
        this.symbol = symbol.trim();
    }


/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /** Get the name of the month. */
    public String getName() {
        return name;
    }

    /** Get the 3 character abbreviation of the month. */
    public String getAbbrev() {
        return abbrev;
    }

    /** Get the number (1-12) of the month. */
    public int    getNumber() {
        return number;
    }

    /** Get the commodity symbol of the month. */
    public String getSymbol() {
        return symbol;
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve a month object by using the month symbol.
     *
     *  @param  monthSymbol The commodity symbol for a given month
     *  @return             A month object of the different ways a month is represented.
     */
    public static Month bySymbol(String monthSymbol) {
        loadTable();
        return (Month)tableBySymbol.get(monthSymbol);
    }

    /**
     *  Retrieve a month object by using the month number (1-12).
     *
     *  @param  month   The month number (1-12) for a given month
     *  @return         A month object of the different ways a month is represented.
     */
    public static Month byNumber(int month) {
        loadTable();
        return (Month)tableByNumber.get(new Integer(month));
    }

    /**
     *  Retrieve a month object by using the month 3 character abbreviation.
     *
     *  @param  abbrev  The 3 character abbreviation for a given month
     *  @return         A month object of the different ways a month is represented.
     */
    public static Month byAbbrev(String abbrev) {
        loadTable();
        return (Month)tableByAbbrev.get(abbrev);
    }

    /**
     *  Load the Month table and store it to be keyed by name, abbrev, number,
     *  or symbol.
     */
    private static void loadTable() {
        if (!loaded) {
//System.out.println("Waiting on load");
            synchronized(Month.class) {
                if (!loaded) {
//System.out.println("Loading");
                    tableByName     = new TreeMap();
                    tableByAbbrev   = new TreeMap();
                    tableByNumber   = new TreeMap();
                    tableBySymbol   = new TreeMap();

                    try {
//System.out.println("Loading getting iterator");
                        Iterator it = dataManager.getMonths();
//System.out.println("Loading iterator");
                        while (it.hasNext()) {
//System.out.println("Loading loop");
                            Month month = (Month)it.next();

                            tableByName.put(month.getName(), month);
                            tableByAbbrev.put(month.getAbbrev(), month);
                            tableByNumber.put(new Integer(month.getNumber()), month);
                            tableBySymbol.put(month.getSymbol(), month);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                loaded = true;
            }
        }
    }
}