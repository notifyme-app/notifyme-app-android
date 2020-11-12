package ch.ubique.notifyme.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import ch.ubique.notifyme.app.checkin.CheckedInFragment;
import ch.ubique.notifyme.app.network.KeyLoadWorker;

import static ch.ubique.notifyme.app.utils.NotificationHelper.NOTIFICATION_TYPE;
import static ch.ubique.notifyme.app.utils.NotificationHelper.REMINDER_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, MainFragment.newInstance())
					.commitNow();
		}

		if (getIntent().hasExtra(NOTIFICATION_TYPE)) {
			int notificationType = getIntent().getIntExtra(NOTIFICATION_TYPE, 0);
			if (notificationType == REMINDER_REQUEST_CODE) {
				showCheckedInScreen();
			}
			//TODO: Handle click on Exposure Notification
		}

		KeyLoadWorker.startKeyLoadWorker(this);
	}

	private void showCheckedInScreen() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, CheckedInFragment.newInstance())
				.addToBackStack(CheckedInFragment.TAG)
				.commit();
	}

}