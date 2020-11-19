package ch.ubique.notifyme.app.reports;

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
import java.util.List;

import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.reports.items.*;
import ch.ubique.notifyme.app.utils.DiaryStorage;
import ch.ubique.notifyme.app.utils.ErrorHelper;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.StringUtils;

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
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		toolbar = view.findViewById(R.id.fragment_reports_toolbar);
		swipeRefreshLayout = view.findViewById(R.id.fragment_reports_swipe_refresh_layout);

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		RecyclerView recyclerView = view.findViewById(R.id.fragment_reports_recycler_view);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		diaryStorage = DiaryStorage.getInstance(getContext());
		viewModel.exposures
				.observe(getViewLifecycleOwner(), exposures -> publishRecyclerItems(exposures, viewModel.errorState.getValue()));
		viewModel.errorState
				.observe(getViewLifecycleOwner(), errorState -> publishRecyclerItems(viewModel.exposures.getValue(), errorState));

		swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshTraceKeys());

		viewModel.traceKeyLoadingState.observe(getViewLifecycleOwner(), loadingState ->
				swipeRefreshLayout.setRefreshing(loadingState == MainViewModel.LoadingState.LOADING));
	}

	private void publishRecyclerItems(List<ExposureEvent> exposures, ErrorState errorState) {
		ArrayList<VenueVisitRecyclerItem> items = new ArrayList<>();

		if (errorState != null) {
			if (errorState == ErrorState.NETWORK) {
				items.add(new ItemError(errorState, () -> viewModel.refreshTraceKeys()));
			} else {
				items.add(new ItemError(errorState, null));
			}
		}

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

		if (exposures != null) {
			String daysAgoString = "";
			for (ExposureEvent exposureEvent : exposures) {
				String newDaysAgoString = StringUtils.getDaysAgoString(exposureEvent.getStartTime(), getContext());
				if (!newDaysAgoString.equals(daysAgoString)) {
					daysAgoString = newDaysAgoString;
					items.add(new ItemVenueVisitDayHeader(daysAgoString));
				}
				items.add(new ItemVenueVisit(exposureEvent, diaryStorage.getDiaryEntryWithId(exposureEvent.getId()),
						v -> showExposureScreen(exposureEvent)));
			}
		}

		recyclerAdapter.setData(items);
	}

	private void showExposureScreen(ExposureEvent exposureEvent) {
		viewModel.setSelectedExposure(exposureEvent);
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				.replace(R.id.container, ExposureFragment.newInstance())
				.addToBackStack(ExposureFragment.TAG)
				.commit();
	}

}
