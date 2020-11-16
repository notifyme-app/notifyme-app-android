package ch.ubique.notifyme.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Collections;
import java.util.List;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.network.WebServiceController;
import ch.ubique.notifyme.app.utils.Storage;

import static ch.ubique.notifyme.app.network.KeyLoadWorker.NEW_NOTIFICATION;

public class MainViewModel extends AndroidViewModel {


	public MutableLiveData<List<ExposureEvent>> exposures = new MutableLiveData<>();
	public MutableLiveData<Long> timeSinceCheckIn = new MutableLiveData<>(0L);
	public MutableLiveData<LoadingState> traceKeyLoadingState = new MutableLiveData<>(LoadingState.SUCCESS);
	private CheckInState checkInState;
	private boolean isQrScanningEnabled = true;


	private Storage storage;
	private final Handler handler = new Handler(Looper.getMainLooper());
	private Runnable timeUpdateRunnable;
	private final long UPDATE_INTERVAL = 60000;
	private WebServiceController webServiceController = new WebServiceController(getApplication());


	private BroadcastReceiver newNotificationBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshExposures();
		}
	};


	public MainViewModel(@NonNull Application application) {
		super(application);
		refreshExposures();
		storage = Storage.getInstance(getApplication());
		checkInState = storage.getCurrentVenue();
		LocalBroadcastManager.getInstance(application).registerReceiver(newNotificationBroadcastReceiver,
				new IntentFilter(NEW_NOTIFICATION));
	}


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
		storage.setCurrentVenue(checkInState);
		this.checkInState = checkInState;
	}

	public CheckInState getCheckInState() {
		return checkInState;
	}

	public void setCheckedIn(boolean checkedIn) {
		if (checkInState != null) checkInState.setCheckedIn(checkedIn);
		storage.setCurrentVenue(checkInState);
	}

	public boolean isCheckedIn() {
		if (checkInState == null) {
			return false;
		} else {
			return checkInState.isCheckedIn();
		}
	}

	public boolean isQrScanningEnabled() {
		return isQrScanningEnabled;
	}

	public void setQrScanningEnabled(boolean qrScanningEnabled) {
		isQrScanningEnabled = qrScanningEnabled;
	}

	public void refreshTraceKeys() {
		traceKeyLoadingState.setValue(LoadingState.LOADING);
		webServiceController.loadTraceKeysAsync(traceKeys -> {
			if (traceKeys == null) {
				traceKeyLoadingState.setValue(LoadingState.FAILURE);
			} else {
				CrowdNotifier.checkForMatches(traceKeys, getApplication());
				refreshExposures();
				traceKeyLoadingState.setValue(LoadingState.SUCCESS);
			}
		});
	}

	private void refreshExposures() {
		List<ExposureEvent> exposuresUnsorted = CrowdNotifier.getExposureEvents(getApplication());
		Collections.sort(exposuresUnsorted, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()));
		exposures.setValue(exposuresUnsorted);
	}

	public ReminderOption getSelectedReminderOption() {
		return checkInState.getSelectedTimerOption();
	}

	public void setSelectedReminderOption(ReminderOption selectedReminderOption) {
		this.checkInState.setSelectedTimerOption(selectedReminderOption);
		storage.setCurrentVenue(checkInState);
	}

	@Override
	public void onCleared() {
		super.onCleared();
		LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(newNotificationBroadcastReceiver);
	}

	public enum LoadingState {
		LOADING, SUCCESS, FAILURE
	}

}