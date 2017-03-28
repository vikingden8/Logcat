package com.viking.logcat;

import com.android.ddmlib.logcat.LogCatMessage;

import java.util.List;

/**
 * Listeners interested in changes in the logcat buffer should implement this interface.
 */
public interface ILogCatBufferChangeListener {
    /**
     * Called when the logcat buffer changes.
     * @param addedMessages list of messages that were added to the logcat buffer
     * @param deletedMessages list of messages that were removed from the logcat buffer
     */
    void bufferChanged(List<LogCatMessage> addedMessages, List<LogCatMessage> deletedMessages);
}
