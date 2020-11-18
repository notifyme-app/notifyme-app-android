package ch.ubique.notifyme.app.utils;

import android.widget.SimpleAdapter;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import ch.ubique.notifyme.app.R;

public enum ErrorState {
	NETWORK(R.string.error_network_title, R.string.error_network_text, R.string.error_action_retry, R.drawable.ic_error),
	NOTIFICATIONS_DISABLED(R.string.error_notification_deactivated_title, R.string.error_notification_deactivated_text,
			R.string.error_action_change_settings, R.drawable.ic_notification_off);


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
