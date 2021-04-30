package ch.ubique.notifyme.app.model;

import ch.ubique.notifyme.base.model.VenueInfoDeprecatedV2;

@Deprecated
public class DiaryEntryDeprecatedV2 {
	private long id;
	private long arrivalTime;
	private long departureTime;
	private VenueInfoDeprecatedV2 venueInfo;
	private String comment;

	public DiaryEntryDeprecatedV2(long id, long arrivalTime, long departureTime, VenueInfoDeprecatedV2 venueInfo, String comment) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.venueInfo = venueInfo;
		this.comment = comment;
	}

	public long getId() {
		return id;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public long getDepartureTime() {
		return departureTime;
	}

	public VenueInfoDeprecatedV2 getVenueInfo() {
		return venueInfo;
	}

	public String getComment() {
		return comment;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public void setDepartureTime(long departureTime) {
		this.departureTime = departureTime;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public DiaryEntry toDiaryEntry() {
		return new DiaryEntry(id, arrivalTime, departureTime, venueInfo.toVenueInfo(), comment);
	}

}
