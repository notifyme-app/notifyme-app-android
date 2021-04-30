package ch.ubique.notifyme.app.diary;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import org.crowdnotifier.android.sdk.CrowdNotifier;

import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.base.utils.StringUtils;
import ch.ubique.notifyme.base.utils.VenueInfoExtensions;

public class EditDiaryEntryFragment extends Fragment {

	public final static String TAG = EditDiaryEntryFragment.class.getCanonicalName();

	private final static long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	private final static String ARG_SHOW_HIDE_IN_DIARY_BUTTON = "ARG_SHOW_HIDE_IN_DIARY_BUTTON";
	private final static String ARG_DIARY_ENTRY_ID = "ARG_DIARY_ENTRY_ID";

	private DiaryStorage diaryStorage;
	private DiaryEntry diaryEntry;
	boolean showHideInDiaryButton;

	private TextView titleTextView;
	private TextView subtitleTextView;
	private View doneButton;
	private View cancelButton;
	private EditText commentEditText;
	private TextView fromTime;
	private TextView toTime;
	private TextView dateTextView;
	private View hideInDiaryButton;
	private ImageView venueTypeIcon;


	public EditDiaryEntryFragment() { super(R.layout.fragment_edit_diary_entry); }

	public static EditDiaryEntryFragment newInstance(boolean showHideInDiaryButton, long diaryEntryId) {
		EditDiaryEntryFragment fragment = new EditDiaryEntryFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_SHOW_HIDE_IN_DIARY_BUTTON, showHideInDiaryButton);
		args.putLong(ARG_DIARY_ENTRY_ID, diaryEntryId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		diaryStorage = DiaryStorage.getInstance(requireContext());
		long diaryEntryId = getArguments().getLong(ARG_DIARY_ENTRY_ID);
		diaryEntry = diaryStorage.getDiaryEntryWithId(diaryEntryId);
		showHideInDiaryButton = getArguments().getBoolean(ARG_SHOW_HIDE_IN_DIARY_BUTTON);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		titleTextView = view.findViewById(R.id.edit_diary_entry_title);
		subtitleTextView = view.findViewById(R.id.edit_diary_entry_subtitle);
		doneButton = view.findViewById(R.id.edit_diary_entry_done_button);
		cancelButton = view.findViewById(R.id.edit_diary_entry_cancel_button);
		commentEditText = view.findViewById(R.id.edit_diary_entry_comment_edit_text);
		fromTime = view.findViewById(R.id.edit_diary_entry_from_text_view);
		toTime = view.findViewById(R.id.edit_diary_entry_to_text_view);
		dateTextView = view.findViewById(R.id.edit_diary_entry_date);
		hideInDiaryButton = view.findViewById(R.id.edit_diary_entry_hide_from_diary_button);
		venueTypeIcon = view.findViewById(R.id.edit_diary_entry_venue_type_icon);

		titleTextView.setText(diaryEntry.getVenueInfo().getTitle());
		subtitleTextView.setText(VenueInfoExtensions.getSubtitle(diaryEntry.getVenueInfo()));
		venueTypeIcon.setImageResource(VenueInfoExtensions.getVenueTypeDrawable(diaryEntry.getVenueInfo()));

		refreshTimeTextViews();

		fromTime.setOnClickListener(v -> showTimePicker(true));
		toTime.setOnClickListener(v -> showTimePicker(false));
		commentEditText.setText(diaryEntry.getComment());

		if (showHideInDiaryButton) {
			hideInDiaryButton.setVisibility(View.VISIBLE);
			hideInDiaryButton.setOnClickListener(v -> hideInDiary());
		} else {
			hideInDiaryButton.setVisibility(View.GONE);
		}

		doneButton.setOnClickListener(v -> {
			saveEntry();
			requireActivity().getSupportFragmentManager().popBackStack();
		});
		cancelButton.setOnClickListener(v -> {
			requireActivity().getSupportFragmentManager().popBackStack();
		});
	}

	private void refreshTimeTextViews() {
		fromTime.setText(StringUtils.getHourMinuteTimeString(diaryEntry.getArrivalTime(), "  :  "));
		toTime.setText(StringUtils.getHourMinuteTimeString(diaryEntry.getDepartureTime(), "  :  "));
		dateTextView.setText(StringUtils.getCheckOutDateString(getContext(), diaryEntry.getArrivalTime(),
				diaryEntry.getDepartureTime()));
	}

	private void showTimePicker(boolean isFromTime) {

		Calendar time = Calendar.getInstance();
		if (isFromTime) {
			time.setTimeInMillis(diaryEntry.getArrivalTime());
		} else {
			time.setTimeInMillis(diaryEntry.getDepartureTime());
		}
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		TimePickerDialog timePicker;
		timePicker = new TimePickerDialog(getContext(), (picker, selectedHour, selectedMinute) -> {
			time.set(Calendar.HOUR_OF_DAY, selectedHour);
			time.set(Calendar.MINUTE, selectedMinute);
			if (isFromTime) {
				diaryEntry.setArrivalTime(time.getTimeInMillis());
			} else {
				diaryEntry.setDepartureTime(time.getTimeInMillis());
			}
			if (diaryEntry.getDepartureTime() < diaryEntry.getArrivalTime()) {
				diaryEntry.setDepartureTime(diaryEntry.getDepartureTime() + ONE_DAY_IN_MILLIS);
			} else if (diaryEntry.getArrivalTime() + ONE_DAY_IN_MILLIS < diaryEntry.getDepartureTime()) {
				diaryEntry.setDepartureTime(diaryEntry.getDepartureTime() - ONE_DAY_IN_MILLIS);
			}
			refreshTimeTextViews();
		}, hour, minute, true);

		timePicker.show();
	}

	private void saveEntry() {
		String comment = commentEditText.getText().toString();
		diaryEntry.setComment(comment);
		CrowdNotifier.updateCheckIn(diaryEntry.getId(), diaryEntry.getArrivalTime(), diaryEntry.getDepartureTime(),
				diaryEntry.getVenueInfo(), getContext());
		diaryStorage.updateEntry(diaryEntry);
	}


	private void hideInDiary() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.add(HideInDiaryDialogFragment.newInstance(diaryEntry.getId()), HideInDiaryDialogFragment.TAG)
				.commit();
	}

}
