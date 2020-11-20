package ch.ubique.notifyme.app.diary;

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

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisit;
import ch.ubique.notifyme.app.reports.items.ItemVenueVisitDayHeader;
import ch.ubique.notifyme.app.reports.items.VenueVisitRecyclerItem;
import ch.ubique.notifyme.app.utils.StringUtils;
import ch.ubique.notifyme.app.utils.VenueTypeIconHelper;

public class DiaryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	List<VenueVisitRecyclerItem> items = new ArrayList<>();

	@Override
	public int getItemViewType(int position) { return items.get(position).getViewType().getId(); }

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		VenueVisitRecyclerItem.ViewType type = VenueVisitRecyclerItem.ViewType.values()[viewType];

		switch (type) {
			case REPORTS_DAY_HEADER:
				return new DayHeaderViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_visits_day_header, parent, false));
			case REPORT:
				return new VenueVisitViewHolder(
						LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_visit, parent, false));
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		VenueVisitRecyclerItem item = items.get(position);

		switch (item.getViewType()) {
			case REPORTS_DAY_HEADER:
				((DayHeaderViewHolder) holder).bind((ItemVenueVisitDayHeader) item);
				break;
			case REPORT:
				((VenueVisitViewHolder) holder).bind((ItemVenueVisit) item);
				break;
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setData(List<VenueVisitRecyclerItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
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


	public class VenueVisitViewHolder extends RecyclerView.ViewHolder {

		private TextView timeTextView;
		private TextView nameTextView;
		private TextView locationTextView;
		private ImageView statusIcon;
		private ImageView venueTypeIcon;
		private View infoBox;
		private TextView infoBoxText;

		public VenueVisitViewHolder(View itemView) {
			super(itemView);
			this.timeTextView = itemView.findViewById(R.id.item_diary_entry_time);
			this.nameTextView = itemView.findViewById(R.id.item_diary_entry_name);
			this.locationTextView = itemView.findViewById(R.id.item_diary_entry_location);
			this.statusIcon = itemView.findViewById(R.id.item_diary_entry_status_icon);
			this.venueTypeIcon = itemView.findViewById(R.id.item_diary_entry_icon);
			this.infoBox = itemView.findViewById(R.id.item_diary_entry_infobox);
			this.infoBoxText = itemView.findViewById(R.id.item_diary_entry_infobox_text);
		}

		public void bind(ItemVenueVisit item) {

			VenueInfo venueInfo = item.getDiaryEntry().getVenueInfo();
			nameTextView.setText(venueInfo.getTitle());
			locationTextView.setText(venueInfo.getSubtitle());
			String start = StringUtils.getHourMinuteTimeString(item.getDiaryEntry().getArrivalTime(), ":");
			String end = StringUtils.getHourMinuteTimeString(item.getDiaryEntry().getDepartureTime(), ":");
			timeTextView.setText(start + " — " + end);
			if (item.getExposure() == null) {
				infoBox.setVisibility(View.GONE);
				statusIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_check_filled));
			} else {
				if (item.getExposure().getMessage() != null && !item.getExposure().getMessage().isEmpty()) {
					infoBox.setVisibility(View.VISIBLE);
					infoBoxText.setText(item.getExposure().getMessage());
				} else {
					infoBox.setVisibility(View.GONE);
				}
				statusIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_info));
			}
			venueTypeIcon.setImageResource(VenueTypeIconHelper.getDrawableForVenueType(venueInfo.getVenueType()));
			itemView.setOnClickListener(item.getOnClickListener());
		}

	}

}
