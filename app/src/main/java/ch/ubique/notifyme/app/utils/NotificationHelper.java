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

import ch.ubique.notifyme.app.MainActivity;
import ch.ubique.notifyme.app.R;

public class NotificationHelper {

	public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
	public static final String EXPOSURE_ID = "EXPOSURE_ID";

	public static final int EXPOSURE_NOTIFICATION_TYPE = 19;
	public static final int REMINDER_TYPE = 20;

	private final String CHANNEL_ID_EXPOSURE_NOTIFICATION = "ExposureNotificaitons";
	private final String CHANNEL_ID_REMINDER = "Reminders";

	private Context context;


	public NotificationHelper(Context context) {
		this.context = context;
	}


	private void createNotificationChannel(String channelId, String channelName) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel channel =
					new NotificationChannel(channelId, channelName,
							NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}
	}

	private PendingIntent createReminderPendingIntent() {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(NOTIFICATION_TYPE, REMINDER_TYPE);
		return TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(intent)
				.getPendingIntent(REMINDER_TYPE, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent createExposurePendingIntent(long exposureId) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(NOTIFICATION_TYPE, EXPOSURE_NOTIFICATION_TYPE);
		intent.putExtra(EXPOSURE_ID, exposureId);
		intent.setAction(Long.toString(exposureId));
		return TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(intent)
				.getPendingIntent(EXPOSURE_NOTIFICATION_TYPE, PendingIntent.FLAG_ONE_SHOT);
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

		createNotificationChannel(CHANNEL_ID_EXPOSURE_NOTIFICATION, context.getString(R.string.android_notification_channel_name));
		PendingIntent pendingIntent = createExposurePendingIntent(exposureId);
		String title = context.getString(R.string.exposure_notification_title);
		String message = context.getString(R.string.exposure_notification_body);

		Notification notification = createNotification(title, message, pendingIntent, CHANNEL_ID_EXPOSURE_NOTIFICATION);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(message.hashCode(), notification);
	}

	public void showReminderNotification() {
		//TODO: Add quick actions to reminder and make sure to directly open CheckOut Fragment
		createNotificationChannel(CHANNEL_ID_REMINDER, context.getString(R.string.android_reminder_channel_name));
		PendingIntent pendingIntent = createReminderPendingIntent();
		String title = context.getString(R.string.checkout_reminder_title);
		String message = context.getString(R.string.checkout_reminder_text);

		Notification notification = createNotification(title, message, pendingIntent, CHANNEL_ID_REMINDER);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(message.hashCode(), notification);
	}

}
