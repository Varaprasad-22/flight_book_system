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
	private FlightRepository flightRepository;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public String bookFlight(Bookingdto data) {
		int outboundId = data.getOutboundFlightId();

		FlightEntity outboundFlight = flightRepository.findById(outboundId)
				.orElseThrow(() -> new ResourceNotFoundException("Outbound flight not found"));

		if (outboundFlight.getAvaliSeats() < data.getNoOfSeats()) {
			throw new BookingException("Not enough seats in outbound flight.");
		}

		User user = getOrCreateUser(data.getEmailId(), data.getName());

		BookingEntity bookingEntity = new BookingEntity();
		bookingEntity.setUser(user);
		bookingEntity.setEmailId(data.getEmailId());
		bookingEntity.setFlight(outboundFlight);
		bookingEntity.setNoOfSeats(data.getNoOfSeats());
		bookingEntity.setStatus(true);
		bookingEntity.setBookingTime(LocalDateTime.now());
		bookingEntity.setPnr(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		data.getPassengers().forEach(passengerRequest -> {
			PassengerEntity passengerEntity = new PassengerEntity();
			passengerEntity.setName(passengerRequest.getName());
			passengerEntity.setAge(passengerRequest.getAge());
			passengerEntity.setGender(passengerRequest.getGender());
			passengerEntity.setMeal(passengerRequest.getMeal());
			passengerEntity.setSeatNo(passengerRequest.getSeatNo());
			bookingEntity.addPassenger(passengerEntity);
		});

		outboundFlight.setAvaliSeats(outboundFlight.getAvaliSeats() - data.getNoOfSeats());
		flightRepository.save(outboundFlight);
		Integer returnId = data.getReturnFlightId();
		if (returnId != null) {

			FlightEntity returnFlight = flightRepository.findById(returnId)
					.orElseThrow(() -> new ResourceNotFoundException("Return flight not found"));

			if (returnFlight.getAvaliSeats() < data.getNoOfSeats()) {
				throw new BookingException("Not enough seats in return flight.");
			}

			bookingEntity.setReturnFlight(returnFlight);
			returnFlight.setAvaliSeats(returnFlight.getAvaliSeats() - data.getNoOfSeats());
			flightRepository.save(returnFlight);

			bookingRepository.save(bookingEntity);

			return "Round-trip Booking Successful! PNR: " + bookingEntity.getPnr();
		}

		bookingRepository.save(bookingEntity);

		return "One-way Booking Successful! PNR: " + bookingEntity.getPnr();
	}

	private User getOrCreateUser(String email, String name) {
		Optional<User> userOpt = userRepository.findByEmail(email);

		if (userOpt.isPresent()) {
			return userOpt.get();
		} else {
			// Create a new User
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setName(name);
			return userRepository.save(newUser);
		}
	}

	@Override
	public BookingGetResponse getBookingDetails(String pnr) {
		// TODO Auto-generated method stub
		Optional<BookingEntity> bookingOpt = bookingRepository.findByPnr(pnr);
		if (!bookingOpt.isPresent()) {
			throw new ResourceNotFoundException("No booking found with PNR: " + pnr);
		}
		BookingEntity bookingEntity = bookingOpt.get();
		BookingGetResponse response = new BookingGetResponse();
		response.setPnr(bookingEntity.getPnr());
		response.setFlightId(String.valueOf(bookingEntity.getFlight().getFlightId()));
		List<Passengers> passengersList = bookingEntity.getPassengers().stream().map(entity -> {
			Passengers passengerDto = new Passengers();
			passengerDto.setName(entity.getName());
			passengerDto.setAge(entity.getAge());
			passengerDto.setGender(entity.getGender());
			passengerDto.setMeal(entity.getMeal());
			passengerDto.setSeatNo(entity.getSeatNo());
			return passengerDto;
		}).collect(Collectors.toList());

		response.setPassengersList(passengersList);
		return response;
	}

	@Override
	@Transactional
	public String cancelTicket(String pnr) {
		// TODO Auto-generated method stub
		Optional<BookingEntity> bookingOpt = bookingRepository.findByPnr(pnr);
		if (!bookingOpt.isPresent()) {
			throw new ResourceNotFoundException("Cancellation Failed: PNR not found.");
		}
		BookingEntity bookingEntity = bookingOpt.get();
		LocalDateTime flightDepartureTime = bookingEntity.getFlight().getDepatureTime();
		LocalDateTime currentTime = LocalDateTime.now();
		long hoursRemaining = Duration.between(currentTime, flightDepartureTime).toHours();

		if (hoursRemaining < 24) {
			throw new BookingException(
					"Cancellation Failed: Cannot cancel ticket less than 24 hours before journey date.");
		}
		FlightEntity flightEntity = bookingEntity.getFlight();
		flightEntity.setAvaliSeats(flightEntity.getAvaliSeats() + bookingEntity.getNoOfSeats());
		flightRepository.save(flightEntity);
		bookingRepository.deleteByPnr(pnr);
		return "Ticket with PNR " + pnr + " successfully cancelled.";
	}

	@Override
	public List<Bookingdto> getHistoryByEmail(String emailId) {
		// TODO Auto-generated method stub
		List<Bookingdto> bookingData = new ArrayList<>();
		try {
			List<BookingEntity> bookingEntityList = bookingRepository.findAllByEmailId(emailId);
			if (bookingEntityList.isEmpty()) {
				throw new ResourceNotFoundException("No booking history found for email: " + emailId);
			}
			for (BookingEntity booking : bookingEntityList) {

				Bookingdto bookingDto = new Bookingdto();
				bookingDto.setEmailId(booking.getEmailId());
				bookingDto.setNoOfSeats(booking.getNoOfSeats());
				if (booking.getUser() != null) {
					bookingDto.setName(booking.getUser().getName());
				}
				List<Passengers> passengerDtoList = new ArrayList<>();
				for (PassengerEntity passengerEntity : booking.getPassengers()) {

					Passengers passengerDto = new Passengers();

					passengerDto.setName(passengerEntity.getName());
					passengerDto.setAge(passengerEntity.getAge());
					passengerDto.setGender(passengerEntity.getGender());
					passengerDto.setMeal(passengerEntity.getMeal());

					passengerDto.setSeatNo(String.valueOf(passengerEntity.getSeatNo()));

					passengerDtoList.add(passengerDto);
				}
				bookingDto.setPassengers(passengerDtoList);
				bookingData.add(bookingDto);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookingData;
	}

}
