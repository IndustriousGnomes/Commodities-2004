/*  _ Properly Documented
 */
/** ObjectPool
 *  This class maintains a pool of generic objects.  Extending classes
 *  should override methods on how to manipulate the actual objects in the pool.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess.pool;

import java.util.*;
import prototype.commodities.*; // debug only

public class ObjectPool {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS CONSTANTS                 *** */
/* *************************************** */
    private static final int    DEFAULT_POOL_MIN= 3;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private LinkedList entirePool    = new LinkedList();
    private LinkedList availablePool = new LinkedList();
    private LinkedList inUsePool     = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public ObjectPool() throws Exception {
        init();

        for (int i = 0; i < DEFAULT_POOL_MIN; i++) {
            Object obj = createObject();
            entirePool.add(obj);
            availablePool.add(obj);
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Perform any initialization setup that needs to occur.
     */
    protected void init() {}

    /**
     *  Retrieve an object from the pool
     */
    public Object retrieveObject() throws Exception {
        if (availablePool.isEmpty()) {
            Object o = createObject();
            entirePool.add(o);
            availablePool.add(o);
        }
        Object obj = availablePool.removeFirst();
        inUsePool.add(obj);
        return obj;
    }

    protected Object createObject() throws Exception{
        return new Object();
    }

    public void returnObject(Object obj) {
        if (inUsePool.remove(obj)) {
            if (availablePool.size() < DEFAULT_POOL_MIN) {
                availablePool.add(obj);
            } else {
                entirePool.remove(obj);
            }
        }
    }

    protected void destroyObject(Object obj) {
        entirePool.remove(obj);
        availablePool.remove(obj);
        inUsePool.remove(obj);
    }

    protected Iterator cyclePool() {
        return entirePool.iterator();
    }

    protected boolean inUse(Object obj) {
        return inUsePool.contains(obj);
    }
}    