package com.flightapp.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Search {
	@NotBlank(message = "From place is required")
	private String fromPlace;
	@NotBlank(message = "To place is required")
	private String toPlace;
	@NotNull(message = "Departure date is required")
	private LocalDate departureDate;
	@NotBlank(message = "Trip type is required")
	private String tripType;
	private LocalDate returnDate;

	public String getFromPlace() {
		return fromPlace;
	}

	public String getToPlace() {
		return toPlace;
	}

	public LocalDate getDepartureDate() {
		return departureDate;
	}

	public String getTripType() {
		return tripType;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
}
