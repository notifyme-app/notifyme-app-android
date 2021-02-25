package ch.ubique.notifyme.app.checkin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainActivity;
import ch.ubique.notifyme.app.MainFragment;
import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.checkout.CheckOutFragment;
import ch.ubique.notifyme.app.utils.StringUtils;
import ch.ubique.notifyme.app.utils.VenueTypeIconHelper;

public class CheckedInFragment extends Fragment implements MainActivity.BackPressListener {

	public final static String TAG = CheckedInFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private VenueInfo venueInfo;

	public CheckedInFragment() { super(R.layout.fragment_checked_in); }

	public static CheckedInFragment newInstance() {
		return new CheckedInFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		if (viewModel.getCheckInState() != null) venueInfo = viewModel.getCheckInState().getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

		TextView titleTextView = view.findViewById(R.id.checked_in_fragment_title);
		TextView subtitleTextView = view.findViewById(R.id.checked_in_fragment_subtitle);
		TextView timerTextView = view.findViewById(R.id.checked_in_fragment_timer);
		ImageView venueTypeIcon = view.findViewById(R.id.checked_in_fragment_venue_type_icon);
		View checkOutButton = view.findViewById(R.id.checked_in_fragment_check_out_button);
		Toolbar toolbar = view.findViewById(R.id.checked_in_fragment_toolbar);

		toolbar.setNavigationOnClickListener(v -> onBackPressed());

		viewModel.startCheckInTimer();
		viewModel.timeSinceCheckIn
				.observe(getViewLifecycleOwner(), duration -> timerTextView.setText(StringUtils.getDurationString(duration)));

		titleTextView.setText(venueInfo.getTitle());
		subtitleTextView.setText(venueInfo.getSubtitle());
		venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));

		checkOutButton.setOnClickListener(v -> showCheckOutFragment());
	}

	private void showCheckOutFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				.replace(R.id.container, CheckOutFragment.newInstance())
				.addToBackStack(CheckedInFragment.TAG)
				.commit();
	}


	@Override
	public boolean onBackPressed() {
		getActivity().getSupportFragmentManager().popBackStack(MainFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		return true;
	}

}
