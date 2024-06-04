package com.carrental.service.impl;

import com.carrental.dto.rental.RentalCreateRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.exception.custom.NoInventoryAvailableException;
import com.carrental.exception.custom.RentalAlreadyReturnedException;
import com.carrental.exception.custom.UnauthorizedViewException;
import com.carrental.mapper.RentalMapper;
import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.security.util.AuthenticationUtil;
import com.carrental.service.NotificationService;
import com.carrental.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private static final String RENTAL_NOT_FOUND_MESSAGE = "Rental not found.";
    private static final String CAR_NOT_FOUND_MESSAGE = "Car not found.";
    private static final String TELEGRAM_MESSAGE = "New rental created\n\n%s\n\n%s";

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;
    private final AuthenticationUtil authenticationUtil;

    @Override
    @Transactional
    public RentalResponseDto createRental(RentalCreateRequestDto request) {
        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));
        int availableInventory = car.getInventory();

        if (availableInventory <= 0) {
            throw new NoInventoryAvailableException("No inventory available for this car.");
        }

        car.setInventory(availableInventory - 1);
        Car savedCar = carRepository.save(car);

        Rental rental = new Rental();
        rental.setCar(savedCar);
        rental.setUser(authenticationUtil.getCurrentUserFromDb());
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(request.returnDate());
        Rental savedRental = rentalRepository.save(rental);

        notificationService.sendNotification(String.format(TELEGRAM_MESSAGE, rental, car));

        return rentalMapper.toDto(savedRental);
    }

    @Override
    @Transactional
    public List<RentalResponseDto> findRentalsByUserIdAndStatus(Long userId, boolean isActive) {
        Rental.Status status = isActive ? Rental.Status.ACTIVE : Rental.Status.RETURNED;
        List<Rental> rentals;

        if (userId == null) {
            rentals = rentalRepository.findByStatus(status);
        } else {
            rentals = rentalRepository.findByUserIdAndStatus(userId, status);
        }

        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public RentalResponseDto findRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RENTAL_NOT_FOUND_MESSAGE));

        validateCurrentUserOwnsRental(rental);

        return rentalMapper.toDto(rental);
    }

    @Override
    @Transactional
    public void returnRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RENTAL_NOT_FOUND_MESSAGE));

        validateCurrentUserOwnsRental(rental);

        if (rental.getStatus() == Rental.Status.RETURNED) {
            throw new RentalAlreadyReturnedException("Rental has already been returned.");
        }

        rental.setActualReturnDate(LocalDate.now());
        rental.setStatus(Rental.Status.RETURNED);
        rentalRepository.save(rental);

        Car car = carRepository.findById(rental.getCar().getId())
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));

        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Rental> overdueRentals = rentalRepository.findAllByReturnDateBetweenAndStatus(
                today, tomorrow, Rental.Status.ACTIVE);

        if (overdueRentals.isEmpty()) {
            notificationService.sendNotification("No rentals overdue today or tomorrow!");
        } else {
            for (Rental rental : overdueRentals) {
                String message = String.format("Overdue rental:\n\n%s", rental);
                notificationService.sendNotification(message);
            }
        }
    }

    private void validateCurrentUserOwnsRental(Rental rental) {
        Long rentalOwnerId = rental.getUser().getId();

        if (!rentalOwnerId.equals(authenticationUtil.getCurrentUserFromDb().getId())) {
            throw new UnauthorizedViewException("You are not authorized to view this rental.");
        }
    }

}
