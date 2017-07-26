/*  x Properly Documented
 */
package prototype.commodities.dataaccess.dataloader;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Properties;
import prototype.commodities.dataaccess.*;
import prototype.commodities.*; // debug only

/**
 *  The PriceFormatter class converts standardly formatted prices to
 *  the appropriate decimal price for storage into the database.
 *
 *  @author J.R. Titko
 */
public class PriceFormatter {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Determines the format of the price for the given commodity symbol
     *  and returns the appropriately formatted price.
     *
     *  @param  symbol  The commodity symbol to format the price for.
     *  @param  price   The unformatted price.
     *  @return         The formatted price.
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
     */
    private static double formatEighths(double price) {
        double number = (int)(price / 10);
        double decimal = (int)(price % 10);
        return number + decimal / 8;
    }

    /**
     *  Determines the decimal places for a price that is recorded in
     *  decimals.
     */
    private static double formatDecimal(double price, double tickSize) {
        while (tickSize < 1) {
            tickSize *= 10;
            price /= 10;
        }
        return price;
    }
}