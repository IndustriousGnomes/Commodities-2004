/*  x Properly Documented
 */
package commodities.util;

import com.lang.Trilean;

/**
 *  The DBTrilean class is a three way switch similar to a boolean switch
 *  but has 3 positions.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class DBTrilean extends Trilean {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new Trilean switch set to the default of position 1.
     */
    public DBTrilean() {
        super();
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Returns if the switch neither an add or update.
     *  @return     true if not add or update
     */
    public boolean isNeither() {
        return isPos1();
    }
    /**
     *  Returns if the switch indicates an add.
     *  @return     true if add
     */
    public boolean isAdd() {
        return isPos2();
    }
    /**
     *  Returns if the switch indicates an update.
     *  @return     true if update
     */
    public boolean isUpdate() {
        return isPos3();
    }
    /**
     *  Set switch to neither add nor update.
     */
    public void neither() {
        pos1();
    }
    /**
     *  Set switch to the add possition.
     */
    public void add() {
        pos2();
    }
    /**
     *  Set switch to the update possition.
     */
    public void update() {
        pos3();
    }
}