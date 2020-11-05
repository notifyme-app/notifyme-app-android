package ch.ubique.n2step.app.checkout;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

import ch.ubique.n2step.app.MainFragment;
import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.sdk.N2STEP;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class CheckOutFragment extends Fragment {

	private final static String TAG = CheckOutFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private VenueInfo venueInfo;

	private TextView nameTextView;
	private TextView locationTextView;
	private TextView roomTextView;
	private View doneButton;
	private EditText commentEditText;
	private TextView fromTime;
	private TextView toTime;

	public CheckOutFragment() { super(R.layout.fragment_check_out); }

	public static CheckOutFragment newInstance() {
		return new CheckOutFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		venueInfo = viewModel.checkInState.getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		nameTextView = view.findViewById(R.id.check_out_fragment_name);
		locationTextView = view.findViewById(R.id.check_out_fragment_location);
		roomTextView = view.findViewById(R.id.check_out_fragment_room);
		doneButton = view.findViewById(R.id.check_out_fragment_done_button);
		commentEditText = view.findViewById(R.id.check_out_fragment_comment_edit_text);
		fromTime = view.findViewById(R.id.check_out_fragment_from_text_view);
		toTime = view.findViewById(R.id.check_out_fragment_to_text_view);

		nameTextView.setText(venueInfo.getName());
		locationTextView.setText(venueInfo.getLocation());
		roomTextView.setText(venueInfo.getRoom());
		viewModel.checkInState.setCheckOutTime(System.currentTimeMillis());
		refreshTimeTextViews();

		fromTime.setOnClickListener(v -> showTimePicker(true));
		toTime.setOnClickListener(v -> showTimePicker(false));

		doneButton.setOnClickListener(v -> {
			saveEntry();
			showStartScreen();
		});
	}

	private void refreshTimeTextViews() {
		Calendar checkIn = Calendar.getInstance();
		checkIn.setTimeInMillis(viewModel.checkInState.getCheckInTime());
		fromTime.setText(prependZero(checkIn.get(Calendar.HOUR_OF_DAY)) + "  :  " + prependZero(checkIn.get(Calendar.MINUTE)));
		Calendar checkOut = Calendar.getInstance();
		checkOut.setTimeInMillis(viewModel.checkInState.getCheckOutTime());
		toTime.setText(prependZero(checkOut.get(Calendar.HOUR_OF_DAY)) + "  :  " + prependZero(checkOut.get(Calendar.MINUTE)));
	}

	private String prependZero(int timeUnit) {
		if (timeUnit < 10) {
			return "0" + timeUnit;
		} else {
			return String.valueOf(timeUnit);
		}
	}

	private void showTimePicker(boolean isFromTime) {

		//TODO: Here we will also need some way to specify the Date
		//TODO: Make sure that Check In Time is always earlier than Check Out Time
		Calendar time = Calendar.getInstance();
		if (isFromTime) {
			time.setTimeInMillis(viewModel.checkInState.getCheckInTime());
		} else {
			time.setTimeInMillis(viewModel.checkInState.getCheckOutTime());
		}
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		TimePickerDialog timePicker;
		timePicker = new TimePickerDialog(getContext(), (picker, selectedHour, selectedMinute) -> {
			time.set(Calendar.HOUR_OF_DAY, selectedHour);
			time.set(Calendar.MINUTE, selectedMinute);
			if (isFromTime) {
				viewModel.checkInState.setCheckInTime(time.getTimeInMillis());
			} else {
				viewModel.checkInState.setCheckOutTime(time.getTimeInMillis());
			}
			refreshTimeTextViews();
		}, hour, minute, true);
		timePicker.setTitle("Select Time");
		timePicker.show();
	}

	private void saveEntry() {
		long id = N2STEP.addVenueVisit(viewModel.checkInState.getCheckInTime(), viewModel.checkInState.getCheckOutTime(),
				venueInfo.getNotificationKey(), venueInfo.getPublicKey(), getContext());
		//TODO: Save entry to Diary
		viewModel.setCheckInState(null);
	}

	private void showStartScreen() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, MainFragment.newInstance())
				.addToBackStack(MainFragment.class.getCanonicalName())
				.commit();
	}

}