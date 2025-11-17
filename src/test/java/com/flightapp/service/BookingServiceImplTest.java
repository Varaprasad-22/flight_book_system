package com.flightapp.service;

import com.flightapp.dto.Bookingdto;
import com.flightapp.dto.Passengers;
import com.flightapp.model.*;

import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Bookingdto booking;
    private FlightEntity flight;
    private User user;

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

        Passengers p = new Passengers();
        p.setName("Passenger1");
        p.setAge(25);
        p.setGender("M");
        p.setMeal("Veg");
        p.setSeatNo("12A");

        booking = new Bookingdto();
        booking.setEmailId("test@mail.com");
        booking.setName("John");
        booking.setNoOfSeats(2);
        booking.setPassengers(List.of(p));
    }

    @Test
    void testBookFlight_success() {
        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(flightRepo.findById(100)).thenReturn(Optional.of(flight));
        when(bookingRepo.save(any())).thenReturn(new BookingEntity());

        String msg = bookingService.bookFlight(booking, "100");

        assertTrue(msg.contains("Booking Successful"));
        verify(bookingRepo, times(1)).save(any());
    }
}
