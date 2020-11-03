package ch.ubique.n2step.app.reports.items;

import android.view.View;

public class ItemReportsHeader extends ReportsRecyclerItem {

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
