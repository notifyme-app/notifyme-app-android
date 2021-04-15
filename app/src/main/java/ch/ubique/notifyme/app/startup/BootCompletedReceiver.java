package ch.ubique.notifyme.app.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ch.ubique.notifyme.app.utils.NotificationHelper;
import ch.ubique.notifyme.app.utils.ReminderHelper;
import ch.ubique.notifyme.base.model.CheckInState;
import ch.ubique.notifyme.base.utils.Storage;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			return;
		}

		invalidateCheckIn(context);
	}

	private void invalidateCheckIn(Context context) {
		Storage storage = Storage.getInstance(context);
		CheckInState checkInState = storage.getCheckInState();
		if (checkInState == null) {
			return;
		}

		boolean checkedOut = ReminderHelper.autoCheckoutIfNecessary(context, checkInState);

		if (!checkedOut) {
			long checkInTime = checkInState.getCheckInTime();
			NotificationHelper.getInstance(context).startOngoingNotification(checkInTime, checkInState.getVenueInfo());
			ReminderHelper.set8HourReminder(checkInTime, context);
			ReminderHelper.setAutoCheckOut(checkInTime, context);
			ReminderHelper.setReminder(checkInTime + checkInState.getSelectedTimerOption().getDelayMillis(), context);
		}
	}

}
