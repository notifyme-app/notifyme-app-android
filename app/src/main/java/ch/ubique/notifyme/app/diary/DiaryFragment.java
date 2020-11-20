package ch.ubique.notifyme.app.diary;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.reports.ExposureFragment;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisit;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisitDayHeader;
import ch.ubique.notifyme.app.reports.items.VenueVisitRecyclerItem;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.app.utils.StringUtils;

public class DiaryFragment extends Fragment {

	public final static String TAG = DiaryFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private Toolbar toolbar;
	private DiaryRecyclerAdapter recyclerAdapter = new DiaryRecyclerAdapter();

	public DiaryFragment() { super(R.layout.fragment_diary); }

	public static DiaryFragment newInstance() {
		return new DiaryFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		toolbar = view.findViewById(R.id.fragment_diary_toolbar);
		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		RecyclerView recyclerView = view.findViewById(R.id.fragment_diary_recycler_view);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		viewModel.exposures.observe(getViewLifecycleOwner(), exposures -> {
			ArrayList<VenueVisitRecyclerItem> items = new ArrayList<>();

			List<DiaryEntry> diaryEntries = DiaryStorage.getInstance(getContext()).getEntries();
			Collections.sort(diaryEntries, (d1, d2) -> Long.compare(d2.getArrivalTime(), d1.getArrivalTime()));

			String daysAgoString = "";
			for (DiaryEntry diaryEntry : diaryEntries) {
				String newDaysAgoString = StringUtils.getDaysAgoString(diaryEntry.getArrivalTime(), getContext());
				if (!newDaysAgoString.equals(daysAgoString)) {
					daysAgoString = newDaysAgoString;
					items.add(new ItemVenueVisitDayHeader(daysAgoString));
				}
				items.add(new ItemVenueVisit(getExposureWithId(exposures, diaryEntry.getId()), diaryEntry,
						v -> onDiaryEntryClicked(diaryEntry, getExposureWithId(exposures, diaryEntry.getId()))));
			}

			recyclerAdapter.setData(items);
		});
	}

	private ExposureEvent getExposureWithId(List<ExposureEvent> exposures, long id) {
		for (ExposureEvent exposure : exposures) {
			if (exposure.getId() == id) {
				return exposure;
			}
		}
		return null;
	}

	private void onDiaryEntryClicked(DiaryEntry diaryEntry, ExposureEvent exposureEvent) {
		if (exposureEvent != null) {
			requireActivity().getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
							R.anim.modal_pop_exit)
					.replace(R.id.container, ExposureFragment.newInstance(exposureEvent.getId()))
					.addToBackStack(ExposureFragment.TAG)
					.commit();
		} else {
			requireActivity().getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
							R.anim.modal_pop_exit)
					.replace(R.id.container, EditDiaryEntryFragment.newInstance(true, diaryEntry.getId()))
					.addToBackStack(EditDiaryEntryFragment.TAG)
					.commit();
		}
	}

}
