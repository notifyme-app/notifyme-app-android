package ch.ubique.notifyme.app.utils;

import android.content.Context;
import android.os.Bundle;
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
		ErrorHelper.updateErrorView(findViewById(R.id.dialog_error_container), errorState, () -> dismiss(), getContext());
		findViewById(R.id.dialog_error_close_button).setOnClickListener(v -> dismiss());
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
	}

}
