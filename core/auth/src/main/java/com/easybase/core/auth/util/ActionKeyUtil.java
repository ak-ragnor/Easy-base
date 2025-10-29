package com.easybase.core.auth.util;

import java.util.Locale;

/**
 * @author Akhash R
 */
public class ActionKeyUtil {

    public static String getActionKey(String table, String action) {
        return table.toUpperCase(Locale.ROOT) + ":" + action.toUpperCase(Locale.ROOT);
    }
}
