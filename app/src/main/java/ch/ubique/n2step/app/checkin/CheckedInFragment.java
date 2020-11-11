package ch.ubique.n2step.app.checkin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.android.material.button.MaterialButtonToggleGroup;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.checkout.CheckOutFragment;
import ch.ubique.n2step.app.utils.ReminderHelper;
import ch.ubique.n2step.app.utils.StringUtils;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class CheckedInFragment extends Fragment {

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
		venueInfo = viewModel.checkInState.getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		TextView nameTextView = view.findViewById(R.id.checked_in_fragment_name);
		TextView locationTextView = view.findViewById(R.id.checked_in_fragment_location);
		TextView roomTextView = view.findViewById(R.id.checked_in_fragment_room);
		ImageView venueTypeIcon = view.findViewById(R.id.checked_in_fragment_venue_type_icon);
		View checkOutButton = view.findViewById(R.id.checked_in_fragment_check_out_button);
		Toolbar toolbar = view.findViewById(R.id.checked_in_fragment_toolbar);
		MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.checked_in_fragment_toggle_group);

		toggleGroup.check(viewModel.getSelectedReminderOption().getToggleButtonId());

		toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
			if (isChecked) {
				ReminderOption selectedReminderOption = ReminderOption.getReminderOptionForToggleButtonId(checkedId);
				ReminderHelper.setReminder(System.currentTimeMillis() + selectedReminderOption.getDelay(), getContext());
				viewModel.setSelectedReminderOption(selectedReminderOption);
			}
		});
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
