package ch.ubique.notifyme.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderHelper extends BroadcastReceiver {

	private static final int REMINDER_INTENT_ID = 12;

	public static void setReminder(long alarmTime, Context context) {
		if (alarmTime <= System.currentTimeMillis()) return;
		Intent intent = new Intent(context, ReminderHelper.class);
		PendingIntent pendingIntent =
				PendingIntent.getBroadcast(context, REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Storage.getInstance(context).getCurrentVenue() != null) {
			new NotificationHelper(context).showReminderNotification();
		}
	}

}
