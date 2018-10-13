package com.bravo.pull_to_refresh.internal;

import com.bravo.utils.Logs;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Logs.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement,true);
	}

}
