package com.flightapp.service;

import java.util.List;

import com.flightapp.dto.Flight;
import com.flightapp.dto.Search;
import com.flightapp.dto.SearchResult;

public interface FlightService {
	int addFlight(Flight flight);
	SearchResult search(Search data);
}
