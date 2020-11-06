package ch.ubique.n2step.app.reports.items;

import ch.ubique.n2step.sdk.model.Exposure;

public class ItemReportsDayHeader extends ReportsRecyclerItem {

	private String dayLabel;

	public ItemReportsDayHeader(String dayLabel) {
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
