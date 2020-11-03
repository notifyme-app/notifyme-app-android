package ch.ubique.n2step.app;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import ch.ubique.n2step.app.model.Report;

public class MainViewModel extends ViewModel {

	public MutableLiveData<ArrayList<Report>> reports = new MutableLiveData(getReports());

	private ArrayList<Report> getReports() {
		ArrayList<Report> reports = new ArrayList<>();
		//reports.add(new Report());
		return reports;
	}

}