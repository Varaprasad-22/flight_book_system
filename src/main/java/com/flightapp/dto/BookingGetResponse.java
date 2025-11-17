package com.flightapp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingGetResponse {
	private String flightId;
	private String pnr;
	private List<Passengers> passengersList;

	public String getFlightId() {
		return flightId;
	}

	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public List<Passengers> getPassengersList() {
		return passengersList;
	}

	public void setPassengersList(List<Passengers> passengersList) {
		this.passengersList = passengersList;
	}
}
