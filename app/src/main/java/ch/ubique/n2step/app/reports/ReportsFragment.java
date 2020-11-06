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

import java.util.ArrayList;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.reports.items.ItemNoReportsHeader;
import ch.ubique.n2step.app.reports.items.ItemReportsHeader;
import ch.ubique.n2step.app.reports.items.ReportsRecyclerItem;

public class ReportsFragment extends Fragment {

	private MainViewModel viewModel;
	private ReportsRecyclerAdapter recyclerAdapter = new ReportsRecyclerAdapter();
	private Toolbar toolbar;

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
		toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		RecyclerView recyclerView = view.findViewById(R.id.reports_recycler_view);
		recyclerView.setAdapter(recyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		viewModel.exposures.observe(getViewLifecycleOwner(), reports -> {
			ArrayList<ReportsRecyclerItem> items = new ArrayList<>();

			if (reports == null || reports.isEmpty()) {
				toolbar.setTitle(R.string.no_report_title);
				items.add(new ItemNoReportsHeader());
			} else if (reports.size() == 1) {
				toolbar.setTitle(R.string.report_title_singular);
				items.add(new ItemReportsHeader(v -> Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show()));
			} else {
				toolbar.setTitle(getString(R.string.report_title_plural).replace("{NUMBER}", String.valueOf(reports.size())));
				items.add(new ItemReportsHeader(v -> Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show()));
			}

			//TODO: Add reports here

			recyclerAdapter.setData(items);
		});
	}

}
