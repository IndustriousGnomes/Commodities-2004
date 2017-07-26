/*  _ Properly Documented
 */
/**
 *  The LinkedTreeMap class extends the gregorian calendar to override
 *  normal calendar functions for commodity processing.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.util;

import java.util.*;
import prototype.commodities.*; // debug only

public class LinkedTreeMap extends TreeMap {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    LinkedList keyList = null;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Constructs a new, empty map, sorted according to the keys' natural order. All keys inserted
     *  into the map must implement the Comparable interface. Furthermore, all such keys must be mutually
     *  comparable: k1.compareTo(k2) must not throw a ClassCastException for any elements k1 and k2
     *  in the map. If the user attempts to put a key into the map that violates this constraint
     *  (for example, the user attempts to put a string key into a map whose keys are integers),
     *  the put(Object key, Object value) call will throw a ClassCastException.
     */
    public LinkedTreeMap() {
        super();
    }

    /**
     *  Constructs a new, empty map, sorted according to the given comparator. All keys inserted into
     *  the map must be mutually comparable by the given comparator: comparator.compare(k1, k2) must
     *  not throw a ClassCastException for any keys k1 and k2 in the map. If the user attempts to put
     *  a key into the map that violates this constraint, the put(Object key, Object value) call will
     *  throw a ClassCastException.
     *
     *  @param  c   the comparator that will be used to sort this map. A null value indicates that the keys' natural ordering should be used.
     */
    public LinkedTreeMap(Comparator c) {
        super(c);
    }

    /**
     *  Constructs a new map containing the same mappings as the given map, sorted according to the
     *  keys' natural order. All keys inserted into the new map must implement the Comparable interface.
     *  Furthermore, all such keys must be mutually comparable: k1.compareTo(k2) must not throw a
     *  ClassCastException for any elements k1 and k2 in the map. This method runs in n*log(n) time.
     *
     *  @param  m   the map whose mappings are to be placed in this map.
     *  @throws ClassCastException      the keys in t are not Comparable, or are not mutually comparable.
     *  @throws NullPointerException    if the specified map is null.
     */
    public LinkedTreeMap(Map m) {
        super(m);
    }

    /**
     *  Constructs a new map containing the same mappings as the given SortedMap, sorted according to the
     *  same ordering. This method runs in linear time.
     *
     *  @param  m   the sorted map whose mappings are to be placed in this map, and whose comparator is to be used to sort this map.
     *  @throws NullPointerException    if the specified sorted map is null.
     */
    public LinkedTreeMap(SortedMap m) {
        super(m);
    }

/* *************************************** */
/* *** TreeMap METHODS                 *** */
/* *************************************** */

    public void clear() {
        keyList = null;
        super.clear();
    }

    public Object remove(Object key) {
        keyList = null;
        return super.remove(key);
    }


    public Object put(Object key,
                      Object value){
        keyList = null;
        return super.put(key, value);
    }

    public void putAll(Map map){
        keyList = null;
        super.putAll(map);
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    public ListIterator listIterator(int index) {
        if (keyList == null) {
            keyList = new LinkedList();
            Iterator it = keySet().iterator();
            while (it.hasNext()) {
                keyList.add(it.next());
            }
        }
        return keyList.listIterator(index);
    }
}
    