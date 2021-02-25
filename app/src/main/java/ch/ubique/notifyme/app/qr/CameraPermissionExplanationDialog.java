package ch.ubique.notifyme.app.qr;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import ch.ubique.notifyme.app.R;

public class CameraPermissionExplanationDialog extends AlertDialog {

	private View.OnClickListener grantCameraAccessClickListener;

	public CameraPermissionExplanationDialog(@NonNull Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_camera_permission_explanation);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

		TextView buttonView = findViewById(R.id.camera_permission_dialog_ok_button);
		buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		findViewById(R.id.camera_permission_dialog_close_button).setOnClickListener(v -> cancel());
		findViewById(R.id.camera_permission_dialog_ok_button).setOnClickListener(v -> {
			dismiss();
			if (grantCameraAccessClickListener != null) grantCameraAccessClickListener.onClick(v);
		});
	}

	public void setGrantCameraAccessClickListener(View.OnClickListener listener) {
		this.grantCameraAccessClickListener = listener;
	}

}
