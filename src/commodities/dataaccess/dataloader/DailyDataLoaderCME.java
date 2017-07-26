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
 *  The DailyDataLoaderCME retrieves and processes the raw data files
 *  that originate from the Chicago Merchentile (CME) web site.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2005.02.17
 */

public class DailyDataLoaderCME extends DailyDataLoaderAbstract {
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
     *  Note:  CME posts data daily by 1am the next day
     *
     *  @return The date to use for data retrieval
     */
    protected CommodityCalendar getRetrievalDate() {
        CommodityCalendar date = (CommodityCalendar)CommodityCalendar.getInstance();
        date.addWeekDays(-1);
        if (date.get(Calendar.HOUR_OF_DAY) < 1) {         // Is it before 1am
            date.addWeekDays(-1);
        }
        date.clearTime();
System.out.println("CME Retrieval date " + date.getTime());
        return date;
    }

    /**
     *  Retrieve the raw data from the exchange and store it
     *  in the raw data folder.
     */
    protected void retrieveRawData() {
        Map commodityNames = Commodity.getExchangeMap(EXCHANGE_ID);

        CommodityCalendar newestDate = null;
/*
        Iterator it = commodityNames.values().iterator();
        while (it.hasNext()) {
            Commodity commodity = (Commodity)it.next();
            String commoditySymbol = commodity.getSymbol();
            String databaseSymbol = commodity.getDatabaseCommoditySymbol();

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

                    // check current year
                    if (monthNumber >= retrievalMonth) {
                        contract = commodity.getContract(retrievalYear + monthSymbol);
                        if ((contract == null) || (newestDate.before(contract.getCurrentDate()))) {
                            newestDate.setTime(contract.getCurrentDate());
                            if (newestDate.equals(retrievalDate)) {
                                System.out.println("Retrieval of CME commodities is already current.");
                                return;
                            }
                        }
                    }

                    // check one year out
                    contract = commodity.getContract((retrievalYear + 1) + monthSymbol);
                    if ((contract == null) || (newestDate.before(contract.getCurrentDate()))) {
                        newestDate.setTime(contract.getCurrentDate());
                        if (newestDate.equals(retrievalDate)) {
                            System.out.println("Retrieval of CME commodities is already current.");
                            return;
                        }
                    }

                    // check two years out
                    if (monthNumber <= retrievalMonth) {
                        contract = commodity.getContract((retrievalYear + 2) + monthSymbol);
                        if ((contract == null) || (newestDate.before(contract.getCurrentDate()))) {
                            newestDate.setTime(contract.getCurrentDate());
                            if (newestDate.equals(retrievalDate)) {
                                System.out.println("Retrieval of CME commodities is already current.");
                                return;
                            }
                        }
                    }
                }
                monthMask >>= 1;
                monthNumber++;
            }
        }
*/
        if (newestDate == null) {
            newestDate = retrievalDate;
        }
//System.out.println("DailyDataLoaderCME.retrieveRawData() - newestDate = " + newestDate);

//        while (!newestDate.after(retrievalDate)) {
            retrieveFile(newestDate);
//            newestDate.addWeekDays(1);
//        }

        System.out.println("Retrieval of CME commodities is complete");
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
     *  @param  date    The date of the file to retrieve.
     */
    private void retrieveFile(CommodityCalendar date) {
System.out.println("DailyDataLoaderCME.retrieveFile() - start");
//        BufferedInputStream in = null;
        InputStream in = null;
        FileOutputStream rawOut = null;

        String year  = "" + date.get(Calendar.YEAR);
        String month = "0" + (date.get(Calendar.MONTH) + 1);
        String day   = "0" + date.get(Calendar.DATE);
System.out.println("DailyDataLoaderCME.retrieveRawData() - newestDate(formatted) = " + year + month + day);

//        String urlName = "http://www.cme.com/ftp.wrap/bulletin/DailyBulletin_html_" + year + month + day + "67.zip";
        String urlName = "ftp://ftp.cme.com/pub/bulletin/DailyBulletin_html_" + year + month + day + "67.zip";
System.out.println("DailyDataLoaderCME.retrieveRawData() - URL = " + urlName);

        String fileName = "CME_" + year + month + day + ".zip";

        try {
System.out.println("DailyDataLoaderCME.retrieveRawData() - #01");
            URL url = new URL(urlName);
System.out.println("DailyDataLoaderCME.retrieveRawData() - #02");
            URLConnection conn = url.openConnection();

System.out.println("DailyDataLoaderCME.retrieveRawData() - #03");
//            in = new BufferedInputStream(conn.getInputStream());
            in = conn.getInputStream();
System.out.println("DailyDataLoaderCME.retrieveRawData() - #04");
            rawOut = new FileOutputStream(new File(rawDataFile, fileName));
System.out.println("DailyDataLoaderCME.retrieveRawData() - #05");

            int c;
int count = 0;
System.out.println("DailyDataLoaderCME.retrieveRawData() - START transfer");
            while ((c = in.read()) != -1) {
                rawOut.write(c);
if (++count > 100) {
    System.out.print(".");
    count = 0;
}
            }
System.out.println("DailyDataLoaderCME.retrieveRawData() - END transfer");

        } catch (MalformedURLException e) {
            System.out.println("DataLoaderCBOT.MalformedURLException url = " + urlName);
            // Dont get any prices
        } catch (UnknownHostException e) {
            System.out.println("Host unavailable");
            // Host is unavailable.  Dont get any prices.
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found at: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Continuing...");
            // Dont get any prices
        } finally {
            try { in.close(); } catch (Exception e) {}
            try { rawOut.close(); } catch (Exception e) {}
        }
System.out.println("DailyDataLoaderCME.retrieveFile() - end");
    }


/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}