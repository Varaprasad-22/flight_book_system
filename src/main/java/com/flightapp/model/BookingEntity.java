package com.flightapp.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ForeignKey;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(name="booking")
public class BookingEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int bookingId;
	private String emailId;
	
	private String pnr;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@ManyToOne
    @JoinColumn(name = "flight_id")
	private FlightEntity flight;
	private LocalDateTime bookingTime;
	private int noOfSeats;
	private boolean status;
	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public FlightEntity getFlight() {
		return flight;
	}

	public void setFlight(FlightEntity flight) {
		this.flight = flight;
	}

	
	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<PassengerEntity> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerEntity> passengers) {
		this.passengers = passengers;
	}

	
	
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PassengerEntity> passengers = new ArrayList<>();

     public void addPassenger(PassengerEntity passenger) {
        passengers.add(passenger);
        passenger.setBooking(this);
    }

	 public void setBookingTime(LocalDateTime bookingTime) {
		 this.bookingTime = bookingTime;
	 }
}
