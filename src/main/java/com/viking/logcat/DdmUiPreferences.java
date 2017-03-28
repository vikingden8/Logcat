package com.viking.logcat;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference entry point for ddmuilib. Allows the lib to access a preference
 * store (org.eclipse.jface.preference.IPreferenceStore) defined by the
 * application that includes the lib.
 */
public final class DdmUiPreferences {

    public static final int DEFAULT_THREAD_REFRESH_INTERVAL = 4;  // seconds

    private static int sThreadRefreshInterval = DEFAULT_THREAD_REFRESH_INTERVAL;

    private static IPreferenceStore mStore;

    private static String sSymbolLocation =""; //$NON-NLS-1$
    private static String sAddr2LineLocation =""; //$NON-NLS-1$
    private static String sAddr2LineLocation64 =""; //$NON-NLS-1$
    private static String sTraceviewLocation =""; //$NON-NLS-1$

    public static void setStore(IPreferenceStore store) {
        mStore = store;
    }

    public static IPreferenceStore getStore() {
        return mStore;
    }

    public static int getThreadRefreshInterval() {
        return sThreadRefreshInterval;
    }

    public static void setThreadRefreshInterval(int port) {
        sThreadRefreshInterval = port;
    }

    public static String getSymbolDirectory() {
        return sSymbolLocation;
    }

    public static void setSymbolsLocation(String location) {
        sSymbolLocation = location;
    }

    public static String getAddr2Line() {
        return sAddr2LineLocation;
    }

    public static void setAddr2LineLocation(String location) {
        sAddr2LineLocation = location;
    }

    public static String getAddr2Line64() {
        return sAddr2LineLocation64;
    }

    public static void setAddr2LineLocation64(String location) {
        sAddr2LineLocation64 = location;
    }

    public static String getTraceview() {
        return sTraceviewLocation;
    }

    public static void setTraceviewLocation(String location) {
        sTraceviewLocation = location;
    }


}
