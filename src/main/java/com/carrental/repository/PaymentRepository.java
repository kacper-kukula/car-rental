package com.carrental.repository;

import com.carrental.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p JOIN p.rental r JOIN r.user u WHERE u.id = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);

    Optional<Payment> findByRentalIdAndType(Long rentalId, Payment.Type type);
}
