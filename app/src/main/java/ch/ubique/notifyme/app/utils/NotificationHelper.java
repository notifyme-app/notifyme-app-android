package ch.ubique.notifyme.app.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainActivity;
import ch.ubique.notifyme.app.R;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW;

public class NotificationHelper {

	public static final String EXPOSURE_NOTIFICATION_ACTION = "EXPOSURE_NOTIFICATION_ACTION";
	public static final String REMINDER_ACTION = "REMINDER_ACTION";
	public static final String ONGOING_ACTION = "ONGOING_ACTION";
	public static final String CHECK_OUT_NOW_ACTION = "CHECK_OUT_NOW_ACTION";
	public static final String SNOOZE_ACTION = "SNOOZE_ACTION";

	public static final String EXPOSURE_ID_EXTRA = "EXPOSURE_ID";

	private final String CHANNEL_ID_EXPOSURE_NOTIFICATION = "ExposureNotificaitons";
	private final String CHANNEL_ID_REMINDER = "Reminders";
	private final String CHANNEL_ID_ONGOING_CHECK_IN = "Ongoing Check In";

	private final int ONGOING_NOTIFICATION_ID = -1;
	private final int REMINDER_NOTIFICATION_ID = -2;


	private static NotificationHelper instance;

	private Context context;
	private NotificationManager notificationManager;

	private NotificationHelper(Context context) {
		this.context = context;
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static synchronized NotificationHelper getInstance(Context context) {
		if (instance == null) {
			instance = new NotificationHelper(context);
		}
		return instance;
	}

	private void createNotificationChannel(String channelId, String channelName, boolean silent, int importance) {
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
			if (silent) {
				channel.setSound(null, null);
				channel.enableVibration(false);
			}
			notificationManager.createNotificationChannel(channel);
		}
	}

	private PendingIntent createBasicPendingIntent(String notificationAction) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(notificationAction);
		return TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(intent)
				.getPendingIntent(notificationAction.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent createExposurePendingIntent(long exposureId) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(EXPOSURE_NOTIFICATION_ACTION);
		intent.putExtra(EXPOSURE_ID_EXTRA, exposureId);
		intent.setAction(Long.toString(exposureId));
		return TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(intent)
				.getPendingIntent(EXPOSURE_NOTIFICATION_ACTION.hashCode(), PendingIntent.FLAG_ONE_SHOT);
	}

	private NotificationCompat.Builder getNotificationBuilder(String channelId) {
		return new NotificationCompat.Builder(context, channelId)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(context, R.color.primary))
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(Notification.DEFAULT_ALL);
	}


	public void showExposureNotification(long exposureId) {

		createNotificationChannel(CHANNEL_ID_EXPOSURE_NOTIFICATION, context.getString(R.string.android_notification_channel_name),
				false, IMPORTANCE_HIGH);

		Notification notification = getNotificationBuilder(CHANNEL_ID_EXPOSURE_NOTIFICATION)
				.setContentIntent(createExposurePendingIntent(exposureId))
				.setContentTitle(context.getString(R.string.exposure_notification_title))
				.setContentText(context.getString(R.string.exposure_notification_body))
				.build();

		notificationManager.notify((int) exposureId, notification);
	}

	public void showReminderNotification() {

		createNotificationChannel(CHANNEL_ID_REMINDER, context.getString(R.string.android_reminder_channel_name), false,
				IMPORTANCE_HIGH);

		Intent snoozeIntent = new Intent(context, NotificationQuickActionReceiver.class);
		snoozeIntent.setAction(SNOOZE_ACTION);
		PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 1, snoozeIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = getNotificationBuilder(CHANNEL_ID_REMINDER)
				.setContentIntent(createBasicPendingIntent(REMINDER_ACTION))
				.setContentTitle(context.getString(R.string.checkout_reminder_title))
				.setContentText(context.getString(R.string.checkout_reminder_text))
				.addAction(R.drawable.ic_close, context.getString(R.string.ongoing_notification_checkout_quick_action),
						createBasicPendingIntent(CHECK_OUT_NOW_ACTION))
				.addAction(R.drawable.ic_snooze, context.getString(R.string.reminder_notification_snooze_action),
						snoozePendingIntent)
				.build();

		notificationManager.notify(REMINDER_NOTIFICATION_ID, notification);
	}

	public void removeReminderNotification() {
		notificationManager.cancel(REMINDER_NOTIFICATION_ID);
	}

	public void startOngoingNotification(long startTime, VenueInfo venueInfo) {
		createNotificationChannel(CHANNEL_ID_ONGOING_CHECK_IN,
				context.getString(R.string.android_ongoing_checkin_notification_channel_name), true, IMPORTANCE_LOW);
		Notification ongoingNotification = new NotificationCompat.Builder(context, CHANNEL_ID_ONGOING_CHECK_IN)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(context, R.color.primary))
				.setContentTitle(context.getString(R.string.ongoing_notification_title)
						.replace("{TIME}", StringUtils.getHourMinuteTimeString(startTime, ":")))
				.setContentText(venueInfo.getTitle() + "\n" + venueInfo.getSubtitle())
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.setOngoing(true)
				.addAction(R.drawable.ic_close, context.getString(R.string.ongoing_notification_checkout_quick_action),
						createBasicPendingIntent(CHECK_OUT_NOW_ACTION))
				.setContentIntent(createBasicPendingIntent(ONGOING_ACTION))
				.build();
		notificationManager.notify(ONGOING_NOTIFICATION_ID, ongoingNotification);
	}

	public void stopOngoingNotification() {
		notificationManager.cancel(ONGOING_NOTIFICATION_ID);
	}

}
