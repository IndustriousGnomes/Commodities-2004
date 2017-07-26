/*  _ Properly Documented
 */
/**
 *  PricingTabbedPane is a JTabbedPane that controls the layout of pricing
 *  panels such that they can be groupped and tagged.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.price;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import prototype.commodities.*;
import prototype.commodities.dataaccess.*;

public class PricingTabbedPane extends JTabbedPane {
    private static boolean DEBUG = false;

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

    /**
     *  The contract currently selected within all of the pricing
     *  panels in this pane.
     */
    private Contract selectedContract = null;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    /**
     *  Creates a panel to hold PricePanels.
     */
    private PricingTabbedPane() {
Debug.println(DEBUG, this, "PricingTabbedPane start");
        loadPages();
Debug.println(DEBUG, this, "PricingTabbedPane end");
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    /**
     *  Gets the preferred size of the PricingPanel.  The preferred size is based on the number of
     *  PricePanels that are currently contained in the PricingPanel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
Debug.println(DEBUG, this, "getPreferredSize start");
        Rectangle tabRect = getUI().getTabBounds(this,0);
        Dimension size = getSelectedComponent().getPreferredSize();
        size.setSize(size.getWidth(), size.getHeight() + tabRect.getY() + tabRect.getHeight());
Debug.println(DEBUG, this, "getPreferredSize end");
        return size;
    }

    /**
     *  Sets the preferred size of the PricingPanel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
Debug.println(DEBUG, this, "setPreferredSize start");
        this.preferredSize = preferredSize;
Debug.println(DEBUG, this, "setPreferredSize end");
    }

    /**
     *  Adds the named commodity to the current pricing panel.
     *
     *  @param  nameExchange   The name(exch) of the commodity to add.
     */
    public void addToCurrentPricingPanel(String nameExchange) {
Debug.println(DEBUG, this, "addToCurrentPricingPanel start");
        int tab = getSelectedIndex();
        PricingPanel pricingPanel = (PricingPanel)getComponentAt(tab);

        pricingPanel.addPricePanel(nameExchange);
        repaint();

        CommodityPriceLocation.addPriceLocation(new CommodityPriceLocation (tab,
                                                                            pricingPanel.getCount() - 1,
                                                                            Commodity.byNameExchange(nameExchange).getSymbol(),
                                                                            getTitleAt(tab)));
Debug.println(DEBUG, this, "addToCurrentPricingPanel end");
    }

    /**
     *  Removes the named commodity to the current pricing panel.
     *
     *  @param  nameExchange   The name(exch) of the commodity to add.
     */
    public void removeFromCurrentPricingPanel(String nameExchange[]) {
Debug.println(DEBUG, this, "removeFromCurrentPricingPanel start");
        int tab = getSelectedIndex();
        PricingPanel pricingPanel = (PricingPanel)getComponentAt(tab);
        for (int i = 0; i < nameExchange.length; i++) {
            pricingPanel.removePricePanel(nameExchange[i]);
        }
        repaint();

        CommodityPriceLocation.removePriceLocations(tab, nameExchange);
Debug.println(DEBUG, this, "removeFromCurrentPricingPanel end");
    }

    /**
     *  Adds a new tab to this instance of the PricingTabbedPane.
     *
     *  @param  title   The title of the tab.
     */
    public boolean addTabToPanel(String title, String nameExchange) {
Debug.println(DEBUG, this, "addTabToPanel start");
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
        addToCurrentPricingPanel(nameExchange);
Debug.println(DEBUG, this, "addTabToPanel end");
        return true;
    }

    public void loadPages() {
Debug.println(DEBUG, this, "loadPages start");
        Iterator it = CommodityPriceLocation.pages();
        while (it.hasNext()) {
            int pg = ((Integer)it.next()).intValue();
            PricingPanel pp = new PricingPanel(CommodityPriceLocation.byPage(pg));
            pricingPanels.add(pp);
            add(CommodityPriceLocation.getTitle(pg),pp);
        }
Debug.println(DEBUG, this, "loadPages end");
    }

    public void clearSelections(PricePanel source) {
Debug.println(DEBUG, this, "clearSelections start");
        Iterator it = pricingPanels.iterator();
        while (it.hasNext()) {
            PricingPanel pp = (PricingPanel)it.next();
            pp.clearPanelSelections(source);
        }
Debug.println(DEBUG, this, "clearSelections end");
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    public Contract getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(Contract selectedContract) {
        this.selectedContract = selectedContract;
    }


/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    public static PricingTabbedPane getInstance() {
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
