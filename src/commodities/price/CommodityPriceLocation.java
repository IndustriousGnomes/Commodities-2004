/*  x Properly Documented
 */
package commodities.price;

import java.io.*;
import java.util.*;

import commodities.commodity.*;
import commodities.dataaccess.*;

/**
 *  The PriceLocation class contains the location in which a commodity is located
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityPriceLocation {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** Table of locations by page. */
    private static Map tableByPage;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The page number the commodity is located on. */
    private int     page;
    /** The location within a page the commodity is located on. */
    private int     location;
    /** The commodity symbol. */
    private String  symbol;
    /** The title of the tab */
    private String  title;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a CommodityPriceLocation.
     *
     *  @param  page        The page number the commodity is located on
     *  @param  location    The location within a page the commodity is located on
     *  @param  symbol      The commodity symbol
     *  @param  title       The title of the tab
     */
    public CommodityPriceLocation(int page, int location, String symbol, String title) {
        this.page       = page;
        this.location   = location;
        this.symbol     = symbol.trim();
        this.title      = title.trim();
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the page number.
     *  @return     the page
     */
    public int getPage() {
        return page;
    }

    /**
     *  Get the location.
     *  @return     the location
     */
    public int getLocation() {
        return location;
    }

    /**
     *  Get the commodity symbol.
     *  @return     the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     *  Get the tab title.
     *  @return     the title
     */
    public String getTitle() {
        return title;
    }

    /**
     *  Set the location.
     *  @param  location    the location
     */
    public void setLocation(int location) {
        this.location = location;
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Get the collection of page numbers.
     *  @return     collection of pages
     */
    public static Collection pages() {
        if (tableByPage == null) {
            loadTable();
        }
        return tableByPage.keySet();
    }

    /**
     *  Get an iterator of contracts on a page.
     *
     *  @param  pg  page number
     *  @return     collection of contracts names
     */
    public static Collection byPage(int pg) {
        if (tableByPage == null) {
            loadTable();
        }
        return (LinkedList)tableByPage.get(new Integer(pg));
    }

    /**
     *  The title of the tab.
     *
     *  @param  pg  page number
     *  @return     the title
     */
    public static String getTitle(int pg) {
        if (tableByPage == null) {
            loadTable();
        }
        return ((CommodityPriceLocation)((LinkedList)tableByPage.get(new Integer(pg))).get(0)).getTitle();
    }

    /**
     *  Add a price location.
     *
     *  @param  cpl commodity price location
     */
    public static void addPriceLocation(CommodityPriceLocation cpl) {
        try {
            dataManager.putCommodityPageLocation(cpl);
            LinkedList locs = (LinkedList)tableByPage.get(new Integer(cpl.getPage()));
            if (locs == null) {
                locs = new LinkedList();
                tableByPage.put(new Integer(cpl.getPage()), locs);
            }

            locs.add(cpl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Remove one or more price location from a page identified by commodity name and exchange.
     *
     *  @param  page    page to remove commodities from
     *  @param  name    name of the commodity
     */
    public static void removePriceLocations(int page, String name[]) {
        try {
            LinkedList toRemove = new LinkedList();
            LinkedList locs = (LinkedList)tableByPage.get(new Integer(page));
            Iterator it = locs.iterator();
            while (it.hasNext()) {
                CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
                for (int i = 0; i < name.length; i++) {
                    if (cpl.getSymbol().equals(Commodity.byNameExchange(name[i]).getSymbol())) {
                        toRemove.add(cpl);
                        break;
                    }
                }
            }
            locs.removeAll(toRemove);
            dataManager.removeCommodityPriceLocations(page, locs);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Load the Commodity Price Location table and store it to be keyed by
     *  page and location.
     */
    private static void loadTable() {
        tableByPage = new TreeMap();
        LinkedList locs = new LinkedList();

        try {
            Iterator it = dataManager.getCommodityPageLoc();
            int pg = 0;
            tableByPage.put(new Integer(pg), locs);
            while (it.hasNext()) {
                CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
                if (cpl.getPage() != pg) {
                    pg = cpl.getPage();
                    locs = new LinkedList();
                    tableByPage.put(new Integer(pg), locs);
                }
                locs.add(cpl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}