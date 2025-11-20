package com.flightapp.repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.model.FlightEntity;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {



	List<FlightEntity> findByFromLocationAndToLocationAndDepatureTimeBetween(String fromPlace, String toPlace,
			LocalDateTime startOfDay, LocalDateTime endOfDay);

	Optional<FlightEntity> findByFlightNumber(String flightNumber);
}
