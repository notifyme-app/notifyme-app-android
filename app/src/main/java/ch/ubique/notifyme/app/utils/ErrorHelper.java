package ch.ubique.notifyme.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import ch.ubique.notifyme.app.R;

public class ErrorHelper {

	public static void updateErrorView(View errorView, ErrorState errorState, Runnable customButtonClickAction, Context context) {
		updateErrorView(errorView, errorState, customButtonClickAction, context, true);
	}

	public static void updateErrorView(View errorView, ErrorState errorState, Runnable customButtonClickAction, Context context,
			boolean showButton) {
		((TextView) errorView.findViewById(R.id.error_status_title)).setText(errorState.getTitleResId());
		((TextView) errorView.findViewById(R.id.error_status_text)).setText(errorState.getTextResId());
		((ImageView) errorView.findViewById(R.id.error_status_image))
				.setImageDrawable(ContextCompat.getDrawable(errorView.getContext(), errorState.getImageResId()));

		TextView buttonView = errorView.findViewById(R.id.error_status_button);
		if (showButton) {
			buttonView.setVisibility(View.VISIBLE);
			buttonView.setText(errorState.getActionResId());
			buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			buttonView.setOnClickListener(v -> executeErrorAction(errorState, customButtonClickAction, context));
		} else {
			buttonView.setVisibility(View.GONE);
		}
	}

	private static void executeErrorAction(ErrorState errorState, Runnable customButtonClickAction, Context context) {
		if (customButtonClickAction != null) customButtonClickAction.run();
		switch (errorState) {
			case NOTIFICATIONS_DISABLED:
				openNotificationSettings(context);
				break;
			case CAMERA_ACCESS_DENIED:
				openApplicationSettings(context);
				break;
			case FORCE_UPDATE_REQUIRED:
			case UPDATE_REQUIRED:
				updateApp(context);
				break;
		}
	}

	private static void updateApp(Context context) {
		UrlUtil.openUrl(context, "market://details?id=" + context.getPackageName());
	}

	private static void openApplicationSettings(Context context) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", context.getPackageName(), null);
		intent.setData(uri);
		context.startActivity(intent);
	}

	private static void openNotificationSettings(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + context.getPackageName()));
			context.startActivity(intent);
		}
	}

}
