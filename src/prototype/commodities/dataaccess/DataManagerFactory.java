/*  _ Properly Documented
 */
/** DataManagerFactory
 *  This class is a factory to decide which implementation
 *  of the data manager is being used.
 */
package prototype.commodities.dataaccess;

import prototype.commodities.dataaccess.database.*;
import prototype.commodities.*; // debug only

public class DataManagerFactory {
    private static boolean DEBUG = false;

    public static final String DATAMANAGER = "DB";

    public static DataManagerInterface getInstance() {
        if ("DB".equals(DATAMANAGER)) {
            return DBDataManager.getInstance();
        }
        return null;
    }
}
