/*  x Properly Documented
 */
package commodities.dataaccess.database;

import java.io.*;
import java.sql.*;
import java.util.*;
import commodities.util.*;
import com.util.ObjectPool;

/**
 *  DBConnectionPool maintains a pool of connections to a database. A properties
 *  file keyed in the properties manager by the name of 'database' should appear
 *  in the execution directory.  If the file does not appear or does not contain
 *  the needed properties, default properties will be used instead.
 *
 *  The following properties and their defaults are used:
 *      database.jdbcdriver      jdbc:odbc:LocalServer
 *      database.url             sun.jdbc.odbc.JdbcOdbcDriver
 *      database.username        <null>
 *      database.password        <null>
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class DBConnectionPool extends ObjectPool {

/* *************************************** */
/* *** CLASS CONSTANTS                 *** */
/* *************************************** */
    /** The property file Id for the PropertyManager */
    private static final String DATABASE_PROPERTIES = "database";
    /** The default URL for the database connection is jdbc:odbc:LocalServer". */
    private static final String DEFAULT_URL     = "jdbc:odbc:LocalServer";
    /** The default JDBC driver is sun.jdbc.odbc.JdbcOdbcDriver". */
    private static final String DEFAULT_DRIVER  = "sun.jdbc.odbc.JdbcOdbcDriver";
    /** The default user id is <null>. */
    private static final String DEFAULT_USERID  = null;
    /** The default password is <null>. */
    private static final String DEFAULT_PASSWORD= null;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The URL used to connect to the database. */
    private static String url;
    /** The JDBC driver used to connect to the database. */
    private static String jdbcDriver;
    /** The user name used to connect to the database. */
    private static String user;
    /** The user password used to connect to the database. */
    private static String password;

    /** The properties used for the database connection. */
    private static Properties properties;

    /** Singleton instance of the DBConnectionPool */
    private static DBConnectionPool dbcm;
    /** Map of prepared statements. */
    private static HashMap statements = new HashMap();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a DBConnectionPool.
     *
     *  @throws SQLException    a problem accessing the database table
     */
    private DBConnectionPool() throws Exception {
        super();
//System.out.println("*********************************");
//System.out.println("** Construct DBConnectionPool  **");
//System.out.println("*********************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieves a connection to the database from the pool.
     *
     *  @return     the connection retrieved
     *  @throws SQLException A connection to the database cannot be established.
     */
    public Connection retrieveConnection() throws SQLException {
        try {
            return (Connection)retrieveObject();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  Returns a connection to the pool.
     *
     *  @param  conn    the connection to return
     */
    public void returnConnection(Connection conn) {
        returnObject(conn);
    }

    /**
     *  Closes a connection to the pool.
     *
     *  @param  conn    the connection to return
     */
    public void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
        }

        destroyObject(conn);
    }

/* *************************************** */
/* *** ObjectPool METHODS              *** */
/* *************************************** */
    /**
     *  Initializes the variables to be used in connecting to the database.
     */
    protected void init() {
        if (properties == null) {
            properties = PropertyManager.instance().getProperties(DATABASE_PROPERTIES);
        }

        jdbcDriver = properties.getProperty("database.jdbcdriver",DEFAULT_DRIVER);
        url = properties.getProperty("database.url",DEFAULT_URL);
        user = properties.getProperty("database.username",DEFAULT_USERID);
        password = properties.getProperty("database.password",DEFAULT_PASSWORD);
    }

    /**
     *  Create an connection to the database as an object for the pool.
     *
     *  @return     the connection created
     *  @throws Exception   any problem in creating the connection
     */
    protected Object createObject() throws SQLException {
        Connection connect = null;
        try {
            Class.forName(jdbcDriver);
        }
        catch(ClassNotFoundException e) {
            throw new RuntimeException(e.toString());
        }

        if (user == null) {
            connect = DriverManager.getConnection(url);
        } else {
            connect = DriverManager.getConnection(url, user, password);
        }

        return connect;
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
    *  Returns a signleton instance of the Database Connection Manager.
     *
     *  @return     the instance of DBConnectionPool
     *  @throws SQLException    problem connecting to the database
     */
    public static DBConnectionPool instance() throws SQLException {
        if (dbcm == null) {
            synchronized(DBConnectionPool.class) {
                if (dbcm == null) {
                    try {
                        dbcm = new DBConnectionPool();
                    } catch (SQLException e) {
                        throw e;
                    } catch (Exception e) {
                        // should not happen
                        e.printStackTrace();
                    }
                }
            }
        }
        return dbcm;
    }
}
