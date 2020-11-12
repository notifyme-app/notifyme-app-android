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

	private PendingIntent createPendingIntent() {
		Intent intent = new Intent(context, MainActivity.class);
		return TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(intent)
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private Notification createNotification(String message, PendingIntent pendingIntent, String channelId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_notification)
				.setColor(ContextCompat.getColor(context, R.color.primary))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		return builder.build();
	}


	public void showExposureNotification() {

		createNotificationChannel(CHANNEL_ID_EXPOSURE_NOTIFICATION, context.getString(R.string.android_notification_channel_name));
		PendingIntent pendingIntent = createPendingIntent();
		String message = "New Message";

		Notification notification = createNotification(message, pendingIntent, CHANNEL_ID_EXPOSURE_NOTIFICATION);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(message.hashCode(), notification);
	}

	public void showReminderNotification() {
		//TODO: Add quick actions to reminder and make sure to directly open CheckOut Fragment
		createNotificationChannel(CHANNEL_ID_REMINDER, context.getString(R.string.android_reminder_channel_name));
		PendingIntent pendingIntent = createPendingIntent();
		String message = "Reminder";

		Notification notification = createNotification(message, pendingIntent, CHANNEL_ID_REMINDER);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(message.hashCode(), notification);
	}

}
