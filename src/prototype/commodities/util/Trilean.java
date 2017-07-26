/*  x Properly Documented
 */
package prototype.commodities.util;

import prototype.commodities.*; // debug only

/**
 *  The Trilean class is a three way switch similar to a boolean switch
 *  but has 3 possitions.
 *
 *  @author J.R. Titko
 */

public class Trilean implements Comparable{
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Switch possition 1 */
    private static final byte POS1 = 1;
    /** Switch possition 2 */
    private static final byte POS2 = 1 << 1;
    /** Switch possition 3 */
    private static final byte POS3 = 1 << 2;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The current state of the switch */
    private byte trilean = 0;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new Trilean switch set to the default of position 1.
     */
    public Trilean() {
        trilean = POS1;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Returns the possition of the switch.
     *  @return     the switch possition
     */
    private byte getPos() {
        return trilean;
    }
    /**
     *  Returns if the switch is in possition 1.
     *  @return     true if switch is in possition 1
     */
    public boolean isPos1() {
        return trilean == POS1;
    }
    /**
     *  Returns if the switch is in possition 2.
     *  @return     true if switch is in possition 2
     */
    public boolean isPos2() {
        return trilean == POS2;
    }
    /**
     *  Returns if the switch is in possition 3.
     *  @return     true if switch is in possition 3
     */
    public boolean isPos3() {
        return trilean == POS3;
    }
    /**
     *  Set switch to possition 1.
     */
    public void pos1() {
        trilean = POS1;
    }
    /**
     *  Set switch to possition 1.
     */
    public void pos2() {
        trilean = POS2;
    }
    /**
     *  Set switch to possition 3.
     */
    public void pos3() {
        trilean = POS3;
    }

/* *************************************** */
/* *** Comparable METHODS              *** */
/* *************************************** */
    /**
     *  Compare the objects for sorting.
     *  @param  o2      the object to compare to
     *  @return         negative, zero, possitive number depending on
     *                  this object to the passed object
     */
    public int compareTo(Object o2) {
        return getPos() - ((Trilean)o2).getPos();
    }

/* *************************************** */
/* *** Object METHODS                  *** */
/* *************************************** */
    /**
     *  Equals the objects for sorting.
     *  @param  obj     the object to equal to
     *  @return         true if equal
     */
    public boolean equals(Object obj) {
        return getPos() == ((Trilean)obj).getPos();
    }
}