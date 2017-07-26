/*  x Properly Documented
 */
package commodities.window;

import java.awt.*;
import javax.swing.*;

import commodities.graph.*;
import commodities.menu.*;
import commodities.order.*;
import commodities.price.*;

import com.awt.*;

/**
 *  The CommodityAnalyzerJFrame is the primary user screen for
 *  the Commodity Analyzer application.
 *
 *  This window is divided into four main parts...
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityAnalyzerJFrame extends JFrame {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Single instance of the CommodityAnalyzerJFrame */
    private static CommodityAnalyzerJFrame frame = null;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /**
     *  The utility JPanel that can take on many roles
     *  within the program.  This panel is located in
     *  the upper left section of the screen and defaults
     *  to the pricingJPanel.
     */
    private JPanel  utilityPanel = new JPanel();
    /**
     *  The component that is in the center of the utility panel.
     */
    private Component ulitilyPanelCenter;

    /**
     *  The utility JScrollPane is set with a vertical scrollbar but no
     *  horizontal scrollbar.
     */
    private JScrollPane utilitySP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    /**
     *  The order JPanel contains all information pertaining to open
     *  orders.  This panel will always be displayed.
     */
    private JPanel  orderPanel = new JPanel();
    /**
     *  The order JScrollPane is set with a vertical scrollbar but no
     *  horizontal scrollbar.
     */
    private JScrollPane orderSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    /**
     *  The graph JPanel contains the graph of prices and any tests
     *  needing to be overlain with the prices.  This panel will always be displayed.
     */
    private JPanel  graphPanel = new JPanel();
    /**
     *  The component that is in the center of the graph panel.
     */
    private Component  graphPanelCenter;
    /**
     *  The component that is in the center of the test graph panel.
     */
    private Component testGraphPanelCenter;
    /**
     *  The graph JScrollPane is set with a vertical scrollbar but no
     *  horizontal scrollbar.
     */
    private JScrollPane graphSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    /**
     *  The test JPanel contains the information of any tests
     *  needing to be displayed.  This panel will always be displayed.
     */
    private JPanel  testPanel = new JPanel();
    /**
     *  The test JScrollPane is set with a vertical scrollbar but no
     *  horizontal scrollbar.
     */
    private JScrollPane testSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create an instance of the CommodityAnalyzerJFrame.
     */
    public CommodityAnalyzerJFrame() {
//System.out.println("****************************************");
//System.out.println("** Construct CommodityAnalyzerJFrame  **");
//System.out.println("****************************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
        setupGUI();
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the price graph panel.
     *  @return     the graph
     */
    public Graph getGraphPanel() {
        return (Graph)graphPanelCenter;
    }

    /**
     *  Get the statistical test graph manager panel.
     *  @return     the TestGraphManager
     */
    public TestGraphManager getTestGraphPanel() {
        return (TestGraphManager)testGraphPanelCenter;
    }

    /**
     *  Sets the contents of the utility panel.  This panel is located
     *  in the upper left section of the screen.
     *
     *  @param  panel   the panel to put in the utility panel
     */
    public void setUtilityPanel(Component panel) {
        utilityPanel.removeAll();
        if (panel != null) {
            utilityPanel.add(panel, BorderLayout.CENTER);
            ulitilyPanelCenter = panel;
        } else {
            panel = new BlankPanel(new Dimension(0,0));
            utilityPanel.add(panel);
            ulitilyPanelCenter = panel;
        }
    }

    /**
     *  Sets the contents of the order panel.  This panel is located
     *  across the bottom of the screen.  This panel should only contain
     *  the order panel.
     *
     *  @param  panel   the panel to put in the order panel
     */
    public void setOrderPanel(JPanel panel) {
        orderPanel.setVisible(false);
        orderPanel.removeAll();
        if (panel != null) {
            orderPanel.add(panel);
        } else {
            orderPanel.add(new BlankPanel(new Dimension(0,0)));
        }
        orderPanel.setVisible(true);
    }

    /**
     *  Sets the contents of the graph panel.  This panel is located
     *  in the upper right section of the screen.  This panel should
     *  only contain price graph and any graphs from tests that are
     *  overlain on the price graph.
     *
     *  @param  panel   the panel to put in the graph panel
     */
    private void setGraphPanel(Component panel) {
        graphPanel.removeAll();
        if (panel != null) {
            graphPanel.add(panel);
            graphPanelCenter = panel;
        } else {
            panel = new BlankPanel(new Dimension(0,0));
            graphPanel.add(panel);
            graphPanelCenter = panel;
        }
    }

    /**
     *  Sets the contents of the test panel.  This panel is located
     *  in the middle right section of the screen.  This panel should
     *  contain any information pertaining to tests.
     *
     *  @param  panel   the panel to put in the test panel
     */
    private void setTestPanel(JPanel panel) {
        testPanel.removeAll();
        if (panel != null) {
            testPanel.add(panel);
            testGraphPanelCenter = panel;
        } else {
            panel = new BlankPanel(new Dimension(0,0));
            testPanel.add(panel);
            testGraphPanelCenter = panel;
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Setup the frame for the Commodity Analyzer application.
     */
    private void setupGUI() {
        setTitle("Commodity Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

// The screen size must be set at the beginning as split panes are being calculated from it.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height - 64);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.white);

// Create a menu bar
        setJMenuBar(new MainMenuBar());

        utilityPanel.setLayout(new BorderLayout());
        utilityPanel.setMinimumSize(new Dimension(100,100));
        utilitySP.setViewportView(utilityPanel);
// Default panel to go into the utility panel
        PricingTabbedPane ptp = PricingTabbedPane.instance();
        ptp.setPreferredSize(new Dimension(0, (int)(getSize().height * 0.75)));
        setUtilityPanel(ptp);

        orderPanel.setLayout(new BorderLayout());
        orderPanel.setMinimumSize(new Dimension(100,100));
        orderSP.setViewportView(orderPanel);
        setOrderPanel(new ContractPanel());

        graphPanel.setLayout(new BorderLayout());
        graphPanel.setMinimumSize(new Dimension(100,100));
        graphSP.setViewportView(graphPanel);
        setGraphPanel(new Graph());
        new GraphDailyPrices(getGraphPanel());

        testPanel.setLayout(new BorderLayout());
        testPanel.setMinimumSize(new Dimension(100,100));
        testSP.setViewportView(testPanel);
        setTestPanel(new TestGraphManager());


// change with each screen added
        JSplitPane jsp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, graphSP, testSP);
        jsp1.setOneTouchExpandable(true);

        JSplitPane jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, utilitySP, jsp1);
        int width = (int)((int)((getSize().width - Math.max(graphPanel.getMinimumSize().width, getSize().width * 0.25)) / PricePanel.PANEL_WIDTH) * PricePanel.PANEL_WIDTH) + 25;
        jsp2.setDividerLocation(width);
        jsp2.setOneTouchExpandable(true);

        JSplitPane jsp3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jsp2, orderSP);
        jsp3.setOneTouchExpandable(true);

        c.add(jsp3);
        setVisible(true);
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    public static CommodityAnalyzerJFrame instance() {
        if (frame == null) {
            synchronized (CommodityAnalyzerJFrame.class) {
                if (frame == null) {
                   frame = new CommodityAnalyzerJFrame();
                }
            }
        }
        return frame;
    }
}
