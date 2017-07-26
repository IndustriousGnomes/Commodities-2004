/*  _ Properly Documented
 */
/** DBConnectionPool
 *  This class maintains a pool of connections to a database. A properties
 *  file by the name of databases.properties should appear in the execution
 *  directory.  If the file does not appear or does not contain the
 *  needed properties, default properties will be used instead.
 *
 *  The following properties and their defaults are used:
 *      database.jdbcdriver      jdbc:odbc:LocalServer
 *      database.url             sun.jdbc.odbc.JdbcOdbcDriver
 *      database.username        <null>
 *      database.password        <null>
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess.database;

import java.io.*;
import java.sql.*;
import java.util.*;
import prototype.commodities.dataaccess.pool.*;
import prototype.commodities.*; // debug only

public class DBConnectionPool extends ObjectPool {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS CONSTANTS                 *** */
/* *************************************** */
    private static final String DEFAULT_URL     = "jdbc:odbc:LocalServer";
    private static final String DEFAULT_DRIVER  = "sun.jdbc.odbc.JdbcOdbcDriver";
    private static final String DEFAULT_USERID  = null;
    private static final String DEFAULT_PASSWORD= null;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static String url;
    private static String jdbcDriver;
    private static String user;
    private static String pass;

    private static Properties props;

    private static DBConnectionPool dbcm;
    private static HashMap statements = new HashMap();
    private static HashMap preparedConnections = new HashMap();

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    private DBConnectionPool() throws Exception {
        super();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    protected void init() {
        if (props == null) {
            try {
                loadProperties();
            } catch (IOException e) {
                // If properties can not be loaded, defaults will be used.
                props = new Properties();
            }
        }

        jdbcDriver = props.getProperty("database.jdbcdriver",DEFAULT_DRIVER);
        url = props.getProperty("database.url",DEFAULT_URL);
        user = props.getProperty("database.username",DEFAULT_USERID);
        pass = props.getProperty("database.password",DEFAULT_PASSWORD);
    }


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
            connect = DriverManager.getConnection(url, user, pass);
        }
        return connect;
    }

    /**
     *  Retrieves a connection to the database.
     *
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
     */
    public void returnConnection(Connection conn) {
        returnObject(conn);
    }

    public boolean containsPreparedStatement(String name) {
        return statements.containsKey(name);
    }

    public void addPreparedStatement(String name, String sql) {
        if (!containsPreparedStatement(name)) {
            statements.put(name, sql);
            LinkedList connections = new LinkedList();
            preparedConnections.put(name, connections);
            Iterator it = cyclePool();
            while (it.hasNext()) {
                try {
                    Connection connect = (Connection)it.next();
                    connections.add(connect.prepareStatement(sql));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Returns a signle instance of the Database Connection Manager.
     */
    public static DBConnectionPool getInstance() throws SQLException {
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

    /**
     *  Load the dataaccess.properties file as properties.
     */
    private static void loadProperties() throws IOException {
        FileInputStream in = null;
        try {
            props = new Properties();
            in = new FileInputStream("D:\\JavaProjects\\Commodities\\etc\\dataaccess.properties");
            props.load(in);
        } finally {
            try {in.close();} catch (Exception e) {}
        }
    }
}
