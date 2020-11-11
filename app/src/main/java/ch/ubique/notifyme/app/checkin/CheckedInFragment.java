package ch.ubique.notifyme.app.checkin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.checkout.CheckOutFragment;
import ch.ubique.notifyme.app.utils.StringUtils;

public class CheckedInFragment extends Fragment {

	public final static String TAG = CheckedInFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private VenueInfo venueInfo;

	private TextView nameTextView;
	private TextView locationTextView;
	private TextView roomTextView;
	private ImageView venueTypeIcon;
	private View checkOutButton;
	private Toolbar toolbar;

	public CheckedInFragment() { super(R.layout.fragment_checked_in); }

	public static CheckedInFragment newInstance() {
		return new CheckedInFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		venueInfo = viewModel.checkInState.getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		nameTextView = view.findViewById(R.id.checked_in_fragment_name);
		locationTextView = view.findViewById(R.id.checked_in_fragment_location);
		roomTextView = view.findViewById(R.id.checked_in_fragment_room);
		venueTypeIcon = view.findViewById(R.id.checked_in_fragment_venue_type_icon);
		checkOutButton = view.findViewById(R.id.checked_in_fragment_check_out_button);
		toolbar = view.findViewById(R.id.checked_in_fragment_toolbar);

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		viewModel.startCheckInTimer();
		viewModel.timeSinceCheckIn
				.observe(getViewLifecycleOwner(), duration -> toolbar.setTitle(StringUtils.getDurationString(duration)));

		nameTextView.setText(venueInfo.getName());
		locationTextView.setText(venueInfo.getLocation());
		roomTextView.setText(venueInfo.getRoom());

		checkOutButton.setOnClickListener(v -> showCheckOutFragment());
	}

	private void showCheckOutFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, CheckOutFragment.newInstance())
				.addToBackStack(CheckOutFragment.TAG)
				.commit();
	}

}
