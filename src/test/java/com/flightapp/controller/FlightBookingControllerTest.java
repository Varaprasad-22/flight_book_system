package com.flightapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.*;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(FlightBookingController.class)
public class FlightBookingControllerTest {
	 @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private FlightService flightService;

	    @MockBean
	    private BookingService bookingService;

	    @Autowired
	    private ObjectMapper objectMapper;

	    private Flight flightDto;
	    private Search searchDto;
	    private Bookingdto bookingDto;
	    private BookingGetResponse responseDto;
	    
	    @BeforeEach
	    void setup() {
	        // Flight DTO
	        flightDto = new Flight();
	        flightDto.setAirlineName("Air India");
	        flightDto.setFlightNumber("AI-101");
	        flightDto.setFromPlace("HYD");
	        flightDto.setToPlace("DEL");
	        flightDto.setDepatureTime(LocalDateTime.now());
	        flightDto.setArrivalTime(LocalDateTime.now().plusHours(2));
	        flightDto.setTotalSeats(150);
	        flightDto.setPrice(5000);

	        // Search DTO
	        searchDto = new Search();
	        searchDto.setFromPlace("HYD");
	        searchDto.setToPlace("DEL");
	        searchDto.setTripType("one-way");
	        searchDto.setDepartureDate(LocalDate.now());

	        // Booking DTO
	        Passengers p = new Passengers();
	        p.setName("John");
	        p.setAge(22);
	        p.setGender("M");
	        p.setMeal("Veg");
	        p.setSeatNo("12A");

	        bookingDto = new Bookingdto();
	        bookingDto.setEmailId("test@mail.com");
	        bookingDto.setName("John");
	        bookingDto.setNoOfSeats(1);
	        bookingDto.setPassengers(List.of(p));

	        // Booking Response
	        responseDto = new BookingGetResponse();
	        responseDto.setPnr("PNR123");
	        responseDto.setFlightId("101");
	        responseDto.setPassengersList(List.of(p));
	    }
	    @Test
	    void testAddFlights_success() throws Exception {
	        when(flightService.addFlight(any())).thenReturn(10);

	        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
	                .contentType("application/json")
	                .content(objectMapper.writeValueAsString(flightDto)))
	                .andExpect(status().isCreated())
	                .andExpect(content().string(String.valueOf(10)));
	    }
	    @Test
	    void testSearchFlights_success() throws Exception {
	        SearchResult result = new SearchResult();
	        result.setOutboundFlights(List.of(flightDto));

	        when(flightService.search(any())).thenReturn(result);

	        mockMvc.perform(post("/api/v1.0/flight/search")
	                .contentType("application/json")
	                .content(objectMapper.writeValueAsString(searchDto)))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.outboundFlights").isArray());
	    }
	    @Test
	    void testFlightBooking_success() throws Exception {
	    	when(bookingService.bookFlight(any()))
	        .thenReturn("One-way Booking Successful! PNR: ABC123");


	        mockMvc.perform(post("/api/v1.0/flight/booking")
	                .contentType("application/json")
	                .content(objectMapper.writeValueAsString(bookingDto)))
	                .andExpect(status().isOk())
	                .andExpect(content().string("One-way Booking Successful! PNR: ABC123"));
	    }
	    @Test
	    void testBookingDetails_success() throws Exception {
	        when(bookingService.getBookingDetails("PNR123"))
	                .thenReturn(responseDto);

	        mockMvc.perform(get("/api/v1.0/flight/ticket/PNR123"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.pnr").value("PNR123"));
	    }
	    @Test
	    void testGetHistoryByEmail_success() throws Exception {
	        when(bookingService.getHistoryByEmail("test@mail.com"))
	                .thenReturn(List.of(bookingDto));

	        mockMvc.perform(get("/api/v1.0/flight/booking/history/test@mail.com"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$[0].emailId").value("test@mail.com"));
	    }
	    @Test
	    void testCancelBooking_success() throws Exception {
	        when(bookingService.cancelTicket("PNR123"))
	                .thenReturn("Ticket Cancelled");

	        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/PNR123"))
	                .andExpect(status().isOk())
	                .andExpect(content().string("Ticket Cancelled"));
	    }
		
}
