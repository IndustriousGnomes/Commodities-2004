/*  _ Properly Documented
 */
package prototype.commodities;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import prototype.commodities.menu.*;
import prototype.commodities.price.*;
import prototype.commodities.graph.*;
import prototype.commodities.contract.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.dataaccess.dataloader.*;

import prototype.commodities.tests.*;

public class CommodityJFrame extends JFrame {
    private static boolean DEBUG = false;

    public static JFrame frame;
    public static Graph graph = new Graph();
    public static StatsGraphManager statsGraph = new StatsGraphManager();

    private JSplitPane  screenJSplitPane;

    private JSplitPane  workJSplitPane;
    private JPanel      pricingPanel = new JPanel();
    private JScrollPane pricingSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JSplitPane  statsJSplitPane;
    private JPanel      priceGraphPanel = new JPanel();
    private JScrollPane priceGraphSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel      statsPanel = new JPanel();
    private JScrollPane statsSP = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JPanel      contractPanel = new JPanel();

    public CommodityJFrame () {
        frame = this;
        new DataLoaderFactory();
        setupGUI();
    }

    private void setupGUI() {
Debug.println(DEBUG, this, "setupGUI start");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

// The screen size must be set at the beginning as split panes are being calculated from it.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height - 64);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.white);

// Create a menu bar
        setJMenuBar(new MainMenuBar());


// Create a default size for the pricing panel so the JSplitPanes can be set up.
        pricingPanel.setLayout(new BorderLayout());
        pricingPanel.setMinimumSize(new Dimension(100,100));
        pricingSP.setViewportView(pricingPanel);
        setPanelJComponent(new BlankPanel(new Dimension(0, (int)(getSize().height * 0.75)),"Pricing"), pricingPanel);

// Create a default contract panel
        contractPanel.setLayout(new BorderLayout());
        contractPanel.setMinimumSize(new Dimension(100,100));
        setPanelJComponent(new BlankPanel(new Dimension(0, 0),"Contract"), contractPanel);

// Create a default price graph panel
        priceGraphPanel.setLayout(new BorderLayout());
        priceGraphPanel.setMinimumSize(new Dimension(100,100));
        priceGraphSP.setViewportView(priceGraphPanel);
        priceGraphPanel.add(graph);

// Create a default statistics display panel
        statsPanel.setLayout(new BorderLayout());
        statsPanel.setMinimumSize(new Dimension(100,100));
        statsSP.setViewportView(statsPanel);
        statsPanel.add(statsGraph);


// Create a split pane that holds the price graph and statistics panels
        statsJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, priceGraphSP, statsSP);
        statsJSplitPane.setOneTouchExpandable(true);

// Create a split pane that holds the pricing panel and statistics split pane
        workJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, pricingSP, statsJSplitPane);
        workJSplitPane.setDividerLocation((int)(getSize().width * 0.75));
        workJSplitPane.setOneTouchExpandable(true);

// Create a split pane that holds the pricing panels and graphics split
        screenJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, workJSplitPane, contractPanel);
        screenJSplitPane.setOneTouchExpandable(false);


// Add the screen split pane to the layout
        c.add(BorderLayout.CENTER, screenJSplitPane);

        setVisible(true);
// Set up the actual panels
        setPanelJComponent(PricingTabbedPane.getInstance(), pricingPanel);
        setPanelJComponent(new ContractPanel(), contractPanel);
        setVisible(true);

Debug.println(DEBUG, this, "setupGUI end");
    }

    /**
     *  Replaces any components in the panel with the new one.  If the component is null,
     *  a default component is added in order to establish sizes.
     */
    public void setPanelJComponent (JComponent c, JPanel panel) {
Debug.println(DEBUG, this, "setPanelJComponent start");
//        setVisible(false);
        panel.removeAll();
        if (c != null) {
            panel.add(c);
        } else {
            panel.add(new BlankPanel(new Dimension(0,0)));
        }
//        setVisible(true);
Debug.println(DEBUG, this, "setPanelJComponent end");
    }


    public static void main(String args []) {
        Thread myMainThread = Thread.currentThread();   // Grab the current (main) thread
        myMainThread.setPriority(8);                    // Set main thread priority to 8
        CommodityJFrame f = new CommodityJFrame();
    }
}
