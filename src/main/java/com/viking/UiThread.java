package com.viking;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Log;
import com.android.menubar.IMenuBarCallback;
import com.android.menubar.IMenuBarEnhancer;
import com.android.menubar.MenuBarEnhancer;
import com.viking.util.Constant;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public class UiThread {

    // our display
    private Display mDisplay;

    // singleton instance
    private static UiThread mInstance = new UiThread();

    /**
     * Get singleton instance of the UI thread.
     */
    public static UiThread getInstance() {
        return mInstance;
    }

    public void runUI(){
        Display.setAppName(Constant.APP_NAME);
        Display.setAppVersion(Constant.APP_VERSION);

        mDisplay = Display.getDefault() ;
        final Shell shell = new Shell(mDisplay , SWT.SHELL_TRIM) ;

//        AndroidDebugBridge.init(true /* debugger support */);
//        AndroidDebugBridge.createBridge(adbLocation, true /* forceNewBridge */);
//
//        // we need to listen to client change to be notified of client status (profiling) change
//        AndroidDebugBridge.addClientChangeListener(this);

        shell.setText("Android Logcat Monitor");

        setConfirmClose(shell);
        createMenus(shell);
/*        createWidgets(shell);*/

        shell.pack();
        setSizeAndPosition(shell);
        shell.open();

        Log.d(Constant.TAG, "UI is up");

        while (!shell.isDisposed()) {
            if (!mDisplay.readAndDispatch())
                mDisplay.sleep();
        }

        mDisplay.dispose();
        Log.d(Constant.TAG, "UI is down");
    }

    /**
     * Set the confirm-before-close dialog.
     */
    private void setConfirmClose(final Shell shell) {
        // Note: there was some commented out code to display a confirmation box
        // when closing. The feature seems unnecessary and the code was not being
        // used, so it has been removed.
    }

    /**
     * Set the size and position of the main window from the preference, and
     * setup listeners for control events (resize/move of the window)
     */
    private void setSizeAndPosition(final Shell shell) {
        shell.setMinimumSize(400, 200);
        // get the x/y and w/h from the prefs
        PreferenceStore prefs = PrefsDialog.getStore();
        int x = prefs.getInt(PrefsDialog.SHELL_X);
        int y = prefs.getInt(PrefsDialog.SHELL_Y);
        int w = prefs.getInt(PrefsDialog.SHELL_WIDTH);
        int h = prefs.getInt(PrefsDialog.SHELL_HEIGHT);

        // check that we're not out of the display area
        Rectangle rect = mDisplay.getClientArea();
        // first check the width/height
        if (w > rect.width) {
            w = rect.width;
            prefs.setValue(PrefsDialog.SHELL_WIDTH, rect.width);
        }
        if (h > rect.height) {
            h = rect.height;
            prefs.setValue(PrefsDialog.SHELL_HEIGHT, rect.height);
        }
        // then check x. Make sure the left corner is in the screen
        if (x < rect.x) {
            x = rect.x;
            prefs.setValue(PrefsDialog.SHELL_X, rect.x);
        } else if (x >= rect.x + rect.width) {
            x = rect.x + rect.width - w;
            prefs.setValue(PrefsDialog.SHELL_X, rect.x);
        }
        // then check y. Make sure the left corner is in the screen
        if (y < rect.y) {
            y = rect.y;
            prefs.setValue(PrefsDialog.SHELL_Y, rect.y);
        } else if (y >= rect.y + rect.height) {
            y = rect.y + rect.height - h;
            prefs.setValue(PrefsDialog.SHELL_Y, rect.y);
        }

        // now we can set the location/size
        shell.setBounds(x, y, w, h);

        // add listener for resize/move
        shell.addControlListener(new ControlListener() {
            @Override
            public void controlMoved(ControlEvent e) {
                // get the new x/y
                Rectangle controlBounds = shell.getBounds();
                // store in pref file
                PreferenceStore currentPrefs = PrefsDialog.getStore();
                currentPrefs.setValue(PrefsDialog.SHELL_X, controlBounds.x);
                currentPrefs.setValue(PrefsDialog.SHELL_Y, controlBounds.y);
            }

            @Override
            public void controlResized(ControlEvent e) {
                // get the new w/h
                Rectangle controlBounds = shell.getBounds();
                // store in pref file
                PreferenceStore currentPrefs = PrefsDialog.getStore();
                currentPrefs.setValue(PrefsDialog.SHELL_WIDTH, controlBounds.width);
                currentPrefs.setValue(PrefsDialog.SHELL_HEIGHT, controlBounds.height);
            }
        });
    }

    /**
     * Create the menu bar and items.
     */
    private void createMenus(final Shell shell) {
        // create menu bar
        Menu menuBar = new Menu(shell, SWT.BAR);
        // create top-level items
        MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
        fileItem.setText("&File");

        // create top-level menus
        Menu fileMenu = new Menu(menuBar);
        fileItem.setMenu(fileMenu);

        MenuItem item;

        IMenuBarEnhancer enhancer = MenuBarEnhancer.setupMenu(Constant.APP_NAME, fileMenu,
                new IMenuBarCallback() {
                    @Override
                    public void printError(String format, Object... args) {
                        Log.e("DDMS Menu Bar", String.format(format, args));
                    }

                    @Override
                    public void onPreferencesMenuSelected() {
//                        PrefsDialog.run(shell);
                    }

                    @Override
                    public void onAboutMenuSelected() {
                        AboutDialog dlg = new AboutDialog(shell);
                        dlg.open();
                    }
                });

        if (enhancer.getMenuBarMode() == IMenuBarEnhancer.MenuBarMode.GENERIC) {
            new MenuItem(fileMenu, SWT.SEPARATOR);

            item = new MenuItem(fileMenu, SWT.NONE);
            item.setText("E&xit\tCtrl-Q");
            item.setAccelerator('Q' | SWT.MOD1);
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    shell.close();
                }
            });
        }

        // tell the shell to use this menu
        shell.setMenuBar(menuBar);
    }
}
