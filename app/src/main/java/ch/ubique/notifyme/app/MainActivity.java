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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.android.gms.instantapps.InstantApps;
import com.google.android.gms.instantapps.PackageManagerCompat;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.ExposureEvent;
import org.crowdnotifier.android.sdk.model.VenueInfo;
import org.crowdnotifier.android.sdk.utils.QrUtils;

import ch.ubique.notifyme.app.checkin.CheckInFragment;
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
import static ch.ubique.notifyme.app.utils.ReminderHelper.ACTION_DID_AUTO_CHECKOUT;
import static ch.ubique.notifyme.app.utils.ReminderHelper.autoCheckoutIfNecessary;

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

		boolean onboardingCompleted = storage.getOnboardingCompleted();

		PackageManagerCompat pmc = InstantApps.getPackageManagerCompat(this);
		boolean isInstantApp = pmc.isInstantApp();

		if (!isInstantApp) {
			byte[] instantAppCookie = pmc.getInstantAppCookie();
			if (instantAppCookie != null && instantAppCookie.length > 0) {
				// If there is an url in the instant app cookies, mark the onboarding as complete and process the url
				onboardingCompleted = true;
				Storage.getInstance(this).setOnboardingCompleted(true);

				String url = new String(instantAppCookie, StandardCharsets.UTF_8);
				checkValidCheckInIntent(url);
				pmc.setInstantAppCookie(null);
			}

			viewModel.refreshExposures();
		}

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

		viewModel.getForceUpdate().observe(this, forceUpdate -> {
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
		PackageManagerCompat pmc = InstantApps.getPackageManagerCompat(this);
		if (storage.getOnboardingCompleted() && !pmc.isInstantApp()) checkIntentForActions();
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(autoCheckoutBroadcastReceiver, new IntentFilter(ACTION_DID_AUTO_CHECKOUT));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!InstantApps.getPackageManagerCompat(this).isInstantApp()) {
			viewModel.refreshTraceKeys();
		}
		viewModel.refreshErrors();
		autoCheckoutIfNecessary(this, viewModel.getCheckInState());
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
		if ((ACTION_REMINDER_NOTIFICATION.equals(intentAction) || ACTION_ONGOING_NOTIFICATION.equals(intentAction)) &&
				viewModel.isCheckedIn().getValue()) {
			showCheckedInScreen();
		} else if (ACTION_CHECK_OUT_NOW.equals(intentAction) && viewModel.isCheckedIn().getValue()) {
			showCheckOutScreen();
		} else if (ACTION_EXPOSURE_NOTIFICATION.equals(intentAction)) {
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
				.addToBackStack(MainFragment.TAG)
				.commit();
	}

	private void showCheckInScreen() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, CheckInFragment.newInstance())
				.addToBackStack(MainFragment.TAG)
				.commit();
	}

	private void showCheckOutScreen() {
		showCheckedInScreen();
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				.replace(R.id.container, CheckOutFragment.newInstance())
				.addToBackStack(MainFragment.TAG)
				.commit();
	}

	private void showExposureScreen(ExposureEvent exposureEvent) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, ExposureFragment.newInstance(exposureEvent.getId()))
				.addToBackStack(MainFragment.TAG)
				.commit();
	}


	private void checkValidCheckInIntent(String qrCodeData) {
		try {
			VenueInfo venueInfo = CrowdNotifier.getVenueInfo(qrCodeData, BuildConfig.ENTRY_QR_CODE_PREFIX);
			if (viewModel.isCheckedIn().getValue()) {
				new ErrorDialog(this, ErrorState.ALREADY_CHECKED_IN).show();
			} else {
				viewModel.setCheckInState(new CheckInState(false, venueInfo, System.currentTimeMillis(),
						System.currentTimeMillis(), ReminderOption.OFF));
				showCheckInScreen();
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