/*  _ Properly Documented
 */
/**
 *  The MainMenuBar class controls the menus and their contents for the commodities application.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.menu;

import javax.swing.*;
import prototype.commodities.*; // debug only

public class MainMenuBar extends JMenuBar {
    private static boolean DEBUG = false;

    public MainMenuBar() {
Debug.println(DEBUG, this, "MainMenuBar start");
        add(new FileMenu());
        add(new EditMenu());
        add(new ContractMenu());
        add(new PriceMenu());
        add(new TestMenu());
        add(new UtilityMenu());
Debug.println(DEBUG, this, "MainMenuBar end");
    }

}
