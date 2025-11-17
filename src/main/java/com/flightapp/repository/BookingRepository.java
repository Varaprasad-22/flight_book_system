package com.flightapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightapp.dto.BookingGetResponse;
import com.flightapp.model.BookingEntity;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer>{


	List<BookingEntity> findAllByEmailId(String emailId);

	Optional<BookingEntity> findByPnr(String pnr);

	void deleteByPnr(String pnr);

}
