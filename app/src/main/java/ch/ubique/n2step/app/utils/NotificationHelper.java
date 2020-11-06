package ch.ubique.n2step.app.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import java.util.Map;

import ch.ubique.n2step.app.MainActivity;
import ch.ubique.n2step.app.R;

public class NotificationHelper {

	private final String CHANNEL_ID = "ExposureNotificaitons";

	private Context context;


	public NotificationHelper(Context context) {
		this.context = context;
	}


	private void createNotificationChannel() {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel channel =
					new NotificationChannel(CHANNEL_ID, context.getString(R.string.android_notification_channel_name),
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

	private Notification createNotification(String message, PendingIntent pendingIntent) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
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

		createNotificationChannel();
		PendingIntent pendingIntent = createPendingIntent();
		String message = "New Message";

		Notification notification = createNotification(message, pendingIntent);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(message.hashCode(), notification);
	}

}
