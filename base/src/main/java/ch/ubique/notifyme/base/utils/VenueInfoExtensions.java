package ch.ubique.notifyme.base.utils;

import androidx.annotation.DrawableRes;


import com.google.protobuf.InvalidProtocolBufferException;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.model.Proto;
import ch.ubique.notifyme.base.R;

public class VenueInfoExtensions {


	public static Proto.NotifyMeLocationData getNotifyMeLocationData(VenueInfo venueInfo) {
		try {
			return Proto.NotifyMeLocationData.parseFrom(venueInfo.getCountryData());
		} catch (InvalidProtocolBufferException e) {
			return Proto.NotifyMeLocationData.newBuilder().build();
		}
	}

	public static String getSubtitle(VenueInfo venueInfo) {
		Proto.NotifyMeLocationData notifyMeLocationData = getNotifyMeLocationData(venueInfo);
		if (notifyMeLocationData.getRoom() == null || notifyMeLocationData.getRoom().equals("")) {
			return venueInfo.getAddress();
		} else {
			return venueInfo.getDescription() + ", " + notifyMeLocationData.getRoom();
		}
	}

	@DrawableRes
	public static int getVenueTypeDrawable(VenueInfo venueInfo) {
		return getDrawableForVenueType(getNotifyMeLocationData(venueInfo).getType());
	}

	@DrawableRes
	public static int getDrawableForVenueType(Proto.VenueType venueType) {
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
