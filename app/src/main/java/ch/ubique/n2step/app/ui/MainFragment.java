package ch.ubique.n2step.app.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.viewmodel.MainViewModel;

public class MainFragment extends Fragment {

	private static final int PERMISSION_REQUEST_CAMERA = 13;

	private MainViewModel mViewModel;
	private View checkInButton;


	public MainFragment() { super(R.layout.fragment_main); }

	public static MainFragment newInstance() {
		return new MainFragment();
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		checkInButton = view.findViewById(R.id.fragment_main_check_in_button);

		checkInButton.setOnClickListener(v -> requestCameraAndShowQRCodeScanner());
	}

	private void requestCameraAndShowQRCodeScanner() {
		if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
				PackageManager.PERMISSION_GRANTED) {
			showQRCodeScanner();
		} else {
			requestPermissions(new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CAMERA) {
			if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				showQRCodeScanner();
			} else {
				//TODO: Handle Camera Permission Denied case
			}
		}
	}

	private void showQRCodeScanner() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, QrCodeScannerFragment.newInstance())
				.addToBackStack(QrCodeScannerFragment.class.getCanonicalName())
				.commit();
	}

}