/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;

/**
 *  BuyArrow dipicts an arrow with the point up that can be
 *  used to indicate a buy point.
 *
 *  @author J.R. Titko
 *  @version 1.00
 *  @update 2004.11.11
 */

public class BuyArrow extends Polygon {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a buy arrow.
     */
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