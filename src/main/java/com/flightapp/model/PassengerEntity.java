package com.flightapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name="passengers")
public class PassengerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int passengerId;

	@ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore 
    @ToString.Exclude 
	private BookingEntity booking; 
	private String name;
	private String gender;
	private int age;
	private String meal;
    
	private String seatNo;

	
	public int getPassengerId() {
		return passengerId;
	}

	public BookingEntity getBooking() {
		return booking;
	}

	public String getName() {
		return name;
	}

	public String getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}

	public String getMeal() {
		return meal;
	}

	public String getSeatNo() {
		return seatNo;
	}

	
	public void setBooking(BookingEntity booking) {
        this.booking = booking;
    }

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}
}
