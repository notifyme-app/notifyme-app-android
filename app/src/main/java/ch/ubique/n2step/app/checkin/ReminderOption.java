package ch.ubique.n2step.app.checkin;

import android.content.Context;
import androidx.annotation.IdRes;

import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.utils.ReminderHelper;

public enum ReminderOption {

	OFF(0, R.id.checked_in_fragment_toggle_button_1),
	THIRTY_MINUTES(30, R.id.checked_in_fragment_toggle_button_2),
	ONE_HOUR(60, R.id.checked_in_fragment_toggle_button_3),
	TWO_HOURS(120, R.id.checked_in_fragment_toggle_button_4);

	private static final long MINUTE_IN_MILLIS = 60 * 1000;

	private int delayMinutes;
	@IdRes private int toggleButtonId;
	private String name;

	ReminderOption(int delayMinutes, @IdRes int toggleButtonId) {
		this.delayMinutes = delayMinutes;
		this.toggleButtonId = toggleButtonId;
		this.name = name;
	}

	public int getToggleButtonId() {
		return toggleButtonId;
	}

	public int getDelayMinutes() {
		return delayMinutes;
	}

	public long getDelayMillis() {
		return delayMinutes * MINUTE_IN_MILLIS;
	}

	public int getDelayHours() {
		return delayMinutes / 60;
	}

	public String getName(Context context) {
		switch (this) {
			case THIRTY_MINUTES:
				return context.getString(R.string.reminder_option_minutes).replace("{MINUTES}", String.valueOf(getDelayMinutes()));
			case OFF:
				return context.getString(R.string.reminder_option_off);
			default:
				return context.getString(R.string.reminder_option_hours).replace("{HOURS}", String.valueOf(getDelayHours()));
		}
	}

	public static ReminderOption getReminderOptionForToggleButtonId(@IdRes int toggleButtonId) {
		switch (toggleButtonId) {
			case R.id.checked_in_fragment_toggle_button_2:
				return THIRTY_MINUTES;
			case R.id.checked_in_fragment_toggle_button_3:
				return ONE_HOUR;
			case R.id.checked_in_fragment_toggle_button_4:
				return TWO_HOURS;
			default:
				return OFF;
		}
	}
}
