package com.flightapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.Bookingdto;
import com.flightapp.dto.Flight;
import com.flightapp.dto.Passengers;
import com.flightapp.dto.SearchResult;
import com.flightapp.exceptions.BookingException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.*;

import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	private FlightRepository flightRepo;

	@Mock
	private BookingRepository bookingRepo;

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private BookingServiceImpl bookingService;
	
	@InjectMocks
	private FlightServiceImpl flightService;

	private Bookingdto booking;
	private FlightEntity flight;
	private User user;
	private Flight flightRequest;

	@BeforeEach
	void setup() {

		user = new User();
		user.setUserId(1);
		user.setEmail("test@mail.com");
		user.setName("John");

		flight = new FlightEntity();
		flight.setFlightId(100);
		flight.setAirlineId(1);
		flight.setAvaliSeats(10);
		flight.setDepatureTime(LocalDateTime.now().plusDays(2));

		Passengers passengerRequest = new Passengers();
		passengerRequest.setName("Passenger1");
		passengerRequest.setAge(25);
		passengerRequest.setGender("M");
		passengerRequest.setMeal("Veg");
		passengerRequest.setSeatNo("12A");

		booking = new Bookingdto();
		booking.setEmailId("test@mail.com");
		booking.setOutboundFlightId(100);

		booking.setName("John");
		booking.setNoOfSeats(2);
		booking.setPassengers(List.of(passengerRequest));
	}

	@Test
	void testBookFlight_success() {

		when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
		when(flightRepo.findById(100)).thenReturn(Optional.of(flight));

		when(bookingRepo.save(any())).thenReturn(new BookingEntity());

		String msg = bookingService.bookFlight(booking);
		assertTrue(msg.contains("One-way Booking Successful"));

		verify(bookingRepo, times(1)).save(any());
	}
	@Test
	void testBookFlight_outboundFlightNotFound() {

	    when(flightRepo.findById(100)).thenReturn(Optional.empty());

	    Exception ex = assertThrows(RuntimeException.class, () -> {
	        bookingService.bookFlight(booking);
	    });

	    assertTrue(ex.getMessage().contains("Outbound flight not found"));
	}
	@Test
	void testBookFlight_roundTripSuccess() {

	    FlightEntity returnFlight = new FlightEntity();
	    returnFlight.setFlightId(200);
	    returnFlight.setAvaliSeats(10);

	    booking.setReturnFlightId(200);

	    when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
	    when(flightRepo.findById(100)).thenReturn(Optional.of(flight));
	    when(flightRepo.findById(200)).thenReturn(Optional.of(returnFlight));
	    when(bookingRepo.save(any())).thenReturn(new BookingEntity());

	    String msg = bookingService.bookFlight(booking);

	    assertTrue(msg.contains("Round-trip Booking Successful"));
	    verify(bookingRepo, times(1)).save(any());
	}
	@Test
	void testGetBookingDetails_success() {

	    BookingEntity bookingEntity = new BookingEntity();
	    bookingEntity.setPnr("PNR123");
	    bookingEntity.setFlight(flight);

	    PassengerEntity passenger = new PassengerEntity();
	    passenger.setName("vnr");
	    passenger.setAge(25);
	    passenger.setGender("M");
	    passenger.setMeal("Veg");
	    passenger.setSeatNo("12Ab");

	    bookingEntity.setPassengers(List.of(passenger));

	    when(bookingRepo.findByPnr("PNR123")).thenReturn(Optional.of(bookingEntity));

	    var response = bookingService.getBookingDetails("PNR123");

	    assertEquals("PNR123", response.getPnr());
	    assertEquals(String.valueOf(flight.getFlightId()), response.getFlightId());
	    assertEquals(1, response.getPassengersList().size());
	}
	@Test
	void testGetBookingDetails_notFound() {

	    when(bookingRepo.findByPnr("INVALID")).thenReturn(Optional.empty());

	    assertThrows(ResourceNotFoundException.class,
	            () -> bookingService.getBookingDetails("INVALID"));
	}
	@Test
	void testGetHistoryByEmail_success() {

	    BookingEntity bookingEntity = new BookingEntity();
	    bookingEntity.setEmailId("vnr@mail.com");
	    bookingEntity.setUser(user);
	    bookingEntity.setNoOfSeats(2);

	    PassengerEntity passenger = new PassengerEntity();
	    passenger.setName("vnr1passenger");
	    passenger.setAge(25);
	    passenger.setSeatNo("12A");

	    bookingEntity.setPassengers(List.of(passenger));

	    when(bookingRepo.findAllByEmailId("vnr@mail.com"))
	            .thenReturn(List.of(bookingEntity));

	    var history = bookingService.getHistoryByEmail("vnr@mail.com");

	    assertEquals(1, history.size());
	    assertEquals("vnr@mail.com", history.get(0).getEmailId());
	    assertEquals("John", history.get(0).getName());
	}
	@Test
	void testCancelTicket_success() {

	    BookingEntity bookingEntity = new BookingEntity();
	    bookingEntity.setPnr("PNR123");
	    bookingEntity.setFlight(flight);
	    bookingEntity.setNoOfSeats(2);

	    flight.setAvaliSeats(5);
	    flight.setDepatureTime(LocalDateTime.now().plusDays(2));

	    when(bookingRepo.findByPnr("PNR123")).thenReturn(Optional.of(bookingEntity));

	    String msg = bookingService.cancelTicket("PNR123");

	    assertTrue(msg.contains("successfully cancelled"));
	    assertEquals(7, flight.getAvaliSeats());

	    verify(bookingRepo, times(1)).deleteByPnr("PNR123");
	}
	@Test
	void testCancelTicket_pnrNotFound() {

	    when(bookingRepo.findByPnr("INVALID")).thenReturn(Optional.empty());

	    assertThrows(ResourceNotFoundException.class,
	            () -> bookingService.cancelTicket("INVALID"));
	}
	@Test
	void testCancelTicket_lessThan24Hours() {

	    BookingEntity bookingEntity = new BookingEntity();
	    bookingEntity.setPnr("PNR123");
	    bookingEntity.setFlight(flight);
	    flight.setDepatureTime(LocalDateTime.now().plusHours(5));

	    when(bookingRepo.findByPnr("PNR123")).thenReturn(Optional.of(bookingEntity));

	    assertThrows(BookingException.class,
	            () -> bookingService.cancelTicket("PNR123"));
	}


}
