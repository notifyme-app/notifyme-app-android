package ch.ubique.notifyme.app.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.app.utils.StringUtils;

public class ExposureFragment extends Fragment {

	public final static String TAG = ExposureFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private ExposureEvent exposure;
	private DiaryEntry diaryEntry;

	public ExposureFragment() { super(R.layout.fragment_exposure); }

	public static ExposureFragment newInstance() {
		return new ExposureFragment();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		//TODO: Refactor this to Fragment argument instead of going over ViewModel
		this.exposure = viewModel.getSelectedExposure();
		this.diaryEntry = DiaryStorage.getInstance(getContext()).getDiaryEntryWithId(exposure.getId());
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View closeButton = view.findViewById(R.id.fragment_exposure_close_button);
		TextView header = view.findViewById(R.id.fragment_exposure_header);
		TextView daysAgo = view.findViewById(R.id.fragment_exposure_days_ago);
		TextView dayTextView = view.findViewById(R.id.fragment_exposure_day_textview);
		TextView timeTextView = view.findViewById(R.id.fragment_exposure_time_textview);
		View whereLabel = view.findViewById(R.id.fragment_exposure_where_header);
		TextView nameTextView = view.findViewById(R.id.fragment_exposure_name_textview);
		TextView locationTextView = view.findViewById(R.id.fragment_exposure_details_textview);
		ImageView venueTypeIcon = view.findViewById(R.id.fragment_exposure_venue_type_icon);
		TextView notesHeader = view.findViewById(R.id.fragment_exposure_notes_header);
		TextView notes = view.findViewById(R.id.fragment_exposure_notes_textview);
		TextView message = view.findViewById(R.id.fragment_exposure_infobox_text);
		View infobox = view.findViewById(R.id.fragment_exposure_infobox);

		closeButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		header.setText(StringUtils
				.getTwoColoredString(getString(R.string.report_message_text), getString(R.string.report_message_text_highlight),
						getResources().getColor(R.color.tertiary, null)));
		daysAgo.setText(StringUtils.getDaysAgoString(exposure.getStartTime(), getContext()));
		dayTextView.setText(StringUtils.getCheckOutDateString(getContext(), exposure.getStartTime(), exposure.getEndTime()));
		String startTime = StringUtils.getHourMinuteTimeString(exposure.getStartTime(), ":");
		String endTime = StringUtils.getHourMinuteTimeString(exposure.getEndTime(), ":");
		timeTextView.setText(startTime + " — " + endTime);

		if (exposure.getMessage() != null) {
			message.setText(exposure.getMessage());
			infobox.setVisibility(View.VISIBLE);
		} else {
			infobox.setVisibility(View.GONE);
		}

		if (diaryEntry != null) {
			nameTextView.setText(diaryEntry.getVenueInfo().getTitle());
			locationTextView.setText(diaryEntry.getVenueInfo().getSubtitle());

			if (diaryEntry.getComment() != null && !diaryEntry.getComment().isEmpty()) {
				notes.setText(diaryEntry.getComment());
			} else {
				notes.setVisibility(View.GONE);
				notesHeader.setVisibility(View.GONE);
			}
			//TODO: Set Venue Type Icon
		} else {
			nameTextView.setVisibility(View.GONE);
			whereLabel.setVisibility(View.GONE);
			locationTextView.setVisibility(View.GONE);
			venueTypeIcon.setVisibility(View.GONE);
			notesHeader.setVisibility(View.GONE);
			notes.setVisibility(View.GONE);
		}
	}

}
