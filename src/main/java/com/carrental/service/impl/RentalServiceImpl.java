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
import com.carrental.repository.UserRepository;
import com.carrental.service.NotificationService;
import com.carrental.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private static final String RENTAL_NOT_FOUND_MESSAGE = "Rental not found.";
    private static final String CAR_NOT_FOUND_MESSAGE = "Car not found.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    private static final String TELEGRAM_MESSAGE = "New rental created\n\n%s\n\n%s";

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    @Transactional
    public RentalResponseDto createRental(RentalCreateRequestDto request) {
        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));

        if (car.getInventory() <= 0) {
            throw new NoInventoryAvailableException("No inventory available for this car.");
        }

        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);

        Rental rental = rentalMapper.toEntity(request);
        rental.setUserId(getCurrentUserIdFromDb());
        rental.setRentalDate(LocalDate.now());
        Rental savedRental = rentalRepository.save(rental);

        notificationService.sendNotification(String.format(TELEGRAM_MESSAGE, rental, car));

        return rentalMapper.toDto(savedRental);
    }

    @Override
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

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));

        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Rental> overdueRentals = rentalRepository.findAllByReturnDateBetweenAndStatus(
                today, tomorrow, Rental.Status.ACTIVE);

        if (overdueRentals.isEmpty()) {
            telegramNotificationService.sendNotification("No rentals overdue today or tomorrow!");
        } else {
            for (Rental rental : overdueRentals) {
                String message = String.format("Overdue rental:\n\n%s", rental);
                telegramNotificationService.sendNotification(message);
            }
        }
    }

    private Long getCurrentUserIdFromDb() {
        String username = ((UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE))
                .getId();
    }

    private void validateCurrentUserOwnsRental(Rental rental) {
        if (!rental.getUserId().equals(getCurrentUserIdFromDb())) {
            throw new UnauthorizedViewException("You are not authorized to view this rental.");
        }
    }

}
