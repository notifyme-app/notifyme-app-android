package ch.ubique.n2step.app.qr;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.checkin.CheckInDialogFragment;
import ch.ubique.n2step.app.model.CheckInState;
import ch.ubique.n2step.sdk.N2STEP;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class QrCodeScannerFragment extends Fragment implements QrCodeAnalyzer.Listener {

	private final static String TAG = QrCodeScannerFragment.class.getCanonicalName();

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

	public QrCodeScannerFragment() { super(R.layout.fragment_qr_code_scanner); }

	public static QrCodeScannerFragment newInstance() {
		return new QrCodeScannerFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		viewModel.isQrScanningEnabled = true;
		cameraExecutor = Executors.newSingleThreadExecutor();
		super.onCreate(savedInstanceState);
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
		Toolbar toolbar = view.findViewById(R.id.toolbar);

		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		startCameraAndQrAnalyzer();
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
		if (!viewModel.isQrScanningEnabled) return;
		VenueInfo venueInfo = N2STEP.getInfo(qrCodeData);
		if (venueInfo == null) {
			getActivity().runOnUiThread(() -> indicateInvalidQrCode(true));
			//TODO: Show that this is not a valid code in the UI.
		} else {
			viewModel.isQrScanningEnabled = false;
			viewModel.setCheckInState(new CheckInState(venueInfo, System.currentTimeMillis(), System.currentTimeMillis()));
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
		stroke.setStroke(getResources().getDimensionPixelSize(R.dimen.qr_scanner_indicator_stroke_width),
				getResources().getColor(color, null));
	}

	private void showCheckInDialog() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.add(CheckInDialogFragment.newInstance(), CheckInDialogFragment.class.getCanonicalName())
				.commit();
	}

}
