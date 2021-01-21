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

public class NotificationHelper {

	public static final String EXPOSURE_NOTIFICATION_ACTION = "EXPOSURE_NOTIFICATION_ACTION";
	public static final String REMINDER_ACTION = "REMINDER_ACTION";
	public static final String ONGOING_ACTION = "ONGOING_ACTION";
	public static final String ONGOING_CHECK_OUT_NOW_ACTION = "ONGOING_CHECK_OUT_NOW_ACTION";

	public static final String EXPOSURE_ID_EXTRA = "EXPOSURE_ID";

	private final String CHANNEL_ID_EXPOSURE_NOTIFICATION = "ExposureNotificaitons";
	private final String CHANNEL_ID_REMINDER = "Reminders";
	private final String CHANNEL_ID_ONGOING_CHECK_IN = "Permanent Check In6";

	private final int ONGOING_NOTIFICATION_ID = 188;


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

	private void createNotificationChannel(String channelId, String channelName, boolean silent) {
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
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

	private Notification createNotification(String title, String message, PendingIntent pendingIntent, String channelId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(context, R.color.primary))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		return builder.build();
	}


	public void showExposureNotification(long exposureId) {

		createNotificationChannel(CHANNEL_ID_EXPOSURE_NOTIFICATION, context.getString(R.string.android_notification_channel_name),
				false);
		PendingIntent pendingIntent = createExposurePendingIntent(exposureId);
		String title = context.getString(R.string.exposure_notification_title);
		String message = context.getString(R.string.exposure_notification_body);

		Notification notification = createNotification(title, message, pendingIntent, CHANNEL_ID_EXPOSURE_NOTIFICATION);
		notificationManager.notify(message.hashCode(), notification);
	}

	public void showReminderNotification() {
		//TODO: Add quick actions to reminder
		createNotificationChannel(CHANNEL_ID_REMINDER, context.getString(R.string.android_reminder_channel_name), false);
		PendingIntent pendingIntent = createBasicPendingIntent(REMINDER_ACTION);
		String title = context.getString(R.string.checkout_reminder_title);
		String message = context.getString(R.string.checkout_reminder_text);

		Notification notification = createNotification(title, message, pendingIntent, CHANNEL_ID_REMINDER);
		notificationManager.notify(message.hashCode(), notification);
	}

	public void startOngoingNotification(long startTime, VenueInfo venueInfo) {
		createNotificationChannel(CHANNEL_ID_ONGOING_CHECK_IN,
				context.getString(R.string.android_ongoing_checkin_notification_channel_name), true);
		NotificationCompat.Builder permanentNotificationBuilder =
				new NotificationCompat.Builder(context, CHANNEL_ID_ONGOING_CHECK_IN)
						.setContentTitle(context.getString(R.string.ongoing_notification_title)
								.replace("{TIME}", StringUtils.getHourMinuteTimeString(startTime, ":")))
						.setContentText(venueInfo.getTitle() + "\n" + venueInfo.getSubtitle())
						.setSmallIcon(R.drawable.ic_notification)
						.setColor(ContextCompat.getColor(context, R.color.primary))
						.setOngoing(true)
						.addAction(R.drawable.ic_close, context.getString(R.string.ongoing_notification_checkout_quick_action),
								createBasicPendingIntent(ONGOING_CHECK_OUT_NOW_ACTION))
						.setContentIntent(createBasicPendingIntent(ONGOING_ACTION));
		notificationManager.notify(ONGOING_NOTIFICATION_ID, permanentNotificationBuilder.build());
	}

	public void stopOngoingNotification() {
		notificationManager.cancel(ONGOING_NOTIFICATION_ID);
	}

}
