package com.carrental.repository;

import com.carrental.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserIdAndStatus(Long userId, Rental.Status status);

    List<Rental> findByStatus(Rental.Status status);

    List<Rental> findAllByReturnDateBetweenAndStatus(
            LocalDate startDate, LocalDate endDate, Rental.Status status);
}
