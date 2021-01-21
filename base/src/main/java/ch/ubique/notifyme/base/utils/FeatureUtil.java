package ch.ubique.notifyme.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * A base utility class to help navigate between feature modules via app links.
 */
public class FeatureUtil {

	public static final String APP_SCHEME = "notifyme://";
	public static final String FEATURE_NAME_MAIN = "main";
	public static final String ARG_MAIN_URL = "arg_main_url";
	public static final String FEATURE_NAME_ONBOARDING = "onboarding";

	public static Intent createIntentForMain(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_SCHEME + FEATURE_NAME_MAIN));
		intent.setPackage(context.getPackageName());

		if (url != null) {
			intent.putExtra(ARG_MAIN_URL, url);
		}

		return intent;
	}

	public static Intent createIntentForOnboarding(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_SCHEME + FEATURE_NAME_ONBOARDING));
		intent.setPackage(context.getPackageName());
		return intent;
	}

}
