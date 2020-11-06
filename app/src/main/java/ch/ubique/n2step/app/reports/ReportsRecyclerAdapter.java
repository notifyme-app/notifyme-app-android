package ch.ubique.n2step.app.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.reports.items.ItemNoReportsHeader;
import ch.ubique.n2step.app.reports.items.ItemReport;
import ch.ubique.n2step.app.reports.items.ItemReportsDayHeader;
import ch.ubique.n2step.app.reports.items.ItemReportsHeader;
import ch.ubique.n2step.app.reports.items.ReportsRecyclerItem;
import ch.ubique.n2step.app.utils.StringUtils;

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
				return new NoReportsHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_reports_header, parent, false));
			case REPORTS_HEADER:
				return new ReportsHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports_header, parent, false));
			case REPORTS_DAY_HEADER:
				return new ReportsDayHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports_day_header, parent, false));
			case REPORT:
				return new ReportViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false));
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		ReportsRecyclerItem item = items.get(position);

		switch (item.getViewType()) {
			case NO_REPORTS_HEADER:
				((NoReportsHeaderViewHolder) holder).bind((ItemNoReportsHeader) item);
				break;
			case REPORTS_HEADER:
				((ReportsHeaderViewHolder) holder).bind((ItemReportsHeader) item);
				break;
			case REPORTS_DAY_HEADER:
				((ReportsDayHeaderViewHolder) holder).bind((ItemReportsDayHeader) item);
				break;
			case REPORT:
				((ReportViewHolder) holder).bind((ItemReport) item);
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


	public class NoReportsHeaderViewHolder extends RecyclerView.ViewHolder {

		public NoReportsHeaderViewHolder(View itemView) {super(itemView);}

		public void bind(ItemNoReportsHeader item) { }

	}


	public class ReportsDayHeaderViewHolder extends RecyclerView.ViewHolder {

		private TextView dayLabel;

		public ReportsDayHeaderViewHolder(View itemView) {
			super(itemView);
			dayLabel = itemView.findViewById(R.id.item_reports_day_header_text_view);
		}

		public void bind(ItemReportsDayHeader item) {
			dayLabel.setText(item.getDayLabel());
		}

	}


	public class ReportsHeaderViewHolder extends RecyclerView.ViewHolder {

		public ReportsHeaderViewHolder(View itemView) {super(itemView);}

		public void bind(ItemReportsHeader item) {
			itemView.findViewById(R.id.reports_what_to_do_button).setOnClickListener(item.getClickListener());
		}

	}


	public class ReportViewHolder extends RecyclerView.ViewHolder {

		private TextView timeTextView;
		private TextView nameTextView;

		public ReportViewHolder(View itemView) {
			super(itemView);
			this.timeTextView = itemView.findViewById(R.id.item_diary_entry_time);
			this.nameTextView = itemView.findViewById(R.id.item_diary_entry_name);
		}

		public void bind(ItemReport item) {
			nameTextView.setText("Anonymous Entry");
			String start = StringUtils.getHourMinuteTimeString(item.getExposure().getStartTime(), ":");
			String end = StringUtils.getHourMinuteTimeString(item.getExposure().getEndTime(), ":");
			timeTextView.setText(start + " — " + end);
		}

	}

}
