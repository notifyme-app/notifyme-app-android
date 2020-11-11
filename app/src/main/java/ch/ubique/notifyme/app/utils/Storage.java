package ch.ubique.notifyme.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ch.ubique.notifyme.app.model.CheckInState;

public class Storage {

	private static final String KEY_SHARED_PREFERENCES_STORAGE = "KEY_SHARED_PREFERENCES_STORAGE";
	private static final String KEY_CURRENT_CHECK_IN = "KEY_CURRENT_CHECK_IN";
	private static final String KEY_LAST_SYNC = "KEY_LAST_SYNC";

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

	public void setCurrentVenue(CheckInState checkInState) {
		sharedPreferences.edit().putString(KEY_CURRENT_CHECK_IN, gson.toJson(checkInState)).apply();
	}

	public CheckInState getCurrentVenue() {
		return gson.fromJson(sharedPreferences.getString(KEY_CURRENT_CHECK_IN, null), CheckInState.class);
	}

	public void setLastSync(long lastSync) {
		sharedPreferences.edit().putLong(KEY_LAST_SYNC, lastSync).apply();
	}

	public long getLastSync() {
		return sharedPreferences.getLong(KEY_LAST_SYNC, 0);
	}

}
