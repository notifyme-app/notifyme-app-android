package ch.ubique.n2step.app;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import ch.ubique.n2step.app.model.CheckInState;
import ch.ubique.n2step.app.model.Report;

public class MainViewModel extends ViewModel {


	public MutableLiveData<ArrayList<Report>> reports = new MutableLiveData<>(getReports());
	public MutableLiveData<Long> timeSinceCheckIn = new MutableLiveData<>(0L);
	public CheckInState checkInState = null;
	public boolean isQrScanningEnabled = true;


	private final Handler handler = new Handler(Looper.getMainLooper());
	private Runnable timeUpdateRunnable;
	private final long UPDATE_INTERVAL = 60000;


	public void startCheckInTimer() {
		handler.removeCallbacks(timeUpdateRunnable);
		timeUpdateRunnable = () -> {
			if (checkInState != null) {
				timeSinceCheckIn.setValue(System.currentTimeMillis() - checkInState.getCheckInTime());
			} else {
				timeSinceCheckIn.setValue(0L);
			}
			handler.postDelayed(timeUpdateRunnable, UPDATE_INTERVAL);
		};
		handler.postDelayed(timeUpdateRunnable,
				UPDATE_INTERVAL - (System.currentTimeMillis() - checkInState.getCheckInTime() % UPDATE_INTERVAL));
		timeSinceCheckIn.setValue(System.currentTimeMillis() - checkInState.getCheckInTime());
	}

	public void setCheckInState(CheckInState checkInState) {
		//TODO: Make sure this is persisted
		this.checkInState = checkInState;
	}

	private ArrayList<Report> getReports() {
		ArrayList<Report> reports = new ArrayList<>();
		//reports.add(new Report());
		return reports;
	}

}