/*  _ Properly Documented
 */
package prototype.commodities.util;
/**
 *  BuyArrow dipicts an arrow with the point up that can be
 *  used to indicate a buy point.
 *
 *  @author J.R. Titko
 */

import java.awt.*;
import java.util.*;
import prototype.commodities.*; // debug only

public class BuyArrow extends Polygon {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public BuyArrow() {
        addPoint(-2, 0);
        addPoint(-2, 4);
        addPoint( 2, 4);
        addPoint( 2, 0);
        addPoint( 4, 0);
        addPoint( 0,-4);
        addPoint(-4, 0);
    }
}