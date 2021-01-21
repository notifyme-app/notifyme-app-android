package ch.ubique.notifyme.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.instantapps.InstantApps;
import com.google.android.gms.instantapps.PackageManagerCompat;

import ch.ubique.notifyme.base.utils.Storage;
import ch.ubique.notifyme.base.utils.StringUtils;
import ch.ubique.notifyme.base.utils.UrlUtil;

public class OnboardingHintsFragment extends Fragment {

	public final static String TAG = OnboardingHintsFragment.class.getCanonicalName();
	private boolean isInstantApp = false;

	public OnboardingHintsFragment() { super(R.layout.fragment_onboarding_hints); }

	public static OnboardingHintsFragment newInstance() {
		return new OnboardingHintsFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Button okButton = view.findViewById(R.id.fragment_onboarding_hints_ok_button);
		View agbLink = view.findViewById(R.id.fragment_onboarding_hints_link);
		TextView title = view.findViewById(R.id.fragment_onboarding_hints_title);

		PackageManagerCompat pmc = InstantApps.getPackageManagerCompat(requireContext());
		isInstantApp = pmc.isInstantApp();

		if (isInstantApp) {
			okButton.setText(ch.ubique.notifyme.base.R.string.onboarding_continue_button);
		}

		title.setText(StringUtils.getTwoColoredString(getString(ch.ubique.notifyme.base.R.string.onboarding_hints_for_usage),
				getString(ch.ubique.notifyme.base.R.string.onboarding_hints_for_usage_highlight), getResources().getColor(ch.ubique.notifyme.base.R.color.primary, null)));
		agbLink.setOnClickListener(v -> UrlUtil.openUrl(requireContext(), getString(ch.ubique.notifyme.base.R.string.onboarding_agb_link)));
		okButton.setOnClickListener(v -> continueOrFinishOnboarding());
	}

	private void continueOrFinishOnboarding() {
		if (isInstantApp) {
			// Show the install now onboarding step if this is the instant app
			requireActivity().getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(ch.ubique.notifyme.base.R.anim.slide_enter, ch.ubique.notifyme.base.R.anim.slide_exit,
							ch.ubique.notifyme.base.R.anim.slide_pop_enter, ch.ubique.notifyme.base.R.anim.slide_pop_exit)
					.replace(ch.ubique.notifyme.onboarding.R.id.container, OnboardingInstantAppFragment.newInstance())
					.addToBackStack(OnboardingInstantAppFragment.TAG)
					.commit();
		} else {
			// Complete the onboarding if this is the installable app
			Storage.getInstance(requireContext()).setOnboardingCompleted(true);
			requireActivity().finish();
		}
	}

}
