package com.viking.logcat;

import com.android.ddmlib.logcat.LogCatFilter;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

public final class LogCatFilterContentProvider implements IStructuredContentProvider {
    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
    }

    /**
     * Obtain the list of filters currently in use.
     * @param model list of {@link LogCatFilter}'s
     * @return array of {@link LogCatFilter} objects, or null.
     */
    @Override
    public Object[] getElements(Object model) {
        return ((List<?>) model).toArray();
    }
}
