package ch.ubique.n2step.app.model;

import ch.ubique.n2step.sdk.model.VenueInfo;

public class DiaryEntry {
	private long id;
	private long arrivalTime;
	private long departureTime;
	private VenueInfo venueInfo;
	private String comment;

	public DiaryEntry(long id, long arrivalTime, long departureTime, VenueInfo venueInfo, String comment) {
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

	public VenueInfo getVenueInfo() {
		return venueInfo;
	}

	public String getComment() {
		return comment;
	}

}
