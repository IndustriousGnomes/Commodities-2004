/*  x Properly Documented
 */
package commodities.event;

import java.awt.*;
import java.util.*;

/**
 *  A GraphRedrawEvent indicates that a graph needs to be redrawn and gives the
 *  parameters in which to draw that graph.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class GraphRedrawEvent extends EventObject {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The graphics area that the graph is to be drawn in. */
    private Graphics graphics;
    /** The x-axis scale the graph should use to stay in the graph boundries. */
    private int   xScale;
    /** The y-axis scale the graph should use to stay in the graph boundries. */
    private double   yScale;
    /** The minimum tick size the graph should use to stay in the graph boundries. */
    private double   minTickSize;
    /** The maximum value represented in the graph. */
    private double   maxValue;
    /** The minimum value represented in the graph. */
    private double   minValue;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a GraphRedrawEvent so that graphs can be drawn appropriately
     *  in regards to other graphs that are sharing the same space.
     *
     *  @param  source      The source of the event.
     *  @param  g           The graphics area to be drawn in.
     *  @param  yScale      The scale to be used for the y-axis
     *  @param  minTickSize The smallest size of a pixel.
     *  @param  maxValue    The maximum value represented in the graph
     *  @param  minValue    The minimum value represented in the graph
     */
    public GraphRedrawEvent(Object source, Graphics g, int xScale, double yScale, double minTickSize, double maxValue, double minValue) {
        super(source);
        this.graphics = g;
        this.xScale = xScale;
        this.yScale = yScale;
        this.minTickSize = minTickSize;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the graphics area to be drawn in.
     *  @return         The graphics area
     */
    public Graphics getGraphics() { return graphics; }
    /**
     *  Get the x-axis scale to be applied to the data.
     *  @return         The scale
     */
    public int   getXScale()   { return xScale; }
    /**
     *  Get the y-axis scale to be applied to the data.
     *  @return         The scale
     */
    public double   getYScale()   { return yScale; }
   /**
     *  Get the minimum tick size represented in the graph.
     *  @return         The minimum tick size
     */
    public double   getMinTickSize()    { return minTickSize; }
    /**
     *  Get the maximum value represented in the graph
     *  @return         The maximum value
     */
    public double   getMaxValue() { return maxValue; }
    /**
     *  Get the minimum value represented in the graph
     *  @return         The minimum value
     */
    public double   getMinValue() { return minValue; }
}
