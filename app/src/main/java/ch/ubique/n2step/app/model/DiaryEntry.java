package ch.ubique.n2step.app.model;

import ch.ubique.n2step.sdk.model.VenueInfo;

public class DiaryEntry {
	private long id;
	private long arrialTime;
	private long departureTime;
	private VenueInfo venueInfo;
	private String comment;

	public DiaryEntry(long id, long arrialTime, long departureTime, VenueInfo venueInfo, String comment) {
		this.id = id;
		this.arrialTime = arrialTime;
		this.departureTime = departureTime;
		this.venueInfo = venueInfo;
		this.comment = comment;
	}

	public long getId() {
		return id;
	}

	public long getArrialTime() {
		return arrialTime;
	}

	public long getDepartureTime() {
		return departureTime;
	}

	public VenueInfo getVenueInfo() {
		return venueInfo;
	}

	public String getComment() {
		return comment;
	}

}
