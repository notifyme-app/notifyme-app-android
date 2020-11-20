package ch.ubique.notifyme.app.reports.items;

public class ItemVenueVisitDayHeader extends VenueVisitRecyclerItem {

	private String dayLabel;

	public ItemVenueVisitDayHeader(String dayLabel) {
		this.dayLabel = dayLabel;
	}

	public String getDayLabel() {
		return dayLabel;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.REPORTS_DAY_HEADER;
	}

}
