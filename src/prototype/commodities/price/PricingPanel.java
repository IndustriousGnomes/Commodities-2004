/*  _ Properly Documented
 */
/**
 *  The PricingPanel class controls the layout of PricePanels.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.price;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import prototype.commodities.*;
import prototype.commodities.dataaccess.*;

public class PricingPanel extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    private Dimension   preferredSize = new Dimension(0,0);
    private LinkedList  pricePanels = new LinkedList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */

    /**
     *  Creates a panel to hold PricePanels.
     */
    public PricingPanel() {
Debug.println(DEBUG, this, "PricingPanel start");
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
Debug.println(DEBUG, this, "PricingPanel end");
    }

    /**
     *  Creates a panel to hold PricePanels.
     */
    public PricingPanel(Iterator it) {
        this();
Debug.println(DEBUG, this, "PricingPanel start");
        loadPage(it);
Debug.println(DEBUG, this, "PricingPanel end");
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
            int panelsWide = Math.max((int)(getWidth() / (PricePanel.PANEL_WIDTH)), 1);
            int panelsHighExtra = getComponentCount() % panelsWide;
            int panelsCount = (panelsHighExtra == 0)?getComponentCount():getComponentCount() + panelsWide - panelsHighExtra;
Debug.println(DEBUG, this, "getPreferredSize end");
            return new Dimension (0, (PricePanel.PANEL_HEIGHT * panelsCount / panelsWide));
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

    public void loadPage(Iterator it) {
Debug.println(DEBUG, this, "loadPage start");
        PricePanel pp = null;
        while (it.hasNext()) {
            CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
            pp = new PricePanel(this, Commodity.bySymbol(cpl.getSymbol()));
            pricePanels.add(pp);
            add(pp);
        }
Debug.println(DEBUG, this, "loadPage end");
    }


    public int getCount() {
        return pricePanels.size();
    }


    public void addPricePanel(String nameExchange) {
Debug.println(DEBUG, this, "addPricingPanel start");
        PricePanel pp = new PricePanel(this, Commodity.byNameExchange(nameExchange));
            pricePanels.add(pp);
            add(pp);
Debug.println(DEBUG, this, "addPricingPanel end");
    }

    public void removePricePanel(String nameExchange) {
Debug.println(DEBUG, this, "removePricePanel start");
        PricePanel toRemove = null;
        Iterator it = pricePanels.iterator();
        while (it.hasNext()) {
            PricePanel pp = (PricePanel)it.next();
            if (pp.getCommodity().getNameExchange().equals(nameExchange)) {
                toRemove = pp;
                break;
            }
        }
        pricePanels.remove(toRemove);
        remove(toRemove);
Debug.println(DEBUG, this, "removePricePanel end");
    }



    public void clearPanelSelections(PricePanel source) {
Debug.println(DEBUG, this, "clearPanelSelections start");
        Iterator it = pricePanels.iterator();
        while (it.hasNext()) {
            PricePanel pp = (PricePanel)it.next();
            if (pp != source) {
                pp.clearSelection();
            }
        }
Debug.println(DEBUG, this, "clearPanelSelections end");
    }
}
