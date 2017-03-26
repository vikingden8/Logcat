package com.viking;

import com.android.sdkstats.DdmsPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public final class PrefsDialog {

    public final static String SHELL_X = "shellX";
    public final static String SHELL_Y = "shellY";
    public final static String SHELL_WIDTH = "shellWidth";
    public final static String SHELL_HEIGHT = "shellHeight";

    // Preference store.
    private static DdmsPreferenceStore mStore = new DdmsPreferenceStore();

    /**
     * Private constructor -- do not instantiate.
     */
    private PrefsDialog() {}

    /**
     * Return the PreferenceStore that holds our values.
     *
     * @deprecated Callers should use {@link DdmsPreferenceStore} directly.
     */
    @Deprecated
    public static PreferenceStore getStore() {
        return mStore.getPreferenceStore();
    }

}
