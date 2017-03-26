package com.viking;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Log;
import com.viking.util.Constant;

/**
 * Author : Viking Den <vikingden7@gmail.com>
 * Date : 2017/3/26
 */
public class LogcatMain {

    public LogcatMain(){

    }

    /*
     * If a thread bails with an uncaught exception, bring the whole
     * thing down.
     */
    private static class UncaughtHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Log.e(Constant.TAG, "shutting down due to uncaught exception");
            Log.e(Constant.TAG, e);
            System.exit(1);
        }
    }

    public static void main(String[] args){

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());

        // load prefs and init the default values
        PrefsDialog.init();

        Log.d(Constant.TAG, "Initializing");

        UiThread uiThread = new UiThread() ;
        try{
            uiThread.runUI();
        }finally {
            AndroidDebugBridge.terminate();
        }

        Log.d(Constant.TAG, "Bye");

        // this is kinda bad, but on MacOS the shutdown doesn't seem to finish because of
        // a thread called AWT-Shutdown. This will help while I track this down.
        System.exit(0);
    }


}
