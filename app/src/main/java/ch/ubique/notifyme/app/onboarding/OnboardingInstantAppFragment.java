package ch.ubique.notifyme.app.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.nio.charset.StandardCharsets;

import com.google.android.gms.instantapps.InstantApps;
import com.google.android.gms.instantapps.PackageManagerCompat;

import ch.ubique.notifyme.app.R;

public class OnboardingInstantAppFragment extends Fragment {

	public final static String TAG = OnboardingInstantAppFragment.class.getCanonicalName();
	private final static int REQUEST_CODE_INSTALL = 1;

	public OnboardingInstantAppFragment() { super(R.layout.fragment_onboarding_instant_app); }

	public static OnboardingInstantAppFragment newInstance() {
		return new OnboardingInstantAppFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Button installButton = view.findViewById(R.id.fragment_onboarding_instant_app_install);
		installButton.setOnClickListener(v -> showInstallPrompt());
	}

	private void showInstallPrompt() {
		storeInstantAppCookie();

		Intent postInstallIntent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_DEFAULT)
				.setPackage(requireContext().getPackageName());

		InstantApps.showInstallPrompt(requireActivity(), postInstallIntent, REQUEST_CODE_INSTALL, null);
	}

	private void storeInstantAppCookie() {
		PackageManagerCompat pmc = InstantApps.getPackageManagerCompat(requireContext());
		String url = requireActivity().getIntent().getData().toString();
		byte[] cookieContent = url.getBytes(StandardCharsets.UTF_8);

		if (cookieContent.length <= pmc.getInstantAppCookieMaxSize()) {
			pmc.setInstantAppCookie(cookieContent);
		}
	}

}
