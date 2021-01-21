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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainFragment;
import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.utils.NotificationHelper;
import ch.ubique.notifyme.app.utils.ReminderHelper;
import ch.ubique.notifyme.base.R;
import ch.ubique.notifyme.base.model.ReminderOption;
import ch.ubique.notifyme.base.utils.VenueTypeIconHelper;

public class CheckInFragment extends Fragment {

	public static final String TAG = CheckInFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private VenueInfo venueInfo;

	public CheckInFragment() { super(R.layout.fragment_check_in); }

	public static CheckInFragment newInstance() {
		return new CheckInFragment();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		if (viewModel.getCheckInState() != null) {
			venueInfo = viewModel.getCheckInState().getVenueInfo();
		}
		checkIfAutoCheckoutHappened();
	}

	@Override
	public void onStart() {
		super.onStart();
		checkIfAutoCheckoutHappened();
	}

	private void checkIfAutoCheckoutHappened() {
		if (viewModel.getCheckInState() == null) {
			getParentFragmentManager().popBackStack(MainFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		TextView titleTextView = view.findViewById(R.id.check_in_fragment_title);
		TextView subtitleTextView = view.findViewById(R.id.check_in_fragment_subtitle);
		ImageView venueTypeIcon = view.findViewById(R.id.check_in_fragment_venue_type_icon);
		View checkInButton = view.findViewById(R.id.check_in_fragment_check_in_button);
		Toolbar toolbar = view.findViewById(R.id.check_in_fragment_toolbar);
		MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.check_in_fragment_toggle_group);

		titleTextView.setText(venueInfo.getTitle());
		subtitleTextView.setText(venueInfo.getSubtitle());
		venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));

		checkInButton.setOnClickListener(v -> {
			long checkInTime = System.currentTimeMillis();
			viewModel.startCheckInTimer();
			viewModel.setCheckedIn(true);
			viewModel.getCheckInState().setCheckInTime(checkInTime);
			NotificationHelper.getInstance(getContext()).startOngoingNotification(checkInTime, venueInfo);
			ReminderHelper.set8HourReminder(getContext());
			ReminderHelper.setAutoCheckOut(getContext());
			ReminderHelper.setReminder(System.currentTimeMillis() + viewModel.getSelectedReminderOption().getDelayMillis(),
					getContext());
			showHomeFragment();
		});

		for (ReminderOption option : ReminderOption.values()) {
			((MaterialButton) view.findViewById(option.getToggleButtonId())).setText(option.getName(getContext()));
		}
		toggleGroup.check(viewModel.getSelectedReminderOption().getToggleButtonId());
		toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
			if (isChecked) {
				ReminderOption selectedReminderOption = ReminderOption.getReminderOptionForToggleButtonId(checkedId);
				viewModel.setSelectedReminderOption(selectedReminderOption);
			}
		});

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		super.onViewCreated(view, savedInstanceState);
	}

	private void showHomeFragment() {
		FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(ch.ubique.notifyme.app.R.id.container, MainFragment.newInstance());
		transaction.commit();
	}

}
