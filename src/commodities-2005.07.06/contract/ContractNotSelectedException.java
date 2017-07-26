/* _ Review Javadocs */
package commodities.contract;

/**
 *  ContractNotSelectedException is generated when a contract has not
 *  been selected yet.
 *
 *  @author     J.R. Titko
 *  @since      1.00
 *  @version    1.00
 */

public class ContractNotSelectedException extends Exception {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create an ContractNotSelectedException without a message.
     */
    public ContractNotSelectedException() {
        super();
    }

    /**
     *  Create an ContractNotSelectedException with the given message.
     *  @param  message The message to put in the Exception.
     */
    public ContractNotSelectedException(String message) {
        super(message);
    }
}