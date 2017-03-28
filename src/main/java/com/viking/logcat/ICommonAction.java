package com.viking.logcat;

/**
 * Common interface for basic action handling. This allows the common ui
 * components to access ToolItem or Action the same way.
 */
public interface ICommonAction {
    /**
     * Sets the enabled state of this action.
     * @param enabled <code>true</code> to enable, and
     *   <code>false</code> to disable
     */
    void setEnabled(boolean enabled);

    /**
     * Sets the checked status of this action.
     * @param checked the new checked status
     */
    void setChecked(boolean checked);
    
    /**
     * Sets the {@link Runnable} that will be executed when the action is triggered.
     */
    void setRunnable(Runnable runnable);
}

