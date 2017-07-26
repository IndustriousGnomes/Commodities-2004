/*  x Properly Documented
 */
package commodities.event;

import java.util.*;
/**
 *  The ContractSelectionListener is an EventListener that listens for contracts
 *  to be selected.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public interface ContractSelectionListener extends EventListener {
    /**
     *  Invoked when the contract selection changes.  This is a 2 pass
     *  execution so that some classes that need to execute first can
     *  process when init is true and the remaining listeners can
     *  process when init is false.
     *
     *  @param  e       the ContractSelectionEvent
     *  @param  init    true if first pass for initialization, otherwise false
     */
    public void selectContract(ContractSelectionEvent e, boolean init);

}
