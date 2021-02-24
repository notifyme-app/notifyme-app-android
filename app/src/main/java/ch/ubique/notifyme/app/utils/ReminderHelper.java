package ch.ubique.notifyme.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.crowdnotifier.android.sdk.CrowdNotifier;

import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.model.DiaryEntry;

public class ReminderHelper extends BroadcastReceiver {

	public static final String AUTO_CHECKOUT_BROADCAST = "AUTO_CHECKOUT_BROADCAST";
	private static final int REMINDER_INTENT_ID = 12;
	private static final String KEY_REMINDER_INTENT = "KEY_REMINDER_INTENT";
	private static final int EIGHT_HOUR_REMINDER_INTENT_ID = 13;
	private static final String KEY_EIGHT_HOUR_REMINDER_INTENT = "KEY_EIGHT_HOUR_REMINDER_INTENT";
	private static final int AUTO_CHECK_OUT_INTENT_ID = 14;
	private static final String KEY_AUTO_CHECKOUT_INTENT = "KEY_AUTO_CHECKOUT_INTENT";
	private static final long EIGHT_HOURS = 1000L * 60 * 60 * 8;
	private static final long TWELVE_HOURS = 1000L * 60 * 60 * 12;

	public static void removeAllReminders(Context context) {
		removeReminder(context);
		remove8HourReminder(context);
		removeAutoCheckOut(context);
	}

	public static void removeReminder(Context context) {
		PendingIntent pendingIntent = getPendingIntent(context, false);
		removeReminder(pendingIntent, context);
	}

	public static void setReminder(long alarmTime, Context context) {
		PendingIntent pendingIntent = getPendingIntent(context, false);
		setReminder(alarmTime, pendingIntent, context);
	}

	public static void set8HourReminder(Context context) {
		PendingIntent pendingIntent = getPendingIntent(context, true);
		setReminder(System.currentTimeMillis() + EIGHT_HOURS, pendingIntent, context);
	}

	public static void remove8HourReminder(Context context) {
		PendingIntent pendingIntent = getPendingIntent(context, true);
		removeReminder(pendingIntent, context);
	}

	public static void setAutoCheckOut(Context context) {
		PendingIntent pendingIntent = getAutoCheckOutPendingIntent(context);
		setReminder(System.currentTimeMillis() + TWELVE_HOURS, pendingIntent, context);
	}

	public static void removeAutoCheckOut(Context context) {
		PendingIntent pendingIntent = getAutoCheckOutPendingIntent(context);
		removeReminder(pendingIntent, context);
	}

	private static void setReminder(long alarmTime, PendingIntent pendingIntent, Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmTime <= System.currentTimeMillis()) {
			alarmManager.cancel(pendingIntent);
			return;
		}
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
	}

	private static void removeReminder(PendingIntent pendingIntent, Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	private static PendingIntent getPendingIntent(Context context, boolean eightHours) {
		Intent intent = new Intent(context, ReminderHelper.class);
		if (eightHours) {
			intent.putExtra(KEY_EIGHT_HOUR_REMINDER_INTENT, KEY_EIGHT_HOUR_REMINDER_INTENT);
			return PendingIntent.getBroadcast(context, EIGHT_HOUR_REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			intent.putExtra(KEY_REMINDER_INTENT, KEY_REMINDER_INTENT);
			return PendingIntent.getBroadcast(context, REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
	}

	private static PendingIntent getAutoCheckOutPendingIntent(Context context) {
		Intent intent = new Intent(context, ReminderHelper.class);
		intent.putExtra(KEY_AUTO_CHECKOUT_INTENT, KEY_AUTO_CHECKOUT_INTENT);
		return PendingIntent.getBroadcast(context, AUTO_CHECK_OUT_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		CheckInState checkInState = Storage.getInstance(context).getCurrentVenue();
		if ((intent.hasExtra(KEY_REMINDER_INTENT) || intent.hasExtra(KEY_EIGHT_HOUR_REMINDER_INTENT)) && checkInState != null) {
			NotificationHelper.getInstance(context).showReminderNotification();
		} else if (intent.hasExtra(KEY_AUTO_CHECKOUT_INTENT) && checkInState != null) {
			autoCheckout(context, checkInState);
		}
	}

	private void autoCheckout(Context context, CheckInState checkInState) {
		NotificationHelper notificationHelper = NotificationHelper.getInstance(context);
		notificationHelper.stopOngoingNotification();
		notificationHelper.removeReminderNotification();
		notificationHelper.showAutoCheckoutNotification();
		long checkIn = checkInState.getCheckInTime();
		long checkOut = System.currentTimeMillis();
		long id = CrowdNotifier.addCheckIn(checkIn, checkOut, checkInState.getVenueInfo(), context);
		DiaryStorage.getInstance(context).addEntry(new DiaryEntry(id, checkIn, checkOut, checkInState.getVenueInfo(), ""));
		Storage storage = Storage.getInstance(context);
		storage.setCurrentVenue(null);
		LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(AUTO_CHECKOUT_BROADCAST));
		storage.setDidAutoCheckout(true);
	}

}
