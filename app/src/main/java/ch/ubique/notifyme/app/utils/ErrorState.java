package ch.ubique.notifyme.app.utils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import ch.ubique.notifyme.app.R;

public enum ErrorState {
	NETWORK(R.string.error_network_title, R.string.error_network_text, R.string.error_action_retry, R.drawable.ic_error),
	NOTIFICATIONS_DISABLED(R.string.error_notification_deactivated_title, R.string.error_notification_deactivated_text,
			R.string.error_action_change_settings, R.drawable.ic_notification_off),
	CAMERA_ACCESS_DENIED(R.string.error_camera_permission_title, R.string.error_camera_permission_text,
			R.string.error_action_change_settings, R.drawable.ic_cam_off),
	NO_VALID_QR_CODE(R.string.error_title, R.string.qrscanner_error, R.string.ok_button, R.drawable.ic_error),
	ALREADY_CHECKED_IN(R.string.error_title, R.string.error_already_checked_in, R.string.ok_button, R.drawable.ic_error),
	FORCE_UPDATE_REQUIRED(R.string.error_force_update_title, R.string.error_force_update_text, R.string.error_action_update,
			R.drawable.ic_error);


	@StringRes private int titleResId;
	@StringRes private int textResId;
	@StringRes private int actionResId;
	@DrawableRes private int imageResId;

	ErrorState(@StringRes int titleResId, @StringRes int textResId, @StringRes int actionResId, @DrawableRes int imageResId) {
		this.titleResId = titleResId;
		this.textResId = textResId;
		this.actionResId = actionResId;
		this.imageResId = imageResId;
	}

	public int getTitleResId() {
		return titleResId;
	}

	public int getTextResId() {
		return textResId;
	}

	public int getActionResId() {
		return actionResId;
	}

	public int getImageResId() {
		return imageResId;
	}
}
