/* _ Review Javadocs */
package commodities.tests.technical;

import java.util.*;

import commodities.contract.*;
import commodities.price.*;

/**
 *  The CriticalPricePointAnalyzer class determines the critical high
 *  and low points for the prices of a contract. A critical high price
 *  point is defined as a point where a day's high is greater than the
 *  previous day's high and greater than or equal to the following day's
 *  high. A critical low price point is defined as a point where a day's
 *  low is less than the previous day's low and less than or equal to the
 *  following day's low.
 *
 *  @author     Jennifer M. Middleton
 *  @since      1.00
 *  @version    1.00
 *  @update     2004.11.20
 */

public class CriticalPricePointAnalyzer {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */

    /** Singleton instance of CriticalPricePointAnalyzer */
    private static CriticalPricePointAnalyzer cppa = null;

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Processes prices in a given contract in order by date. The
     *  critical high prices points are returned.
     *
     *  @param  contract            the contract to be analyzed
     *  @return                     a TreeSet of the critical high
     *                              price points
     */
    public Collection getHighPricePoints(Contract contract) {
        TreeSet highPoints = new TreeSet();
        Prices previous = null;
        Prices current = null;
        Prices next = null;
        CriticalPricePoint point = null;

        ListIterator it = contract.getPriceDates(Contract.ASCENDING);
        if (it.hasNext()) {
            previous = contract.getPrices((Date)it.next());
        } else {
            return highPoints;
        }
        if (it.hasNext()) {
            current = contract.getPrices((Date)it.next());
        } else {
            return highPoints;
        }
        if (it.hasNext()) {
            next = contract.getPrices((Date)it.next());
        }
        else {
            return highPoints;
        }

        while (it.hasNext()) {
            if ((current.getHigh() >= previous.getHigh()) && (current.getHigh() > next.getHigh())) {
                point = new CriticalPricePoint(current.getDate(), CriticalPricePoint.HIGH);
                highPoints.add(point);
            }
            previous = current;
            current = next;
            next = contract.getPrices((Date)it.next());
        }

        return highPoints;
    }

    /**
     *  Processes prices in a given contract in order by date. The
     *  critical low prices points are returned.
     *
     *  @param  contract            the contract to be analyzed
     *  @return                     a TreeSet of the critical low
     *                              price points
     */
    public Collection getLowPricePoints(Contract contract) {
        TreeSet lowPoints = new TreeSet();
        Prices previous = null;
        Prices current = null;
        Prices next = null;
        CriticalPricePoint point = null;

       ListIterator it = contract.getPriceDates(Contract.ASCENDING);
        if (it.hasNext()) {
            previous = contract.getPrices((Date)it.next());
        } else {
            return lowPoints;
        }
        if (it.hasNext()) {
            current = contract.getPrices((Date)it.next());
        } else {
            return lowPoints;
        }
        if (it.hasNext()) {
            next = contract.getPrices((Date)it.next());
        }
        else {
            return lowPoints;
        }

        while (it.hasNext()) {
            if ((current.getLow() <= previous.getLow()) && (current.getLow() < next.getLow())) {
                point = new CriticalPricePoint(current.getDate(), CriticalPricePoint.LOW);
                lowPoints.add(point);
            }
            previous = current;
            current = next;
            next = contract.getPrices((Date)it.next());
        }

       return lowPoints;
    }

    /**
     *  Processes prices in a given contract in order by date. The
     *  critical high and low prices points are returned.
     *
     *  @param  contract            the contract to be analyzed
     *  @return                     a TreeSet of the critical high and
     *                              low price points
     */
   public Collection getHighLowPricePoints(Contract contract) {
        TreeSet highLowPoints = (TreeSet)getHighPricePoints(contract);
        highLowPoints.addAll(getLowPricePoints(contract));
        return highLowPoints;
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Return the single instance of CriticalPricePointAnalyzer.
     *
     *  @return     singleton CriticalPricePointAnalyzer
     */
    public static CriticalPricePointAnalyzer instance() {
        if (cppa == null) {
            synchronized (CriticalPricePointAnalyzer.class) {
                if (cppa == null) {
//System.out.println("*******************************************");
//System.out.println("** Construct CriticalPricePointAnalyzer  **");
//System.out.println("*******************************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
                    cppa = new CriticalPricePointAnalyzer();
                }
            }
        }
        return cppa;
    }
}