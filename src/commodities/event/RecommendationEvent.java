/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

import commodities.contract.*;
import commodities.tests.*;

/**
 *  RecommendationEvent indicates that a new recommendation was created.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.28
 */
public class RecommendationEvent extends EventObject {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The date the request is for. */
    private java.util.Date date;
    /** The contract affected by the event. */
    private Recommendation recommendation;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a RecommendationEvent to inform listeners a recommendation is being requested
     *  but has not yet been determined.
     *
     *  @param  source  the contract the event was issued for
     *  @param  date    the date the request is being made for
     */
    public RecommendationEvent(Object source, java.util.Date date) {
        super(source);
        this.date = date;
        this.recommendation = null;
    }

    /**
     *  Creates a RecommendationEvent to inform listeners that a new recommendation
     *  has been made.
     *
     *  @param  source          the contract the event was issued for
     *  @param  recommendation  the recommendation
     */
    public RecommendationEvent(Object source, Recommendation recommendation) {
        super(source);
        this.date = recommendation.getDate();
        this.recommendation = recommendation;
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

    /**
     *  Get the date the recommendation is for.
     *
     *  @return     the date of the recommendation
     */
    public java.util.Date getDate() {
        return date;
    }

    /**
     *  Get the recommendation.  This will be null if the recommendation
     *  is not yet prepared.  This condition can exist for if the event was
     *  issued to inform listeners that a recommendation request has been made.
     *
     *  @return     the recommendation if it is ready, otherwise null
     */
    public Recommendation getRecommendation() {
        return recommendation;
    }
}
