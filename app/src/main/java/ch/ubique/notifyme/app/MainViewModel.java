package ch.ubique.notifyme.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
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
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.Storage;

import static ch.ubique.notifyme.app.network.KeyLoadWorker.NEW_NOTIFICATION;

public class MainViewModel extends AndroidViewModel {


	public MutableLiveData<List<ExposureEvent>> exposures = new MutableLiveData<>();
	public MutableLiveData<Long> timeSinceCheckIn = new MutableLiveData<>(0L);
	public MutableLiveData<LoadingState> traceKeyLoadingState = new MutableLiveData<>(LoadingState.SUCCESS);
	public MutableLiveData<ErrorState> errorState = new MutableLiveData<>(null);
	private CheckInState checkInState;
	private boolean isQrScanningEnabled = true;
	private ExposureEvent selectedExposure = null;

	private Storage storage;
	private final Handler handler = new Handler(Looper.getMainLooper());
	private Runnable timeUpdateRunnable;
	private final long CHECK_IN_TIME_UPDATE_INTERVAL = 60000;
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
		traceKeyLoadingState.observeForever(loadingState -> { if (loadingState != LoadingState.LOADING) refreshErrors(); });
	}


	public void startCheckInTimer() {
		handler.removeCallbacks(timeUpdateRunnable);
		timeUpdateRunnable = () -> {
			if (checkInState != null) {
				timeSinceCheckIn.setValue(System.currentTimeMillis() - checkInState.getCheckInTime());
			} else {
				timeSinceCheckIn.setValue(0L);
			}
			handler.postDelayed(timeUpdateRunnable, CHECK_IN_TIME_UPDATE_INTERVAL);
		};
		handler.postDelayed(timeUpdateRunnable, CHECK_IN_TIME_UPDATE_INTERVAL -
				(System.currentTimeMillis() - checkInState.getCheckInTime() % CHECK_IN_TIME_UPDATE_INTERVAL));
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

	public void refreshErrors() {
		//TODO: Also check for disabled notification channels
		boolean notificationsEnabled = NotificationManagerCompat.from(getApplication()).areNotificationsEnabled();

		if (traceKeyLoadingState.getValue() == LoadingState.FAILURE) {
			errorState.setValue(ErrorState.NETWORK);
		} else if (!notificationsEnabled) {
			errorState.setValue(ErrorState.NOTIFICATIONS_DISABLED);
		} else {
			errorState.setValue(null);
		}
	}

	private void refreshExposures() {
		List<ExposureEvent> newExposures = CrowdNotifier.getExposureEvents(getApplication());
		Collections.sort(newExposures, (e1, e2) -> Long.compare(e2.getStartTime(), e1.getStartTime()));
		exposures.setValue(newExposures);
	}

	public ReminderOption getSelectedReminderOption() {
		return checkInState.getSelectedTimerOption();
	}

	public void setSelectedReminderOption(ReminderOption selectedReminderOption) {
		this.checkInState.setSelectedTimerOption(selectedReminderOption);
		storage.setCurrentVenue(checkInState);
	}

	public ExposureEvent getSelectedExposure() {
		return selectedExposure;
	}

	public void setSelectedExposure(ExposureEvent selectedExposure) {
		this.selectedExposure = selectedExposure;
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