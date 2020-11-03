package ch.ubique.n2step.app.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.reports.items.ItemNoReportsHeader;
import ch.ubique.n2step.app.reports.items.ItemReportsHeader;
import ch.ubique.n2step.app.reports.items.ReportsRecyclerItem;

public class ReportsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	List<ReportsRecyclerItem> items = new ArrayList<>();

	@Override
	public int getItemViewType(int position) { return items.get(position).getViewType().getId(); }

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ReportsRecyclerItem.ViewType type = ReportsRecyclerItem.ViewType.values()[viewType];

		switch (type) {
			case NO_REPORTS_HEADER:
				return new NoReportsViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_reports_header, parent, false));
			case REPORTS_HEADER:
				return new ReportsViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports_header, parent, false));
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		ReportsRecyclerItem item = items.get(position);

		switch (item.getViewType()) {
			case NO_REPORTS_HEADER:
				((NoReportsViewHolder) holder).bind((ItemNoReportsHeader) item);
				break;
			case REPORTS_HEADER:
				((ReportsViewHolder) holder).bind((ItemReportsHeader) item);
				break;
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setData(List<ReportsRecyclerItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}


	public class NoReportsViewHolder extends RecyclerView.ViewHolder {

		public NoReportsViewHolder(View itemView) {super(itemView);}

		public void bind(ItemNoReportsHeader item) { }

	}


	public class ReportsViewHolder extends RecyclerView.ViewHolder {

		public ReportsViewHolder(View itemView) {super(itemView);}

		public void bind(ItemReportsHeader item) {
			itemView.findViewById(R.id.reports_what_to_do_button).setOnClickListener(item.getClickListener());
		}

	}

}
