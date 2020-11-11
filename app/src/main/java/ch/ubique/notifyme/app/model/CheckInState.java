package ch.ubique.notifyme.app.model;

import org.crowdnotifier.android.sdk.model.VenueInfo;

public class CheckInState {
	VenueInfo venueInfo;
	long checkInTime;
	long checkOutTime;

	public CheckInState(VenueInfo venueInfo, long checkInTime, long checkOutTime) {
		this.venueInfo = venueInfo;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
	}

	public VenueInfo getVenueInfo() {
		return venueInfo;
	}

	public long getCheckInTime() {
		return checkInTime;
	}

	public long getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckInTime(long checkInTime) {
		this.checkInTime = checkInTime;
	}

	public void setCheckOutTime(long checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

}
