package com.viking.util;

import com.android.ddmlib.DdmConstants;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public class Constant {

    public static final String TAG = "Viking" ;

    public static final String APP_NAME = "Logcat" ;

    public static final String APP_VERSION = "1.0.0" ;

    public static final String PREFERENCE_LOGSASH = "logSashLocation";
    public static final String PREFERENCE_SASH = "sashLocation";

    /** Preference key to use for storing list of logcat filters. */
    public static final String LOGCAT_FILTERS_LIST = "logcat.view.filters.list";

    /** Preference key to use for storing font settings. */
    public static final String LOGCAT_VIEW_FONT_PREFKEY = "logcat.view.font";

    /** Preference key to use for deciding whether to automatically en/disable scroll lock. */
    public static final String AUTO_SCROLL_LOCK_PREFKEY = "logcat.view.auto-scroll-lock";

    // Preference keys for message colors based on severity level
    public static final String MSG_COLOR_PREFKEY_PREFIX = "logcat.msg.color.";
    public static final String VERBOSE_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "verbose";
    public static final String DEBUG_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "debug";
    public static final String INFO_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "info";
    public static final String WARN_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "warn";
    public static final String ERROR_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "error";
    public static final String ASSERT_COLOR_PREFKEY = MSG_COLOR_PREFKEY_PREFIX + "assert";

    // Use a monospace font family
    public static final String FONT_FAMILY =
            DdmConstants.CURRENT_PLATFORM == DdmConstants.PLATFORM_DARWIN ? "Monaco":"Courier New";
}
