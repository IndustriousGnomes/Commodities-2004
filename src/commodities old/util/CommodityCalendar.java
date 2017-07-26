/*  x Properly Documented
 */
package commodities.util;

import java.util.*;
import com.util.GregorianWeekdayCalendar;

/**
 *  The CommodityCalendar class extends the gregorian weekday calendar
 *  to override normal calendar functions for commodity processing.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityCalendar extends GregorianWeekdayCalendar {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static int alternateYear = -1;
    private static int alternateMonth = -1;
    private static int alternateDate = -1;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Constructs a default CommodityCalendar using the current time in the
     *  default time zone with the default locale.
     */
    public CommodityCalendar() {
        super();
//        this(2003, 9, 29);
    }

    /**
     *  Constructs a CommodityCalendar with the given date set in the default
     *  time zone with the default locale.
     */
    public CommodityCalendar(Date date) {
        super();
        if (date != null) {
            setTime(date);
        }
    }

    /**
     *  Constructs a CommodityCalendar with the given date set in the default
     *  time zone with the default locale.
     */
    public CommodityCalendar(int year, int month, int date) {
        super(year, month, date);
    }

    /**
     *  Constructs a CommodityCalendar with the given date and time set for
     *  the default time zone with the default locale.
     */
    public CommodityCalendar(int year, int month, int date, int hour, int minute) {
        super(year, month, date, hour, minute);
    }

    /**
     *  Constructs a CommodityCalendar with the given date and time set for
     *  the default time zone with the default locale.
     */
    public CommodityCalendar(int year, int month, int date, int hour, int minute, int second) {
        super(year, month, date, hour, minute, second);
    }

    /**
     *  Constructs a CommodityCalendar based on the current time in the
     *  default time zone with the given locale.
     */
    public CommodityCalendar(Locale aLocale) {
        super(aLocale);
    }

    /**
     *  Constructs a CommodityCalendar based on the current time in the
     *  given time zone with the default locale.
     */
    public CommodityCalendar(TimeZone zone) {
        super(zone);
    }

    /**
     *  Constructs a CommodityCalendar based on the current time in the
     *  given time zone with the given locale.
     */
    public CommodityCalendar(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }


/* *************************************** */
/* *** Calendar METHODS                *** */
/* *************************************** */
    /**
     *  Gets a calendar using the default time zone and locale or the simulated
     *  date. The Calendar returned is based on the current time in the default
     *  time zone with the default locale.
     *
     *  If the simulated date has been set, but not cleared, the returned calendar
     *  will be set to the simulator date.
     *
     *  @return     a calendar
     */
    public static Calendar getInstance() {
        if (alternateYear == -1) {
            return new CommodityCalendar();
        } else {
            return new CommodityCalendar(alternateYear, alternateMonth, alternateDate);
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Sets the calendar date for the Commodities application to the current
     *  date for this calendar.
     */
    public void setSimulatorDate() {
        alternateYear    = get(Calendar.YEAR);
        alternateMonth   = get(Calendar.MONTH);
        alternateDate    = get(Calendar.DATE);
    }

    /**
     *  Sets the calendar date for the Commodities application to today's date.
     */
    public void clearSimulatorDate() {
        alternateYear    = -1;
        alternateMonth   = -1;
        alternateDate    = -1;
    }
}