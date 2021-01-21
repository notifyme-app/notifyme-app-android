package ch.ubique.notifyme.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ch.ubique.notifyme.base.model.CheckInState;


public class Storage {

	private static final String KEY_SHARED_PREFERENCES_STORAGE = "KEY_SHARED_PREFERENCES_STORAGE";
	private static final String KEY_CURRENT_CHECK_IN = "KEY_CURRENT_CHECK_IN";
	private static final String KEY_LAST_KEY_BUNDLE_TAG = "KEY_LAST_KEY_BUNDLE_TAG";
	private static final String KEY_ONBOARDING_COMPLETE = "KEY_ONBOARDING_COMPLETE";

	private static Storage instance;

	private SharedPreferences sharedPreferences;
	private Gson gson = new Gson();

	private Storage(Context context) {
		sharedPreferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES_STORAGE, Context.MODE_PRIVATE);
	}

	public static synchronized Storage getInstance(Context context) {
		if (instance == null) {
			instance = new Storage(context);
		}
		return instance;
	}

	public void setCheckInState(CheckInState checkInState) {
		sharedPreferences.edit().putString(KEY_CURRENT_CHECK_IN, gson.toJson(checkInState)).apply();
	}

	public CheckInState getCheckInState() {
		return gson.fromJson(sharedPreferences.getString(KEY_CURRENT_CHECK_IN, null), CheckInState.class);
	}

	public void setLastKeyBundleTag(long lastSync) {
		sharedPreferences.edit().putLong(KEY_LAST_KEY_BUNDLE_TAG, lastSync).apply();
	}

	public long getLastKeyBundleTag() {
		return sharedPreferences.getLong(KEY_LAST_KEY_BUNDLE_TAG, 0);
	}

	public void setOnboardingCompleted(boolean completed) {
		sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETE, completed).apply();
	}

	public boolean getOnboardingCompleted() {
		return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false);
	}

}
