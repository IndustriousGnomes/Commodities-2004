/*  _ Properly Documented
 */
/**
 *  This DataLoaderFactory goes through all of the files that are located in
 *  the data folder and loads them into the Daily Price table.
 *
 *  The data folder location is controlled by the folder.commodity_data property in the
 *  dataaccess.properties file.
 *
 *  The factory is controlled by the naming structure of the files in the data folder.
 *
 *  Property Name                   Valid Values
 *  ----------------------------    ------------
 *  folder.commodity_data           File path of where commodity data files are stored before loading to DB.
 *  folder.commodity_data_backup    File path of where commodity data files are stored after loading to DB.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess.dataloader;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Properties;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.dataaccess.*;

public class DataLoaderFactory {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    protected   static  DataManagerInterface dataManager = DataManagerFactory.getInstance();

    protected   static  Properties props = null;
    protected   static  String dataDir = "";
    protected   static  String backupDir = "";

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public DataLoaderFactory () {
        try {
            loadProperties();
            dataDir     = props.getProperty("folder.commodity_data");
            backupDir   = props.getProperty("folder.commodity_data_backup");

            retrieveData();
            loadData();
        } catch (Exception e) {
            System.out.println("Error in loading data");
            e.printStackTrace();
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
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
    public void retrieveData() {
        CommodityCalendar today = (CommodityCalendar)CommodityCalendar.getInstance();
        CommodityCalendar lastDay = null;
        try {
            lastDay = (CommodityCalendar)CommodityCalendar.getInstance();
            lastDay.setTime(DataManagerFactory.getInstance().getLastPriceDateLoaded());
        } catch (Exception e) {
            lastDay = today;
        }

        DataLoaderCBOT.retrievePrices(today, lastDay);
    }

    /**
     *  Loads the data from a file to the database.  The name format of the
     *  file directs how the file will be processed.
     */
    public void loadData() {
        String file[] = getDirectoryList(dataDir);
        for (int i = 0; i < file.length; i++) {
            try {
                if ((file[i].startsWith("bot") && file[i].endsWith(".txt")) ||
                    (file[i].startsWith("cbot") && file[i].endsWith(".txt"))) {
                    DataLoaderCBOT.loadPrices(file[i]);
                }
            } catch (Exception e) {
                System.out.println("File " + file[i] + " could not be loaded.");
                e.printStackTrace();
            }
        }
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Load the dataaccess.properties file as properties.
     */
    /*
     * TODO: The loading of properties needs to be centralized.
     */
    public static void loadProperties() throws IOException {
        if (props == null) {
            props = new Properties();
            FileInputStream in = null;
            try {
                in = new FileInputStream("D:\\JavaProjects\\Commodities\\etc\\dataaccess.properties");
                props.load(in);
            } catch (IOException e) {
                System.out.println("Failed to load properties file: dataaccess.properties");
                throw e;
            } finally {
                try {in.close();} catch (Exception e) {}
            }
        }
    }

    public static void main (String args[]) {
        new DataLoaderFactory();
    }
}