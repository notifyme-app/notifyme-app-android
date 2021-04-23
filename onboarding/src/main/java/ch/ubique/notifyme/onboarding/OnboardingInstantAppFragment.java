package ch.ubique.notifyme.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.nio.charset.StandardCharsets;

import com.google.android.gms.instantapps.InstantApps;
import com.google.android.gms.instantapps.PackageManagerCompat;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.VenueInfo;
import org.crowdnotifier.android.sdk.utils.QrUtils;

import ch.ubique.notifyme.base.BuildConfig;
import ch.ubique.notifyme.base.utils.ErrorHelper;
import ch.ubique.notifyme.base.utils.ErrorState;
import ch.ubique.notifyme.base.utils.Storage;
import ch.ubique.notifyme.base.utils.VenueTypeIconHelper;

public class OnboardingInstantAppFragment extends Fragment {

	public final static String TAG = OnboardingInstantAppFragment.class.getCanonicalName();
	private final static int REQUEST_CODE_INSTALL = 1;
	private final static String QR_URL_PREFIX = "https://qr.notify-me.ch?v=";
	private String qrCodeUrl;
	private View errorView;
	private ViewGroup venueInfoContainer;
	private ImageView venueTypeIcon;
	private TextView venueTitle;
	private TextView venueSubtitle;

	public OnboardingInstantAppFragment() { super(R.layout.fragment_onboarding_instant_app); }

	public static OnboardingInstantAppFragment newInstance() {
		return new OnboardingInstantAppFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		errorView = view.findViewById(R.id.onboarding_instant_app_error_view);
		venueInfoContainer = view.findViewById(R.id.onboarding_instant_app_venue_info);
		venueTypeIcon = view.findViewById(R.id.onboarding_instant_app_venue_type_icon);
		venueTitle = view.findViewById(R.id.onboarding_instant_app_venue_title);
		venueSubtitle = view.findViewById(R.id.onboarding_instant_app_venue_subtitle);
		Button installButton = view.findViewById(R.id.onboarding_instant_app_install_button);
		installButton.setOnClickListener(v -> showInstallPrompt());

		if (requireActivity().getIntent().getData() != null) {
			qrCodeUrl = requireActivity().getIntent().getData().toString();
		}

		Log.d(TAG, "QR Code Url: " + qrCodeUrl);

		showVenueInfo();
	}

	private void showVenueInfo() {
		// If the instant app is started without a url (should never happen) or with the default url, don't show any QR Code
		// information nor error.
		// Note: https://qr.notify-me.ch is defined as the default url in the Manifest. When clicking on "Try now" in the Playstore
		// the Instant App is started with this url and some parameters, e.g.:
		// https://qr.notify-me.ch/?referrer=utm_source%3D(not%2520set)%26utm_medium%3D(not%2520set)
		if (qrCodeUrl == null || !qrCodeUrl.startsWith(QR_URL_PREFIX)) {
			venueInfoContainer.setVisibility(View.GONE);
			errorView.setVisibility(View.GONE);
			return;
		}

		try {
			VenueInfo venueInfo = CrowdNotifier.getVenueInfo(qrCodeUrl, BuildConfig.ENTRY_QR_CODE_PREFIX);
			venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));
			venueTitle.setText(venueInfo.getTitle());
			venueSubtitle.setText(venueInfo.getSubtitle());
		} catch (QrUtils.QRException e) {
			venueInfoContainer.setVisibility(View.GONE);
			errorView.setVisibility(View.VISIBLE);
			handleInvalidQRCodeExceptions(qrCodeUrl, e);
		}
	}

	private void handleInvalidQRCodeExceptions(String qrCodeUrl, QrUtils.QRException e) {
		if (e instanceof QrUtils.InvalidQRCodeVersionException) {
			ErrorHelper.updateErrorView(errorView, ErrorState.UPDATE_REQUIRED, null, getContext(), true);
		} else if (e instanceof QrUtils.NotYetValidException) {
			ErrorHelper.updateErrorView(errorView, ErrorState.QR_CODE_NOT_YET_VALID, null, getContext(), false);
		} else if (e instanceof QrUtils.NotValidAnymoreException) {
			ErrorHelper.updateErrorView(errorView, ErrorState.QR_CODE_NOT_VALID_ANYMORE, null, getContext(), false);
		} else {
			if (qrCodeUrl.startsWith(BuildConfig.TRACE_QR_CODE_PREFIX)) {
				Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCodeUrl));
				startActivity(openBrowserIntent);
			} else {
				ErrorHelper.updateErrorView(errorView, ErrorState.NO_VALID_QR_CODE, null, getContext(), false);
			}
		}
	}

	private void showInstallPrompt() {
		storeInstantAppCookie();

		Intent postInstallIntent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_DEFAULT)
				.setPackage(requireContext().getPackageName());

		InstantApps.showInstallPrompt(requireActivity(), postInstallIntent, REQUEST_CODE_INSTALL, null);
		Storage.getInstance(requireContext()).setOnboardingCompleted(true);
	}

	private void storeInstantAppCookie() {
		if (qrCodeUrl == null || !qrCodeUrl.startsWith(QR_URL_PREFIX)) return;
		PackageManagerCompat pmc = InstantApps.getPackageManagerCompat(requireContext());
		byte[] cookieContent = qrCodeUrl.getBytes(StandardCharsets.UTF_8);

		if (cookieContent.length <= pmc.getInstantAppCookieMaxSize()) {
			pmc.setInstantAppCookie(cookieContent);
		}
	}

}
