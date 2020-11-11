package ch.ubique.n2step.app.model;

import androidx.annotation.IdRes;

import ch.ubique.n2step.app.checkin.ReminderOption;
import ch.ubique.n2step.sdk.model.VenueInfo;

public class CheckInState {
	private VenueInfo venueInfo;
	private long checkInTime;
	private long checkOutTime;
	ReminderOption selectedTimerOption;

	public CheckInState(VenueInfo venueInfo, long checkInTime, long checkOutTime, ReminderOption selectedTimerOption) {
		this.venueInfo = venueInfo;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.selectedTimerOption = selectedTimerOption;
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

	public ReminderOption getSelectedTimerOption() {
		return selectedTimerOption;
	}

	public void setCheckInTime(long checkInTime) {
		this.checkInTime = checkInTime;
	}

	public void setCheckOutTime(long checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public void setSelectedTimerOption(ReminderOption selectedTimerOption) {
		this.selectedTimerOption = selectedTimerOption;
	}

}
