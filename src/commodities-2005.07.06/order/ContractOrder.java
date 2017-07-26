/*  x Properly Documented
 */
package commodities.order;

import java.io.*;
import java.util.*;

import commodities.dataaccess.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The ContractOrder class contains the information for a single
 *  buy or sell contract order.  This includes the commodity, the
 *  number of contracts, the order's status, and any prices associated
 *  with the contract.
 *
 *  If an order is not totally fulfilled then a single order may need to
 *  be broken up into multiple orders, one for the fulfilled amount and
 *  a second for the remaining unfullfilled amount.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class ContractOrder {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The singleton instance of the DataManagerFactory. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

    /** Properties file identifier for the properties manager. */
    private static final String PROPERTIES_FILE = "commodities";
    /** The property name for date formatting on the order screen. */
    private static final String DATE_FORMAT = "dateformat.orderscreen";

    /** A long (buy) possition for an order. */
    public static final boolean LONG = true;
    /** A short (sell) possition for an order. */
    public static final boolean SHORT = false;
    /** Market orders are represented by a special constant value. */
    public static final Double MARKET_ORDER = new Double(-1.0);


    /** The index of the last order placed */
    private static long index = -1;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** A unique id to identify this contract order.  */
    private long    id;
    /** The account number for this order. */
    private int acctNumber;
    /** LONG or SHORT order. */
    private boolean orderType;
    /**
     *  The date the order was recorded to have taken place.
     *  This is not necessarially the date that the contract
     *  was entered into the system, but rather the date that
     *  was entered by the user.
     */
    private Date    date = null;
    /** The number of contracts in this order. */
    private Integer number = new Integer(0);
    /** The month of the commodity. */
    private String  month = "";
    /** The name of the commodity. */
    private String  commodityName = "";              // ???? should this be the commodity itself or maybe the contract
    /** The desired price. */
    private Double  desiredPrice = new Double(0);
    /** The stop price. */
    private Double  stopPrice = new Double(0);
    /** The status of this order. */
    private String  status = ContractOrderTablePanel.NO_STATUS;
    /** The actual price that the order was filled at. */
    private Double  actualPrice = new Double(0);
    /** A ticket associated with the order. */
    private String  ticket = "";
    /** A note associated with the order. */
    private String  note = "";

    /** The offsetting order of this order. */
    private long  offsettingOrder = -1;
    /** The date this order was considered. */
    private Date  dateConsidered = null;
    /** The date this order was placed. */
    private Date  datePlaced = null;
    /** The date this order was filled. */
    private Date  dateFilled = null;
    /** The date this order had a cancel placed. */
    private Date  dateCancelPlaced = null;
    /** The date this order was cancelled. */
    private Date  dateCancelled = null;
    /** The date this order had a stop loss. */
    private Date  dateStopLoss = null;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a single commodity order.
     *
     *  @param  acctNumber  The account number that owns the order
     *  @param  orderType   LONG for a buy order or SHORT for a sell order
     */
    public ContractOrder(int acctNumber, boolean orderType) {
        this(acctNumber,
             orderType,
             null,
             new Integer(0),
             "",
             "",
             new Double(0),
             new Double(0),
             ContractOrderTablePanel.NO_STATUS,
             new Double(0),
             "",
             "");
    }

    /**
     *  Creates a single commodity order.
     *
     *  @param  acctNumber      The account number that owns the order
     *  @param  orderType       LONG for a buy order or SHORT for a sell order
     *  @param  date            the date
     *  @param  number          the number of contracts
     *  @param  month           the month of the commodity
     *  @param  commodityName   the name of the commodity and exchange
     *  @param  desiredPrice    the desired price
     *  @param  stopPrice       the stop price
     *  @param  status          the order's status
     *  @param  actualPrice     the actual price
     *  @param  ticket          the ticket
     *  @param  note            the note
     */
    public ContractOrder(int acctNumber,
                         boolean orderType,
                         Date date,
                         Integer number,
                         String month,
                         String commodityName,
                         Double desiredPrice,
                         Double stopPrice,
                         String status,
                         Double actualPrice,
                         String ticket,
                         String note) {

        this(-1,
             acctNumber,
             orderType,
             date,
             number,
             month,
             commodityName,
             desiredPrice,
             stopPrice,
             status,
             actualPrice,
             ticket,
             note,
             -1l,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    /**
     *  Creates a single commodity order.
     *
     *  @param  id              the order id
     *  @param  acctNumber      The account number that owns the order
     *  @param  orderType       LONG for a buy order or SHORT for a sell order
     *  @param  date            the date
     *  @param  number          the number of contracts
     *  @param  month           the month of the commodity
     *  @param  commodityName   the name of the commodity and exchange
     *  @param  desiredPrice    the desired price
     *  @param  stopPrice       the stop price
     *  @param  status          the order's status
     *  @param  actualPrice     the actual price
     *  @param  ticket          the ticket
     *  @param  note            the note
     *  @param  offsettingOrder the offsetting order or -1 for none
     *  @param  dateConsidered  the date the order was considered or null
     *  @param  datePlaced      the date the order was placed or null
     *  @param  dateFilled      the date the order was filled or null
     *  @param  dateCancelPlaced    the date the order had a cancel placed or null
     *  @param  dateCancelled   the date the order was cancelled or null
     *  @param  dateStopLoss    the date the order had a stop loss or null
     */
    public ContractOrder(long id,
                         int acctNumber,
                         boolean orderType,
                         Date date,
                         Integer number,
                         String month,
                         String commodityName,
                         Double desiredPrice,
                         Double stopPrice,
                         String status,
                         Double actualPrice,
                         String ticket,
                         String note,
                         long   offsettingOrder,
                         Date   dateConsidered,
                         Date   datePlaced,
                         Date   dateFilled,
                         Date   dateCancelPlaced,
                         Date   dateCancelled,
                         Date   dateStopLoss) {
        this(id,
             acctNumber,
             orderType,
             date,
             number,
             month,
             commodityName,
             desiredPrice,
             stopPrice,
             status,
             actualPrice,
             ticket,
             note,
             offsettingOrder,
             dateConsidered,
             datePlaced,
             dateFilled,
             dateCancelPlaced,
             dateCancelled,
             dateStopLoss,
             false);
    }

    /**
     *  Creates a single commodity order.
     *
     *  @param  id              the order id
     *  @param  acctNumber      The account number that owns the order
     *  @param  orderType       LONG for a buy order or SHORT for a sell order
     *  @param  date            the date
     *  @param  number          the number of contracts
     *  @param  month           the month of the commodity
     *  @param  commodityName   the name of the commodity and exchange
     *  @param  desiredPrice    the desired price
     *  @param  stopPrice       the stop price
     *  @param  status          the order's status
     *  @param  actualPrice     the actual price
     *  @param  ticket          the ticket
     *  @param  note            the note
     *  @param  offsettingOrder the offsetting order or -1 for none
     *  @param  dateConsidered  the date the order was considered or null
     *  @param  datePlaced      the date the order was placed or null
     *  @param  dateFilled      the date the order was filled or null
     *  @param  dateCancelPlaced    the date the order had a cancel placed or null
     *  @param  dateCancelled   the date the order was cancelled or null
     *  @param  dateStopLoss    the date the order had a stop loss or null
     *  @param  initialLoad     is this the initial screen load
     */
    public ContractOrder(long id,
                         int acctNumber,
                         boolean orderType,
                         Date date,
                         Integer number,
                         String month,
                         String commodityName,
                         Double desiredPrice,
                         Double stopPrice,
                         String status,
                         Double actualPrice,
                         String ticket,
                         String note,
                         long   offsettingOrder,
                         Date   dateConsidered,
                         Date   datePlaced,
                         Date   dateFilled,
                         Date   dateCancelPlaced,
                         Date   dateCancelled,
                         Date   dateStopLoss,
                         boolean initialLoad) {
        if (id == -1) {
            setId();
        } else {
            this.id             = id;
        }
        this.acctNumber         = acctNumber;
        this.orderType          = orderType;

        if (date == null) {
            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            calendar.clearTime();
            this.date = calendar.getTime();
        } else {
            this.date = date;
        }

        this.number             = number;
        this.month              = month;
        this.commodityName      = commodityName;
        this.desiredPrice       = desiredPrice;
        this.stopPrice          = stopPrice;
        this.status             = status;
        this.actualPrice        = actualPrice;
        this.ticket             = ticket;
        this.note               = note;
        this.offsettingOrder    = offsettingOrder;
        this.dateConsidered     = dateConsidered;
        this.datePlaced         = datePlaced;
        this.dateFilled         = dateFilled;
        this.dateCancelPlaced   = dateCancelPlaced;
        this.dateCancelled      = dateCancelled;
        this.dateStopLoss       = dateStopLoss;

        if (!initialLoad && !ContractOrderTablePanel.NO_STATUS.equals(status)) {
            recordOrder();
        }
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the orders unique id.
     *
     *  @return     the order's unique id.
     */
    public  long getId() {
        return id;
    }

    /**
     *  Get the account number which owns this order.
     *  @return     the account number.
     */
    public  int getAccountNumber() {
        return acctNumber;
    }

    /**
     *  Get if this order is a buy (long) order or a sell (short) order.  True
     *  indicates a buy order, false indicates a sell order.
     *  @return     true if a buy order; false for a sell order
     */
    public  boolean isLongOrder() {
        return orderType;
    }

    /**
     *  Get the date the order was recorded as having taken place.
     *  @return     the date of the order.
     */
    public  Date getDate() {
        return date;
    }

    /**
     *  Get the number of contracts in this order.
     *  @return     the number of contracts.
     */
    public  Integer getNumberOfContracts() {
        return number;
    }

    /**
     *  Get the month of the commodity.
     *  @return     the month of the commodity
     */
    public  String getMonth() {
        return month;
    }

    /**
     *  Get the name of the commodity.
     *  @return     the name and exchange of the commodity
     */
    public  String getCommodityName() {
        return commodityName;
    }

    /**
     *  Get the desired price for this order.  If there is no desired
     *  price, then 0 is returned.
     *  @return     the desired price
     */
    public  Double getDesiredPrice() {
        return desiredPrice;
    }

    /**
     *  Get the stop price for this order.  If there is no stop price,
     *  then 0 is returned.
     *  @return     the stop price
     */
    public  Double getStopPrice() {
        return stopPrice;
    }

    /**
     *  Get the actual price the order was fullfilled at.  If there is
     *  no actual price, then 0 is returned.
     *  @return     the actual price
     */
    public  Double getActualPrice() {
        return actualPrice;
    }

    /**
     *  Get the status of this order.
     *  @return     the order's status
     */
    public  String getStatus() {
        try {
        if ("".equals(status)) {
            return " ";
        } else {
            return status;
        }
        } finally {
        }
    }

    /**
     *  Get the ticket for this order.
     *  @return     the ticket
     */
    public  String getTicket() {
        return ticket;
    }

    /**
     *  Get the note for this order.
     *  @return     the note
     */
    public  String getNote() {
        return note;
    }

    /**
     *  Get the offsetting order that filled this one.  A returned
     *  value of -1 indicates that this order has not been filled.
     *  @return     the offsetting order number or -1 if none
     */
    public  long getOffsettingOrder() {
        return offsettingOrder;
    }

    /**
     *  Get the date the order was considered.  If the order has not
     *  been considered, null is returned.
     *  @return     the date the order was considered.
     */
    public  Date getDateConsidered() {
        return dateConsidered;
    }

    /**
     *  Get the date the order was placed.  If the order has not
     *  been placed, null is returned.
     *  @return     the date the order was placed.
     */
    public  Date getDatePlaced() {
        return datePlaced;
    }

    /**
     *  Get the date the order was filled.  If the order has not
     *  been filled, null is returned.
     *  @return     the date the order was filled.
     */
    public  Date getDateFilled() {
        return dateFilled;
    }

    /**
     *  Get the date the order had the cancel placed.  If the order has not
     *  had a cancel placed, null is returned.
     *  @return     the date the order had a cancel placed.
     */
    public  Date getDateCancelPlaced() {
        return dateCancelPlaced;
    }

    /**
     *  Get the date the order was cancelled.  If the order has not
     *  been cancelled, null is returned.
     *  @return     the date the order was cancelled.
     */
    public  Date getDateCancelled() {
        return dateCancelled;
    }

    /**
     *  Get the date the order had a stop loss.  If the order has not
     *  had a stop loss, null is returned.
     *  @return     the date the order had a stop loss.
     */
    public  Date getDateStopLoss() {
        return dateStopLoss;
    }


    /**
     *  Set the orders unique id.
     */
    private void setId() {
        if (index == -1) {
            try {
                index = dataManager.getLastOrderIndex();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        id = ++index;
    }


    /**
     *  Sets the date the order was recorded as having taken place.
     *  @param  date    the date
     */
    public  void setDate (String date) {
        this.date = FormatDate.formatDateString(date, PropertyManager.instance().getProperties(PROPERTIES_FILE).getProperty(DATE_FORMAT));
    }

    /**
     *  Set the number of contracts in this order.
     *  @param  number  the number of contracts
     */
    public  void setNumberOfContracts(Integer number) {
        if (!this.number.equals(number)) {
            this.number = number;
            if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
                recordOrder();
            }
        }
    }

    /**
     *  Set the number of contracts in this order.
     *  @param  number  the number of contracts
     */
    public  void setNumberOfContracts(int number) {
        setNumberOfContracts(new Integer(number));
    }

    /**
     *  Set the month of the commodity.  This is in the format of the month
     *  abbreviation and year.
     *  @param  month   the month of the commodity
     */
    public  void setMonth(String month) {
        this.month = month.trim();
    }

    /**
     *  Set the commodity name, including exchange.
     *  @param  name    the name of the commodity and exchange
     */
    public  void setCommodityName(String name) {
        commodityName = name.trim();
    }

    /**
     *  Set the desired price for this order.
     *  @param  price   the desired price
     */
    public  void setDesiredPrice(Double price) {
        if (!desiredPrice.equals(price)) {
            desiredPrice = price;
            if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
                recordOrder();
            }
        }
    }

    /**
     *  Set the stop price for this order.
     *  @param  price   the stop price
     */
    public  void setStopPrice(Double price) {
        if (!stopPrice.equals(price)) {
            stopPrice = price;
            if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
                recordOrder();
            }
        }
    }

    /**
     *  Set the actual price for this order.
     *  @param  price   the actual price
     */
    public  void setActualPrice(Double price) {
        if (!actualPrice.equals(price)) {
            actualPrice = price;
            recordOrder();
        }
    }

    /**
     *  Set the status for this order.
     *  @param  status  the order's status
     */
    public  void setStatus(String status) {
        if (!this.status.equals(status)) {
            this.status = status;
            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            calendar.clearTime();

            if (ContractOrderTablePanel.NO_STATUS.equals(status)) {
                delete();
            } else if (ContractOrderTablePanel.CONSIDERATION.equals(status)) {
                dateConsidered = calendar.getTime();
                datePlaced = null;
                offsettingOrder = -1;
                recordOrder();
            } else if (ContractOrderTablePanel.PLACED.equals(status)) {
                datePlaced = calendar.getTime();
                dateFilled = null;
                offsettingOrder = -1;
                recordOrder();
            } else if (ContractOrderTablePanel.FILLED.equals(status)) {
                dateFilled = calendar.getTime();
                dateCancelled = null;
                dateStopLoss = null;
                recordOrder();
            } else if (ContractOrderTablePanel.CANCEL_PLACED.equals(status)) {
                dateCancelPlaced = calendar.getTime();
                dateFilled = null;
                dateCancelled = null;
                offsettingOrder = -1;
                recordOrder();
            } else if (ContractOrderTablePanel.CANCELLED.equals(status)) {
                dateCancelled = calendar.getTime();
                dateFilled = null;
                offsettingOrder = 0;
                recordOrder();
            } else if (ContractOrderTablePanel.STOP_LOSS.equals(status)) {
                dateStopLoss = calendar.getTime();
                recordOrder();
            }
        }
    }

    /**
     *  Set the ticket.
     *  @param  ticket    the ticket
     */
    public  void setTicket(String ticket) {
        if (!this.ticket.equals(ticket.trim())) {
            this.ticket = ticket.trim();
            if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
                recordOrder();
            }
        }
    }

    /**
     *  Set the note.
     *  @param  note    the note
     */
    public  void setNote(String note) {
        if (!this.note.equals(note.trim())) {
            this.note = note.trim();
            if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
                recordOrder();
            }
        }
    }

    /**
     *  set the offsetting order that filled this one.
     *  @param  offsettingOrder     the offsetting order number
     */
    public  void setOffsettingOrder(long offsettingOrder) {
        this.offsettingOrder = offsettingOrder;
        recordOrder();
    }


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    /**
     *  Indicates if this order is equal to another order.  Equal, in this
     *  case, indicates that the orders have the same account number, order type,
     *  initial date, contract, desired price, stoploss price, status, and actual price.
     *  It does not include the number of contracts.
     *
     *  @param  toOrder     the order to compare this one to
     */
    public boolean equals(ContractOrder toOrder) {
        if (acctNumber == toOrder.getAccountNumber() &&
            orderType == toOrder.isLongOrder() &&
            date.equals(toOrder.getDate()) &&
            month.equals(toOrder.getMonth()) &&
            commodityName.equals(toOrder.getCommodityName()) &&
            desiredPrice.equals(toOrder.getDesiredPrice()) &&
            stopPrice.equals(toOrder.getStopPrice()) &&
            actualPrice.equals(toOrder.getActualPrice()) &&
            status.equals(toOrder.getStatus())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  Creates a new order with the requested number of contracts
     *  in the new order and deducted from the original order.  If
     *  the number of contracts in the new order is not less than
     *  the original order, then a null order is returned and the
     *  original order remains unchanged.
     *
     *  @param  numberInNew     number of contracts in new order
     */
    public ContractOrder split(int numberInNew) {
        ContractOrder order = null;
        if (numberInNew >= number.intValue()) {
            return null;
        }
        number = new Integer(number.intValue() - numberInNew);
        recordOrder();
        order = new ContractOrder(-1,
                                  acctNumber,
                                  orderType,
                                  date,
                                  new Integer(numberInNew),
                                  month,
                                  commodityName,
                                  desiredPrice,
                                  stopPrice,
                                  status,
                                  actualPrice,
                                  ticket,
                                  note,
                                  -1,
                                  dateConsidered,
                                  datePlaced,
                                  dateFilled,
                                  dateCancelPlaced,
                                  dateCancelled,
                                  dateStopLoss);
        return order;
    }

    /**
     *  Combines the addOrder with this order.  All dates fields are
     *  updated with the most current date between the two orders.
     *  The addOrder is also deleted.
     *
     *  @param  addOrder    the order to be added into this order
     *  @return             true if the combine was successful
     */
    public boolean combine(ContractOrder addOrder) {
        if ((addOrder == null) ||
            !this.equals(addOrder) ||
            (addOrder.getNumberOfContracts().intValue() <= 0)) {
            return false;
        }

        number = new Integer(number.intValue() + addOrder.getNumberOfContracts().intValue());

        dateConsidered  = (dateConsidered == null || dateConsidered.compareTo(addOrder.getDateConsidered()) < 0)?addOrder.getDateConsidered():dateConsidered;
        datePlaced      = (datePlaced== null || datePlaced.compareTo(addOrder.getDatePlaced()) < 0)?addOrder.getDatePlaced():datePlaced;
        dateFilled      = (dateFilled == null || dateFilled.compareTo(addOrder.getDateFilled()) < 0)?addOrder.getDateFilled():dateFilled;
        dateCancelPlaced = (dateCancelPlaced == null || dateCancelPlaced.compareTo(addOrder.getDateCancelPlaced()) < 0)?addOrder.getDateCancelPlaced():dateCancelPlaced;
        dateCancelled   = (dateCancelled == null || dateCancelled.compareTo(addOrder.getDateCancelled()) < 0)?addOrder.getDateCancelled():dateCancelled;
        dateStopLoss    = (dateStopLoss == null || dateStopLoss.compareTo(addOrder.getDateStopLoss()) < 0)?addOrder.getDateStopLoss():dateStopLoss;

        recordOrder();
        addOrder.delete();
        return true;
    }

   /**
     *  Deletes an order from the database.  This is usually from combining orders.
     */
    public void delete() {
//        if (!ContractOrderTablePanel.NO_STATUS.equals(status)) {
            try {
                dataManager.deleteOrderEntry(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }

//        id                  = -1;
//        orderType           = false;
        CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
        calendar.clearTime();
        date                = calendar.getTime();
        number              = new Integer(0);
//        month               = "";
//        commodityName       = "";
        desiredPrice        = new Double(0);
        stopPrice           = new Double(0);
        status              = ContractOrderTablePanel.NO_STATUS;
        actualPrice         = new Double(0);
        ticket              = "";
        note                = "";
        offsettingOrder     = -1;
        dateConsidered      = null;
        datePlaced          = null;
        dateFilled          = null;
        dateCancelPlaced    = null;
        dateCancelled       = null;
        dateStopLoss        = null;
    }

    /**
     *  Record the current state of the order.
     */
    private void recordOrder() {
        try {
            dataManager.updateOrderEntry(id,
                                         acctNumber,
                                         orderType,
                                         date,
                                         number,
                                         month,
                                         commodityName,
                                         desiredPrice,
                                         stopPrice,
                                         status,
                                         actualPrice,
                                         ticket,
                                         note,
                                         offsettingOrder,
                                         dateConsidered,
                                         datePlaced,
                                         dateFilled,
                                         dateCancelPlaced,
                                         dateCancelled,
                                         dateStopLoss);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
