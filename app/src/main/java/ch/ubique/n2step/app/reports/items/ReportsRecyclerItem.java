package ch.ubique.n2step.app.reports.items;

public abstract class ReportsRecyclerItem {
	public enum ViewType {
		NO_REPORTS_HEADER(0), REPORTS_HEADER(1), REPORT(2), REPORTS_DAY_HEADER(3);
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
