/*  _ Properly Documented
 */
/**
 *  The PriceLocation class contains the location in which a commodity is located
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess;

import java.io.*;
import java.util.*;
import prototype.commodities.*; // debug only

public class CommodityPriceLocation {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static DataManagerInterface dataManager = DataManagerFactory.getInstance();

    private static Map tableByPage;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private int     page;               // The page number the commodity is located on
    private int     location;           // The location within a page the commodity is located on
    private String  symbol;             // The commodity symbol
    private String  title;              // The title of the tab

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a month with the different representations for the month.
     *
     *  @param  page        The page number the commodity is located on
     *  @param  location    The location within a page the commodity is located on
     *  @param  symbol      The commodity symbol
     */
    public CommodityPriceLocation(int page, int location, String symbol, String title) {
        this.page       = page;
        this.location   = location;
        this.symbol     = symbol.trim();
        this.title      = title.trim();
    }


/* *************************************** */
/* *** BEAN METHODS (gets & sets)      *** */
/* *************************************** */
    public int    getPage()     { return page; }
    public int    getLocation() { return location; }
    public String getSymbol()   { return symbol; }
    public String getTitle()    { return title; }

    public void   setLocation(int location) { this.location = location; }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    public static Iterator pages() {
        if (tableByPage == null) {
            loadTable();
        }
        return tableByPage.keySet().iterator();
    }

    public static Iterator byPage(int pg) {
        if (tableByPage == null) {
            loadTable();
        }
        return ((LinkedList)tableByPage.get(new Integer(pg))).iterator();
    }

    public static String getTitle(int pg) {
        if (tableByPage == null) {
            loadTable();
        }
        return ((CommodityPriceLocation)((LinkedList)tableByPage.get(new Integer(pg))).get(0)).getTitle();
    }

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

    public static void removePriceLocations(int page, String nameExchange[]) {
        try {
            LinkedList toRemove = new LinkedList();
            LinkedList locs = (LinkedList)tableByPage.get(new Integer(page));
            Iterator it = locs.iterator();
            while (it.hasNext()) {
                CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
                for (int i = 0; i < nameExchange.length; i++) {
                    if (cpl.getSymbol().equals(Commodity.byNameExchange(nameExchange[i]).getSymbol())) {
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