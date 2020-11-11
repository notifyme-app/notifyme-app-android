package ch.ubique.n2step.app.diary;

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

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.model.DiaryEntry;
import ch.ubique.n2step.app.reports.items.ItemVenueVisit;
import ch.ubique.n2step.app.reports.items.ItemVenueVisitDayHeader;
import ch.ubique.n2step.app.reports.items.VenueVisitRecyclerItem;
import ch.ubique.n2step.app.utils.DiaryStorage;
import ch.ubique.n2step.app.utils.StringUtils;
import ch.ubique.n2step.sdk.model.Exposure;

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
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		toolbar = (Toolbar) view.findViewById(R.id.fragment_diary_toolbar);
		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		RecyclerView recyclerView = view.findViewById(R.id.fragment_diary_recycler_view);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		viewModel.exposures.observe(getViewLifecycleOwner(), exposures -> {
			ArrayList<VenueVisitRecyclerItem> items = new ArrayList<>();

			List<DiaryEntry> diaryEntries = DiaryStorage.getInstance(getContext()).getEntries();
			Collections.sort(diaryEntries, (d1, d2) -> Long.compare(d1.getArrivalTime(), d2.getArrivalTime()));

			String daysAgoString = "";
			for (DiaryEntry diaryEntry : diaryEntries) {
				String newDaysAgoString = StringUtils.getDaysAgoString(diaryEntry.getArrivalTime(), getContext());
				if (!newDaysAgoString.equals(daysAgoString)) {
					daysAgoString = newDaysAgoString;
					items.add(new ItemVenueVisitDayHeader(daysAgoString));
				}
				items.add(new ItemVenueVisit(getExposureWithId(exposures, diaryEntry.getId()), diaryEntry, v -> {
					//TODO: Implement diary entry click
				}));
			}

			recyclerAdapter.setData(items);
		});
	}

	private Exposure getExposureWithId(List<Exposure> exposures, long id) {
		for (Exposure exposure : exposures) {
			if (exposure.getId() == id) {
				return exposure;
			}
		}
		return null;
	}

}
