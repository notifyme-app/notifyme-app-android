package ch.ubique.n2step.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;

import ch.ubique.n2step.app.R;
import ch.ubique.n2step.app.utils.QrCodeAnalyzer;

public class QrCodeScannerFragment extends Fragment implements QrCodeAnalyzer.Listener {

	private final static String TAG = QrCodeScannerFragment.class.getCanonicalName();
	private PreviewView previewView;
	private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
	private ExecutorService cameraExecutor;
	private Toast toast;

	public QrCodeScannerFragment() { super(R.layout.fragment_qr_code_scanner); }

	public static QrCodeScannerFragment newInstance() {
		return new QrCodeScannerFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraExecutor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		previewView = view.findViewById(R.id.camera_preview);
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
		startCameraAndQrAnalyzer();
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
				cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalyzer);
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
	public void onQRCodeFound(String qrCodeData) {
		//TODO: Do stuff with QR Code
		requireActivity().runOnUiThread(() -> {
			if (toast != null) {
				toast.cancel();
			}
			toast = Toast.makeText(requireContext(), qrCodeData, Toast.LENGTH_SHORT);
			toast.show();
		});
	}

}
