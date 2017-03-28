package com.viking.logcat;

import org.eclipse.swt.dnd.Clipboard;

/**
 * An object listening to focus change in Table objects.<br>
 * For application not relying on a RCP to provide menu changes based on focus,
 * this class allows to get monitor the focus change of several Table widget
 * and update the menu action accordingly.
 */
public interface ITableFocusListener {

    public interface IFocusedTableActivator {
        public void copy(Clipboard clipboard);

        public void selectAll();
    }

    public void focusGained(IFocusedTableActivator activator);

    public void focusLost(IFocusedTableActivator activator);
}
