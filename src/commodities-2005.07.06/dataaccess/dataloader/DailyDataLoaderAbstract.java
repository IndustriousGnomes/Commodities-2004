/* _ Review Javadocs */
package commodities.dataaccess.dataloader;

import java.io.*;
//import java.net.*;
import java.util.*;
//import java.util.Properties;

//import commodities.commodity.*;
//import commodities.contract.*;
import commodities.dataaccess.*;
//import commodities.price.*;
import commodities.util.*;

//import com.util.FormatDate;

/**
 *  The DailyDataLoaderAbstract must be extended by all DailyDataLoader classes
*  and supplies common routines for formatting and saving the pricing data
 *  retrieved from the commodity exchanges.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2005.02.17
 */

public abstract class DailyDataLoaderAbstract {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    protected   static  DataManagerInterface dataManager = DataManagerFactory.instance();


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The date that the data is being retrieved on. */
    protected CommodityCalendar retrievalDate;

    /** The file that the raw data is to be stored in before it has been processed. */
    protected File rawDataFile = new File(DailyDataLoaderFactory.dataDir + "\\rawdata");

    /** The file that the raw data is to be stored in after it has been processed. */
    protected File rawDataLoadedFile;

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieve the daily prices from the commodity exchange and load them
     *  into the database.  This includes saving the raw data files and
     *  creating standard formatted files which can be used to reload
     *  the database if necessary without going through the extraction
     *  process again.
     *
     *  Note: If it is before the exchanges normal data posting time,
     *  the current date will be set to the previous business date
     *  for data retrieval.
     */
    protected void retrievePrices() {
        retrievalDate = getRetrievalDate();
        rawDataLoadedFile = createRawDataFolder(retrievalDate);
        retrieveRawData();
    }

    /**
     *  Create the file folder that the raw data will be stored in.
     *
     *  @param  retrievalDate   The date the retrieval was made on
     */
    protected File createRawDataFolder(CommodityCalendar retrievalDate) {
        String year  = "" + retrievalDate.get(Calendar.YEAR);
        String month = "0" + retrievalDate.get(Calendar.MONTH);
        String date  = "0" + retrievalDate.get(Calendar.DATE);

        String subDirName = getExchangeId() +
                            (year.substring(2)) +
                            (month.substring(month.length() - 2)) +
                            (date.substring(date.length() - 2));

        File newDir = new File(DailyDataLoaderFactory.dataDir + "\\rawdata\\" + subDirName);
        newDir.mkdirs();
        return newDir;
    }

/* *************************************** */
/* *** ABSTRACT METHODS                *** */
/* *************************************** */
    /**
     *  Retrieve the exchange abreviation identifier.
     *
     *  @return The exchange identifier.
     */
    abstract protected String getExchangeId();

    /**
     *  The date that should be used in the retrieval of data from
     *  an exchange.  The date should be based on the normal time that
     *  data is posted at the exchange for distribution.
     *
     *  @return The date to use for data retrieval
     */
    abstract protected CommodityCalendar getRetrievalDate();

    /**
     *  Retrieve the raw data from the exchange and store it
     *  in the raw data folder.
     */
    abstract protected void retrieveRawData();

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}