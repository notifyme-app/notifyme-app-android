package ch.ubique.notifyme.app.reports.items;

import android.view.View;

import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.model.DiaryEntry;

public class ItemVenueVisit extends VenueVisitRecyclerItem {

	private ExposureEvent exposure;
	private DiaryEntry diaryEntry;
	private View.OnClickListener onClickListener;

	public ItemVenueVisit(ExposureEvent exposure, DiaryEntry diaryEntry, View.OnClickListener onClickListener) {
		this.exposure = exposure;
		this.diaryEntry = diaryEntry;
		this.onClickListener = onClickListener;
	}

	public ExposureEvent getExposure() {
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
