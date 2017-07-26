/*  _ Properly Documented
 */
/**
 *  The Graph class is a class for the common functionallity
 *  between different kinds of graphs.  It does not draw anything
 *  itself and its paintComponent method should be overridden.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.dataaccess.*;

public class Graph extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    public static final int PIXEL_SCALE = 3;
    public static final int XDIVISIONS = 5 * PIXEL_SCALE;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private Dimension   preferredSize = new Dimension(0, 300);
    private Rectangle   graphRect = new Rectangle(0,0,0,0);

    private LinkedList listeners = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public Graph () {
Debug.println(DEBUG, this, "Graph start");
Debug.println(DEBUG, this, "Graph end");
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    public Rectangle getGraphRect()    {
Debug.println(DEBUG, this, "getGraphRect only");
        return graphRect;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Gets the preferred size of the graph panel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
Debug.println(DEBUG, this, "getPreferredSize only");
        return preferredSize;
    }
    /**
     *  Sets the preferred size of the graph panel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
Debug.println(DEBUG, this, "setPreferredSize start");
        this.preferredSize = preferredSize;
Debug.println(DEBUG, this, "setPreferredSize end");
    }

    public void addListener(GraphRedrawListener listener) {
Debug.println(DEBUG, this, "addListener start");
        listeners.add(listener);
Debug.println(DEBUG, this, "addListener end");
    }

    public void removeListener(GraphRedrawListener listener) {
Debug.println(DEBUG, this, "removeListener start");
        listeners.remove(listener);
Debug.println(DEBUG, this, "removeListener end");
    }

    public void clearListeners() {
Debug.println(DEBUG, this, "clearListeners start");
        listeners.clear();
Debug.println(DEBUG, this, "clearListeners end");
    }

    public double round (double value) {
//Debug.println(DEBUG, this, "round start");
        long iVal = (long)((value + .000001) * 10000);
//Debug.println(DEBUG, this, "round end");
        return iVal / 10000.0;
    }


    public void paintComponent(Graphics g) {
Debug.println(DEBUG, this, "paintComponent() start");
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

/*
System.out.println("Graph - yAxisBottom = " + yAxisBottom);
System.out.println("Graph - yAxisTop    = " + yAxisTop);
System.out.println("Graph - yBuffer     = " + yBuffer);
System.out.println("Graph - maxValue    = " + maxValue);
System.out.println("Graph - minValue    = " + minValue);
System.out.println("Graph - minTickSize = " + minTickSize);
*/

        double  ticks       = round((maxValue - minValue) / minTickSize);
        int     drawSize    = yAxisBottom - yAxisTop - (int)(2 * yBuffer);
        double  yScale      = round(ticks / drawSize); // number of ticks per pixel

/*
System.out.println("Graph - ticks       = " + ticks);
System.out.println("Graph - drawSize    = " + drawSize);
System.out.println("Graph - yScale      = " + yScale);
*/

        setBackground(Color.WHITE);

        g.setFont(new Font("Serif", Font.PLAIN, 10));

        double  minGraphValue = round(minValue - round(yBuffer * yScale * minTickSize));
        double  maxGraphValue = round(maxValue + round(yBuffer * yScale * minTickSize));

/*
System.out.println("Graph - minGraphValue   = " + minGraphValue);
System.out.println("Graph - maxGraphValue   = " + maxGraphValue);
*/

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
        for (int x = xAxisRight; x > xAxisLeft; x -= XDIVISIONS) {  // X-Axis
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

            rl.redrawComponent(new GraphRedrawEvent(this, g, yScale, minTickSize, maxGraphValue, minGraphValue));
        }
Debug.println(DEBUG, this, "paintComponent() end");
    }
}
