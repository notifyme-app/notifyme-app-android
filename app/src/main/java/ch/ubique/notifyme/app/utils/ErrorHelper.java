package ch.ubique.notifyme.app.utils;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import ch.ubique.notifyme.app.R;

public class ErrorHelper {

	public static void updateErrorView(View errorView, ErrorState errorState, Runnable customButtonClickAction) {
		((TextView) errorView.findViewById(R.id.error_status_title)).setText(errorState.getTitleResId());
		((TextView) errorView.findViewById(R.id.error_status_text)).setText(errorState.getTextResId());
		((ImageView) errorView.findViewById(R.id.error_status_image))
				.setImageDrawable(ContextCompat.getDrawable(errorView.getContext(), errorState.getImageResId()));

		TextView buttonView = errorView.findViewById(R.id.error_status_button);
		buttonView.setText(errorState.getActionResId());
		buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		buttonView.setOnClickListener(v -> executeErrorAction(errorState, customButtonClickAction));
	}

	public static void executeErrorAction(ErrorState errorState, Runnable customButtonClickAction) {
		if (customButtonClickAction != null) customButtonClickAction.run();
	}

}
