package com.viking.logcat;

import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;


public abstract class SelectionDependentPanel extends Panel {
    private IDevice mCurrentDevice = null;
    private Client mCurrentClient = null;

    protected final IDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    /**
     * Returns the current {@link Client}.
     * @return the current client or null if none are selected.
     */
    protected final Client getCurrentClient() {
        return mCurrentClient;
    }

    /**
     * Sent when a new device is selected.
     * @param selectedDevice the selected device.
     */
    public final void deviceSelected(IDevice selectedDevice) {
        if (selectedDevice != mCurrentDevice) {
            mCurrentDevice = selectedDevice;
            deviceSelected();
        }
    }

    /**
     * Sent when a new client is selected.
     * @param selectedClient the selected client.
     */
    public final void clientSelected(Client selectedClient) {
        if (selectedClient != mCurrentClient) {
            mCurrentClient = selectedClient;
            clientSelected();
        }
    }

    /**
     * Sent when a new device is selected. The new device can be accessed
     * with {@link #getCurrentDevice()}.
     */
    public abstract void deviceSelected();

    /**
     * Sent when a new client is selected. The new client can be accessed
     * with {@link #getCurrentClient()}.
     */
    public abstract void clientSelected();
}
