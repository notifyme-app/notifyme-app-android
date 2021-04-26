package ch.ubique.notifyme.base.model;

import org.crowdnotifier.android.sdk.model.VenueInfo;
import ch.ubique.notifyme.app.model.Proto;

public class VenueInfoDeprecatedV2 {
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

	public VenueInfoDeprecatedV2(String name, String location, String room, byte[] notificationKey, Proto.VenueType venueType,
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

	public VenueInfo toVenueInfo(){

		Proto.NotifyMeLocationData notifyMeLocationData = Proto.NotifyMeLocationData.newBuilder()
				.setRoom(room)
				.setType(venueType)
				.setVersion(2)
				.build();

		return new VenueInfo(name, location, notificationKey, masterPublicKey, nonce1, nonce2, validFrom, validTo, null,
				notifyMeLocationData.toByteArray());

	}

}
