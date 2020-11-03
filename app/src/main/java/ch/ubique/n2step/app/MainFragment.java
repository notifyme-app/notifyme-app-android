package ch.ubique.n2step.app;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.n2step.app.reports.ReportsFragment;
import ch.ubique.n2step.app.qr.QrCodeScannerFragment;
import ch.ubique.n2step.app.utils.StringUtils;

public class MainFragment extends Fragment {

	private static final int PERMISSION_REQUEST_CAMERA = 13;

	private MainViewModel viewModel;
	private View checkInButton;
	private View reportsHeader;
	private View noReportsHeader;
	private TextView splashText;
	private View mainImageView;


	public MainFragment() { super(R.layout.fragment_main); }

	public static MainFragment newInstance() {
		return new MainFragment();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		checkInButton = view.findViewById(R.id.fragment_main_check_in_button);
		reportsHeader = view.findViewById(R.id.fragment_main_reports_header);
		noReportsHeader = view.findViewById(R.id.fragment_main_reports_header_no_report);
		splashText = view.findViewById(R.id.fragment_main_splash_text);
		mainImageView = view.findViewById(R.id.fragment_main_image);

		checkInButton.setOnClickListener(v -> requestCameraAndShowQRCodeScanner());
		reportsHeader.setOnClickListener(v -> showReportsFragment());
		noReportsHeader.setOnClickListener(v -> showReportsFragment());

		viewModel.reports.observe(getViewLifecycleOwner(), reports -> {
			if (reports == null || reports.isEmpty()) {
				noReportsHeader.setVisibility(View.VISIBLE);
				reportsHeader.setVisibility(View.GONE);
				splashText.setVisibility(View.VISIBLE);
				splashText.setText(StringUtils.getTwoColoredString(getString(R.string.no_report_hero_text),
						getString(R.string.no_report_hero_text_highlight),
						getResources().getColor(R.color.secondary, null)));

				mainImageView.setVisibility(View.VISIBLE);
			} else {
				noReportsHeader.setVisibility(View.GONE);
				reportsHeader.setVisibility(View.VISIBLE);
				splashText.setVisibility(View.GONE);
				mainImageView.setVisibility(View.GONE);
				((TextView) reportsHeader.findViewById(R.id.reports_header_text)).setText(StringUtils
						.getTwoColoredString(getString(R.string.report_message_text),
								getString(R.string.report_message_text_highlight),
								getResources().getColor(R.color.tertiary, null)));

				if (reports.size() == 1) {
					((TextView) reportsHeader.findViewById(R.id.reports_header_title)).setText(R.string.report_title_singular);
				} else {
					((TextView) reportsHeader.findViewById(R.id.reports_header_title))
							.setText(getString(R.string.report_title_plural).replace("{NUMBER}", String.valueOf(reports.size())));
				}
			}
		});
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

	private void showReportsFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, ReportsFragment.newInstance())
				.addToBackStack(ReportsFragment.class.getCanonicalName())
				.commit();
	}

}