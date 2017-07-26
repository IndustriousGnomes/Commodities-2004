/*  x Properly Documented
 */
package commodities.price;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import commodities.commodity.*;

/**
 *  The PricingPanel class controls the layout of PricePanels.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PricingPanel extends JPanel {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Preferred size of the pane. */
//    private Dimension   preferredSize = new Dimension(0,0);
    /** List of the keyed price panels contained in this panel. */
    private LinkedHashMap  pricePanels = new LinkedHashMap();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a panel to hold PricePanels.
     */
    public PricingPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    }

    /**
     *  Creates a panel to hold PricePanels initiated with the passed in collection.
     *  @param  collection  price panels to include
     */
    public PricingPanel(Collection collection) {
        this();
        loadPage(collection);
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Add a price panel to this panel based on the commodity name.
     *
     *  @param  commodityName   the name of the commodity
     */
    public void addPricePanel(String commodityName) {
        PricePanel pp = new PricePanel(Commodity.byNameExchange(commodityName));
        pricePanels.put(commodityName, pp);
        add(pp);
    }

    /**
     *  Remove a price panel from this panel based on the commodity name.
     *
     *  @param  commodityName   the name of the commodity
     */
    public void removePricePanel(String commodityName) {
        remove((Component)pricePanels.remove(commodityName));
    }

    /**
     *  Get the count of how many price panels are in this panel.
     *
     *  @return     the number of price panels
     */
    public int getCount() {
        return pricePanels.size();
    }

    /**
     *  Load this panel with price panels based on the collection
     *  of locations price panels should be placed.
     *
     *  @param  priceLocations  the locations to place price panels
     */
    public void loadPage(Collection priceLocations) {
        Iterator it = priceLocations.iterator();
        while (it.hasNext()) {
            CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
            addPricePanel(Commodity.bySymbol(cpl.getSymbol()).getNameExchange());
        }
    }


/* *************************************** */
/* *** JPanel METHODS                  *** */
/* *************************************** */
    /**
     *  Gets the preferred size of the PricingPanel.  The preferred size is based on the number of
     *  PricePanels that are currently contained in the PricingPanel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
        int panelsWide = Math.max((int)(getWidth() / (PricePanel.PANEL_WIDTH)), 1);
        int panelsHighExtra = getComponentCount() % panelsWide;
        int panelsCount = (panelsHighExtra == 0)?getComponentCount():getComponentCount() + panelsWide - panelsHighExtra;
        return new Dimension (0, (PricePanel.PANEL_HEIGHT * panelsCount / panelsWide));
    }
}
