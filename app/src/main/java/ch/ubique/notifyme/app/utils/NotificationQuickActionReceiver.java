package ch.ubique.notifyme.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static ch.ubique.notifyme.app.utils.NotificationHelper.SNOOZE_ACTION;

public class NotificationQuickActionReceiver extends BroadcastReceiver {

	private final long SNOOZE_DURATION = 1000L * 60 * 30; // 30 minutes

	@Override
	public void onReceive(Context context, Intent intent) {
		if (SNOOZE_ACTION.equals(intent.getAction())) {
			NotificationHelper.getInstance(context).removeReminderNotification();
			ReminderHelper.setReminder(System.currentTimeMillis() + SNOOZE_DURATION, context);
		}
	}

}
