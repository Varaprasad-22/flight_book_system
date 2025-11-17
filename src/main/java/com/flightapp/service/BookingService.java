package com.flightapp.service;

import java.util.List;

import com.flightapp.dto.BookingGetResponse;
import com.flightapp.dto.Bookingdto;

public interface BookingService {
	String bookFlight(Bookingdto data, String id);

	BookingGetResponse getBookingDetails(String pnr);

	String cancelTicket(String pnr);

	List<Bookingdto> getHistoryByEmail(String emailId);
}
