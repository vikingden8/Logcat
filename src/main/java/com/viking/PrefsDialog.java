package com.viking;

import com.android.ddmlib.DdmConstants;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.Log;
import com.android.sdkstats.DdmsPreferenceStore;
import com.viking.logcat.DdmUiPreferences;
import com.viking.util.Constant;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import java.io.File;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public final class PrefsDialog {

    public final static String SHELL_X = "shellX";
    public final static String SHELL_Y = "shellY";
    public final static String SHELL_WIDTH = "shellWidth";
    public final static String SHELL_HEIGHT = "shellHeight";


    public final static String LOGCAT_COLUMN_MODE = "ddmsLogColumnMode"; //$NON-NLS-1$
    public final static String LOGCAT_FONT = "ddmsLogFont"; //$NON-NLS-1$

    public final static String LOGCAT_COLUMN_MODE_AUTO = "auto"; //$NON-NLS-1$
    public final static String LOGCAT_COLUMN_MODE_MANUAL = "manual"; //$NON-NLS-1$

    private final static String PREFS_DEBUG_PORT_BASE = "adbDebugBasePort"; //$NON-NLS-1$
    private final static String PREFS_SELECTED_DEBUG_PORT = "debugSelectedPort"; //$NON-NLS-1$
    private final static String PREFS_DEFAULT_THREAD_UPDATE = "defaultThreadUpdateEnabled"; //$NON-NLS-1$
    private final static String PREFS_DEFAULT_HEAP_UPDATE = "defaultHeapUpdateEnabled"; //$NON-NLS-1$
    private final static String PREFS_THREAD_REFRESH_INTERVAL = "threadStatusInterval"; //$NON-NLS-1$
    private final static String PREFS_LOG_LEVEL = "ddmsLogLevel"; //$NON-NLS-1$
    private final static String PREFS_TIMEOUT = "timeOut"; //$NON-NLS-1$
    private final static String PREFS_PROFILER_BUFFER_SIZE_MB = "profilerBufferSizeMb"; //$NON-NLS-1$
    private final static String PREFS_USE_ADBHOST = "useAdbHost"; //$NON-NLS-1$
    private final static String PREFS_ADBHOST_VALUE = "adbHostValue"; //$NON-NLS-1$

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

    /**
     * Do some one-time prep.
     *
     * The original plan was to let the individual classes define their
     * own defaults, which we would get and then override with the config
     * file.  However, PreferencesStore.load() doesn't trigger the "changed"
     * events, which means we have to pull the loaded config values out by
     * hand.
     *
     * So, we set the defaults, load the values from the config file, and
     * then run through and manually export the values.  Then we duplicate
     * the second part later on for the "changed" events.
     */
    public static void init() {
        PreferenceStore prefStore = mStore.getPreferenceStore();

        if (prefStore == null) {
            // we have a serious issue here...
            Log.e(Constant.TAG,"failed to access both the user HOME directory and the system wide temp folder. Quitting.");
            System.exit(1);
        }

        // configure default values
        setDefaults(System.getProperty("user.home"));

        // listen for changes
        prefStore.addPropertyChangeListener(new ChangeListener());

        // Now we initialize the value of the preference, from the values in the store.

        // First the ddm lib.
        DdmPreferences.setDebugPortBase(prefStore.getInt(PREFS_DEBUG_PORT_BASE));
        DdmPreferences.setSelectedDebugPort(prefStore.getInt(PREFS_SELECTED_DEBUG_PORT));
        DdmPreferences.setLogLevel(prefStore.getString(PREFS_LOG_LEVEL));
        DdmPreferences.setInitialThreadUpdate(prefStore.getBoolean(PREFS_DEFAULT_THREAD_UPDATE));
        DdmPreferences.setInitialHeapUpdate(prefStore.getBoolean(PREFS_DEFAULT_HEAP_UPDATE));
        DdmPreferences.setTimeOut(prefStore.getInt(PREFS_TIMEOUT));
        DdmPreferences.setProfilerBufferSizeMb(prefStore.getInt(PREFS_PROFILER_BUFFER_SIZE_MB));
        DdmPreferences.setUseAdbHost(prefStore.getBoolean(PREFS_USE_ADBHOST));
        DdmPreferences.setAdbHostValue(prefStore.getString(PREFS_ADBHOST_VALUE));

        // some static values
        String out = System.getenv("ANDROID_PRODUCT_OUT"); //$NON-NLS-1$
        DdmUiPreferences.setSymbolsLocation(out + File.separator + "symbols"); //$NON-NLS-1$
        DdmUiPreferences.setAddr2LineLocation("arm-linux-androideabi-addr2line"); //$NON-NLS-1$
        DdmUiPreferences.setAddr2LineLocation64("aarch64-linux-android-addr2line");
        String traceview = System.getProperty("com.android.ddms.bindir");  //$NON-NLS-1$
        if (traceview != null && traceview.length() != 0) {
            traceview += File.separator + DdmConstants.FN_TRACEVIEW;
        } else {
            traceview = DdmConstants.FN_TRACEVIEW;
        }
        DdmUiPreferences.setTraceviewLocation(traceview);

        // Now the ddmui lib
        DdmUiPreferences.setStore(prefStore);
        DdmUiPreferences.setThreadRefreshInterval(prefStore.getInt(PREFS_THREAD_REFRESH_INTERVAL));
    }

    /**
     * Set default values for all preferences.  These are either defined
     * statically or are based on the values set by the class initializers
     * in other classes.
     *
     * The other threads (e.g. VMWatcherThread) haven't been created yet,
     * so we want to use static values rather than reading fields from
     * class.getInstance().
     */
    private static void setDefaults(String homeDir) {
        PreferenceStore prefStore = mStore.getPreferenceStore();

 /*       prefStore.setDefault(PREFS_DEBUG_PORT_BASE, DdmPreferences.DEFAULT_DEBUG_PORT_BASE);

        prefStore.setDefault(PREFS_SELECTED_DEBUG_PORT,
                DdmPreferences.DEFAULT_SELECTED_DEBUG_PORT);

        prefStore.setDefault(PREFS_USE_ADBHOST, DdmPreferences.DEFAULT_USE_ADBHOST);
        prefStore.setDefault(PREFS_ADBHOST_VALUE, DdmPreferences.DEFAULT_ADBHOST_VALUE);*/

/*        prefStore.setDefault(PREFS_DEFAULT_THREAD_UPDATE, true);
        prefStore.setDefault(PREFS_DEFAULT_HEAP_UPDATE, false);*/
        prefStore.setDefault(PREFS_THREAD_REFRESH_INTERVAL, DdmUiPreferences.DEFAULT_THREAD_REFRESH_INTERVAL);

        prefStore.setDefault("textSaveDir", homeDir);
        prefStore.setDefault("imageSaveDir", homeDir);

        prefStore.setDefault(PREFS_LOG_LEVEL, "info");

        prefStore.setDefault(PREFS_TIMEOUT, DdmPreferences.DEFAULT_TIMEOUT);
 /*       prefStore.setDefault(PREFS_PROFILER_BUFFER_SIZE_MB,
                DdmPreferences.DEFAULT_PROFILER_BUFFER_SIZE_MB);*/

        // choose a default font for the text output
        FontData fdat = new FontData("Courier", 10, SWT.NORMAL);
        prefStore.setDefault("textOutputFont", fdat.toString());

        // layout information.
        prefStore.setDefault(SHELL_X, 100);
        prefStore.setDefault(SHELL_Y, 100);
        prefStore.setDefault(SHELL_WIDTH, 800);
        prefStore.setDefault(SHELL_HEIGHT, 600);

/*        prefStore.setDefault(EXPLORER_SHELL_X, 50);
        prefStore.setDefault(EXPLORER_SHELL_Y, 50);

        prefStore.setDefault(SHOW_NATIVE_HEAP, false);*/
    }

    /**
     * Create a "listener" to take action when preferences change.  These are
     * required for ongoing activities that don't check prefs on each use.
     *
     * This is only invoked when something explicitly changes the value of
     * a preference (e.g. not when the prefs file is loaded).
     */
    private static class ChangeListener implements IPropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String changed = event.getProperty();
            PreferenceStore prefStore = mStore.getPreferenceStore();

            /*if (changed.equals(PREFS_DEBUG_PORT_BASE)) {
                DdmPreferences.setDebugPortBase(prefStore.getInt(PREFS_DEBUG_PORT_BASE));
            } else if (changed.equals(PREFS_SELECTED_DEBUG_PORT)) {
                DdmPreferences.setSelectedDebugPort(prefStore.getInt(PREFS_SELECTED_DEBUG_PORT));
            } else */if (changed.equals(PREFS_LOG_LEVEL)) {
                DdmPreferences.setLogLevel((String)event.getNewValue());
            } else if (changed.equals("textSaveDir")) {
                prefStore.setValue("lastTextSaveDir",
                        (String) event.getNewValue());
            } else if (changed.equals("imageSaveDir")) {
                prefStore.setValue("lastImageSaveDir",
                        (String) event.getNewValue());
            } else if (changed.equals(PREFS_TIMEOUT)) {
                DdmPreferences.setTimeOut(prefStore.getInt(PREFS_TIMEOUT));
            } /*else if (changed.equals(PREFS_PROFILER_BUFFER_SIZE_MB)) {
                DdmPreferences.setProfilerBufferSizeMb(
                        prefStore.getInt(PREFS_PROFILER_BUFFER_SIZE_MB));
            } else if (changed.equals(PREFS_USE_ADBHOST)) {
                DdmPreferences.setUseAdbHost(prefStore.getBoolean(PREFS_USE_ADBHOST));
            } else if (changed.equals(PREFS_ADBHOST_VALUE)) {
                DdmPreferences.setAdbHostValue(prefStore.getString(PREFS_ADBHOST_VALUE));
            }*/ else {
                Log.v(Constant.TAG, "Preference change: " + event.getProperty()
                        + ": '" + event.getOldValue()
                        + "' --> '" + event.getNewValue() + "'");
            }
        }
    }

}
