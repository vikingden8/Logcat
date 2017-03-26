package com.viking;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Log;
import com.android.ddmuilib.DdmUiPreferences;
import com.android.ddmuilib.DevicePanel;
import com.android.ddmuilib.ImageLoader;
import com.android.ddmuilib.actions.ToolItemAction;
import com.android.ddmuilib.logcat.LogCatPanel;
import com.android.ddmuilib.logcat.LogColors;
import com.android.ddmuilib.logcat.LogFilter;
import com.android.ddmuilib.logcat.LogPanel;
import com.android.menubar.IMenuBarCallback;
import com.android.menubar.IMenuBarEnhancer;
import com.android.menubar.MenuBarEnhancer;
import com.viking.util.Constant;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public class UiThread {

    // our display
    private Display mDisplay;

    // the table we show in the left-hand pane
    private DevicePanel mDevicePanel;
    private LogCatPanel mLogCatPanel;

    private ImageLoader mDdmUiLibLoader;

    // status line at the bottom of the app window
    private Label mStatusLine;

    // singleton instance
    private static UiThread mInstance = new UiThread();

    /**
     * Generic constructor.
     */
    public UiThread() {

    }

    /**
     * Get singleton instance of the UI thread.
     */
    public static UiThread getInstance() {
        return mInstance;
    }

    /**
     * Return the Display. Don't try this unless you're in the UI thread.
     */
    public Display getDisplay() {
        return mDisplay;
    }

    public void asyncExec(Runnable r) {
        if (mDisplay != null && mDisplay.isDisposed() == false) {
            mDisplay.asyncExec(r);
        }
    }

    /** returns the IPreferenceStore */
    public IPreferenceStore getStore() {
        return PrefsDialog.getStore();
    }

    public void runUI(){
        Display.setAppName(Constant.APP_NAME);
        Display.setAppVersion(Constant.APP_VERSION);

        mDisplay = Display.getDefault() ;
        final Shell shell = new Shell(mDisplay , SWT.SHELL_TRIM) ;

        // create the image loaders for DDMS and DDMUILIB
        mDdmUiLibLoader = ImageLoader.getDdmUiLibLoader();

        shell.setImage(ImageLoader.getLoader(this.getClass()).loadImage(mDisplay,
                "ddms-128.png",
                100, 50, null));

        Log.setLogOutput(new Log.ILogOutput() {
            @Override
            public void printAndPromptLog(final Log.LogLevel logLevel, final String tag,
                                          final String message) {
                Log.printLog(logLevel, tag, message);
                // dialog box only run in UI thread..
                mDisplay.asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Shell activeShell = mDisplay.getActiveShell();
                        if (logLevel == Log.LogLevel.ERROR) {
                            MessageDialog.openError(activeShell, tag, message);
                        } else {
                            MessageDialog.openWarning(activeShell, tag, message);
                        }
                    }
                });
            }

            @Override
            public void printLog(Log.LogLevel logLevel, String tag, String message) {
                Log.printLog(logLevel, tag, message);
            }
        });

//        AndroidDebugBridge.init(true /* debugger support */);
//        AndroidDebugBridge.createBridge(adbLocation, true /* forceNewBridge */);
//
//        // we need to listen to client change to be notified of client status (profiling) change
//        AndroidDebugBridge.addClientChangeListener(this);

        shell.setText("Android Logcat Monitor");

        setConfirmClose(shell);
        createMenus(shell);
        createWidgets(shell);

        shell.pack();
        setSizeAndPosition(shell);
        shell.open();

        Log.d(Constant.TAG, "UI is up");

        while (!shell.isDisposed()) {
            if (!mDisplay.readAndDispatch())
                mDisplay.sleep();
        }
        mDevicePanel.dispose();

        ImageLoader.dispose();
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

    private void createWidgets(final Shell shell) {
        Color darkGray = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
        /*
         * Create three areas: tool bar, split panels, status line
         */
        shell.setLayout(new GridLayout(1, false));
        // 1. panel area
        final Composite panelArea = new Composite(shell, SWT.BORDER);

        // make the panel area absorb all space
        panelArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        // 2. status line.
        mStatusLine = new Label(shell, SWT.NONE);

        // make status line extend all the way across
        mStatusLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        mStatusLine.setText("Initializing...");

        /*
         * Configure the split-panel area.
         */
        final PreferenceStore prefs = PrefsDialog.getStore();

        Composite topPanel = new Composite(panelArea, SWT.NONE);
        final Sash sash = new Sash(panelArea, SWT.HORIZONTAL);
        sash.setBackground(darkGray);
        Composite bottomPanel = new Composite(panelArea, SWT.NONE);

        panelArea.setLayout(new FormLayout());

        createTopPanel(topPanel, darkGray);
        createLogCatView(bottomPanel);

        // form layout data
        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(sash, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        topPanel.setLayoutData(data);

        final FormData sashData = new FormData();
        if (prefs != null && prefs.contains(Constant.PREFERENCE_LOGSASH)) {
            sashData.top = new FormAttachment(0, prefs.getInt(Constant.PREFERENCE_LOGSASH));
        } else {
            sashData.top = new FormAttachment(50,0); // 50% across
        }
        sashData.left = new FormAttachment(0, 0);
        sashData.right = new FormAttachment(100, 0);
        sash.setLayoutData(sashData);

        // allow resizes, but cap at minPanelWidth
        sash.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                Rectangle sashRect = sash.getBounds();
                Rectangle panelRect = panelArea.getClientArea();
                int bottom = panelRect.height - sashRect.height - 100;
                e.y = Math.max(Math.min(e.y, bottom), 100);
                if (e.y != sashRect.y) {
                    sashData.top = new FormAttachment(0, e.y);
                    if (prefs != null) {
                        prefs.setValue(Constant.PREFERENCE_LOGSASH, e.y);
                    }
                    panelArea.layout();
                }
            }
        });

        mStatusLine.setText("");

    }

    private void createTopPanel(final Composite comp, Color darkGray) {
        final PreferenceStore prefs = PrefsDialog.getStore();

        comp.setLayout(new FormLayout());

        Composite leftPanel = new Composite(comp, SWT.NONE);
        final Sash sash = new Sash(comp, SWT.VERTICAL);
        sash.setBackground(darkGray);

        createLeftPanel(leftPanel);

        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(sash, 0);
        leftPanel.setLayoutData(data);

        final FormData sashData = new FormData();
        sashData.top = new FormAttachment(0, 0);
        sashData.bottom = new FormAttachment(100, 0);
        if (prefs != null && prefs.contains(Constant.PREFERENCE_SASH)) {
            sashData.left = new FormAttachment(0, prefs.getInt(Constant.PREFERENCE_SASH));
        } else {
            // position the sash 380 from the right instead of x% (done by using
            // FormAttachment(x, 0)) in order to keep the sash at the same
            // position
            // from the left when the window is resized.
            // 380px is just enough to display the left table with no horizontal
            // scrollbar with the default font.
            sashData.left = new FormAttachment(0, 380);
        }
        sash.setLayoutData(sashData);

        final int minPanelWidth = 60;

        // allow resizes, but cap at minPanelWidth
        sash.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                Rectangle sashRect = sash.getBounds();
                Rectangle panelRect = comp.getClientArea();
                int right = panelRect.width - sashRect.width - minPanelWidth;
                e.x = Math.max(Math.min(e.x, right), minPanelWidth);
                if (e.x != sashRect.x) {
                    sashData.left = new FormAttachment(0, e.x);
                    if (prefs != null) {
                        prefs.setValue(Constant.PREFERENCE_SASH, e.x);
                    }
                    comp.layout();
                }
            }
        });
    }

    private void createLogCatView(Composite parent) {
        IPreferenceStore prefStore = DdmUiPreferences.getStore();
        mLogCatPanel = new LogCatPanel(prefStore);
        mLogCatPanel.createPanel(parent);

/*        if (mCurrentDevice != null) {
            mLogCatPanel.deviceSelected(mCurrentDevice);
        }*/
    }

    /**
     * Create the contents of the left panel: a table of VMs.
     */
    private void createLeftPanel(final Composite comp) {
        comp.setLayout(new GridLayout(1, false));
        /*ToolBar toolBar = new ToolBar(comp, SWT.HORIZONTAL | SWT.RIGHT | SWT.WRAP);
        toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createDevicePanelToolBar(toolBar);*/

        Composite c = new Composite(comp, SWT.NONE);
        c.setLayoutData(new GridData(GridData.FILL_BOTH));

        mDevicePanel = new DevicePanel(true /* showPorts */);
        mDevicePanel.createPanel(c);

        // add ourselves to the device panel selection listener
//        mDevicePanel.addSelectionListener(this);
    }
}
