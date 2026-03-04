package com.ctse.booking_service.repository;

import com.ctse.booking_service.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

@Repository
public class BookingRepository {
	private final Map<String, Booking> store = new ConcurrentHashMap<>();

	public Booking save(Booking booking) {
		if (booking == null) {
			throw new IllegalArgumentException("booking must not be null");
		}
		if (booking.getId() == null || booking.getId().isBlank()) {
			booking.setId(UUID.randomUUID().toString());
		}
		store.put(booking.getId(), booking);
		return booking;
	}

	public Optional<Booking> findById(String id) {
		if (id == null) return Optional.empty();
		return Optional.ofNullable(store.get(id));
	}

	public List<Booking> findByUserId(String userId) {
		if (userId == null) return Collections.emptyList();
		return store.values().stream()
				.filter(b -> userId.equals(b.getUserId()))
				.collect(Collectors.toList());
	}
}
