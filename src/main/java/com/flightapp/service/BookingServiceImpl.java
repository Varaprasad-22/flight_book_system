package com.flightapp.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.dto.BookingGetResponse;
import com.flightapp.dto.Bookingdto;
import com.flightapp.dto.Passengers;
import com.flightapp.exceptions.BookingException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.BookingEntity;
import com.flightapp.model.FlightEntity;
import com.flightapp.model.PassengerEntity;
import com.flightapp.model.User;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private FlightRepository flightrepo;
	@Autowired
	private BookingRepository bookingrepo;
	@Autowired
	private UserRepository userrepo;

	@Override
	@Transactional
	public String bookFlight(Bookingdto data, String flightId) {
		// TODO Auto-generated method stub
		User user = getOrCreateUser(data.getEmailId(), data.getName());
		int id;
		try {
			id = Integer.parseInt(flightId);
		} catch (NumberFormatException e) {
			throw new BookingException("Invalid Flight ID format. Must be a number.");

		}
		Optional<FlightEntity> flightOpt = flightrepo.findById(id);
		if (!flightOpt.isPresent()) {
			throw new ResourceNotFoundException("Flight not found with ID: " + id);
		}
		FlightEntity flight = flightOpt.get();
		if (flight.getAvaliSeats() < data.getNoOfSeats()) {
			throw new BookingException("Failed Booking: Not enough available seats.");
		}
		BookingEntity booking = new BookingEntity();
		booking.setUser(user);
		booking.setEmailId(user.getEmail());
		booking.setNoOfSeats(data.getNoOfSeats());
		booking.setFlight(flight);
		booking.setStatus(true);
		booking.setBookingTime(LocalDateTime.now());
		booking.setPnr(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		data.getPassengers().forEach(a -> {
			PassengerEntity pass = new PassengerEntity();
			pass.setName(a.getName());
			pass.setAge(a.getAge());
			pass.setGender(a.getGender());

			pass.setMeal(a.getMeal());

			pass.setSeatNo(a.getSeatNo());
			booking.addPassenger(pass);
		});
		flight.setAvaliSeats(flight.getAvaliSeats() - data.getNoOfSeats());
		flightrepo.save(flight);

		bookingrepo.save(booking);

		return "Booking Successful! Your PNR is: " + booking.getPnr();
	}

	private User getOrCreateUser(String email, String name) {
		// Check if user already exists
		Optional<User> userOpt = userrepo.findByEmail(email);

		if (userOpt.isPresent()) {
			return userOpt.get();
		} else {
			// Create a new User
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setName(name);
			return userrepo.save(newUser);
		}
	}

	@Override
	public BookingGetResponse getBookingDetails(String pnr) {
		// TODO Auto-generated method stub
		Optional<BookingEntity> bookingOpt = bookingrepo.findByPnr(pnr);
		if (!bookingOpt.isPresent()) {
			throw new ResourceNotFoundException("No booking found with PNR: " + pnr);
		}
		BookingEntity booking = bookingOpt.get();
		BookingGetResponse response = new BookingGetResponse();
		response.setPnr(booking.getPnr());
		response.setFlightId(String.valueOf(booking.getFlight().getFlightId()));
		List<Passengers> passengersList = booking.getPassengers().stream().map(entity -> {
			Passengers dto = new Passengers();
			dto.setName(entity.getName());
			dto.setAge(entity.getAge());
			dto.setGender(entity.getGender());
			dto.setMeal(entity.getMeal());
			dto.setSeatNo(entity.getSeatNo());
			return dto;
		}).collect(Collectors.toList());

		response.setPassengersList(passengersList);
		return response;
	}

	@Override
	@Transactional
	public String cancelTicket(String pnr) {
		// TODO Auto-generated method stub
		Optional<BookingEntity> bookingOpt = bookingrepo.findByPnr(pnr);
		if (!bookingOpt.isPresent()) {
			throw new ResourceNotFoundException("Cancellation Failed: PNR not found.");
		}
		BookingEntity booking = bookingOpt.get();
		LocalDateTime flightDepartureTime = booking.getFlight().getDepatureTime();
		LocalDateTime currentTime = LocalDateTime.now();
		long hoursRemaining = Duration.between(currentTime, flightDepartureTime).toHours();

		if (hoursRemaining < 24) {
			throw new BookingException(
					"Cancellation Failed: Cannot cancel ticket less than 24 hours before journey date.");
		}
		FlightEntity flight = booking.getFlight();
		flight.setAvaliSeats(flight.getAvaliSeats() + booking.getNoOfSeats());
		flightrepo.save(flight);
		bookingrepo.deleteByPnr(pnr);
		return "Ticket with PNR " + pnr + " successfully cancelled.";
	}

	@Override
	public List<Bookingdto> getHistoryByEmail(String emailId) {
		// TODO Auto-generated method stub
		List<Bookingdto> bookingData = new ArrayList<>();
		try {
			List<BookingEntity> bookingEntityList = bookingrepo.findAllByEmailId(emailId);
			if (bookingEntityList.isEmpty()) {
				throw new ResourceNotFoundException("No booking history found for email: " + emailId);
			}
			for (BookingEntity booking : bookingEntityList) {

				Bookingdto dto = new Bookingdto();
				dto.setEmailId(booking.getEmailId());
				dto.setNoOfSeats(booking.getNoOfSeats());
				if (booking.getUser() != null) {
					dto.setName(booking.getUser().getName());
				}
				List<Passengers> passengerDtoList = new ArrayList<>();
				for (PassengerEntity passengerEntity : booking.getPassengers()) {

					Passengers passDto = new Passengers();

					passDto.setName(passengerEntity.getName());
					passDto.setAge(passengerEntity.getAge());
					passDto.setGender(passengerEntity.getGender());
					passDto.setMeal(passengerEntity.getMeal());

					passDto.setSeatNo(String.valueOf(passengerEntity.getSeatNo()));

					passengerDtoList.add(passDto);
				}
				dto.setPassengers(passengerDtoList);
				bookingData.add(dto);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookingData;
	}

}
