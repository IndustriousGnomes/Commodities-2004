/*  x Properly Documented
 */
package commodities.dataaccess;

import commodities.dataaccess.database.*;
import commodities.util.*;

/**
 *  The DataManagerFactory decides which implementation
 *  of the data manager is being used.
 *
 *  The name of the datamanager is located in the
 *  properties file identified as dataaccess.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class DataManagerFactory {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Properties file identifier for the properties manager. */
    private static final String DATA_ACCESS_PROPERTIES_FILE = "dataaccess";
    /** Property for identifying the properties manager. */
    private static final String DATA_MANAGER = "datamanager";
    /** SQL Server data manager */
    public static final String SQLSERVER = "SQLServer";
    /** Access data manager */
    public static final String ACCESS = "Access";
    /** Access data manager */
    public static final String MYSQL = "MySQL";

    /** The datamanager to use. */
   private static String dataManager;

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve the data manager.
     *
     *  @return     the active data manager
     */
    public static DataManagerInterface instance() {
        if (dataManager == null) {
            synchronized (DataManagerFactory.class) {
                if (dataManager == null) {
//System.out.println("***********************************");
//System.out.println("** Construct DataManagerFactory  **");
//System.out.println("***********************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
                   dataManager = PropertyManager.instance().getProperties(DATA_ACCESS_PROPERTIES_FILE).getProperty(DATA_MANAGER);
                }
            }
        }
        if (SQLSERVER.equals(dataManager)) {
//            return SQLServerDataManager.instance();
            System.out.println("SQLServerDataManager database manager needs to be updated before being reused.");
        } else if (ACCESS.equals(dataManager)) {
//            return AccessDataManager.instance();
            System.out.println("AccessDataManager database manager needs to be updated before being reused.");
        } else if (MYSQL.equals(dataManager)) {
            return MySQLDataManager.instance();
        }

        return null;
    }
}
