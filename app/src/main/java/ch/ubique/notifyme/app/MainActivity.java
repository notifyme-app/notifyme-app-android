package ch.ubique.notifyme.app;

import android.content.Intent;
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

import ch.ubique.notifyme.app.checkin.CheckInDialogFragment;
import ch.ubique.notifyme.app.checkin.CheckedInFragment;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.network.KeyLoadWorker;
import ch.ubique.notifyme.app.onboarding.OnboardingIntroductionFragment;
import ch.ubique.notifyme.app.reports.ExposureFragment;
import ch.ubique.notifyme.app.utils.ErrorDialog;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.Storage;

import static ch.ubique.notifyme.app.utils.NotificationHelper.EXPOSURE_ID;
import static ch.ubique.notifyme.app.utils.NotificationHelper.EXPOSURE_NOTIFICATION_TYPE;
import static ch.ubique.notifyme.app.utils.NotificationHelper.NOTIFICATION_TYPE;
import static ch.ubique.notifyme.app.utils.NotificationHelper.REMINDER_TYPE;

public class MainActivity extends AppCompatActivity {

	private MainViewModel viewModel;
	private Storage storage;

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
		}

		//TODO: Handle intents also when Activity is not newly created, but the app was already opened
		if (onboardingCompleted && !isInstantApp) handleCustomIntents();

		KeyLoadWorker.startKeyLoadWorker(this);

		viewModel.forceUpdate.observe(this, forceUpdate -> {
			if (forceUpdate) new ErrorDialog(this, ErrorState.FORCE_UPDATE_REQUIRED).show();
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!InstantApps.getPackageManagerCompat(this).isInstantApp()) {
			viewModel.refreshTraceKeys();
		}
		viewModel.refreshErrors();
	}

	private void handleCustomIntents() {
		if (getIntent().hasExtra(NOTIFICATION_TYPE)) {
			int notificationType = getIntent().getIntExtra(NOTIFICATION_TYPE, 0);
			if (notificationType == REMINDER_TYPE && viewModel.isCheckedIn()) {
				showCheckedInScreen();
			} else if (notificationType == EXPOSURE_NOTIFICATION_TYPE) {
				long id = getIntent().getLongExtra(EXPOSURE_ID, -1);
				ExposureEvent exposureEvent = getExposureWithId(id);
				if (exposureEvent != null) {
					showExposureScreen(exposureEvent);
				}
			}
		} else if (getIntent().getData() != null) {
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

	public interface BackPressListener {
		boolean onBackPressed();

	}

}