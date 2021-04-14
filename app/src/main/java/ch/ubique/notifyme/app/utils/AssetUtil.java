package ch.ubique.notifyme.app.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.ubique.notifyme.base.BuildConfig;

public class AssetUtil {

	private static final String PREFIX_ASSET_FILE = "file:///android_asset/";
	private static final String FOLDER_NAME_IMPRESSUM = "impressum/";
	private static final String FILE_NAME_IMPRESSUM = "impressum.html";

	private static final String REPLACE_STRING_VERSION = "{VERSION}";
	private static final String REPLACE_STRING_BUILDNR = "{BUILD}";

	public static String getImpressumBaseUrl(Context context) {
		return PREFIX_ASSET_FILE + getFolderNameImpressum(context);
	}


	private static String getFolderNameImpressum(Context context) {
		return FOLDER_NAME_IMPRESSUM + context.getString(ch.ubique.notifyme.base.R.string.language_code) + "/";
	}

	public static String getImpressumHtml(Context context) {
		return loadImpressumHtmlFile(context, FILE_NAME_IMPRESSUM);
	}

	public static String loadImpressumHtmlFile(Context context, String filename) {
		try {
			StringBuilder html = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(context.getAssets().open(getFolderNameImpressum(context) + filename)))) {
				for (String line; (line = reader.readLine()) != null; ) {
					html.append(line);
				}
			}
			String impressum = html.toString();

			StringBuilder buildString =
					new StringBuilder(String.valueOf(BuildConfig.BUILD_TIME))
							.append(" / ")
							.append(BuildConfig.FLAVOR);

			impressum = impressum.replace(REPLACE_STRING_VERSION, BuildConfig.VERSION_NAME);
			impressum = impressum.replace(REPLACE_STRING_BUILDNR, buildString);
			return impressum;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
