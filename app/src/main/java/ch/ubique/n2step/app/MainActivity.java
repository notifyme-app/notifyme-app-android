package ch.ubique.n2step.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import ch.ubique.n2step.app.network.KeyLoadWorker;

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
		KeyLoadWorker.startKeyLoadWorker(this);
	}

}