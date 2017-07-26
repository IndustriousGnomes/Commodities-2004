/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;
import javax.swing.*;

import commodities.window.*;

/**
 *  The TestGraphPanel creates a JPanel in which a statistical test
 *  graph can be added.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class TestGraphPanel extends Graph {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The test graph manager to put this graph in */
    private TestGraphManager testGraphManager = null;

    /**
     *  The prefered size of panel to draw this graph in.
     *  A height of 155 allows 2 lines of titles and 100 pixel graph
     */
    private Dimension   preferredSize = new Dimension(0, 155);

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a JPanel to put a test graph into.
     */
    public TestGraphPanel() {
        setLayout(new BorderLayout());
        testGraphManager = CommodityAnalyzerJFrame.instance().getTestGraphPanel();
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
        return new Dimension(testGraphManager.getWidth(), (int)preferredSize.getHeight());
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