package ch.ubique.notifyme.app.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ch.ubique.notifyme.app.R;


public class StringUtils {

	private static final long ONE_HOUR = 60 * 60 * 1000;

	public static SpannableString getTwoColoredString(String wholeString, String substring, int substringColor) {

		SpannableString spannable = new SpannableString(wholeString);
		int start = wholeString.toLowerCase().indexOf(substring.toLowerCase());
		int end = start + substring.length();
		if (start != -1) {
			spannable.setSpan(new ForegroundColorSpan(substringColor), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	/**
	 * Formats a duration in milliseconds to a String of hours, minutes and seconds, or to only hours and minutes if the
	 * duration is more than 10 hours
	 * @param duration in milliseconds
	 * @return a formatted duration String
	 */
	public static String getShortDurationString(long duration) {
		if (duration >= 10 * ONE_HOUR) {
			return String.format(Locale.GERMAN, "%1d:%02d",
					TimeUnit.MILLISECONDS.toHours(duration),
					TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS.toHours(duration)))
			);
		} else {
			return getDurationString(duration);
		}
	}

	public static String getDurationString(long duration) {
		if (duration >= ONE_HOUR) {
			return String.format(Locale.GERMAN, "%01d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(duration),
					TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS.toHours(duration))),
					TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(duration)))
			);
		} else {
			return String.format(Locale.GERMAN, "%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(duration)))
			);
		}
	}

	public static String getCheckOutDateString(Context context, long checkInTime, long checkOutTime) {
		DateFormat dateFormat = new SimpleDateFormat("EEEE, dd. MMMM");
		String checkInDate = dateFormat.format(new Date(checkInTime));
		String checkOutDate = dateFormat.format(new Date(checkOutTime));
		if (checkInDate.equals(checkOutDate)) {
			return checkInDate;
		} else {
			return context.getResources().getString(R.string.checkout_from_to_date).replace("{DATE1}", checkInDate)
					.replace("{DATE2}", "\n" + checkOutDate);
		}
	}

	public static String getHourMinuteTimeString(long timeStamp, String delimiter) {
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeStamp);
		return prependZero(time.get(Calendar.HOUR_OF_DAY)) + delimiter + prependZero(time.get(Calendar.MINUTE));
	}

	public static String getDaysAgoString(long timeStamp, Context context) {
		final long diff = getStartOfDay().getTime() - timeStamp;
		if (diff < 0) {
			return context.getResources().getString(R.string.report_message_today);
		} else {
			long daysAgo = TimeUnit.MILLISECONDS.toDays(diff) + 1;
			if (daysAgo == 1) {
				return context.getResources().getString(R.string.report_message_one_day_ago);
			} else {
				return context.getResources().getString(R.string.report_message_days_ago)
						.replace("{NUMBER}", String.valueOf(daysAgo));
			}
		}
	}

	private static String prependZero(int timeUnit) {
		if (timeUnit < 10) {
			return "0" + timeUnit;
		} else {
			return String.valueOf(timeUnit);
		}
	}

	private static Date getStartOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
