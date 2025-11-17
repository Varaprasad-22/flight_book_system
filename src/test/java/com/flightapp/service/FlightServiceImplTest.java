package com.flightapp.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightapp.dto.Flight;
import com.flightapp.model.Airline;
import com.flightapp.model.FlightEntity;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
public class FlightServiceImplTest {
	@Mock
	private FlightRepository flightrepo;

	@Mock
	private AirlineRepository airlinerepo;

	@InjectMocks
	private FlightServiceImpl flightservice;

	private Airline airline;
	private Flight flightDto;
	private FlightEntity entity;

	@BeforeEach
	void setup() {
		airline = new Airline();
		airline.setAirlineId(1);
		airline.setAirlineName("Air India");

		flightDto = new Flight();
		flightDto.setAirlineName("Air India");
		flightDto.setFlightNumber("ABC-123");
		flightDto.setFromPlace("HYD");
		flightDto.setToPlace("DEL");
		flightDto.setTotalSeats(150);
		flightDto.setPrice(5000);
		flightDto.setArrivalTime(LocalDateTime.now().plusHours(2));
		flightDto.setDepatureTime(LocalDateTime.now());

		entity = new FlightEntity();
		entity.setAirlineId(1);
		entity.setFlightId(10);
		entity.setFlightNumber("ABC-123");
		entity.setFromLocation("HYD");
		entity.setToLocation("DEL");
		entity.setDepatureTime(LocalDateTime.now());
		entity.setArrivalTime(LocalDateTime.now().plusHours(2));
		entity.setPrice(5000);
		entity.setTotalSeats(150);
		entity.setAvaliSeats(150);
	}

	@Test
	void testAddFlight_success() {
		when(airlinerepo.findByAirlineName("Air India")).thenReturn(Optional.of(airline));
		when(flightrepo.save(any())).thenReturn(entity);

		String msg = flightservice.addFlight(flightDto);

		assertEquals("Flight Saved", msg);
		verify(flightrepo, times(1)).save(any());
	}

}
