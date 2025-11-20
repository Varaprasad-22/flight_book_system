package com.flightapp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookingdto {
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid Email format")
	private String emailId;
	@NotBlank(message = "Name is required")
	private String name;
	@Min(value = 1, message = "Must book at least 1 seat")
	private int noOfSeats;
	@Valid
	@NotEmpty(message = "Passenger list cannot be empty")
	private List<Passengers> passengers;
	private Integer outboundFlightId;
	private Integer returnFlightId;


	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public List<Passengers> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passengers> passengers) {
		this.passengers = passengers;
	}

	public Integer getOutboundFlightId() {
		return outboundFlightId;
	}

	public void setOutboundFlightId(Integer outboundFlightId) {
		this.outboundFlightId = outboundFlightId;
	}

	public Integer getReturnFlightId() {
		return returnFlightId;
	}

	public void setReturnFlightId(Integer returnFlightId) {
		this.returnFlightId = returnFlightId;
	}

}
