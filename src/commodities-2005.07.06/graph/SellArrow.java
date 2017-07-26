/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;

/**
 *  SellArrow dipicts an arrow with the point down that can be
 *  used to indicate a sell point.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class SellArrow extends Polygon {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a sell arrow.
     */
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