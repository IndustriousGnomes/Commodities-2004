/*  x Properly Documented
 */
package prototype.commodities.util;

import java.util.*;
import java.text.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import prototype.commodities.*; // debug only

/**
 *  The FormatDate class formats dates to the format defined in the
 *  properties file with the following properties:
 *
 *  Static Variable         Property Name           Valid Values
 *  ------------------      -------------           ----------------
 *  FORMAT_SCREEN_DATE      dateformat.screen       Valid date format
 *  FORMAT_SQL_DATE         dateformat.sql          Valid date format
 *  FORMAT_SIMULATOR_DATE   dateformat.simulator    Valid date format
 *
 *  @author J.R. Titko
 */

public class FormatDate {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /**
     *  The name of the date format for the screen property in the properties file.
     *  The associated property name is 'dateformat.screen'.
     */
    public static final String FORMAT_SCREEN_DATE   = "dateformat.screen";
    /**
     *  The name of the date format for formatting SQL dates in the properties file.
     *  The associated property name is 'dateformat.sql'.
     */
    public static final String FORMAT_SQL_DATE      = "dateformat.sql";
    /**
     *  The name of the date format for the simulator file date in the properties file.
     *  The associated property name is 'dateformat.simulator'.
     */
    public static final String FORMAT_SIMULATOR_DATE= "dateformat.simulator";

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */

    /**
     *  Formats a date into a specific format based on the format
     *  parameter to be retrieved from the properties file.
     *
     *  @param  date        The date to be formatted
     *  @param  formatProp  The property name of the desired format
     */

    public static Date formatDateStringProp (String date, String formatProp) {
        return formatDateString(date, CommodityProperties.instance().getProperty(formatProp));
    }

    /**
     *  Formats a date into a specific format based on the format
     *  parameter to be retrieved from the properties file.
     *
     *  @param  date        The date to be formatted
     *  @param  formatProp  The property name of the desired format
     */
    public static String formatDateProp (Date date, String formatProp) {
        return formatDate(date, CommodityProperties.instance().getProperty(formatProp));
    }

    /**
     *  Formats a date into a specific format based on the format
     *  parameter to be retrieved from the properties file.
     *
     *  @param  date        The date to be formatted
     *  @param  formatProp  The property name of the desired format
     */
    public static String formatDateProp (long date, String formatProp) {
        return formatDate(date, CommodityProperties.instance().getProperty(formatProp));
    }

    /**
     *  Formats a date into a specific format that is defined in
     *  a SimpleDateFormat class.  The format comes in the form of
     *  a date mask (the mask elements can be found in the API).
     *
     *  @param  date    The date to be formatted
     *  @param  format  The format to convert the date to
     */

    public static Date formatDateString (String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date t;
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            System.out.println("unparsable using " + formatter);
            return null;
        }
    }

    /**
     *  Formats a date into a specific format that is defined in
     *  a SimpleDateFormat class.  The format comes in the form of
     *  a date mask (the mask elements can be found in the API).
     *
     *  @param  date    The date to be formatted
     *  @param  format  The format to convert the date to
     */
    public static String formatDate (Date date, String format) {
        SimpleDateFormat outFormat = new SimpleDateFormat(format);
        return outFormat.format(date);
    }

    /**
     *  Formats a date into a specific format that is defined in
     *  a SimpleDateFormat class.  The format comes in the form of
     *  a date mask (the mask elements can be found in the API).
     *
     *  @param  date    The date to be formatted
     *  @param  format  The format to convert the date to
     */
    public static String formatDate (long date, String format) {
        return formatDate(new Date(date), format);
    }
}
