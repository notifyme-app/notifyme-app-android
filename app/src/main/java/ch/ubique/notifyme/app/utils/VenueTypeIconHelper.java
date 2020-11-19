package ch.ubique.notifyme.app.utils;

import androidx.annotation.DrawableRes;

import org.crowdnotifier.android.sdk.model.Qr;

import ch.ubique.notifyme.app.R;

public class VenueTypeIconHelper {

	@DrawableRes
	public static int getDrawableForVenueType(Qr.QRCodeContent.VenueType venueType) {
		switch (venueType) {
			case CAFETERIA:
				return R.drawable.ic_illus_caffeteria;
			case MEETING_ROOM:
				return R.drawable.ic_illus_meeting;
			case PRIVATE_PARTY:
				return R.drawable.ic_illus_private_event;
			default:
				return R.drawable.ic_illus_other;
		}
	}

}
