package ch.ubique.n2step.app.reports.items;

import ch.ubique.n2step.sdk.model.Exposure;

public class ItemReport extends ReportsRecyclerItem {

	private Exposure exposure;

	public ItemReport(Exposure exposure) {
		this.exposure = exposure;
	}

	public Exposure getExposure() {
		return exposure;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.REPORT;
	}

}
