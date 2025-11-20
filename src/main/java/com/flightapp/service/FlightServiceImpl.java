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
	private FlightRepository flightRepository;
	@Autowired
	private AirlineRepository airlineRepository;

	@Override
	public int addFlight(Flight flightRequest) {
		// TODO Auto-generated method stub
		try {
			FlightEntity flightEntity = new FlightEntity();
			Optional<Airline> airlines = airlineRepository.findByAirlineName(flightRequest.getAirlineName());
			if (!airlines.isPresent()) {
				throw new ResourceNotFoundException(
						"Flight save failed: Airline '" + flightRequest.getAirlineName() + "' not found.");
			}
			Optional<FlightEntity> flightOpt = flightRepository.findByFlightNumber(flightRequest.getFlightNumber());
			if (flightOpt.isPresent()) {
			    throw new ResourceNotFoundException("Flight with number " + flightRequest.getFlightNumber() + " does NOT exist.");
			}
			Airline airline = airlines.get();
			flightEntity.setAirlineId(airline.getAirlineId());
			flightEntity.setFlightNumber(flightRequest.getFlightNumber());
			flightEntity.setFromLocation(flightRequest.getFromPlace());
			flightEntity.setToLocation(flightRequest.getToPlace());
			flightEntity.setPrice(flightRequest.getPrice());
			flightEntity.setArrivalTime(flightRequest.getArrivalTime());
			flightEntity.setDepatureTime(flightRequest.getDepatureTime());
			flightEntity.setTotalSeats(flightRequest.getTotalSeats());
			flightEntity.setAvaliSeats(flightRequest.getTotalSeats());
			FlightEntity savedFlight=flightRepository.save(flightEntity);
			return savedFlight.getFlightId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Flight Saved failed: " + e.getMessage());
		}

	}

	@Override
	public SearchResult search(Search searchRequest) {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();

		LocalDateTime startOfDay = searchRequest.getDepartureDate().atStartOfDay();
		LocalDateTime endOfDay = searchRequest.getDepartureDate().atTime(LocalTime.MAX);

		List<FlightEntity> outboundEntities = flightRepository.findByFromLocationAndToLocationAndDepatureTimeBetween(
				searchRequest.getFromPlace(), searchRequest.getToPlace(), startOfDay, endOfDay);
		result.setOutboundFlights(mapEntitiesToDTOs(outboundEntities));
		 if (outboundEntities.isEmpty()) {
		        throw new ResourceNotFoundException("No flights found");
		}

		if ("round-trip".equalsIgnoreCase(searchRequest.getTripType()) && searchRequest.getReturnDate() != null) {

			LocalDateTime returnStart = searchRequest.getReturnDate().atStartOfDay();
			LocalDateTime returnEnd = searchRequest.getReturnDate().atTime(LocalTime.MAX);

			List<FlightEntity> inboundEntities = flightRepository.findByFromLocationAndToLocationAndDepatureTimeBetween(
					searchRequest.getToPlace(), searchRequest.getFromPlace(), returnStart, returnEnd);
			if (inboundEntities.isEmpty()) {
	            throw new ResourceNotFoundException("No return flights found");
	        }
			result.setInboundFlights(mapEntitiesToDTOs(inboundEntities));
		}

		return result;
	}

	private List<Flight> mapEntitiesToDTOs(List<FlightEntity> flightEntities) {
		return flightEntities.stream().map(entity -> {
			Flight flightRequestDto = new Flight();
			Optional<Airline> airlines = airlineRepository.findByAirlineId(entity.getAirlineId());
			if (!airlines.isPresent()) {
				throw new ResourceNotFoundException("Airline not found for ID: " + entity.getAirlineId());
			}
			Airline airline = airlines.get();
			flightRequestDto.setAirlineName(airline.getAirlineName());
			flightRequestDto.setFlightNumber(entity.getFlightNumber());
			flightRequestDto.setFromPlace(entity.getFromLocation());
			flightRequestDto.setToPlace(entity.getToLocation());
			flightRequestDto.setPrice(entity.getPrice());
			flightRequestDto.setTotalSeats(entity.getTotalSeats());
			flightRequestDto.setDepatureTime(entity.getDepatureTime());
			flightRequestDto.setArrivalTime(entity.getArrivalTime());

			// TODO: Add your logic to find airlineName from entity.getAirlineId()

			return flightRequestDto;
		}).collect(Collectors.toList());
	}

}
