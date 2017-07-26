/*  x Properly Documented
 */
package commodities.dataaccess.dataloader;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Properties;

import commodities.dataaccess.*;
import commodities.util.*;

/**
 *  This DailyDataLoaderFactory goes through all of the files that are located in
 *  the data folder and loads them into the Daily Price table.
 *
 *  The factory is controlled by the naming structure of the files in the data folder.
 *
 *  Property Name                   Valid Values
 *  ----------------------------    ------------
 *  folder.commodity_data           File path of where commodity data files are stored before loading to DB.
 *  folder.commodity_data_backup    File path of where commodity data files are stored after loading to DB.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class DailyDataLoaderFactory extends Thread {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    protected   static  DataManagerInterface dataManager = DataManagerFactory.instance();

    /** Properties file identifier for the properties manager. */
    private static final String DATA_ACCESS_PROPERTIES_FILE = "dataaccess";

    /** Property for identifying the price data file. */
    private static final String DATA_FILE = "folder.commodity_data";
    /** Property for identifying the price data file backup. */
    private static final String DATA_BACKUP_FILE = "folder.commodity_data_backup";
    /** The directory name where the pricing data is stored before loading into the system. */
    protected   static  String dataDir = "";
    /** The directory name where the pricing data is backed up after loading into the system. */
    protected   static  String backupDir = "";

    /** Singleton instance of the DailyDataLoaderFactory */
    private     static  DailyDataLoaderFactory  factory;


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the daily data loader factory.
     */
    private DailyDataLoaderFactory () {
//System.out.println("***************************************");
//System.out.println("** Construct DailyDataLoaderFactory  **");
//System.out.println("***************************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
        try {
            dataDir     = PropertyManager.instance().getProperties(DATA_ACCESS_PROPERTIES_FILE).getProperty(DATA_FILE);
            backupDir   = PropertyManager.instance().getProperties(DATA_ACCESS_PROPERTIES_FILE).getProperty(DATA_BACKUP_FILE);
        } catch (Exception e) {
            System.out.println("Error in loading data");
            e.printStackTrace();
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieve the prices from the internet.
     */
    public void retrievePrices() {
        setPriority(7);
        start();
    }

    /**
     *  Retrieve the prices from the internet in a non-thread fashion so the
     *  program will shut down afterwards.  The value for noThreads is irrelevant
     *  and is simply used to create an alternate signature.
     */
    public void retrievePrices(boolean noThreads) {
        run();
    }

    /**
     *  Retrieve a filtered listing of file names from a directory.
     *
     *  @param  dirName     The name of the directory
     */
    public String[] getDirectoryList(String dirName) {
        String dir[] = new File(dirName + "\\.").list(new FilenameFilter() {
                public boolean accept(File dir, String s) {
                    boolean good = false;
                    if ((s.startsWith("bot") && s.endsWith(".txt")) ||
                        (s.startsWith("cbot") && s.endsWith(".txt"))) {
                        good = true;
                    }
                    return good;
                }
            });
        if (dir == null) {
            return new String[0];
        } else {
            return dir;
        }
    }

    /**
     *  Requests that prices get retrieved from their original sources by all the loaders
     *  from the maximum date in the database to today.
     */
    private void retrieveData() {
        DailyDataLoaderCBOT.retrievePrices();
//        DailyDataLoaderCME cme = new DailyDataLoaderCME();
//        cme.retrievePrices();
    }

    /**
     *  Loads the data from a file to the database.  The name format of the
     *  file directs how the file will be processed.
     */
    private void loadData() {
        String file[] = getDirectoryList(dataDir);
        for (int i = 0; i < file.length; i++) {
            try {
                if ((file[i].startsWith("bot") && file[i].endsWith(".txt")) ||
                    (file[i].startsWith("cbot") && file[i].endsWith(".txt"))) {
                    DailyDataLoaderCBOT.loadPrices(file[i]);
                }
            } catch (Exception e) {
                System.out.println("File " + file[i] + " could not be loaded.");
                e.printStackTrace();
            }
        }
    }

/* *************************************** */
/* *** Thread METHODS                  *** */
/* *************************************** */
    public void run() {
        retrieveData();
        loadData();
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve the daily data loader.
     *
     *  @return     the active data loader for daily prices
     */
    public static DailyDataLoaderFactory instance() {
        if (factory == null) {
            synchronized(DailyDataLoaderFactory.class) {
                if (factory == null) {
                    factory = new DailyDataLoaderFactory();
                }
            }
        }
        return factory;
    }
}
