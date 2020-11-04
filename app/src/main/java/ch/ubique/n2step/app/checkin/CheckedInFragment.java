package ch.ubique.n2step.app.checkin;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.ubique.n2step.app.MainViewModel;
import ch.ubique.n2step.app.R;

public class CheckedInFragment extends Fragment {

	private final static String TAG = CheckedInFragment.class.getCanonicalName();

	private MainViewModel viewModel;

	public CheckedInFragment() { super(R.layout.fragment_checked_in); }

	public static CheckedInFragment newInstance() {
		return new CheckedInFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
		super.onCreate(savedInstanceState);
	}

}
