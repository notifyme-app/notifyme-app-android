package ch.ubique.notifyme.app.reports.items;

import android.view.View;

public class ItemReportsHeader extends VenueVisitRecyclerItem {

	private View.OnClickListener clickListener;

	public ItemReportsHeader(View.OnClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public View.OnClickListener getClickListener() {
		return clickListener;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.REPORTS_HEADER;
	}

}
