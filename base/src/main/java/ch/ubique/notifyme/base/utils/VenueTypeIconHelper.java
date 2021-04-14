package ch.ubique.notifyme.base.utils;

import androidx.annotation.DrawableRes;

import org.crowdnotifier.android.sdk.model.Qr;

import ch.ubique.notifyme.base.R;

public class VenueTypeIconHelper {

	@DrawableRes
	public static int getDrawableForVenueType(Qr.QRCodeContent.VenueType venueType) {
		switch (venueType) {
			case CAFETERIA:
				return R.drawable.ic_illus_caffeteria;
			case MEETING_ROOM:
				return R.drawable.ic_illus_meeting;
			case PRIVATE_EVENT:
				return R.drawable.ic_illus_private_event;
			case CANTEEN:
				return R.drawable.ic_illus_canteen;
			case LIBRARY:
				return R.drawable.ic_illus_library;
			case LECTURE_ROOM:
				return R.drawable.ic_illus_lecture_room;
			case SHOP:
				return R.drawable.ic_illus_shop;
			case GYM:
				return R.drawable.ic_illus_gym;
			case KITCHEN_AREA:
				return R.drawable.ic_illus_kitchen_area;
			case OFFICE_SPACE:
				return R.drawable.ic_illus_office_space;
			default:
				return R.drawable.ic_illus_other;
		}
	}

}
