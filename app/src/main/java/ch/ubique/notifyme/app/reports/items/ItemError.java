package ch.ubique.notifyme.app.reports.items;

import ch.ubique.notifyme.app.utils.ErrorState;

public class ItemError extends VenueVisitRecyclerItem {

	private ErrorState errorState;
	private Runnable customButtonAction;

	public ItemError(ErrorState errorState, Runnable customButtonAction) {
		this.errorState = errorState;
		this.customButtonAction = customButtonAction;
	}

	public ErrorState getErrorState() {
		return errorState;
	}

	public Runnable getCustomButtonAction() {
		return customButtonAction;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.ERROR;
	}

}
