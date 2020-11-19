package ch.ubique.notifyme.app.checkin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;

public class CheckInDialogFragment extends DialogFragment {

	public final static String TAG = CheckInDialogFragment.class.getCanonicalName();
	private static final String ADD_CHECK_IN_TRANSACTION_TO_BACKSTACK_ARG = "ADD_CHECK_IN_TRANSACTION_TO_BACKSTACK_ARG";

	private MainViewModel viewModel;
	VenueInfo venueInfo;

	private View closeButton;
	private TextView titleTextView;
	private TextView subtitleTextView;
	private ImageView venueTypeIcon;
	private View checkInButton;

	public static CheckInDialogFragment newInstance(boolean addCheckInTransactionToBackStack) {
		CheckInDialogFragment fragment = new CheckInDialogFragment();
		Bundle args = new Bundle();
		args.putBoolean(ADD_CHECK_IN_TRANSACTION_TO_BACKSTACK_ARG, addCheckInTransactionToBackStack);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		venueInfo = viewModel.getCheckInState().getVenueInfo();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

		super.onResume();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_fragment_check_in, container);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		closeButton = view.findViewById(R.id.check_in_dialog_close_button);
		titleTextView = view.findViewById(R.id.check_in_dialog_title);
		subtitleTextView = view.findViewById(R.id.check_in_dialog_subtitle);
		venueTypeIcon = view.findViewById(R.id.check_in_dialog_venue_type_icon);
		checkInButton = view.findViewById(R.id.check_in_dialog_check_in_button);

		//TODO: Set venueType icon

		titleTextView.setText(venueInfo.getTitle());
		subtitleTextView.setText(venueInfo.getSubtitle());

		checkInButton.setOnClickListener(v -> {
			viewModel.startCheckInTimer();
			viewModel.setCheckedIn(true);
			showCheckedInFragment();
			dismiss();
		});
		closeButton.setOnClickListener(v -> {
			viewModel.setQrScanningEnabled(true);
			dismiss();
		});
		super.onViewCreated(view, savedInstanceState);
	}

	private void showCheckedInFragment() {
		boolean addToBackStack = getArguments().getBoolean(ADD_CHECK_IN_TRANSACTION_TO_BACKSTACK_ARG);

		FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, CheckedInFragment.newInstance());
		if (addToBackStack) transaction.addToBackStack(CheckedInFragment.TAG);
		transaction.commit();
	}

	@Override
	public void onCancel(@NonNull DialogInterface dialog) {
		viewModel.setQrScanningEnabled(true);
		super.onCancel(dialog);
	}

}
