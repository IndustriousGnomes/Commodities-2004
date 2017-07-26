/* _ Review Javadocs */
package commodities.dataaccess.dataloader;

import java.io.*;
import java.net.*;
import java.util.*;
//import java.util.Properties;

import commodities.commodity.*;
import commodities.contract.*;
//import commodities.dataaccess.*;
//import commodities.price.*;
import commodities.util.*;

//import com.util.FormatDate;

/**
 *  The DailyDataLoaderCBOT retrieves and processes the raw data files
 *  that originate from the Chicago Board of Trade (CBOT) web site.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2005.02.17
 */

public class DailyDataLoaderCBOT2 extends DailyDataLoaderAbstract {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The abreviated exchange identifier. */
    private static final String EXCHANGE_ID = "CME";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */


/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */


/* *************************************** */
/* *** DailyDataLoaderAbstract METHODS *** */
/* *************************************** */
    /**
     *  Retrieve the exchange abreviation identifier.
     *
     *  @return The exchange identifier.
     */
    protected String getExchangeId() {
        return EXCHANGE_ID;
    }

    /**
     *  The date that should be used in the retrieval of data from
     *  an exchange.  The date should be based on the normal time that
     *  data is posted at the exchange for distribution.
     *
     *  Note:  CBOT posts data daily by 3pm
     *
     *  @return The date to use for data retrieval
     */
    protected CommodityCalendar getRetrievalDate() {
        CommodityCalendar date = (CommodityCalendar)CommodityCalendar.getInstance();
        if (date.get(Calendar.HOUR_OF_DAY) < 15) {         // Is it before 3pm
            date.addWeekDays(-1);
        }
        date.clearTime();
System.out.println("CBOT Retrieval date " + date.getTime());
        return date;
    }

    /**
     *  Retrieve the raw data from the exchange and store it
     *  in the raw data folder.
     */
    protected void retrieveRawData() {
        Map commodityNames = Commodity.getExchangeMap(EXCHANGE_ID);

        Iterator it = commodityNames.values().iterator();
        while (it.hasNext()) {
            Commodity commodity = (Commodity)it.next();
            String commoditySymbol = commodity.getSymbol();
            String databaseSymbol = commodity.getDatabaseCommoditySymbol();
            System.out.println("Retrieving commodity = " + commodity.getName() + "  DB Symbol = " + commodity.getDatabaseCommoditySymbol());

            int monthsMask = commodity.getTradeMonthMask();

            int monthMask = Commodity.JAN;
            int monthNumber = 0;
            while (monthMask > 0) {
                if ((monthsMask & monthMask) > 0) {
                    int     retrievalYear = retrievalDate.get(Calendar.YEAR);
                    int     retrievalMonth = retrievalDate.get(Calendar.MONTH);
                    boolean currentMonth = (monthNumber == retrievalMonth);
                    String  monthSymbol = Month.byNumber(monthNumber).getSymbol();
                    Contract contract = null;

                    // retrieve current year
                    if (monthNumber >= retrievalMonth) {
                        contract = commodity.getContract(retrievalYear + monthSymbol);
                        if ((contract == null) || (!retrievalDate.getTime().equals(contract.getCurrentDate()))) {
                            System.out.print(".");
                            retrieveFile(commoditySymbol, databaseSymbol, monthSymbol, retrievalYear);
                        } else {
                            System.out.println("-");
                        }
                    }

                    // retrieve one year out
                    contract = commodity.getContract((retrievalYear + 1) + monthSymbol);
                    if ((contract == null) || (!retrievalDate.getTime().equals(contract.getCurrentDate()))) {
                        System.out.print(".");
                        retrieveFile(commoditySymbol, databaseSymbol, monthSymbol, (retrievalYear + 1));
                    } else {
                        System.out.println("-");
                    }

                    // retrieve two years out
                    if (monthNumber <= retrievalMonth) {
                        contract = commodity.getContract((retrievalYear + 2) + monthSymbol);
                        if ((contract == null) || (!retrievalDate.getTime().equals(contract.getCurrentDate()))) {
                            System.out.print(".");
                            retrieveFile(commoditySymbol, databaseSymbol, monthSymbol, (retrievalYear + 2));
                        } else {
                            System.out.println("-");
                        }
                    }
                }
                monthMask >>= 1;
                monthNumber++;
            }
            System.out.println();
        }



        System.out.println("Retrieval of CBOT commodities is complete");
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieve the data file from the Internet and save it.
     *
     *  @param  commoditySymbol The symbol for the commodity to retrieve
     *  @param  databaseSymbol  The database symbol for the commodity to retrieve
     *  @param  monthSymbol     The symbol of the commodity month
     *  @param  yearInt         The year of data to retrieve
     */
    private void retrieveFile(String commoditySymbol, String databaseSymbol, String monthSymbol, int yearInt) {
        BufferedReader in = null;
        PrintWriter rawOut = null;

        String year = "" + yearInt;
        String month = monthSymbol + year.substring(year.length() - 2);
        String urlName = "http://www.cbot.com/cbot/pub/hist_download/1,3333," + databaseSymbol + month + ",00.html";

        String fileName = rawDataFile.getName() +
                          commoditySymbol + month + ".txt";
/*
        String fileName = "cbot04-" + (("" + today.get(Calendar.YEAR)).substring(2)) +
                                    (todayMonth.substring(todayMonth.length() - 2)) +
                                    (todayDate.substring(todayDate.length() - 2))
                                    + commoditySymbol + month + ".txt";
*/
        try {
            URL url = new URL(urlName);
            URLConnection conn = url.openConnection();

            String line = null;
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            rawOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(rawDataFile, fileName))));

            while ((line = in.readLine()) != null) {
                rawOut.println(line);
            }
        } catch (MalformedURLException e) {
            System.out.println("DataLoaderCBOT.MalformedURLException url = " + urlName);
            // Dont get any prices
        } catch (UnknownHostException e) {
            System.out.println("Host unavailable");
            // Host is unavailable.  Dont get any prices.
        } catch (FileNotFoundException e) {
            System.out.println();
            System.out.println("Data file not found at: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Continuing...");
            // Dont get any prices
        } finally {
            try { in.close(); } catch (Exception e) {}
            try { rawOut.close(); } catch (Exception e) {}
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */


/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}