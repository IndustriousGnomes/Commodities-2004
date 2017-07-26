/*  x Properly Documented
 */
package commodities.price;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import commodities.commodity.*;

/**
 *  PricingTabbedPane is a JTabbedPane that controls the layout of pricing
 *  panels such that they can be groupped and tagged.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PricingTabbedPane extends JTabbedPane {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static PricingTabbedPane ptp = null;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Preferred size of the pane. */
    private Dimension preferredSize = new Dimension(0,0);
    /** List of the pricing panels contained in this pane. */
    private LinkedList  pricingPanels = new LinkedList();

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  LoadThread loads the panels contained in the PricingTabbedPane
     *  as a thread so the main processing can continue.
     *
     *  @author J.R. Titko
     */
    public class LoadThread extends Thread {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Load the pricing tabbed pane as a thread.
         */
        public LoadThread() {
            setPriority(7);
            start();
        }

    /* *************************************** */
    /* *** Thread METHODS                  *** */
    /* *************************************** */
        public void run() {
            Iterator it = CommodityPriceLocation.pages().iterator();
            while (it.hasNext()) {
                int pg = ((Integer)it.next()).intValue();
                PricingPanel pp = new PricingPanel(CommodityPriceLocation.byPage(pg));
                pricingPanels.add(pp);
                add(CommodityPriceLocation.getTitle(pg),pp);
            }
        }
    }


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a panel to hold PricePanels.
     */
    private PricingTabbedPane() {
//System.out.println("**********************************");
//System.out.println("** Construct PricingTabbedPane  **");
//System.out.println("**********************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
//        loadPages();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    /**
     *  Adds a new tab to the PricingTabbedPane.
     *
     *  @param  title   the title of the tab.
     *  @return     if tab was successfully added
     */
    public boolean addTabToPanel(String title) {
        int tabs = getTabCount();
        for (int i = 0; i < tabs; i++) {
            if (title.equals(getTitleAt(i))) {
                JOptionPane.showMessageDialog(null, "Tab name already exists", "alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        PricingPanel pp = new PricingPanel();
        pricingPanels.add(pp);
        add(title, pp);
        setSelectedIndex(tabs);
        return true;
    }

    /**
     *  Load pricing pages.
     */
    public void loadPages() {
        new LoadThread();
    }

    /**
     *  Adds the named commodity to the current pricing panel.
     *
     *  @param  nameExchange   The name(exch) of the commodity to add.
     */
    public void addToCurrentPricingPanel(String nameExchange) {
        int tab = getSelectedIndex();
        PricingPanel pricingPanel = (PricingPanel)getComponentAt(tab);

        pricingPanel.addPricePanel(nameExchange);
        repaint();

        CommodityPriceLocation.addPriceLocation(new CommodityPriceLocation (tab,
                                                                            pricingPanel.getCount() - 1,
                                                                            Commodity.byNameExchange(nameExchange).getSymbol(),
                                                                            getTitleAt(tab)));
    }

    /**
     *  Removes the named commodity to the current pricing panel.
     *
     *  @param  nameExchange   The name(exch) of the commodity to add.
     */
    public void removeFromCurrentPricingPanel(String nameExchange[]) {
        int tab = getSelectedIndex();
        PricingPanel pricingPanel = (PricingPanel)getComponentAt(tab);
        for (int i = 0; i < nameExchange.length; i++) {
            pricingPanel.removePricePanel(nameExchange[i]);
        }
        repaint();

        CommodityPriceLocation.removePriceLocations(tab, nameExchange);
    }

/* *************************************** */
/* *** JTabbedPane METHODS             *** */
/* *************************************** */
    /**
     *  Gets the preferred size of the PricingPanel.  The preferred size is based on the number of
     *  PricePanels that are currently contained in the PricingPanel.
     *
     *  @return     the preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
        try {
            Rectangle tabRect = getUI().getTabBounds(this,0);
            Dimension size = getSelectedComponent().getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + tabRect.getY() + tabRect.getHeight());
            return size;
        } catch (ArrayIndexOutOfBoundsException e) {
            return preferredSize;
        }
    }

    /**
     *  Sets the preferred size of the PricingTabbedPane.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    public static PricingTabbedPane instance() {
        if (ptp == null) {
            synchronized(PricingTabbedPane.class) {
                if (ptp == null) {
                    ptp = new PricingTabbedPane();
                }
            }
        }
        return ptp;
    }
}
