package ch.ubique.notifyme.app.checkin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.utils.ReminderHelper;
import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.checkout.CheckOutFragment;
import ch.ubique.notifyme.app.utils.StringUtils;

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
		venueInfo = viewModel.getCheckInState().getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

		TextView titleTextView = view.findViewById(R.id.checked_in_fragment_title);
		TextView subtitleTextView = view.findViewById(R.id.checked_in_fragment_subtitle);
		View checkOutButton = view.findViewById(R.id.checked_in_fragment_check_out_button);
		Toolbar toolbar = view.findViewById(R.id.checked_in_fragment_toolbar);
		MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.checked_in_fragment_toggle_group);
		for (ReminderOption option : ReminderOption.values()) {
			((MaterialButton) view.findViewById(option.getToggleButtonId())).setText(option.getName(getContext()));
		}

		toggleGroup.check(viewModel.getSelectedReminderOption().getToggleButtonId());

		toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
			if (isChecked) {
				ReminderOption selectedReminderOption = ReminderOption.getReminderOptionForToggleButtonId(checkedId);
				ReminderHelper.setReminder(System.currentTimeMillis() + selectedReminderOption.getDelayMillis(), getContext());
				viewModel.setSelectedReminderOption(selectedReminderOption);
			}
		});
		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		viewModel.startCheckInTimer();
		viewModel.timeSinceCheckIn
				.observe(getViewLifecycleOwner(), duration -> toolbar.setTitle(StringUtils.getDurationString(duration)));

		titleTextView.setText(venueInfo.getTitle());
		subtitleTextView.setText(venueInfo.getSubtitle());

		checkOutButton.setOnClickListener(v -> showCheckOutFragment());
	}

	private void showCheckOutFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				.replace(R.id.container, CheckOutFragment.newInstance())
				.addToBackStack(CheckOutFragment.TAG)
				.commit();
	}

}
