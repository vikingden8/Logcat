package com.viking.logcat;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Base class for our information panels.
 */
public abstract class Panel {

    public final Control createPanel(Composite parent) {
        Control panelControl = createControl(parent);

        postCreation();

        return panelControl;
    }

    protected abstract void postCreation();

    /**
     * Creates a control capable of displaying some information.  This is
     * called once, when the application is initializing, from the UI thread.
     */
    protected abstract Control createControl(Composite parent);
    
    /**
     * Sets the focus to the proper control inside the panel.
     */
    public abstract void setFocus();
}

