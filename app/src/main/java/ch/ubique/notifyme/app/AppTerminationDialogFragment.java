package ch.ubique.notifyme.app;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AppTerminationDialogFragment extends DialogFragment {

	public static final String TAG = AppTerminationDialogFragment.class.getCanonicalName();

	public static AppTerminationDialogFragment newInstance() {
		return new AppTerminationDialogFragment();
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
		return inflater.inflate(R.layout.dialog_fragment_app_termination, container);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		view.findViewById(R.id.warning_dialog_close_button).setOnClickListener(v -> dismiss());

		Button moreInfoButton = view.findViewById(R.id.app_termination_more_info);
		moreInfoButton.setOnClickListener(v -> showMoreInfo());
	}

	private void showMoreInfo() {
		dismiss();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(getString(ch.ubique.notifyme.base.R.string.app_termination_detail_url)));
		try {
			requireActivity().startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(requireContext(), "No browser installed", Toast.LENGTH_LONG).show();
		}
	}

}
