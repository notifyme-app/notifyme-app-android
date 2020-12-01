package ch.ubique.notifyme.app.onboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.notifyme.app.MainFragment;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.utils.Storage;
import ch.ubique.notifyme.app.utils.StringUtils;
import ch.ubique.notifyme.app.utils.UrlUtil;

public class OnboardingHintsFragment extends Fragment {

	public final static String TAG = OnboardingHintsFragment.class.getCanonicalName();

	public OnboardingHintsFragment() { super(R.layout.fragment_onboarding_hints); }

	public static OnboardingHintsFragment newInstance() {
		return new OnboardingHintsFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		View okButton = view.findViewById(R.id.fragment_onboarding_hints_ok_button);
		View agbLink = view.findViewById(R.id.fragment_onboarding_hints_link);
		TextView title = view.findViewById(R.id.fragment_onboarding_hints_title);

		title.setText(StringUtils.getTwoColoredString(getString(R.string.onboarding_hints_for_usage),
				getString(R.string.onboarding_hints_for_usage_highlight), getResources().getColor(R.color.primary, null)));
		agbLink.setOnClickListener(v -> UrlUtil.openUrl(requireContext(), getString(R.string.onboarding_agb_link)));
		okButton.setOnClickListener(v -> finishOnboarding());
	}

	private void finishOnboarding() {

		Storage.getInstance(requireContext()).setOnboardingCompleted(true);

		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, MainFragment.newInstance())
				.commit();
	}

}
