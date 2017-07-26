/*  _ Properly Documented
 */
/**
 *  The CommodityCalendar class extends the gregorian calendar to override
 *  normal calendar functions for commodity processing.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.util;

import java.util.*;
import prototype.commodities.*; // debug only

public class CommodityCalendar extends GregorianCalendar {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static int alternateYear = -1;
    private static int alternateMonth = -1;
    private static int alternateDate = -1;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public CommodityCalendar() {
        super();
//        this(2003, 9, 29);
    }
    public CommodityCalendar(int year, int month, int date) {
        super(year, month, date);
    }
    public CommodityCalendar(int year, int month, int date, int hour, int minute) {
        super(year, month, date, hour, minute);
    }
    public CommodityCalendar(int year, int month, int date, int hour, int minute, int second) {
        super(year, month, date, hour, minute, second);
    }
    public CommodityCalendar(Locale aLocale) {
        super(aLocale);
    }
    public CommodityCalendar(TimeZone zone) {
        super(zone);
    }
    public CommodityCalendar(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }


/* *************************************** */
/* *** Calendar METHODS                *** */
/* *************************************** */
    public int getMaximum(int field){ return 0; }

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
     *  Adds a value to the weekdays.  Weekends are ignored in this calculation.
     */
    public void addWeekDays(int offset) {
        int change = offset / Math.abs(offset);
        int offsetWeeks = (change * offset) / 5;
        int offsetDays  = (change * offset) % 5;

        add(DATE, change * offsetWeeks * 7);

        for (int i = 0; i < offsetDays; i++) {
            do {
                add(DATE, change);
            } while ((get(DAY_OF_WEEK) == SATURDAY) || (get(DAY_OF_WEEK) == SUNDAY));
        }
    }

    /**
     *  Clears the time entries in the calendar by setting it to midnight.
     */
    public void clearTime() {
        set(get(YEAR), get(MONTH), get(DATE), 0, 0, 0);
        clear(MILLISECOND);
    }

    /**
     *  Gets the number of days to the next occurance of the day of the week.  If the day of the week
     *  is today, then the following week's day is returned.
     *
     *  @param  dayOfWeek   The day of the week to find the offset to
     *  @return The number of days between the requested day of the week and the current date on the calendar.
     */
    public int getNextDayOfWeekOffset(int dayOfWeek) {
        int delta = dayOfWeek - get(CommodityCalendar.DAY_OF_WEEK);
        if (delta <= 0) {   // Guarantees that we will never have a price on the right edge line.
            delta += 7;
        }
        return delta;
    }

    /**
     *  Sets the calendar to the next occurance of the day of the week.  If the day of the week
     *  is today, then the following week's day is set.
     *
     *  @param  dayOfWeek   The day of the week to set the calendar to
     */
    public void setNextDayOfWeek(int dayOfWeek) {
        add(DATE, getNextDayOfWeekOffset(dayOfWeek));
    }

    /**
     *  Sets the calendar date for the Commodities application to an alternative date.
     *
     *  @param  year    the year to set the alternative date to
     *  @param  month   the month to set the alternative date to
     *  @param  date    the date to set the alternative date to
     */
/*    public void setSimulatorDate(int year, int month, int date) {
        alternateYear    = year;
        alternateMonth   = month;
        alternateDate    = date;
    }

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