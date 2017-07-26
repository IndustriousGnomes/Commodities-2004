/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

import commodities.contract.*;

/**
 *  ContractEvent indicates that a new contract was added to the system.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class ContractEvent extends EventObject {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The contract affected by the event. */
    private Contract contract;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a ContractEvent to inform listeners that the current price has
     *  changed.
     *
     *  @param  source      the commodity the event was issued for
     *  @param  contract    the contract affected
     */
    public ContractEvent(Object source, Contract contract) {
       super(source);
        this.contract = contract;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the prices to display.
     */
    public Contract getContract() {
        return contract;
    }
}
