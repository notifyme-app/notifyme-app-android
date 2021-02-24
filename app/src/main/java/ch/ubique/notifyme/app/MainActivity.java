package ch.ubique.notifyme.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;


import java.util.List;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.ExposureEvent;
import org.crowdnotifier.android.sdk.model.VenueInfo;
import org.crowdnotifier.android.sdk.utils.QrUtils;

import ch.ubique.notifyme.app.checkin.CheckInDialogFragment;
import ch.ubique.notifyme.app.checkin.CheckedInFragment;
import ch.ubique.notifyme.app.checkout.CheckOutFragment;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.network.KeyLoadWorker;
import ch.ubique.notifyme.app.onboarding.OnboardingIntroductionFragment;
import ch.ubique.notifyme.app.reports.ExposureFragment;
import ch.ubique.notifyme.app.utils.ErrorDialog;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.Storage;

import static ch.ubique.notifyme.app.utils.NotificationHelper.*;
import static ch.ubique.notifyme.app.utils.ReminderHelper.AUTO_CHECKOUT_BROADCAST;

public class MainActivity extends AppCompatActivity {

	private MainViewModel viewModel;
	private Storage storage;
	private static final String KEY_IS_INTENT_CONSUMED = "KEY_IS_INTENT_CONSUMED";
	private boolean isIntentConsumed = false;

	private BroadcastReceiver autoCheckoutBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showMainFragment();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
		setContentView(R.layout.activity_main);

		storage = Storage.getInstance(this);
		if (storage.getDidAutoCheckout()) {
			showMainFragment();
			storage.setDidAutoCheckout(false);
		}

		boolean onboardingCompleted = storage.getOnboardingCompleted();

		if (savedInstanceState == null) {
			if (onboardingCompleted) {
				showMainFragment();
			} else {
				showOnboarding();
			}
		} else {
			isIntentConsumed = savedInstanceState.getBoolean(KEY_IS_INTENT_CONSUMED);
		}

		KeyLoadWorker.startKeyLoadWorker(this);
		KeyLoadWorker.cleanUpOldData(this);

		viewModel.forceUpdate.observe(this, forceUpdate -> {
			if (forceUpdate) new ErrorDialog(this, ErrorState.FORCE_UPDATE_REQUIRED).show();
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		isIntentConsumed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (storage.getOnboardingCompleted()) checkIntentForActions();
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(autoCheckoutBroadcastReceiver, new IntentFilter(AUTO_CHECKOUT_BROADCAST));
	}

	@Override
	protected void onStart() {
		super.onStart();
		viewModel.refreshTraceKeys();
		viewModel.refreshErrors();
	}

	private void checkIntentForActions() {
		Intent intent = getIntent();
		boolean launchedFromHistory = (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
		if (!launchedFromHistory && !isIntentConsumed) {
			isIntentConsumed = true;
			handleCustomIntents();
		}
	}

	private void handleCustomIntents() {
		String intentAction = getIntent().getAction();
		if ((REMINDER_ACTION.equals(intentAction) || ONGOING_ACTION.equals(intentAction)) && viewModel.isCheckedIn()) {
			showCheckedInScreen();
		} else if (CHECK_OUT_NOW_ACTION.equals(intentAction) && viewModel.isCheckedIn()) {
			showCheckOutScreen();
		} else if (EXPOSURE_NOTIFICATION_ACTION.equals(intentAction)) {
			long id = getIntent().getLongExtra(EXPOSURE_ID_EXTRA, -1);
			ExposureEvent exposureEvent = getExposureWithId(id);
			if (exposureEvent != null) {
				showExposureScreen(exposureEvent);
			}
		}

		if (getIntent().getData() != null) {
			checkValidCheckInIntent(getIntent().getData().toString());
		}
	}

	private ExposureEvent getExposureWithId(long id) {
		List<ExposureEvent> exposureEvents = CrowdNotifier.getExposureEvents(this);
		for (ExposureEvent exposureEvent : exposureEvents) {
			if (exposureEvent.getId() == id) {
				return exposureEvent;
			}
		}
		return null;
	}

	private void showMainFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, MainFragment.newInstance())
				.commitNow();
	}

	private void showCheckedInScreen() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, CheckedInFragment.newInstance())
				.addToBackStack(CheckedInFragment.TAG)
				.commit();
	}

	private void showCheckOutScreen() {
		showCheckedInScreen();
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				.replace(R.id.container, CheckOutFragment.newInstance())
				.addToBackStack(CheckOutFragment.TAG)
				.commit();
	}

	private void showExposureScreen(ExposureEvent exposureEvent) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, ExposureFragment.newInstance(exposureEvent.getId()))
				.addToBackStack(ExposureFragment.TAG)
				.commit();
	}


	private void checkValidCheckInIntent(String qrCodeData) {
		try {
			VenueInfo venueInfo = CrowdNotifier.getVenueInfo(qrCodeData, BuildConfig.ENTRY_QR_CODE_PREFIX);
			if (viewModel.isCheckedIn()) {
				new ErrorDialog(this, ErrorState.ALREADY_CHECKED_IN).show();
			} else {
				viewModel.setCheckInState(new CheckInState(false, venueInfo, System.currentTimeMillis(),
						System.currentTimeMillis(), ReminderOption.OFF));
				getSupportFragmentManager().beginTransaction()
						.add(CheckInDialogFragment.newInstance(true), CheckInDialogFragment.TAG)
						.commit();
			}
		} catch (QrUtils.QRException e) {
			handleInvalidQRCodeExceptions(qrCodeData, e);
		}
	}

	private void handleInvalidQRCodeExceptions(String qrCodeData, QrUtils.QRException e) {
		if (e instanceof QrUtils.InvalidQRCodeVersionException) {
			new ErrorDialog(this, ErrorState.UPDATE_REQUIRED).show();
		} else if (e instanceof QrUtils.NotYetValidException) {
			new ErrorDialog(this, ErrorState.QR_CODE_NOT_YET_VALID).show();
		} else if (e instanceof QrUtils.NotValidAnymoreException) {
			new ErrorDialog(this, ErrorState.QR_CODE_NOT_VALID_ANYMORE).show();
		} else {
			if (qrCodeData.startsWith(BuildConfig.TRACE_QR_CODE_PREFIX)) {
				Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCodeData));
				startActivity(openBrowserIntent);
			} else {
				new ErrorDialog(this, ErrorState.NO_VALID_QR_CODE).show();
			}
		}
	}

	private void showOnboarding() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, OnboardingIntroductionFragment.newInstance())
				.commit();
	}


	@Override
	public void onBackPressed() {
		Fragment currentFragment =
				getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);
		if (currentFragment instanceof BackPressListener) {
			boolean interceptedByFragment = ((BackPressListener) currentFragment).onBackPressed();
			if (interceptedByFragment) return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_IS_INTENT_CONSUMED, isIntentConsumed);
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(autoCheckoutBroadcastReceiver);
		super.onPause();
	}

	public interface BackPressListener {
		boolean onBackPressed();

	}

}