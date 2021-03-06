package ch.ubique.notifyme.app.reports.items;

public abstract class VenueVisitRecyclerItem {
	public enum ViewType {
		NO_REPORTS_HEADER(0), REPORTS_HEADER(1), REPORT(2), REPORTS_DAY_HEADER(3), ERROR(4);
		private int id;

		ViewType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public abstract ViewType getViewType();

}
