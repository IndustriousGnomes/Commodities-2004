/* _ Review Javadocs */
package commodities.tests.technical;

import java.util.*;

/**
 *  The CriticalPricePoint class is a storage class for data concerning
 *  the critical points of prices.
 *
 *  @author     Jennifer M. Middleton
 *  @since      1.00
 *  @version    1.00
 *  @update     2004.11.19
 */

public class CriticalPricePoint implements Comparable {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Constant value to designate a critical high price point. */
    public static final boolean HIGH = false;
    /** Constant value to designate a critical low price point. */
    public static final boolean LOW = true;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Signifies to which date this critical price point applies. */
    private Date date;
    /** Signifies whether this critical price point is low or high. */
    private boolean isLow;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     * Constructor to assign values to private members.
     *
     * @param   date    the date to which this critical price point applies.
     * @param   isLow   whether or not this critical price point is a low.
     */
    public CriticalPricePoint(Date date, boolean isLow) {
        this.date = date;
        this.isLow = isLow;
    }

/* *************************************** */
/* *** Comparable METHODS              *** */
/* *************************************** */
    /**
     * Allows for the ordering of two or more critical price points.
     *
     * @param   point   the other critical price point to compare to this one
     */
    public int compareTo(Object object) {
        int dateVal = getDate().compareTo(((CriticalPricePoint)object).getDate());
        if (dateVal == 0) {
            return (isLow?-1:1);
        }
        return dateVal;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Returns the value of the private member date
     *
     *  @return     The date for this point.
     */
    public Date getDate() {
        return date;
    }

    /**
     *  Returns the value of the private member isLow.
     *
     *  @return     true if this is a critical low price point or
     *              false if it is a critical high price point.
     */
    public boolean isLow() {
        return isLow;
    }
}