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
import ch.ubique.notifyme.app.diary.HideInDiaryDialogFragment;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.base.utils.StringUtils;
import ch.ubique.notifyme.base.utils.VenueTypeIconHelper;

public class ExposureFragment extends Fragment {

	public final static String TAG = ExposureFragment.class.getCanonicalName();
	private static final String EXPOSURE_ID_ARG = "EXPOSURE_ID_ARG";

	private MainViewModel viewModel;
	private ExposureEvent exposure;
	private DiaryEntry diaryEntry;

	public ExposureFragment() { super(R.layout.fragment_exposure); }

	public static ExposureFragment newInstance(long exposureId) {
		ExposureFragment fragment = new ExposureFragment();
		Bundle args = new Bundle();
		args.putLong(EXPOSURE_ID_ARG, exposureId);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		this.exposure = viewModel.getExposureWithId(getArguments().getLong(EXPOSURE_ID_ARG));
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
		View deleteButton = view.findViewById(R.id.fragment_exposure_delete_button);

		closeButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		deleteButton.setOnClickListener(v -> showDeleteExposureDialog());
		header.setText(StringUtils
				.getTwoColoredString(getString(ch.ubique.notifyme.base.R.string.report_message_text), getString(ch.ubique.notifyme.base.R.string.report_message_text_highlight),
						getResources().getColor(ch.ubique.notifyme.base.R.color.tertiary, null)));
		daysAgo.setText(StringUtils.getDaysAgoString(exposure.getStartTime(), getContext()));
		dayTextView.setText(StringUtils.getCheckOutDateString(getContext(), exposure.getStartTime(), exposure.getEndTime()));
		String startTime = StringUtils.getHourMinuteTimeString(exposure.getStartTime(), ":");
		String endTime = StringUtils.getHourMinuteTimeString(exposure.getEndTime(), ":");
		timeTextView.setText(startTime + " — " + endTime);

		if (exposure.getMessage() != null && !exposure.getMessage().isEmpty()) {
			infobox.setVisibility(View.VISIBLE);
			message.setText(exposure.getMessage());
		} else {
			infobox.setVisibility(View.GONE);
		}

		if (diaryEntry != null) {
			nameTextView.setText(diaryEntry.getVenueInfo().getTitle());
			locationTextView.setText(diaryEntry.getVenueInfo().getSubtitle());
			venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(diaryEntry.getVenueInfo().getVenueType()));

			if (diaryEntry.getComment() != null && !diaryEntry.getComment().isEmpty()) {
				notes.setText(diaryEntry.getComment());
			} else {
				notes.setVisibility(View.GONE);
				notesHeader.setVisibility(View.GONE);
			}
		} else {
			nameTextView.setVisibility(View.GONE);
			whereLabel.setVisibility(View.GONE);
			locationTextView.setVisibility(View.GONE);
			venueTypeIcon.setVisibility(View.GONE);
			notesHeader.setVisibility(View.GONE);
			notes.setVisibility(View.GONE);
		}
	}

	private void showDeleteExposureDialog() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.add(DeleteNotificationDialogFragment.newInstance(exposure.getId()), DeleteNotificationDialogFragment.TAG)
				.commit();
	}

}
