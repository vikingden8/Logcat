package com.viking;

import com.android.ddmlib.Log;
import com.android.ddmuilib.ImageLoader;

import com.viking.util.Constant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.io.InputStream;

/**
 * Our "about" box.
 */
public class AboutDialog extends Dialog {

    private Image logoImage;

    /**
     * Create with default style.
     */
    public AboutDialog(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }

    /**
     * Create with app-defined style.
     */
    public AboutDialog(Shell parent, int style) {
        super(parent, style);
    }

    /**
     * Prepare and display the dialog.
     */
    public void open() {
        Shell parent = getParent();
        Shell shell = new Shell(parent, getStyle());
        shell.setText("About...");

        logoImage = loadImage(shell, "ddms-128.png");
        createContents(shell);
        shell.pack();

        shell.open();

        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        logoImage.dispose();
    }

    /*
     * Load an image file from a resource.
     *
     * This depends on Display, so I'm not sure what the rules are for
     * loading once and caching in a static class field.
     */
    private Image loadImage(Shell shell, String fileName) {
        InputStream imageStream;
        String pathName = "/images/" + fileName;

        imageStream = this.getClass().getResourceAsStream(pathName);
        if (imageStream == null) {
            //throw new NullPointerException("couldn't find " + pathName);
            Log.w(Constant.TAG, "Couldn't load " + pathName);
            Display display = shell.getDisplay();
            return ImageLoader.createPlaceHolderArt(display, 100, 50,
                    display.getSystemColor(SWT.COLOR_BLUE));
        }

        Image img = new Image(shell.getDisplay(), imageStream);
        if (img == null)
            throw new NullPointerException("couldn't load " + pathName);
        return img;
    }

    /*
     * Create the about box contents.
     */
    private void createContents(final Shell shell) {
        GridLayout layout;
        GridData data;
        Label label;

        shell.setLayout(new GridLayout(2, false));

        // Fancy logo
        Label logo = new Label(shell, SWT.BORDER);
        logo.setImage(logoImage);

        // Text Area
        Composite textArea = new Composite(shell, SWT.NONE);
        layout = new GridLayout(1, true);
        textArea.setLayout(layout);

        // Text lines
        label = new Label(textArea, SWT.NONE);
        if (Constant.APP_VERSION != null && Constant.APP_VERSION.length() > 0) {
            label.setText("Android Logcat Monitor Revision " + Constant.APP_VERSION);
        } else {
            label.setText("Android Logcat Monitor");
        }
        label = new Label(textArea, SWT.NONE);
        // TODO: update with new year date (search this to find other occurrences to update)
        label.setText("Copyright 2016-2017, Viking Den Open Source Project");
        label = new Label(textArea, SWT.NONE);
        label.setText("All Rights Reserved.");

        // blank spot in grid
        label = new Label(shell, SWT.NONE);

        // "OK" button
        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("OK");
        data = new GridData(GridData.HORIZONTAL_ALIGN_END);
        data.widthHint = 80;
        ok.setLayoutData(data);
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });

        //TODO let about dialog locate in center
/*        Shell parent = getParent() ;
        Rectangle bounds = parent.getBounds() ;
        shell.setLocation(bounds.width/2 - 40 , bounds.height/2-40);*/

        shell.pack();

        shell.setDefaultButton(ok);
    }
}
