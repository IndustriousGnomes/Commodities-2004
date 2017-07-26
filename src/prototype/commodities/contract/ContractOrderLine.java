/*  x Properly Documented
 */
package prototype.commodities.contract;

/**
 *  The ContractOrderLine contains the information for a complete buy
 *  and sell ContractOrder along with any supporting fields that do not
 *  directly relate to either the buy or sell order.
 *
 *  @author J.R. Titko
 */

import prototype.commodities.*; // debug only

public class ContractOrderLine {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Buy order */
    private ContractOrder   longOrder;
    /** Sell order */
    private ContractOrder   shortOrder;
    /** Profit for the line */
    private double          profit;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new contract order line with default
     *  buy and sell ContractOrder.
     */
    public ContractOrderLine() {
        this(null, null);
    }

    /**
     *  Create a new contract order line with default
     *  buy and sell ContractOrder.
     */
    public ContractOrderLine(ContractOrder longOrder, ContractOrder shortOrder) {
        if (longOrder == null) {
            this.longOrder = new ContractOrder(ContractOrder.LONG);
        } else {
            this.longOrder = longOrder;
        }

        if (shortOrder == null) {
            this.shortOrder = new ContractOrder(ContractOrder.SHORT);
        } else {
            this.shortOrder = shortOrder;
        }
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the long ContractOrder.
     *  @return     the long ContractOrder
     */
    public  ContractOrder getLongOrder() { return longOrder; }

    /**
     *  Get the short ContractOrder.
     *  @return     the short ContractOrder
     */
    public  ContractOrder getShortOrder() { return shortOrder; }

    /**
     *  Get the profit for this line.
     *  @return     the profit
     */
    public  double getProfit() { return profit; }


    /**
     *  Set the long ContractOrder.
     *  @param  longOrder   the long ContractOrder
     */
    public  void setLongOrder(ContractOrder longOrder) {
        this.longOrder = longOrder;
    }


    /**
     *  Set the short ContractOrder.
     *  @param  shortOrder  the short ContractOrder
     */
    public  void setShortOrder(ContractOrder shortOrder) {
        this.shortOrder = shortOrder;
    }

    /**
     *  Set the profit for this line.
     *  @param  profit  the profit
     */
    public  void setProfit(double profit) {
        this.profit = profit;
    }

}        