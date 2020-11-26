package ch.ubique.notifyme.app.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.utils.StringUtils;

public class OnboardingIntroductionFragment extends Fragment {


	public final static String TAG = OnboardingIntroductionFragment.class.getCanonicalName();

	public OnboardingIntroductionFragment() { super(R.layout.fragment_onboarding_introduction); }

	public static OnboardingIntroductionFragment newInstance() {
		return new OnboardingIntroductionFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		View continueButton = view.findViewById(R.id.fragment_onboarding_introduction_continue_button);
		TextView titleTextView = view.findViewById(R.id.fragment_onboarding_introduction_title);

		titleTextView.setText(StringUtils.getTwoColoredString(getString(R.string.app_name), getString(R.string.app_name_highlight),
				getResources().getColor(R.color.primary, null)));
		continueButton.setOnClickListener(v -> showOnboardingHintsFragment());
	}

	private void showOnboardingHintsFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, OnboardingHintsFragment.newInstance())
				.addToBackStack(OnboardingHintsFragment.TAG)
				.commit();
	}

}
