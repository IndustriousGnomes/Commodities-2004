/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.event.*;

/**
 *  The TestGraphManager class manages the layout of the
 *  statistical test graphs within this panel.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class TestGraphManager extends JPanel {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Preferred size of a the Statistics Graph Panel. */
    private Dimension   preferredSize = new Dimension(0, 300);
    /** The list of graph panels in the TestGraphManager. */
    private LinkedList  testGraphPanels = new LinkedList();
    /** The current xScale to draw graphs in. */
    private int xScale = Graph.DEFAULT_X_SCALE;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a test graph manager.
     */
    public TestGraphManager () {
        setBackground(Color.BLUE);
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the scale of the x-axis.
     *  @return     the scale in pixels
     */
    public int getXScale(int xScale) {
        return xScale;
    }

    /**
     *  Set the scale of the x-axis.
     *  @param  xScale  the scale in pixels
     */
    public void setXScale(int xScale) {
        this.xScale = xScale;
        Iterator it = testGraphPanels.iterator();
        while (it.hasNext()) {
            ((TestGraphPanel)it.next()).setXScale(xScale);
        }
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
//        return preferredSize;
// NOTE: Somewhere we are getting a division of 4 pixels between graphs in here.
// NOTE: The addition of 4 to the height and each graph is to compensate for scrolling
// NOTE: It needs to be determined where these extra pixels are coming from and account for them correctly
        double height = 0.0 + 4;
        Iterator it = testGraphPanels.iterator();
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
        this.preferredSize = preferredSize;
    }

    /**
     *  Add a test graph to the test graph
     *  manager.
     *
     *  @param  test   the test graph to add
     */
    public void addTestGraph(TestGraphPanel test) {
        if (!testGraphPanels.contains(test)) {
            testGraphPanels.add(test);
            add(test);
            setVisible(false);
            setVisible(true);
        }
    }

    /**
     *  Remove a test graph from the test graph
     *  manager.
     *
     *  @param  test   the test graph to add
     */
    public void removeTestGraph(TestGraphPanel test) {
        if (testGraphPanels.remove(test)) {
            remove(test);
        }
    }

    /**
    *  Remove all of the test graphs from the test graph
     *  manager.
     */
    public void clearTestGraph() {
        testGraphPanels.clear();
        removeAll();
    }
}
