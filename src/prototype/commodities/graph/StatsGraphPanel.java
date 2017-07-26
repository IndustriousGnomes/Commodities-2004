/*  x Properly Documented
 */
package prototype.commodities.graph;

import java.awt.*;
import javax.swing.*;

import prototype.commodities.*;

/**
 *  The StatsGraphPanel creates a JPanel in which a statistical
 *  graph can be added.
 *
 *  @author J.R. Titko
 */
public class StatsGraphPanel extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The statistics graph manager to put this graph in */
    private StatsGraphManager statsGraph = null;

    /**
     *  The prefered size of panel to draw this graph in.
     *  A height of 155 allows 2 lines of titles and 100 pixel graph
     */
    private Dimension   preferredSize = new Dimension(0, 155);

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a JPanel to put a statistical graph into.
     */
    public StatsGraphPanel() {
        setLayout(new BorderLayout());
        statsGraph = CommodityJFrame.statsGraph;
    }

/* *************************************** */
/* *** JPanel METHODS                  *** */
/* *************************************** */
    /**
     *  Get the preferred size of this JPanel
     *
     *  @return     the dimensions of the preferred panel size
     */
    public Dimension getPreferredSize() {
        return new Dimension(statsGraph.getWidth(), (int)preferredSize.getHeight());
    }

    /**
     *  Set the preferred size of this JPanel
     *
     *  @param  preferredSize   the dimensions that are preferred for this panel
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }
}