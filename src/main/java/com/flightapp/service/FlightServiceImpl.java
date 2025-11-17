package com.flightapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.dto.Flight;
import com.flightapp.model.Airline;
import com.flightapp.model.FlightEntity;
import com.flightapp.dto.Search;
import com.flightapp.dto.SearchResult;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepo;
	@Autowired
	private AirlineRepository airlinerepo;

	@Override
	public String addFlight(Flight flight) {
		// TODO Auto-generated method stub
		try {
			FlightEntity fli = new FlightEntity();
			Optional<Airline> airlines = airlinerepo.findByAirlineName(flight.getAirlineName());
			if (!airlines.isPresent()) {
				throw new ResourceNotFoundException(
						"Flight save failed: Airline '" + flight.getAirlineName() + "' not found.");
			}
			Airline airline = airlines.get();
			fli.setAirlineId(airline.getAirlineId());
			fli.setFlightNumber(flight.getFlightNumber());
			fli.setFromLocation(flight.getFromPlace());
			fli.setToLocation(flight.getToPlace());
			fli.setPrice(flight.getPrice());
			fli.setArrivalTime(flight.getArrivalTime());
			fli.setDepatureTime(flight.getDepatureTime());
			fli.setTotalSeats(flight.getTotalSeats());
			fli.setAvaliSeats(flight.getTotalSeats());
			flightRepo.save(fli);
			return "Flight Saved";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Flight Saved failed: " + e.getMessage());
		}

	}

	@Override
	public SearchResult search(Search data) {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();

		LocalDateTime startOfDay = data.getDepartureDate().atStartOfDay();
		LocalDateTime endOfDay = data.getDepartureDate().atTime(LocalTime.MAX);

		List<FlightEntity> outboundEntities = flightRepo.findByFromLocationAndToLocationAndDepatureTimeBetween(
				data.getFromPlace(), data.getToPlace(), startOfDay, endOfDay);
		result.setOutboundFlights(mapEntitiesToDTOs(outboundEntities));

		if ("round-trip".equalsIgnoreCase(data.getTripType()) && data.getReturnDate() != null) {

			LocalDateTime returnStart = data.getReturnDate().atStartOfDay();
			LocalDateTime returnEnd = data.getReturnDate().atTime(LocalTime.MAX);

			List<FlightEntity> inboundEntities = flightRepo.findByFromLocationAndToLocationAndDepatureTimeBetween(
					data.getToPlace(), data.getFromPlace(), returnStart, returnEnd);
			result.setInboundFlights(mapEntitiesToDTOs(inboundEntities));
		}

		return result;
	}

	private List<Flight> mapEntitiesToDTOs(List<FlightEntity> entities) {
		return entities.stream().map(entity -> {
			Flight dto = new Flight();
			Optional<Airline> airlines = airlinerepo.findByAirlineId(entity.getAirlineId());
			if (!airlines.isPresent()) {
				throw new ResourceNotFoundException("Airline not found for ID: " + entity.getAirlineId());
			}
			Airline airline = airlines.get();
			dto.setAirlineName(airline.getAirlineName());
			dto.setFlightNumber(entity.getFlightNumber());
			dto.setFromPlace(entity.getFromLocation());
			dto.setToPlace(entity.getToLocation());
			dto.setPrice(entity.getPrice());
			dto.setTotalSeats(entity.getTotalSeats());
			dto.setDepatureTime(entity.getDepatureTime());
			dto.setArrivalTime(entity.getArrivalTime());

			// TODO: Add your logic to find airlineName from entity.getAirlineId()

			return dto;
		}).collect(Collectors.toList());
	}

}
