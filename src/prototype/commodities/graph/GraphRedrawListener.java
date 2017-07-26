/*  x Properly Documented
 */
package prototype.commodities.graph;

import java.awt.*;
import java.util.*;
import prototype.commodities.*; // debug only

/**
 *  The GraphRedrawListener is an EventListener that controls the drawing
 *  of graphs.  It is responsible for supplying the title of the graph
 *  and the contents of the graph.  Although it is used to supply the
 *  information for calculating the graph's scale, the actual scale is
 *  in the GraphRedrawEvent as multiple graphs can occupy the same drawing
 *  panel.
 *
 *  @see    commodities.graph.Graph
 *  @author J.R. Titko
 */

public interface GraphRedrawListener extends EventListener {
    /**
     *  Gets the number of pixels that should be added as white
     *  space to the top and bottom of the graph when calculating
     *  the scale of the graph.  This value is inside the graph's
     *  frame and therefore reduces the amount of drawing space
     *  by twice its amount.
     *
     *  @return         The number of pixels to use at the
     *                  top and bottom of the graph.
     */
    public double getYBuffer();
    /**
     *  Gets the maximum value represented in the graph.
     *
     *  @return         The data's maximum value.
     */
    public double getMaxValue();
    /**
     *  Gets the minimum value represented in the graph.
     *
     *  @return         The data's minimum value.
     */
    public double getMinValue();
    /**
     *  Gets the size of a tick for the commodity being drawn.
     *  This keeps the scale of the graph in multiples of this
     *  tick size.
     *
     *  @return         The data's tick size.
     */
    public double getTickSize();
    /**
     *  Defines how the title for the graph is to be drawn.
     *  The yOffset is used if multiple titles appear in
     *  the same graph to offset them so they dont write over
     *  each other.
     *
     *  @param  g       The graphics area to draw in.
     *  @param  yOffset The y-axis offset the title should have.
     *  @return         True if a title was drawn.
     */
    public boolean redrawTitle(Graphics g, int yOffset);
    /**
     *  Draws the graph using the GraphRedrawEvent.  This is limited
     *  to the statistical part of the graph and not the graph frame
     *  or scale indicators.
     *
     *  @param  e       The GraphRedrawEvent
     */
    public void   redrawComponent(GraphRedrawEvent e);
}
