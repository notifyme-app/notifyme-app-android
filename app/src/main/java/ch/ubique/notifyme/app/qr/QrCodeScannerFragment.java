package ch.ubique.notifyme.app.qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;

import org.crowdnotifier.android.sdk.CrowdNotifier;
import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.BuildConfig;
import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;
import ch.ubique.notifyme.app.checkin.CheckInDialogFragment;
import ch.ubique.notifyme.app.model.ReminderOption;
import ch.ubique.notifyme.app.model.CheckInState;
import ch.ubique.notifyme.app.utils.ErrorHelper;
import ch.ubique.notifyme.app.utils.ErrorState;

public class QrCodeScannerFragment extends Fragment implements QrCodeAnalyzer.Listener {

	public final static String TAG = QrCodeScannerFragment.class.getCanonicalName();
	private static final int PERMISSION_REQUEST_CAMERA = 13;

	private MainViewModel viewModel;
	private PreviewView previewView;
	private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
	private ExecutorService cameraExecutor;
	private ImageButton flashButton;
	private TextView invalidCodeText;
	private View topLeftIndicator;
	private View topRightIndicator;
	private View bottomRightIndicator;
	private View bottomLeftIndicator;
	private View errorView;
	private View mainView;

	public QrCodeScannerFragment() { super(R.layout.fragment_qr_code_scanner); }

	public static QrCodeScannerFragment newInstance() {
		return new QrCodeScannerFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		cameraExecutor = Executors.newSingleThreadExecutor();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		viewModel.setQrScanningEnabled(true);
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) !=
				PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
		} else {
			startCameraAndQrAnalyzer();
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		previewView = view.findViewById(R.id.camera_preview);
		flashButton = view.findViewById(R.id.fragment_qr_scanner_flash_button);
		invalidCodeText = view.findViewById(R.id.qr_code_scanner_invalid_code_text);
		topLeftIndicator = view.findViewById(R.id.qr_code_scanner_top_left_indicator);
		topRightIndicator = view.findViewById(R.id.qr_code_scanner_top_right_indicator);
		bottomLeftIndicator = view.findViewById(R.id.qr_code_scanner_bottom_left_indicator);
		bottomRightIndicator = view.findViewById(R.id.qr_code_scanner_bottom_right_indicator);
		errorView = view.findViewById(R.id.fragment_qr_scanner_error_view);
		mainView = view.findViewById(R.id.fragment_qr_scanner_main_view);
		Toolbar toolbar = view.findViewById(R.id.fragment_qr_scanner_toolbar);

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
	}


	private void setupFlashButton(Camera camera) {

		if (!camera.getCameraInfo().hasFlashUnit()) {
			flashButton.setVisibility(View.GONE);
		} else {
			flashButton.setVisibility(View.VISIBLE);
		}

		camera.getCameraInfo().getTorchState().observe(getViewLifecycleOwner(), v -> {
			if (v == TorchState.ON) {
				flashButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_off));
			} else {
				flashButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_on));
			}
		});

		flashButton.setOnClickListener(v -> {
			camera.getCameraControl().enableTorch(camera.getCameraInfo().getTorchState().getValue() == TorchState.OFF);
		});
	}

	private void startCameraAndQrAnalyzer() {
		cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
		cameraProviderFuture.addListener(() -> {
			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
				Preview preview = new Preview.Builder().build();
				preview.setSurfaceProvider(previewView.getSurfaceProvider());

				ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder().build();
				imageAnalyzer.setAnalyzer(cameraExecutor, new QrCodeAnalyzer(this));

				CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

				cameraProvider.unbindAll();
				Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalyzer);
				setupFlashButton(camera);
			} catch (ExecutionException | InterruptedException e) {
				Log.d(TAG, "Error starting camera " + e.getMessage());
				e.printStackTrace();
			}
		}, ContextCompat.getMainExecutor(requireContext()));
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		cameraExecutor.shutdown();
	}

	@Override
	public void noQRCodeFound() {
		getActivity().runOnUiThread(() -> indicateInvalidQrCode(false));
	}

	@Override
	public synchronized void onQRCodeFound(String qrCodeData) {
		if (!viewModel.isQrScanningEnabled()) return;
		VenueInfo venueInfo = CrowdNotifier.getVenueInfo(qrCodeData, BuildConfig.ENTRY_QR_CODE_PREFIX);
		if (venueInfo == null) {
			if (qrCodeData.startsWith(BuildConfig.TRACE_QR_CODE_PREFIX)) {
				viewModel.setQrScanningEnabled(false);
				Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCodeData));
				startActivity(openBrowserIntent);
			} else {
				getActivity().runOnUiThread(() -> indicateInvalidQrCode(true));
			}
		} else {
			viewModel.setQrScanningEnabled(false);
			viewModel.setCheckInState(new CheckInState(false, venueInfo, System.currentTimeMillis(), System.currentTimeMillis(),
					ReminderOption.OFF));
			showCheckInDialog();
		}
	}

	private void indicateInvalidQrCode(boolean invalid) {
		int color = R.color.primary;
		if (invalid) {
			invalidCodeText.setVisibility(View.VISIBLE);
			color = R.color.tertiary;
		} else {
			invalidCodeText.setVisibility(View.INVISIBLE);
		}
		setIndicatorColor(topLeftIndicator, color);
		setIndicatorColor(topRightIndicator, color);
		setIndicatorColor(bottomLeftIndicator, color);
		setIndicatorColor(bottomRightIndicator, color);
	}

	private void setIndicatorColor(View indicator, @ColorRes int color) {
		LayerDrawable drawable = (LayerDrawable) indicator.getBackground();
		GradientDrawable stroke = (GradientDrawable) drawable.findDrawableByLayerId(R.id.indicator);
		if (getContext() == null) return;
		stroke.setStroke(getResources().getDimensionPixelSize(R.dimen.qr_scanner_indicator_stroke_width),
				getResources().getColor(color, null));
	}

	private void showCheckInDialog() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.add(CheckInDialogFragment.newInstance(), CheckInDialogFragment.TAG)
				.commit();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CAMERA) {
			if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				mainView.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.GONE);
				startCameraAndQrAnalyzer();
			} else {
				errorView.setVisibility(View.VISIBLE);
				ErrorHelper.updateErrorView(errorView, ErrorState.CAMERA_ACCESS_DENIED, null, getContext());
				mainView.setVisibility(View.GONE);
			}
		}
	}

}
