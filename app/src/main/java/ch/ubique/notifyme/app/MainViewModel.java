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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Collections;
import java.util.List;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.network.ConfigServiceController;
import ch.ubique.notifyme.app.network.TraceKeysServiceController;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.Storage;

import static ch.ubique.notifyme.app.network.KeyLoadWorker.ACTION_NEW_EXPOSURE_NOTIFICATION;
import static ch.ubique.notifyme.app.utils.ReminderHelper.ACTION_DID_AUTO_CHECKOUT;

public class MainViewModel extends AndroidViewModel {


	public MutableLiveData<List<ExposureEvent>> exposures = new MutableLiveData<>();
	public MutableLiveData<Long> timeSinceCheckIn = new MutableLiveData<>(0L);
	public MutableLiveData<LoadingState> traceKeyLoadingState = new MutableLiveData<>(LoadingState.SUCCESS);
	public MutableLiveData<ErrorState> errorState = new MutableLiveData<>(null);
	public MutableLiveData<Boolean> forceUpdate = new MutableLiveData<>(false);
	private MutableLiveData<Boolean> isCheckedIn = new MutableLiveData<>(false);
	private CheckInState checkInState;

	private Storage storage;
	private final Handler handler = new Handler(Looper.getMainLooper());
	private Runnable timeUpdateRunnable;
	private final long CHECK_IN_TIME_UPDATE_INTERVAL = 1000;
	private TraceKeysServiceController traceKeysServiceController = new TraceKeysServiceController(getApplication());
	private ConfigServiceController configServiceController = new ConfigServiceController(getApplication());

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_DID_AUTO_CHECKOUT.equals(intent.getAction())) {
				setCheckInState(null);
			} else if (ACTION_NEW_EXPOSURE_NOTIFICATION.equals(intent.getAction())) {
				refreshExposures();
			}
		}
	};

	public MainViewModel(@NonNull Application application) {
		super(application);
		refreshExposures();
		storage = Storage.getInstance(getApplication());
		checkInState = storage.getCheckInState();
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(application);
		localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_DID_AUTO_CHECKOUT));
		localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_NEW_EXPOSURE_NOTIFICATION));
		traceKeyLoadingState.observeForever(loadingState -> { if (loadingState != LoadingState.LOADING) refreshErrors(); });
		reloadConfig();
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
		storage.setCheckInState(checkInState);
		this.checkInState = checkInState;
		if (checkInState == null) {
			this.isCheckedIn.setValue(false);
		} else {
			this.isCheckedIn.setValue(checkInState.isCheckedIn());
		}
	}

	public CheckInState getCheckInState() {
		return checkInState;
	}

	public void setCheckedIn(boolean checkedIn) {
		if (checkInState != null) checkInState.setCheckedIn(checkedIn);
		setCheckInState(checkInState);
	}

	public LiveData<Boolean> isCheckedIn() {
		return isCheckedIn;
	}

	public void refreshTraceKeys() {
		traceKeyLoadingState.setValue(LoadingState.LOADING);
		traceKeysServiceController.loadTraceKeysAsync(traceKeys -> {
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

	public void removeExposure(long exposureId) {
		CrowdNotifier.removeExposure(getApplication(), exposureId);
		refreshExposures();
	}

	public ExposureEvent getExposureWithId(long id) {
		List<ExposureEvent> exposureEvents = exposures.getValue();
		if (exposureEvents == null) return null;
		for (ExposureEvent exposureEvent : exposureEvents) {
			if (exposureEvent.getId() == id) {
				return exposureEvent;
			}
		}
		return null;
	}

	public ReminderOption getSelectedReminderOption() {
		return checkInState.getSelectedTimerOption();
	}

	public void setSelectedReminderOption(ReminderOption selectedReminderOption) {
		this.checkInState.setSelectedTimerOption(selectedReminderOption);
		storage.setCheckInState(checkInState);
	}

	public void reloadConfig() {
		configServiceController.loadConfigAsync(configResponseModel -> {
			if (configResponseModel != null) {
				this.forceUpdate.setValue(configResponseModel.isForceUpdate());
			}
		});
	}

	@Override
	public void onCleared() {
		super.onCleared();
		LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(broadcastReceiver);
	}

	public enum LoadingState {
		LOADING, SUCCESS, FAILURE
	}

}