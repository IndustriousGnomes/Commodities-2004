/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

import commodities.contract.*;

/**
 *  ContractSelectionEvent indicates that the selected contract has changed.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class ContractSelectionEvent extends EventObject {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a ContractSelectionEvent to inform listeners that the selected contract has
     *  changed.
     *
     *  @param  source  the contract the event was issued for
     */
    public ContractSelectionEvent(Object source) {
        super(source);
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the contract affected.
     *  @return     the contract
     */
    public Contract getContract() {
        return (Contract)source;
    }

}
