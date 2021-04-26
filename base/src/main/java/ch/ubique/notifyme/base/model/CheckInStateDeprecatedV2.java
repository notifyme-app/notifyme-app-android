package ch.ubique.notifyme.base.model;

/**
 * This class is only used for Migration Purposes from CrowdNotifier V2 to V3, if a user is checked in while updating the app
 */
public class CheckInStateDeprecatedV2 {
	private boolean isCheckedIn;
	private VenueInfoDeprecatedV2 venueInfo;
	private long checkInTime;
	private long checkOutTime;
	ReminderOption selectedTimerOption;

	public CheckInStateDeprecatedV2(boolean isCheckedIn, VenueInfoDeprecatedV2 venueInfo, long checkInTime, long checkOutTime,
			ReminderOption selectedTimerOption) {
		this.isCheckedIn = isCheckedIn;
		this.venueInfo = venueInfo;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.selectedTimerOption = selectedTimerOption;
	}

	public CheckInState toCheckInState() {

		return new CheckInState(isCheckedIn, venueInfo.toVenueInfo(), checkInTime, checkOutTime, selectedTimerOption);
	}

}
