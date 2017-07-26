/*  x Properly Documented
 */
package commodities.order;

import commodities.commodity.*;
import commodities.contract.*;

/**
 *  The ContractOrderLine contains the information for a complete buy
 *  and sell ContractOrder along with any supporting fields that do not
 *  directly relate to either the buy or sell order.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class ContractOrderLine {
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
     *
     *  @param  acctNumber  The account number that owns the orders.
     */
    public ContractOrderLine(int acctNumber) {
        this(acctNumber, null, null);
    }

    /**
     *  Create a new contract order line with default
     *  buy and sell ContractOrder.
     *
     *  @param  acctNumber  The account number that owns the orders.
     *  @param  longOrder   the long order of an order line
     *  @param  shortOrder  the short order of an order line
     */
    public ContractOrderLine(int acctNumber, ContractOrder longOrder, ContractOrder shortOrder) {
        if (longOrder == null) {
            this.longOrder = new ContractOrder(acctNumber, ContractOrder.LONG);
        } else {
            this.longOrder = longOrder;
        }

        if (shortOrder == null) {
            this.shortOrder = new ContractOrder(acctNumber, ContractOrder.SHORT);
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
    public  ContractOrder getLongOrder() {
        return longOrder;
    }

    /**
     *  Get the short ContractOrder.
     *  @return     the short ContractOrder
     */
    public  ContractOrder getShortOrder() {
        return shortOrder;
    }

    /**
     *  Get the profit for this line.
     *  @return     the profit
     */
    public  double getProfit() {
        try {

        boolean shortOrderFilled = (ContractOrderTablePanel.FILLED.equals(getShortOrder().getStatus()) ||
                                    ContractOrderTablePanel.STOP_LOSS.equals(getShortOrder().getStatus()));
        boolean longOrderFilled  = (ContractOrderTablePanel.FILLED.equals(getLongOrder().getStatus()) ||
                                    ContractOrderTablePanel.STOP_LOSS.equals(getLongOrder().getStatus()));

        if (shortOrderFilled && longOrderFilled &&
            (getShortOrder().getActualPrice().doubleValue() != 0) &&
            (getLongOrder().getActualPrice().doubleValue() != 0)) {

            Commodity commodity = Commodity.byNameExchange(getShortOrder().getCommodityName());

            return  (int)((getShortOrder().getActualPrice().doubleValue() - getLongOrder().getActualPrice().doubleValue()) *
                    getShortOrder().getNumberOfContracts().intValue() / commodity.getTickSize() * commodity.getTickPrice() *
                    100.0) / 100.0;

        } else if (shortOrderFilled && (getShortOrder().getActualPrice().doubleValue() != 0)) {

            Commodity commodity = Commodity.byNameExchange(getShortOrder().getCommodityName());
            Contract  contract = commodity.getContract(Contract.convertFormattedMonthToKey(getShortOrder().getMonth()));
            return  (int)((getShortOrder().getActualPrice().doubleValue() - contract.getPrices().getClose() - getLongOrder().getActualPrice().doubleValue()) *
                    getShortOrder().getNumberOfContracts().intValue() / commodity.getTickSize() * commodity.getTickPrice() *
                    100.0) / 100.0;

        } else if (longOrderFilled && (getLongOrder().getActualPrice().doubleValue() != 0)) {

            Commodity commodity = Commodity.byNameExchange(getLongOrder().getCommodityName());
            Contract  contract = commodity.getContract(Contract.convertFormattedMonthToKey(getLongOrder().getMonth()));
            return  (int)((contract.getPrices().getClose() - getLongOrder().getActualPrice().doubleValue()) *
                    getLongOrder().getNumberOfContracts().intValue() / commodity.getTickSize() * commodity.getTickPrice() *
                    100.0) / 100.0;

        } else {
            return 0;
        }

        } finally {
        }
    }


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
