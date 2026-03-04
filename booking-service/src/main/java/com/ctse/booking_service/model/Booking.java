package com.ctse.booking_service.model;

import java.util.Objects;

public class Booking {
	private String id;
	private String userId;
	private String slotId;
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSlotId() {
		return slotId;
	}

	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Booking)) return false;
		Booking booking = (Booking) o;
		return Objects.equals(id, booking.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Booking{" +
				"id='" + id + '\'' +
				", userId='" + userId + '\'' +
				", slotId='" + slotId + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
