package de.universallp.va.core.util.libs;

/**
 * Created by universallp on 30.03.2016 13:32.
 */
public class LibLocalization {
    public static final String GUI_DIST = "va.gui.reachdistance";
    public static final String GUI_FACE = "va.gui.face";
    public static final String GUI_XPHOPPER = "va.gui.xphopper";
    public static final String GUI_FILTEREDHOPPER = "va.gui.filteredhopper";
    public static final String GUI_FILTER = "va.gui.filteredhopper.filter";
    public static final String BTN_MENU = "va.gui.buttonmenu";
    public static final String BTN_BACK = "va.gui.buttonback";
    public static final String GUIDE_DESC = "item.va:vaguide.desc";
    public static final String GUIDE_LOOK = "va.guide.entry.lookup";
    public static final String ENTRY_INTRO = "va.guide.entry.intro";
    public static final String ENTRY_CREDITS = "va.guide.entry.credits";
    public static final String ENTRY_BLOCKPLACER = "va.guide.entry.blockplacer";
    public static final String ENTRY_DISPENSER = "va.guide.entry.dispenser";
    public static final String ENTRY_POKE_STICK = "va.guide.entry.pokestick";
    public static final String ENTRY_XPHOPPER = "va.guide.entry.xphopper";
    public static final String RECIPE_SHAPED = "va.recipe.shaped";
    public static final String RECIPE_SHAPELESS = "va.recipe.shapeless";
    private static final String PAGE = "va.guide.entry.";

    public static String getGuidePage(String entry, int p) {
        return PAGE + entry + ".page" + p;
    }
}
