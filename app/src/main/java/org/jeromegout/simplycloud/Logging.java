package org.jeromegout.simplycloud;

import android.util.Log;

public final class Logging {

	private final static String LOG_TAG="ONE_SHARE_LOGGING";

	public static void e(String message, Throwable e) {
		Log.e(LOG_TAG, message, e);
	}

	public static void e(String message) {
		Log.e(LOG_TAG, message);
	}

	public static void d(String message) {
		Log.d(LOG_TAG, message);
	}
}


