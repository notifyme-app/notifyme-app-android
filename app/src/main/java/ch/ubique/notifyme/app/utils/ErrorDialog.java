package ch.ubique.notifyme.app.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import ch.ubique.notifyme.app.R;

public class ErrorDialog extends AlertDialog {

	ErrorState errorState;

	public ErrorDialog(@NonNull Context context, ErrorState errorState) {
		super(context);
		this.errorState = errorState;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_error);
		View closeButton = findViewById(R.id.dialog_error_close_button);

		if (errorState == ErrorState.FORCE_UPDATE_REQUIRED) {
			setCancelable(false);
			closeButton.setVisibility(View.GONE);
			ErrorHelper.updateErrorView(findViewById(R.id.dialog_error_container), errorState, null, getContext());
		} else {
			closeButton.setOnClickListener(v -> dismiss());
			ErrorHelper.updateErrorView(findViewById(R.id.dialog_error_container), errorState, this::dismiss, getContext());
		}

		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
	}

}
