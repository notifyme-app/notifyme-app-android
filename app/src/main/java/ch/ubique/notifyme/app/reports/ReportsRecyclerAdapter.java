package ch.ubique.notifyme.app.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import org.crowdnotifier.android.sdk.model.Qr;
import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.reports.items.*;
import ch.ubique.notifyme.base.utils.ErrorHelper;
import ch.ubique.notifyme.base.utils.StringUtils;
import ch.ubique.notifyme.base.utils.VenueTypeIconHelper;

public class ReportsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	List<VenueVisitRecyclerItem> items = new ArrayList<>();

	@Override
	public int getItemViewType(int position) { return items.get(position).getViewType().getId(); }

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		VenueVisitRecyclerItem.ViewType type = VenueVisitRecyclerItem.ViewType.values()[viewType];

		switch (type) {
			case NO_REPORTS_HEADER:
				return new NoReportsHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_reports_header, parent, false));
			case REPORTS_HEADER:
				return new ReportsHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports_header, parent, false));
			case REPORTS_DAY_HEADER:
				return new DayHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_visits_day_header, parent, false));
			case REPORT:
				return new VenueVisitViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_visit, parent, false));
			case ERROR:
				return new ErrorViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(ch.ubique.notifyme.base.R.layout.item_error_status, parent, false));
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		VenueVisitRecyclerItem item = items.get(position);

		switch (item.getViewType()) {
			case NO_REPORTS_HEADER:
				((NoReportsHeaderViewHolder) holder).bind((ItemNoReportsHeader) item);
				break;
			case REPORTS_HEADER:
				((ReportsHeaderViewHolder) holder).bind((ItemReportsHeader) item);
				break;
			case REPORTS_DAY_HEADER:
				((DayHeaderViewHolder) holder).bind((ItemVenueVisitDayHeader) item);
				break;
			case REPORT:
				((VenueVisitViewHolder) holder).bind((ItemVenueVisit) item);
				break;
			case ERROR:
				((ErrorViewHolder) holder).bind((ItemError) item);
				break;
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setData(List<VenueVisitRecyclerItem> items) {
		//TODO: Add DiffUtil
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}


	public class NoReportsHeaderViewHolder extends RecyclerView.ViewHolder {

		public NoReportsHeaderViewHolder(View itemView) {super(itemView);}

		public void bind(ItemNoReportsHeader item) { }

	}


	public class DayHeaderViewHolder extends RecyclerView.ViewHolder {

		private TextView dayLabel;

		public DayHeaderViewHolder(View itemView) {
			super(itemView);
			dayLabel = itemView.findViewById(R.id.item_reports_day_header_text_view);
		}

		public void bind(ItemVenueVisitDayHeader item) {
			dayLabel.setText(item.getDayLabel());
		}

	}


	public class ReportsHeaderViewHolder extends RecyclerView.ViewHolder {

		public ReportsHeaderViewHolder(View itemView) {super(itemView);}

		public void bind(ItemReportsHeader item) { }

	}


	public class ErrorViewHolder extends RecyclerView.ViewHolder {

		public ErrorViewHolder(View itemView) {super(itemView);}

		public void bind(ItemError item) {
			ErrorHelper.updateErrorView(itemView, item.getErrorState(), item.getCustomButtonAction(), itemView.getContext());
		}

	}


	public class VenueVisitViewHolder extends RecyclerView.ViewHolder {

		private TextView timeTextView;
		private TextView nameTextView;
		private TextView locationTextView;
		private ImageView statusIcon;
		private ImageView venueTypeIcon;
		private View infoBox;
		private TextView infoBoxText;
		private ImageView hiddenEventPlaceholder;

		public VenueVisitViewHolder(View itemView) {
			super(itemView);
			this.timeTextView = itemView.findViewById(R.id.item_diary_entry_time);
			this.nameTextView = itemView.findViewById(R.id.item_diary_entry_name);
			this.locationTextView = itemView.findViewById(R.id.item_diary_entry_location);
			this.statusIcon = itemView.findViewById(R.id.item_diary_entry_status_icon);
			this.venueTypeIcon = itemView.findViewById(R.id.item_diary_entry_icon);
			this.infoBox = itemView.findViewById(R.id.item_diary_entry_infobox);
			this.infoBoxText = itemView.findViewById(R.id.item_diary_entry_infobox_text);
			this.hiddenEventPlaceholder = itemView.findViewById(R.id.item_diary_hidden_event_placeholder);
		}

		public void bind(ItemVenueVisit item) {
			if (item.getDiaryEntry() != null) {
				VenueInfo venueInfo = item.getDiaryEntry().getVenueInfo();
				nameTextView.setText(venueInfo.getTitle());
				locationTextView.setText(venueInfo.getSubtitle());
				venueTypeIcon.setVisibility(View.VISIBLE);
				venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));
				hiddenEventPlaceholder.setVisibility(View.GONE);
			} else {
				nameTextView.setText("");
				locationTextView.setText("");
				venueTypeIcon.setVisibility(View.INVISIBLE);
				hiddenEventPlaceholder.setVisibility(View.VISIBLE);
			}
			statusIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), ch.ubique.notifyme.base.R.drawable.ic_info));
			String start = StringUtils.getHourMinuteTimeString(item.getExposure().getStartTime(), ":");
			String end = StringUtils.getHourMinuteTimeString(item.getExposure().getEndTime(), ":");
			timeTextView.setText(start + " — " + end);
			itemView.setOnClickListener(item.getOnClickListener());

			if (item.getExposure().getMessage() != null && !item.getExposure().getMessage().isEmpty()) {
				infoBox.setVisibility(View.VISIBLE);
				infoBoxText.setText(item.getExposure().getMessage());
			} else {
				infoBox.setVisibility(View.GONE);
			}
			statusIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), ch.ubique.notifyme.base.R.drawable.ic_info));
		}

	}

}
