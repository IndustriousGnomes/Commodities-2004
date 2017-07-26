/*  _ Properly Documented
 */
/**
 *  The StatsGraphManager class manages the layout of the
 *  statistical graphs within this panel.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.*; // debug only

public class StatsGraphManager extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private Dimension   preferredSize = new Dimension(0, 300);
    private LinkedList  graphPanels = new LinkedList();


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public StatsGraphManager () {
Debug.println(this, "StatsGraphManager() start");
        setBackground(Color.BLUE);
Debug.println(this, "StatsGraphManager() end");
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
Debug.println(this, "getPreferredSize() only");
//        return preferredSize;
// NOTE: Somewhere we are getting a division of 4 pixels between graphs in here.
// NOTE: The addition of 4 to the height and each graph is to compensate for scrolling
// NOTE: It needs to be determined where these extra pixels are coming from and account for them correctly
        double height = 0.0 + 4;
        Iterator it = graphPanels.iterator();
        while (it.hasNext()) {
            height += ((JPanel)it.next()).getPreferredSize().getHeight() + 4;
        }
        return new Dimension(0, (int)height);
    }
    /**
     *  Sets the preferred size of the graph panel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
Debug.println(this, "setPreferredSize() start");
        this.preferredSize = preferredSize;
Debug.println(this, "setPreferredSize() end");
    }

    public void addStatsGraph(JPanel stats) {
Debug.println(DEBUG, this, "addStatsGraph() start");
        if (!graphPanels.contains(stats)) {
            graphPanels.add(stats);
            add(stats);
            setVisible(false);
            setVisible(true);
        }
Debug.println(DEBUG, this, "addStatsGraph() end");
    }

    public void removeStatsGraph(JPanel stats) {
Debug.println(DEBUG, this, "removeStatsGraph() start");
        if (graphPanels.remove(stats)) {
            remove(stats);
//            repaint();
        }
Debug.println(DEBUG, this, "removeStatsGraph() end");
    }

    public void clearStatsGraph() {
Debug.println(DEBUG, this, "clearStatsGraph() start");
        graphPanels.clear();
        removeAll();
//        repaint();
Debug.println(DEBUG, this, "clearStatsGraph() end");
    }
}
