/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

/**
 *  The RecommendationListener is an EventListener that listens for recommendations.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.28
 */

public interface RecommendationListener extends EventListener {
    /**
     *  Invoked when a recommendation has been requested.
     *  @param  e   the RecommendationEvent
     */
    public void recommendationRequested(RecommendationEvent e);

    /**
     *  Invoked when a short term recommendation is changed.
     *  @param  e   the RecommendationEvent
     */
    public void changeShortTermRecommondation(RecommendationEvent e);

    /**
     *  Invoked when a medium term recommendation is changed.
     *  @param  e   the RecommendationEvent
     */
    public void changeMediumTermRecommondation(RecommendationEvent e);

    /**
     *  Invoked when a long term recommendation is changed.
     *  @param  e   the RecommendationEvent
     */
    public void changeLongTermRecommondation(RecommendationEvent e);
}
