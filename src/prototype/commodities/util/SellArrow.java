/*  _ Properly Documented
 */
package prototype.commodities.util;
/**
 *  SellArrow dipicts an arrow with the point down that can be
 *  used to indicate a sell point.
 *
 *  @author J.R. Titko
 */

import java.awt.*;
import java.util.*;
import prototype.commodities.*; // debug only

public class SellArrow extends Polygon {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public SellArrow() {
        addPoint(-2, 0);
        addPoint(-2,-4);
        addPoint( 2,-4);
        addPoint( 2, 0);
        addPoint( 4, 0);
        addPoint( 0, 4);
        addPoint(-4, 0);
    }
}