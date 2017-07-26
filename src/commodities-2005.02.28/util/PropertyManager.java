/*  x Properly Documented
 */
package commodities.util;

import java.io.*;
import java.util.*;

/**
 *  The PropertyManager centrally manages property files.
 *
 *  The properties file manager.properties contains entries
 *  for each property file that includes the name of the
 *  property file and the location of that property file.
 *
 *  To retrieve a property from on of the files, use the following: <br>
 *  PropertyManager.instance().getProperties(PROPERTIES_FILE).getProperty(PROPERTY)
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PropertyManager {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The path of the properties file used to manage all the other property file locations. */
    private static final String MANAGER_FILE = "etc/manager.properties";
    /** Singleton instance of the property manager */
    private static PropertyManager manager;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Manager properties */
    private Properties managerProps = new Properties();
    /** Map of properties that have been loaded. */
    private Map propertiesMap = new HashMap();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Construct an instance of the PropertyManager and load the
     *  property manager file.
     */
    public PropertyManager() {
//System.out.println("********************************");
//System.out.println("** Construct PropertyManager  **");
//System.out.println("********************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
        managerProps = loadProperties(MANAGER_FILE);
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Get the property file based on the property file id in the manager.properties
     *  file.
     *
     *  @param  propertyFileId  the property name in the manager.properties file
     *                          of the property file to retreive
     *  @return     the requested property file
     */
    public Properties getProperties(String propertyFileId) {
        String path = null;
        if (!propertiesMap.containsKey(propertyFileId)) {
            if ((path = managerProps.getProperty(propertyFileId)) == null) {
                throw new IllegalArgumentException(propertyFileId);
            } else {
                propertiesMap.put(propertyFileId, loadProperties(path));
            }
        }
        return (Properties)propertiesMap.get(propertyFileId);
    }

    /**
     *  Load a properties file based on the file path.
     *
     *  @param  path    file path
     *  @return         properties from file
     */
    private Properties loadProperties(String path) {
        FileInputStream in = null;
        Properties props = new Properties();
        try {
            in = new FileInputStream(path);
            props.load(in);
            return props;
        } catch (IOException e) {
            System.out.println("Property file not found at: " + path);
            return props;
        } finally {
            try {in.close();} catch (Exception e) {}
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve the singleton instance of the PropertyManager.
     *
     *  @return     the instance of PropertyManager
     */
    public static PropertyManager instance() {
        if (manager == null) {
            synchronized (PropertyManager.class) {
                if (manager == null) {
                   manager = new PropertyManager();
                }
            }
        }
        return manager;
    }
}