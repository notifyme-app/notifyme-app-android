package ch.ubique.notifyme.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;


import java.util.List;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.checkin.CheckedInFragment;
import ch.ubique.notifyme.app.network.KeyLoadWorker;
import ch.ubique.notifyme.app.reports.ExposureFragment;

import static ch.ubique.notifyme.app.utils.NotificationHelper.EXPOSURE_ID;
import static ch.ubique.notifyme.app.utils.NotificationHelper.EXPOSURE_NOTIFICATION_TYPE;
import static ch.ubique.notifyme.app.utils.NotificationHelper.NOTIFICATION_TYPE;
import static ch.ubique.notifyme.app.utils.NotificationHelper.REMINDER_TYPE;

public class MainActivity extends AppCompatActivity {

	private MainViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, MainFragment.newInstance())
					.commitNow();
		}

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
		}

		KeyLoadWorker.startKeyLoadWorker(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		viewModel.refreshErrors();
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

	private void showCheckedInScreen() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, CheckedInFragment.newInstance())
				.addToBackStack(CheckedInFragment.TAG)
				.commit();
	}

	private void showExposureScreen(ExposureEvent exposureEvent) {
		viewModel.setSelectedExposure(exposureEvent);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, ExposureFragment.newInstance())
				.addToBackStack(ExposureFragment.TAG)
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