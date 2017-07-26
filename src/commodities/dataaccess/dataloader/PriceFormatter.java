/*  x Properly Documented
 */
package commodities.dataaccess.dataloader;

import commodities.commodity.*;

/**
 *  The PriceFormatter class converts standardly formatted prices to
 *  the appropriate decimal price for storage into the database.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class PriceFormatter {
/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Determines the format of the price for the given commodity symbol
     *  and returns the appropriately formatted price.
     *
     *  @param  symbol  the commodity symbol to format the price for
     *  @param  price   the unformatted price
     *  @return         the formatted price
     */
    public static double formatPrice(String symbol, double price) {
        double tickSize = Commodity.bySymbol(symbol).getTickSize();

        if (tickSize == 0.25) {
            return formatEighths(price);
        } else {
            return formatDecimal(price, tickSize);
        }
    }

    /**
     *  Determines the decimal places for a price that is recorded in
     *  eighths.
     *
     *  @param  price   the unformatted price
     *  @return         the formatted price
     */
    private static double formatEighths(double price) {
        double number = (int)(price / 10);
       double decimal = (int)(price % 10);
        return number + decimal / 8;
    }

    /**
     *  Determines the decimal places for a price that is recorded in
     *  decimals.
     *
     *  @param  price       the unformatted price
     *  @param  tickSize    the size of a price tick
     *  @return     the formatted price
     */
    private static double formatDecimal(double price, double tickSize) {
        while (tickSize < 1) {
            tickSize *= 10;
            price /= 10;
        }
        return price;
    }
}