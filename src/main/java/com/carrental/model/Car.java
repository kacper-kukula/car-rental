package com.carrental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@SQLDelete(sql = "UPDATE cars SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private int inventory;

    @Column(nullable = false, scale = 2)
    private BigDecimal dailyFee;

    @Column(nullable = false)
    private boolean isDeleted = false; // Default deletion status

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }

    @Override
    public String toString() {
        return "Car ID: " + id + '\n'
                + "Brand: " + brand + '\n'
                + "Model: " + model + '\n'
                + "Type: " + type + '\n'
                + "Daily fee: $" + dailyFee + '\n'
                + "Inventory left: " + inventory;
    }
}
