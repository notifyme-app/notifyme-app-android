package ch.ubique.notifyme.app.diary.share;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.diary.DiaryRecyclerAdapter;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisit;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisitDayHeader;
import ch.ubique.notifyme.app.reports.items.VenueVisitRecyclerItem;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.app.utils.ErrorDialog;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.StringUtils;

public class ShareDiaryFragment extends Fragment {

	public final static String TAG = ShareDiaryFragment.class.getCanonicalName();

	private ShareDiaryViewModel viewModel;

	private View cancelButton;
	private RecyclerView diaryList;
	private ViewGroup emptyDiaryView;
	private Button shareDiaryButton;
	private ProgressBar shareDiaryProgress;

	private DiaryStorage diaryStorage;
	private DiaryRecyclerAdapter diaryAdapter = new DiaryRecyclerAdapter();

	private List<DiaryEntry> diaryEntries = new ArrayList<>();

	public ShareDiaryFragment() {
		super(R.layout.fragment_share_diary);
	}

	public static ShareDiaryFragment newInstance() {
		return new ShareDiaryFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(ShareDiaryViewModel.class);
		diaryStorage = DiaryStorage.getInstance(requireContext());
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		cancelButton = view.findViewById(R.id.fragment_share_diray_cancel_button);
		diaryList = view.findViewById(R.id.fragment_share_diray_diary_list);
		emptyDiaryView = view.findViewById(R.id.fragment_share_diray_empty_diary);
		shareDiaryButton = view.findViewById(R.id.fragment_share_diray_button);
		shareDiaryProgress = view.findViewById(R.id.fragment_share_diray_loading_indicator);

		diaryAdapter.setShowStatusIcon(false);
		diaryList.setAdapter(diaryAdapter);

		shareDiaryButton.setOnClickListener(v -> uploadDiary());
		cancelButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

		viewModel.getDiaryUploadState().observe(getViewLifecycleOwner(), loadingState -> {
			switch (loadingState) {
				case LOADING:
					shareDiaryButton.setVisibility(View.INVISIBLE);
					shareDiaryProgress.setVisibility(View.VISIBLE);
					break;
				case SUCCESS:
					shareDiaryButton.setVisibility(View.VISIBLE);
					shareDiaryProgress.setVisibility(View.GONE);
					showSuccessFragment();
					break;
				case FAILURE:
					shareDiaryButton.setVisibility(View.VISIBLE);
					shareDiaryProgress.setVisibility(View.GONE);
					new ErrorDialog(requireContext(), ErrorState.NETWORK).show();
					break;
			}
		});

		loadDiaryEntries();
	}

	private void loadDiaryEntries() {
		diaryEntries = DiaryStorage.getInstance(getContext()).getEntries();

		ArrayList<VenueVisitRecyclerItem> items = new ArrayList<>();
		Collections.sort(diaryEntries, (d1, d2) -> Long.compare(d2.getArrivalTime(), d1.getArrivalTime()));

		boolean isEmpty = diaryEntries == null || diaryEntries.isEmpty();
		emptyDiaryView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
		shareDiaryButton.setEnabled(!isEmpty);

		String daysAgoString = "";
		for (DiaryEntry diaryEntry : diaryEntries) {
			String newDaysAgoString = StringUtils.getDaysAgoString(diaryEntry.getArrivalTime(), getContext());
			if (!newDaysAgoString.equals(daysAgoString)) {
				daysAgoString = newDaysAgoString;
				items.add(new ItemVenueVisitDayHeader(daysAgoString));
			}
			items.add(new ItemVenueVisit(null, diaryEntry, v -> {}));
		}

		diaryAdapter.setData(items);
	}

	private void uploadDiary() {
		viewModel.uploadDiaryEntries(diaryEntries);
	}

	private void showSuccessFragment() {
		requireActivity().getSupportFragmentManager().popBackStack();
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter, R.anim.modal_pop_exit)
				.replace(R.id.container, ShareDiarySuccessFragment.newInstance())
				.addToBackStack(ShareDiarySuccessFragment.TAG)
				.commit();
	}

}
