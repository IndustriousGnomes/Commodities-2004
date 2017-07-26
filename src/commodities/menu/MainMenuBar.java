/*  x Properly Documented
 */
package commodities.menu;

import javax.swing.*;

/**
 *  The MainMenuBar class controls the menus and their contents for the commodities application.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.15
 */

public class MainMenuBar extends JMenuBar {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a menu bar with all of the menues.
     */
    public MainMenuBar() {
        add(new FileMenu());
        add(new EditMenu());
        add(new AccountMenu());
        add(new CommodityMenu());
        add(new UtilityMenu());
        add(new PriceMenu());
        add(new GraphMenu());
        add(new TestMenu());
        add(new TestOptimizationMenu());
    }

}
