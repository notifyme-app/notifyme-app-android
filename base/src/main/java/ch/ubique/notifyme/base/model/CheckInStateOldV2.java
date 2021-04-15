package ch.ubique.notifyme.base.model;

import org.crowdnotifier.android.sdk.model.VenueInfo;

import ch.ubique.notifyme.app.model.Proto;

/**
 * This class is only used for Migration Purposes from CrowdNotifier V2 to V3, if a user is checked in while updating the app
 */
public class CheckInStateOldV2 {
	private boolean isCheckedIn;
	private VenueInfoOldV2 venueInfo;
	private long checkInTime;
	private long checkOutTime;
	ReminderOption selectedTimerOption;

	public CheckInStateOldV2(boolean isCheckedIn, VenueInfoOldV2 venueInfo, long checkInTime, long checkOutTime,
			ReminderOption selectedTimerOption) {
		this.isCheckedIn = isCheckedIn;
		this.venueInfo = venueInfo;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.selectedTimerOption = selectedTimerOption;
	}

	public CheckInState toCheckInState() {

		Proto.NotifyMeLocationData notifyMeLocationData = Proto.NotifyMeLocationData.newBuilder()
				.setRoom(venueInfo.room)
				.setType(venueInfo.venueType)
				.setVersion(2)
				.build();

		VenueInfo venueInfoNew = new VenueInfo(venueInfo.name, venueInfo.location, venueInfo.notificationKey,
				venueInfo.masterPublicKey, venueInfo.nonce1, venueInfo.nonce2, venueInfo.validFrom, venueInfo.validTo, null,
				notifyMeLocationData.toByteArray());

		return new CheckInState(isCheckedIn, venueInfoNew, checkInTime, checkOutTime, selectedTimerOption);
	}

	public class VenueInfoOldV2 {
		private String name;
		private String location;
		private String room;
		private byte[] notificationKey;
		private Proto.VenueType venueType;
		private byte[] masterPublicKey;
		private byte[] nonce1;
		private byte[] nonce2;
		long validFrom;
		long validTo;

		public VenueInfoOldV2(String name, String location, String room, byte[] notificationKey, Proto.VenueType venueType,
				byte[] masterPublicKey, byte[] nonce1, byte[] nonce2, long validFrom, long validTo) {
			this.name = name;
			this.location = location;
			this.room = room;
			this.notificationKey = notificationKey;
			this.venueType = venueType;
			this.masterPublicKey = masterPublicKey;
			this.nonce1 = nonce1;
			this.nonce2 = nonce2;
			this.validFrom = validFrom;
			this.validTo = validTo;
		}

	}

}
