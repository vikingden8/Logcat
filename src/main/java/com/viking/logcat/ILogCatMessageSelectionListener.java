package com.viking.logcat;

import com.android.ddmlib.logcat.LogCatMessage;

/**
 * Classes interested in listening to user selection of logcat
 * messages should implement this interface.
 */
public interface ILogCatMessageSelectionListener {
    void messageDoubleClicked(LogCatMessage m);
}
