package ch.ubique.n2step.app;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import ch.ubique.n2step.app.model.Report;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class MainViewModel extends ViewModel {

	public MutableLiveData<ArrayList<Report>> reports = new MutableLiveData(getReports());

	public VenueInfo currentVenue = null;
	public boolean isQrScanningEnabled = true;

	public void setCurrentVenue(VenueInfo venue) {
		currentVenue = venue;
	}

	private ArrayList<Report> getReports() {
		ArrayList<Report> reports = new ArrayList<>();
		//reports.add(new Report());
		return reports;
	}

}