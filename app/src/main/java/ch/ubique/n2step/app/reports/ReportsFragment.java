package ch.ubique.n2step.app.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.diary.DiaryFragment;
import ch.ubique.n2step.app.reports.items.ItemNoReportsHeader;
import ch.ubique.n2step.app.reports.items.ItemVenueVisit;
import ch.ubique.n2step.app.reports.items.ItemVenueVisitDayHeader;
import ch.ubique.n2step.app.reports.items.ItemReportsHeader;
import ch.ubique.n2step.app.reports.items.VenueVisitRecyclerItem;
import ch.ubique.n2step.app.utils.DiaryStorage;
import ch.ubique.n2step.app.utils.StringUtils;
import ch.ubique.n2step.sdk.model.Exposure;

public class ReportsFragment extends Fragment {

	public final static String TAG = ReportsFragment.class.getCanonicalName();

	private MainViewModel viewModel;
	private ReportsRecyclerAdapter recyclerAdapter = new ReportsRecyclerAdapter();
	private Toolbar toolbar;
	private SwipeRefreshLayout swipeRefreshLayout;
	private DiaryStorage diaryStorage;

	public ReportsFragment() { super(R.layout.fragment_reports); }

	public static ReportsFragment newInstance() {
		return new ReportsFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		toolbar = (Toolbar) view.findViewById(R.id.fragment_reports_toolbar);
		swipeRefreshLayout = view.findViewById(R.id.fragment_reports_swipe_refresh_layout);

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		RecyclerView recyclerView = view.findViewById(R.id.fragment_reports_recycler_view);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		diaryStorage = DiaryStorage.getInstance(getContext());
		viewModel.exposures.observe(getViewLifecycleOwner(), exposures -> {
			ArrayList<VenueVisitRecyclerItem> items = new ArrayList<>();

			if (exposures == null || exposures.isEmpty()) {
				toolbar.setTitle(R.string.no_report_title);
				items.add(new ItemNoReportsHeader());
			} else if (exposures.size() == 1) {
				toolbar.setTitle(R.string.report_title_singular);
				items.add(new ItemReportsHeader(v -> Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show()));
			} else {
				toolbar.setTitle(getString(R.string.report_title_plural).replace("{NUMBER}", String.valueOf(exposures.size())));
				items.add(new ItemReportsHeader(v -> Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show()));
			}

			String daysAgoString = "";
			for (Exposure exposure : exposures) {
				String newDaysAgoString = StringUtils.getDaysAgoString(exposure.getStartTime(), getContext());
				if (!newDaysAgoString.equals(daysAgoString)) {
					daysAgoString = newDaysAgoString;
					items.add(new ItemVenueVisitDayHeader(daysAgoString));
				}
				items.add(new ItemVenueVisit(exposure, diaryStorage.getDiaryEntryWithId(exposure.getId()), null));
			}

			recyclerAdapter.setData(items);
		});

		swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshTraceKeys());

		viewModel.traceKeyLoadingState.observe(getViewLifecycleOwner(), loadingState ->
				swipeRefreshLayout.setRefreshing(loadingState == MainViewModel.LoadingState.LOADING));
	}

}
