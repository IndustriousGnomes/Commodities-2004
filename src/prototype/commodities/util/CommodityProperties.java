/*  x Properly Documented
 */
package prototype.commodities.util;

import java.io.*;
import java.util.Properties;
import prototype.commodities.*; // debug only

/**
 *  The CommodityProperties class is a singleton point for retrieving all of
 *  the properties used in the Commodities system.
 */
public class CommodityProperties {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The file name of the properties file. */
    private static final String PROPERTY_FILE = "D:\\JavaProjects\\Commodities\\etc\\commodities.properties";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The single instance of the properties file */
    private static Properties props;

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Return the single instance of properties.
     *  @return     singleton properties file
     */
    public static Properties instance() {
        if (props == null) {
            loadProperties();
        }
        return props;
    }

    /**
     *  Loads a property file.
     */
    private static void loadProperties() {
        FileInputStream in = null;
        try {
            props = new Properties();
            in = new FileInputStream(PROPERTY_FILE);
            props.load(in);
        } catch (IOException e) {
            // file could not be processed.
        } finally {
            try {in.close();} catch (Exception e) {}
        }
    }
}