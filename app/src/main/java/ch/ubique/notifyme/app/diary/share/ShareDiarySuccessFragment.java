package ch.ubique.notifyme.app.diary.share;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.notifyme.app.R;

public class ShareDiarySuccessFragment extends Fragment {

	public final static String TAG = ShareDiarySuccessFragment.class.getCanonicalName();

	public ShareDiarySuccessFragment() {
		super(R.layout.fragment_share_diary_success);
	}

	public static ShareDiarySuccessFragment newInstance() {
		return new ShareDiarySuccessFragment();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View cancelButton = view.findViewById(R.id.fragment_share_diray_success_cancel_button);
		Button doneButton = view.findViewById(R.id.fragment_share_diray_success_button);
		cancelButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
		doneButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
	}

}
