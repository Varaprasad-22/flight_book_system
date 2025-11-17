package com.flightapp.repository;

import java.util.Optional;

import javax.swing.JPanel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.model.Airline;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer>{

	Optional<Airline> findByAirlineName(String airlineName);

	Optional<Airline> findByAirlineId(int airlineId);

}
