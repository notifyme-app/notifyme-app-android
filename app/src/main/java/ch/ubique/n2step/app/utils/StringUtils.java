package ch.ubique.n2step.app.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ch.ubique.n2step.app.R;

public class StringUtils {

	public static SpannableString getTwoColoredString(String wholeString, String substring, int substringColor) {

		SpannableString spannable = new SpannableString(wholeString);
		int start = wholeString.toLowerCase().indexOf(substring.toLowerCase());
		int end = start + substring.length();
		spannable.setSpan(new ForegroundColorSpan(substringColor), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	public static String getDurationString(long duration) {
		return String.format(Locale.GERMAN, "%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(duration),
				TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS.toHours(duration)))
		);
	}

	public static String getHourMinuteTimeString(long timeStamp, String delimiter) {
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeStamp);
		return prependZero(time.get(Calendar.HOUR_OF_DAY)) + delimiter + prependZero(time.get(Calendar.MINUTE));
	}

	private static String prependZero(int timeUnit) {
		if (timeUnit < 10) {
			return "0" + timeUnit;
		} else {
			return String.valueOf(timeUnit);
		}
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
