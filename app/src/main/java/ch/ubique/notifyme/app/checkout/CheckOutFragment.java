package ch.ubique.notifyme.app.checkout;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainFragment;
import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.app.utils.ReminderHelper;
import ch.ubique.notifyme.app.utils.StringUtils;
import ch.ubique.notifyme.app.utils.VenueTypeIconHelper;

public class CheckOutFragment extends Fragment {

	public final static String TAG = CheckOutFragment.class.getCanonicalName();

	private final static long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

	private MainViewModel viewModel;
	private VenueInfo venueInfo;
	private CheckInState checkInState;

	private TextView titleTextView;
	private TextView subtitleTextView;
	private View doneButton;
	private View cancelButton;
	private EditText commentEditText;
	private TextView fromTime;
	private TextView toTime;
	private TextView dateTextView;
	private ImageView venueTypeIcon;

	public CheckOutFragment() { super(R.layout.fragment_check_out); }

	public static CheckOutFragment newInstance() {
		return new CheckOutFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		checkInState = viewModel.getCheckInState();
		venueInfo = checkInState.getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		titleTextView = view.findViewById(R.id.check_out_fragment_title);
		subtitleTextView = view.findViewById(R.id.check_out_fragment_subtitle);
		doneButton = view.findViewById(R.id.check_out_fragment_done_button);
		cancelButton = view.findViewById(R.id.check_out_fragment_cancel_button);
		commentEditText = view.findViewById(R.id.check_out_fragment_comment_edit_text);
		fromTime = view.findViewById(R.id.check_out_fragment_from_text_view);
		toTime = view.findViewById(R.id.check_out_fragment_to_text_view);
		dateTextView = view.findViewById(R.id.check_out_fragment_date);
		venueTypeIcon = view.findViewById(R.id.check_out_fragment_venue_type_icon);

		titleTextView.setText(venueInfo.getTitle());
		subtitleTextView.setText(venueInfo.getSubtitle());
		venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));

		checkInState.setCheckOutTime(System.currentTimeMillis());
		refreshTimeTextViews();

		fromTime.setOnClickListener(v -> showTimePicker(true));
		toTime.setOnClickListener(v -> showTimePicker(false));

		doneButton.setOnClickListener(v -> {
			ReminderHelper.removeReminder(getContext());
			saveEntry();
			showStartScreen();
		});
		cancelButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
	}

	private void refreshTimeTextViews() {
		fromTime.setText(StringUtils.getHourMinuteTimeString(checkInState.getCheckInTime(), "  :  "));
		toTime.setText(StringUtils.getHourMinuteTimeString(checkInState.getCheckOutTime(), "  :  "));
		dateTextView.setText(StringUtils.getCheckOutDateString(getContext(), checkInState.getCheckInTime(),
				checkInState.getCheckOutTime()));
	}

	private void showTimePicker(boolean isFromTime) {

		Calendar time = Calendar.getInstance();
		if (isFromTime) {
			time.setTimeInMillis(checkInState.getCheckInTime());
		} else {
			time.setTimeInMillis(checkInState.getCheckOutTime());
		}
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		TimePickerDialog timePicker;
		timePicker = new TimePickerDialog(getContext(), (picker, selectedHour, selectedMinute) -> {
			time.set(Calendar.HOUR_OF_DAY, selectedHour);
			time.set(Calendar.MINUTE, selectedMinute);
			if (isFromTime) {
				checkInState.setCheckInTime(time.getTimeInMillis());
			} else {
				checkInState.setCheckOutTime(time.getTimeInMillis());
			}
			if (checkInState.getCheckOutTime() < checkInState.getCheckInTime()) {
				checkInState.setCheckOutTime(checkInState.getCheckOutTime() + ONE_DAY_IN_MILLIS);
			} else if (checkInState.getCheckInTime() + ONE_DAY_IN_MILLIS < checkInState.getCheckOutTime()) {
				checkInState.setCheckOutTime(checkInState.getCheckOutTime() - ONE_DAY_IN_MILLIS);
			}
			refreshTimeTextViews();
		}, hour, minute, true);

		timePicker.show();
	}

	private void saveEntry() {
		long checkIn = checkInState.getCheckInTime();
		long checkOut = checkInState.getCheckOutTime();
		String comment = commentEditText.getText().toString();
		long id = CrowdNotifier.addCheckIn(checkIn, checkOut, venueInfo.getNotificationKey(), venueInfo.getPublicKey(),
				getContext());
		DiaryStorage.getInstance(getContext()).addEntry(new DiaryEntry(id, checkIn, checkOut, venueInfo, comment));
		viewModel.setCheckInState(null);
	}

	private void showStartScreen() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, MainFragment.newInstance())
				.commit();
	}

}
