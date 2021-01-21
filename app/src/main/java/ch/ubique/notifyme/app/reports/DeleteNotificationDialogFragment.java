package ch.ubique.notifyme.app.reports;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import ch.ubique.notifyme.app.MainViewModel;
import ch.ubique.notifyme.app.R;

public class DeleteNotificationDialogFragment extends DialogFragment {

	public static final String TAG = DeleteNotificationDialogFragment.class.getCanonicalName();
	private static final String EXPOSURE_ID_ARG = "EXPOSURE_ID_ARG";

	private MainViewModel viewModel;

	public static DeleteNotificationDialogFragment newInstance(long exposureId) {
		DeleteNotificationDialogFragment fragment = new DeleteNotificationDialogFragment();
		Bundle args = new Bundle();
		args.putLong(EXPOSURE_ID_ARG, exposureId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
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
		return inflater.inflate(R.layout.dialog_fragment_warning, container);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		View closeButton = view.findViewById(R.id.warning_dialog_close_button);
		Button deleteButton = view.findViewById(R.id.warning_dialog_action_button);
		TextView explanationText = view.findViewById(R.id.warning_dialog_text);
		explanationText.setText(R.string.delete_exposure_warning_dialog_text);
		deleteButton.setText(R.string.delete_now_exposure_button_title);

		closeButton.setOnClickListener(v -> dismiss());
		deleteButton.setOnClickListener(v -> deleteNow());
	}

	private void deleteNow() {
		viewModel.removeExposure(getArguments().getLong(EXPOSURE_ID_ARG));
		dismiss();
		requireActivity().getSupportFragmentManager().popBackStack();
	}

}
