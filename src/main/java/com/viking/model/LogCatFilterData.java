package com.viking.model;

import com.android.ddmlib.logcat.LogCatFilter;
import com.android.ddmlib.logcat.LogCatMessage;

import java.util.List;

public class LogCatFilterData {
    private final LogCatFilter mFilter;

    /** Indicates the number of messages that match this filter, but have not
     * yet been read by the user. This is really metadata about this filter
     * necessary for the UI. If we ever end up needing to store more metadata,
     * then it is probably better to move it out into a separate class. */
    private int mUnreadCount;

    /** Indicates that this filter is transient, and should not be persisted
     * across Eclipse sessions. */
    private boolean mTransient;

    public LogCatFilterData(LogCatFilter f) {
        mFilter = f;

        // By default, all filters are persistent. Transient filters should explicitly
        // mark it so by calling setTransient.
        mTransient = false;
    }

    /**
     * Update the unread count based on new messages received. The unread count
     * is incremented by the count of messages in the received list that will be
     * accepted by this filter.
     * @param newMessages list of new messages.
     */
    public void updateUnreadCount(List<LogCatMessage> newMessages) {
        for (LogCatMessage m : newMessages) {
            if (mFilter.matches(m)) {
                mUnreadCount++;
            }
        }
    }

    /**
     * Reset count of unread messages.
     */
    public void resetUnreadCount() {
        mUnreadCount = 0;
    }

    /**
     * Get current value for the unread message counter.
     */
    public int getUnreadCount() {
        return mUnreadCount;
    }

    /** Make this filter transient: It will not be persisted across sessions. */
    public void setTransient() {
        mTransient = true;
    }

    public boolean isTransient() {
        return mTransient;
    }
}
