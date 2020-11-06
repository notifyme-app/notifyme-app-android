package ch.ubique.n2step.app.network;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import ch.ubique.n2step.app.utils.NotificationHelper;
import ch.ubique.n2step.sdk.N2STEP;
import ch.ubique.n2step.sdk.model.Exposure;
import ch.ubique.n2step.sdk.model.ProblematicEventInfo;

public class KeyLoadWorker extends Worker {

	public static final String NEW_NOTIFICATION = "NEW_NOTIFICATION";

	public KeyLoadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}

	@NonNull
	@Override
	public Result doWork() {

		List<ProblematicEventInfo> problematicEventInfos = new WebServiceController(getApplicationContext()).loadTraceKeys();
		if (problematicEventInfos == null) return Result.retry();
		List<Exposure> exposures = N2STEP.checkForMatches(problematicEventInfos, getApplicationContext());
		if (!exposures.isEmpty()) {
			new NotificationHelper(getApplicationContext()).showExposureNotification();
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(NEW_NOTIFICATION));
		}

		return Result.success();
	}

}
