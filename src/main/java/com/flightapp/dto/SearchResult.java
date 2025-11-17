package com.flightapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class SearchResult {

	private List<Flight> outboundFlights;
	private List<Flight> inboundFlights;

	public List<Flight> getOutboundFlights() {
		return outboundFlights;
	}

	public void setOutboundFlights(List<Flight> outboundFlights) {
		this.outboundFlights = outboundFlights;
	}

	public List<Flight> getInboundFlights() {
		return inboundFlights;
	}

	public void setInboundFlights(List<Flight> inboundFlights) {
		this.inboundFlights = inboundFlights;
	}
}