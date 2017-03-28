package com.viking.logcat;

import com.android.ddmlib.logcat.LogCatFilter;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import java.util.Map;

/**
 * A JFace label provider for the LogCat filters. It expects elements of type
 * {@link LogCatFilter}.
 */
public final class LogCatFilterLabelProvider extends LabelProvider implements ITableLabelProvider {
    private Map<LogCatFilter, LogCatFilterData> mFilterData;

    public LogCatFilterLabelProvider(Map<LogCatFilter, LogCatFilterData> filterData) {
        mFilterData = filterData;
    }

    @Override
    public Image getColumnImage(Object arg0, int arg1) {
        return null;
    }

    /**
     * Implements {@link ITableLabelProvider#getColumnText(Object, int)}.
     * @param element an instance of {@link LogCatFilter}
     * @param index index of the column
     * @return text to use in the column
     */
    @Override
    public String getColumnText(Object element, int index) {
        if (!(element instanceof LogCatFilter)) {
            return null;
        }

        LogCatFilter f = (LogCatFilter) element;
        LogCatFilterData fd = mFilterData.get(f);

        if (fd != null && fd.getUnreadCount() > 0) {
            return String.format("%s (%d)", f.getName(), fd.getUnreadCount());
        } else {
            return f.getName();
        }
    }
}
