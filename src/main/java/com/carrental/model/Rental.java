package com.carrental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@SQLDelete(sql = "UPDATE rentals SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate rentalDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Column
    private LocalDate actualReturnDate;

    @Column(nullable = false)
    private Long carId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE; // Default return state

    @Column(nullable = false)
    private boolean isDeleted = false; // Default deletion status

    public enum Status {
        ACTIVE,
        RETURNED
    }

    @Override
    public String toString() {
        return "Rental id: " + id + '\n'
                + "Start Date: " + rentalDate + '\n'
                + "Return Date: " + returnDate + '\n'
                + "Total days: " + ChronoUnit.DAYS.between(rentalDate, returnDate) + '\n'
                + "Status: " + status;
    }
}
