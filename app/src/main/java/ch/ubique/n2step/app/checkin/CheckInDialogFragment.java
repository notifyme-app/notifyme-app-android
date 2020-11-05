package ch.ubique.n2step.app.checkin;

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
import androidx.lifecycle.ViewModelProvider;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class CheckInDialogFragment extends DialogFragment {

	private MainViewModel viewModel;
	VenueInfo venueInfo;

	private View closeButton;
	private TextView nameTextView;
	private TextView locationTextView;
	private TextView roomTextView;
	private ImageView venueTypeIcon;
	private View checkInButton;

	public static CheckInDialogFragment newInstance() {
		return new CheckInDialogFragment();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		venueInfo = viewModel.checkInState.getVenueInfo();
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
		nameTextView = view.findViewById(R.id.check_in_dialog_name);
		locationTextView = view.findViewById(R.id.check_in_dialog_location);
		roomTextView = view.findViewById(R.id.check_in_dialog_room);
		venueTypeIcon = view.findViewById(R.id.check_in_dialog_venue_type_icon);
		checkInButton = view.findViewById(R.id.check_in_dialog_check_in_button);

		//TODO: Set venueType icon

		nameTextView.setText(venueInfo.getName());
		locationTextView.setText(venueInfo.getLocation());
		roomTextView.setText(venueInfo.getRoom());

		checkInButton.setOnClickListener(v -> {
			viewModel.startCheckInTimer();
			showCheckedInFragment();
			dismiss();
		});
		closeButton.setOnClickListener(v -> {
			dismiss();
			viewModel.setCheckInState(null);
		});
		super.onViewCreated(view, savedInstanceState);
	}

	private void showCheckedInFragment() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.slide_pop_enter, R.anim.slide_pop_exit)
				.replace(R.id.container, CheckedInFragment.newInstance())
				.commit();
	}

	@Override
	public void onCancel(@NonNull DialogInterface dialog) {
		viewModel.isQrScanningEnabled = true;
		super.onCancel(dialog);
	}

	@Override
	public void dismiss() {
		viewModel.isQrScanningEnabled = true;
		super.dismiss();
	}

}
