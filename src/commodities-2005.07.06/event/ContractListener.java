/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

/**
 *  The ContractListener is an EventListener that listens for contracts.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public interface ContractListener extends EventListener {
    /**
     *  Invoked when a contract is added to the system.
     *  @param  e   the PriceEvent
     */
    public void addContract(ContractEvent e);
}
