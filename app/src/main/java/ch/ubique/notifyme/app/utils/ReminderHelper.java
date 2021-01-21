package ch.ubique.notifyme.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.model.CheckInState;

public class ReminderHelper extends BroadcastReceiver {

	private static final int REMINDER_INTENT_ID = 12;
	private static final String KEY_REMINDER_INTENT = "KEY_REMINDER_INTENT";

	public static void removeReminder(Context context) {
		PendingIntent pendingIntent = getPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	public static void setReminder(long alarmTime, Context context) {
		PendingIntent pendingIntent = getPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmTime <= System.currentTimeMillis()) {
			alarmManager.cancel(pendingIntent);
			return;
		}
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
	}

	private static PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, ReminderHelper.class);
		intent.putExtra(KEY_REMINDER_INTENT, KEY_REMINDER_INTENT);
		return PendingIntent.getBroadcast(context, REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		CheckInState checkInState = Storage.getInstance(context).getCurrentVenue();
		if (intent.hasExtra(KEY_REMINDER_INTENT) && checkInState != null) {
			NotificationHelper.getInstance(context).showReminderNotification(checkInState.getCheckInTime(), checkInState.getVenueInfo());
		}
	}

}
