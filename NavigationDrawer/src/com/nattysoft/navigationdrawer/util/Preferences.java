package com.nattysoft.navigationdrawer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = Preferences.class.getSimpleName();
	private static final String NAME = "Matjabeleng";

	public static void savePreference(final Context context, final String key, final String value) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getPreference(final Context context, final String key) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, null);
	}

	public static void deletePreference(final Context context, final String key) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(key).commit();
	}
}
