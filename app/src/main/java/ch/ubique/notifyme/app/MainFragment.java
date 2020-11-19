package ch.ubique.notifyme.app;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.concurrent.Executor;

import org.crowdnotifier.android.sdk.model.ExposureEvent;

import ch.ubique.notifyme.app.checkin.CheckedInFragment;
import ch.ubique.notifyme.app.diary.DiaryFragment;
import ch.ubique.notifyme.app.impressum.HtmlFragment;
import ch.ubique.notifyme.app.reports.ReportsFragment;
import ch.ubique.notifyme.app.qr.QrCodeScannerFragment;
import ch.ubique.notifyme.app.utils.AssetUtil;
import ch.ubique.notifyme.app.utils.ErrorHelper;
import ch.ubique.notifyme.app.utils.ErrorState;
import ch.ubique.notifyme.app.utils.StringUtils;

public class MainFragment extends Fragment implements MainActivity.BackPressListener {

	public final static String TAG = MainFragment.class.getCanonicalName();

	private MainViewModel viewModel;

	public MainFragment() { super(R.layout.fragment_main); }

	public static MainFragment newInstance() {
		return new MainFragment();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Button checkInButton = view.findViewById(R.id.fragment_main_check_in_button);
		Button checkOutButton = view.findViewById(R.id.fragment_main_check_out_button);
		View reportsHeader = view.findViewById(R.id.fragment_main_reports_header);
		View noReportsHeader = view.findViewById(R.id.fragment_main_reports_header_no_report);
		TextView splashText = view.findViewById(R.id.fragment_main_splash_text);
		View mainImageView = view.findViewById(R.id.fragment_main_image);
		View checkedInLabel = view.findViewById(R.id.fragment_main_checked_in_label);
		SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.fragment_main_swipe_refresh_layout);
		View diaryButton = view.findViewById(R.id.fragment_main_diary_button);
		TextView appNameTextView = view.findViewById(R.id.fragment_main_app_name);
		View infoButton = view.findViewById(R.id.fragment_main_info_button);
		View errorView = view.findViewById(R.id.no_reports_header_error_view);
		View errorViewSmall = view.findViewById(R.id.reports_header_error_view);

		String appName = getString(R.string.app_name);
		appNameTextView.setText(StringUtils.getTwoColoredString(appName, appName.substring(appName.length() - 2),
				getResources().getColor(R.color.primary, null)));
		infoButton.setOnClickListener(v -> {
			showImpressum();
		});
		reportsHeader.setOnClickListener(v -> showReportsFragment());
		noReportsHeader.setOnClickListener(v -> showReportsFragment());

		if (viewModel.isCheckedIn()) {
			checkOutButton.setOnClickListener(v -> showCheckedInScreen());
			checkInButton.setVisibility(View.GONE);
			checkOutButton.setVisibility(View.VISIBLE);
			checkedInLabel.setVisibility(View.VISIBLE);
			viewModel.timeSinceCheckIn
					.observe(getViewLifecycleOwner(), duration -> checkOutButton.setText(StringUtils.getDurationString(duration)));
			viewModel.startCheckInTimer();
		} else {
			checkInButton.setOnClickListener(v -> showQRCodeScanner());
			checkInButton.setVisibility(View.VISIBLE);
			checkOutButton.setVisibility(View.GONE);
			checkedInLabel.setVisibility(View.GONE);
		}

		viewModel.exposures.observe(getViewLifecycleOwner(), reports -> {
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
				((TextView) reportsHeader.findViewById(R.id.reports_header_days_ago))
						.setText(StringUtils.getDaysAgoString(reports.get(0).getStartTime(), getContext()));
			}
		});

		viewModel.errorState.observe(getViewLifecycleOwner(), errorState -> {
			if (errorState == null) {
				List<ExposureEvent> reports = viewModel.exposures.getValue();
				if (reports == null || reports.isEmpty()) {
					splashText.setVisibility(View.VISIBLE);
					mainImageView.setVisibility(View.VISIBLE);
				}
				errorViewSmall.setVisibility(View.GONE);
				errorView.setVisibility(View.GONE);
			} else {
				splashText.setVisibility(View.GONE);
				mainImageView.setVisibility(View.GONE);
				errorViewSmall.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.VISIBLE);
				if (errorState == ErrorState.NETWORK) {
					ErrorHelper.updateErrorView(errorView, errorState, () -> viewModel.refreshTraceKeys(), requireContext());
				} else {
					ErrorHelper.updateErrorView(errorView, errorState, null, requireContext());
				}
			}
		});

		diaryButton.setOnClickListener(v -> authenticateAndShowDiary());

		swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshTraceKeys());

		viewModel.traceKeyLoadingState.observe(getViewLifecycleOwner(), loadingState ->
				swipeRefreshLayout.setRefreshing(loadingState == MainViewModel.LoadingState.LOADING));
	}

	private void showCheckedInScreen() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, CheckedInFragment.newInstance())
				.addToBackStack(CheckedInFragment.TAG)
				.commit();
	}

	private void showQRCodeScanner() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, QrCodeScannerFragment.newInstance())
				.addToBackStack(QrCodeScannerFragment.TAG)
				.commit();
	}

	private void showReportsFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, ReportsFragment.newInstance())
				.addToBackStack(ReportsFragment.TAG)
				.commit();
	}

	private void authenticateAndShowDiary() {
		Executor executor = ContextCompat.getMainExecutor(requireContext());
		BiometricPrompt biometricPrompt =
				new BiometricPrompt(requireActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
					@Override
					public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
						super.onAuthenticationSucceeded(result);
						showDiary();
					}
				});

		if (BiometricManager.from(requireContext()).canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL |
				BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
			BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
					.setTitle(getString(R.string.authenticate_for_diary))
					.setAllowedAuthenticators(
							BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_WEAK)
					.build();
			biometricPrompt.authenticate(promptInfo);
		} else {
			showDiary();
		}
	}

	private void showDiary() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, DiaryFragment.newInstance())
				.addToBackStack(DiaryFragment.TAG)
				.commitAllowingStateLoss();
	}

	private void showImpressum() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.modal_slide_enter, R.anim.modal_slide_exit, R.anim.modal_pop_enter,
						R.anim.modal_pop_exit)
				//TODO: It would be nice to load this asynchronous
				.replace(R.id.container, HtmlFragment
						.newInstance(AssetUtil.getImpressumBaseUrl(getContext()), AssetUtil.getImpressumHtml(getContext())))
				.addToBackStack(DiaryFragment.TAG)
				.commitAllowingStateLoss();
	}


	@Override
	public boolean onBackPressed() {
		requireActivity().finish();
		return true;
	}

}