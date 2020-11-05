package ch.ubique.n2step.app.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
				TimeUnit.MILLISECONDS.toMinutes(duration) -
						TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(duration))
		);
	}

}
