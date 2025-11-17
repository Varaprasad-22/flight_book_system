package com.flightapp.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
	@NotBlank(message = "Airline name is required")
	private String airlineName;
	@NotBlank(message = "Flight number is required")
	private String flightNumber;
	@NotBlank(message = "From place is required")
	private String fromPlace;
	@NotBlank(message = "To place is required")
	private String toPlace;
	@NotNull(message = "Arrival time is required")
	private LocalDateTime arrivalTime;
	@NotNull(message = "Departure time is required")
	private LocalDateTime depatureTime;
	@Min(value = 1, message = "Total seats must be at least 1")
	private int totalSeats;
	@Min(value = 1, message = "Price must be greater than 0")
	private double price;

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getFromPlace() {
		return fromPlace;
	}

	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	public String getToPlace() {
		return toPlace;
	}

	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double d) {
		this.price = d;
	}

	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public LocalDateTime getDepatureTime() {
		return depatureTime;
	}

	public void setDepatureTime(LocalDateTime depatureTime) {
		this.depatureTime = depatureTime;
	}
}
