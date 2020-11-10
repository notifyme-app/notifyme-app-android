package ch.ubique.n2step.app.reports.items;

import android.view.View;

import ch.ubique.n2step.app.model.DiaryEntry;
import ch.ubique.n2step.sdk.model.Exposure;

public class ItemVenueVisit extends VenueVisitRecyclerItem {

	private Exposure exposure;
	private DiaryEntry diaryEntry;
	private View.OnClickListener onClickListener;

	public ItemVenueVisit(Exposure exposure, DiaryEntry diaryEntry, View.OnClickListener onClickListener) {
		this.exposure = exposure;
		this.diaryEntry = diaryEntry;
		this.onClickListener = onClickListener;
	}

	public Exposure getExposure() {
		return exposure;
	}

	public DiaryEntry getDiaryEntry() {
		return diaryEntry;
	}

	public View.OnClickListener getOnClickListener() {
		return onClickListener;
	}

	@Override
	public ViewType getViewType() {
		return ViewType.REPORT;
	}

}
