/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.event.*;
import commodities.util.*;

/**
 *  The Graph class is a class for the common functionallity
 *  between different kinds of graphs.  It does not draw anything
 *  itself and its paintComponent method should be overridden.
 *
 *  @author J.R. Titko
 *  @since      1.00
 *  @version 1.00
 *  @update 2004.11.11
 */

public class Graph extends JPanel implements ContractSelectionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The default number of pixels between prices on the x-axis. */
    public static final int DEFAULT_X_SCALE = 3;

    /** The current scale for the x-axis. */
    private static int xScale = DEFAULT_X_SCALE;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The preferred size of the graph screen. */
    private Dimension   preferredSize = new Dimension(0, 300);
    /** The rectangle of the drawable graph. */
    private Rectangle   graphRect = new Rectangle(0,0,0,0);

    /** Listener list for graphs redraws. */
    private LinkedList listeners = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a graph
     */
    public Graph() {
        Contract.addSelectionListener(this);
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the rectangle of the drawable graph.
     *  @return     the rectangle to draw in
     */
    public Rectangle getGraphRect() {
        return graphRect;
    }

    /**
     *  Get the scale of the x-axis.
     *  @return     the scale in pixels
     */
    public int getXScale() {
        return xScale;
    }

    /**
     *  Set the scale of the x-axis.
     *  @param  xScale  the scale in pixels
     */
    public void setXScale(int xScale) {
        this.xScale = xScale;
        repaint();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Round a value to 4 decimal places.
     *  @param  value   the value to round
     *  @return     the rounded value
     */
    public double round (double value) {
        long iVal = (long)((value + .000001) * 10000);
        return iVal / 10000.0;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        double temp     = 0.0;
        double yBuffer = 0.0;
        double minValue = 999999.0;
        double maxValue = 0.0;
        double minTickSize = 0.0;
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            GraphRedrawListener rl = (GraphRedrawListener)it.next();
            temp = rl.getYBuffer();
            if (temp > yBuffer) { yBuffer = temp; }
            temp = rl.getMaxValue();
            if (temp > maxValue) { maxValue = temp; }
            temp = rl.getMinValue();
            if (temp < minValue) { minValue = temp; }
            temp = rl.getTickSize();
            if (minTickSize == 0) {
                minTickSize = temp;
            } else if (round(minTickSize / temp) == (int)round(minTickSize / temp)) {
                minTickSize = temp; // smaller multiple
            } else if (round(temp / minTickSize) == (int)round(temp / minTickSize)) {
                // larger tick size, do nothing
            } else {
                minTickSize *= temp; // should probably be reduced (.25 & .1)
            }
        }

        int     xAxisLeft   = 10;
        int     xAxisRight  = getWidth() - 40;
        int     yAxisTop    = 30;
        int     yAxisBottom = getHeight() - 25;
        graphRect = new Rectangle(xAxisLeft, yAxisTop, xAxisRight - xAxisLeft, yAxisBottom - yAxisTop);

        double  ticks       = round((maxValue - minValue) / minTickSize);
        int     drawSize    = yAxisBottom - yAxisTop - (int)(2 * yBuffer);
        double  yScale      = round(ticks / drawSize); // number of ticks per pixel

        setBackground(Color.WHITE);

        g.setFont(new Font("Serif", Font.PLAIN, 10));

        double  minGraphValue = round(minValue - round(yBuffer * yScale * minTickSize));
        double  maxGraphValue = round(maxValue + round(yBuffer * yScale * minTickSize));

        double  c = minGraphValue;
        int     ydivisions = 20;
        for (int y = yAxisBottom; y > yAxisTop; y -= ydivisions) {  // Y-Axis
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(xAxisLeft, y, xAxisRight, y);
            g.setColor(Color.BLACK);
            g.drawString("" + round((int)(c / minTickSize) * minTickSize), xAxisRight + 2, y + 5);  // change  all
            c += ydivisions * yScale * minTickSize;
        }

        CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
        calendar.clearTime();
        calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);

        g.setFont(new Font("Serif", Font.PLAIN, 9));
        for (int x = xAxisRight; x > xAxisLeft; x -= (5 * xScale)) {  // X-Axis
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, yAxisBottom, x, yAxisTop);

            g.setColor(Color.BLACK);
            int date = calendar.get(CommodityCalendar.DATE);
            if (date < 10) {
                g.drawString("" + calendar.get(CommodityCalendar.DATE), x - 3, yAxisBottom + 12); // change all
            } else {
                g.drawString("" + calendar.get(CommodityCalendar.DATE), x - 6, yAxisBottom + 12); // change all
            }
            if (date < 8) {
                if (calendar.get(CommodityCalendar.MONTH) == 0) {
                    g.drawString(Month.byNumber(calendar.get(CommodityCalendar.MONTH)).getAbbrev() + " " + calendar.get(CommodityCalendar.YEAR), x - 3, yAxisBottom + 21); // change all
                } else {
                    g.drawString(Month.byNumber(calendar.get(CommodityCalendar.MONTH)).getAbbrev(), x - 3, yAxisBottom + 21); // change all
                }
            }
            calendar.addWeekDays(-5);
        }

        g.setColor(Color.BLACK);                                        // Draw bold box around outside
        g.drawLine(xAxisLeft, yAxisBottom, xAxisRight, yAxisBottom);
        g.drawLine(xAxisLeft, yAxisTop, xAxisRight, yAxisTop);
        g.drawLine(xAxisLeft, yAxisTop, xAxisLeft, yAxisBottom);
        g.drawLine(xAxisRight, yAxisTop, xAxisRight, yAxisBottom);
        g.drawLine(xAxisLeft-1, yAxisBottom+1, xAxisRight+1, yAxisBottom+1);
        g.drawLine(xAxisLeft-1, yAxisTop-1, xAxisRight+1, yAxisTop-1);
        g.drawLine(xAxisLeft-1, yAxisTop-1, xAxisLeft-1, yAxisBottom+1);
        g.drawLine(xAxisRight+1, yAxisTop-1, xAxisRight+1, yAxisBottom+1);

        int yTitleOffset = 0;
        it = listeners.iterator();
        while (it.hasNext()) {
            GraphRedrawListener rl = (GraphRedrawListener)it.next();

            g.setFont(new Font("Serif", Font.BOLD, 12));
            if (rl.redrawTitle(g, yTitleOffset)) {
                yTitleOffset += g.getFontMetrics().getAscent();
            }

            rl.redrawComponent(new GraphRedrawEvent(this, g, xScale, yScale, minTickSize, maxGraphValue, minGraphValue));
        }
    }

/* ***---------------------------------*** */
/* *** EVENT LISTENER METHODS          *** */
/* ***---------------------------------*** */
    /**
     *  Add a listener for redrawing graphs.
     *
     *  @param  listener    a GraphRedrawListener
     */
    public void addListener(GraphRedrawListener listener) {
        listeners.add(listener);
    }

    /**
     *  Remove a listener from redrawing graphs.
     *
     *  @param  listener    a GraphRedrawListener
     */
    public void removeListener(GraphRedrawListener listener) {
        listeners.remove(listener);
    }

    /**
     *  Clear all listeners for redrawing graphs.
     */
    public void clearListeners() {
        listeners.clear();
    }

/* *************************************** */
/* *** JPanel METHODS                  *** */
/* *************************************** */
    /**
     *  Gets the preferred size of the graph panel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }
    /**
     *  Sets the preferred size of the graph panel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

/* *************************************** */
/* ***ContractSelectionListener METHODS*** */
/* *************************************** */
    /**
     *  Invoked when the contract selection changes.  This is a 2 pass
     *  execution so that some classes that need to execute first can
     *  process when init is true and the remaining listeners can
     *  process when init is false.
     *
     *  @param  e       the ContractSelectionEvent
     *  @param  init    true if first pass for initialization, otherwise false
     */
    public void selectContract(ContractSelectionEvent e, boolean init) {
        if (init) {
            Contract contract = (Contract)e.getSource();
            clearListeners();
            repaint();
        }
    }
}
